package com.gempukku.libgdx.shader.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.*;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.vertex.PerVertexLightingApplyFunction;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.vertex.PerVertexLightingWrapperFunctionCall;

import java.util.LinkedList;
import java.util.List;

public class ApplyPerVertexLighting extends AbstractPluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature applyPerVertexLighting = PluggableShaderFeatureRegistry.registerFeature();

    private PluggableFragmentFunctionCall lightDiffuseSource;
    private PluggableFragmentFunctionCall lightSpecularSource;
    private List<PerVertexLightingApplyFunction> lightingApplyFunctions = new LinkedList<PerVertexLightingApplyFunction>();

    private List<PerVertexLightingWrapperFunctionCall> lightWrappers = new LinkedList<PerVertexLightingWrapperFunctionCall>();

    public ApplyPerVertexLighting() {
    }

    public ApplyPerVertexLighting(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    public void setLightDiffuseSource(PluggableFragmentFunctionCall lightDiffuseSource) {
        this.lightDiffuseSource = lightDiffuseSource;
    }

    public void setLightSpecularSource(PluggableFragmentFunctionCall lightSpecularSource) {
        this.lightSpecularSource = lightSpecularSource;
    }

    public void addLightingApplyFunction(PerVertexLightingApplyFunction lightingApplyFunction) {
        this.lightingApplyFunctions.add(lightingApplyFunction);
    }

    public void addLightWrapper(PerVertexLightingWrapperFunctionCall lightWrapper) {
        lightWrappers.add(lightWrapper);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "applyPerVertexLighting";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(applyPerVertexLighting);
        lightDiffuseSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
        boolean hasSpecular = lightSpecularSource.isProcessing(renderable);
        if (hasSpecular)
            lightSpecularSource.appendShaderFeatures(renderable, pluggableShaderFeatures);

        for (PerVertexLightingWrapperFunctionCall lightWrapper : lightWrappers) {
            if (lightWrapper.isProcessing(renderable, hasSpecular))
                lightWrapper.appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
        }

        findFirstMatchingLightingApplyFunction(renderable, hasSpecular).appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addStructure("Lighting",
                "vec3 diffuse;\n" +
                        "vec3 specular;\n");

        lightDiffuseSource.appendFunction(renderable, fragmentShaderBuilder);
        boolean specularCalculation = lightSpecularSource.isProcessing(renderable);
        if (specularCalculation)
            lightSpecularSource.appendFunction(renderable, fragmentShaderBuilder);

        StringBuilder function = new StringBuilder();

        function.append("vec4 applyPerVertexLighting(vec4 color) {\n");
        function.append("  vec3 diffuse = " + lightDiffuseSource.getFunctionName(renderable) + "();\n");
        if (specularCalculation)
            function.append("  vec3 specular = " + lightSpecularSource.getFunctionName(renderable) + "();\n");
        else
            function.append("  vec3 specular = vec3(0.0);\n");

        PerVertexLightingApplyFunction lightingApplyFunction = findFirstMatchingLightingApplyFunction(renderable, specularCalculation);
        lightingApplyFunction.appendFunction(renderable, fragmentShaderBuilder, specularCalculation);

        if (lightWrappers.size() > 0) {
            function.append("  Lighting lighting = Lighting(diffuse, specular);\n");

            for (PerVertexLightingWrapperFunctionCall lightWrapper : lightWrappers) {
                if (lightWrapper.isProcessing(renderable, specularCalculation)) {
                    lightWrapper.appendFunction(renderable, fragmentShaderBuilder, specularCalculation);
                    function.append("  lighting = " + lightWrapper.getFunctionName(renderable, specularCalculation) + "(lighting);\n");
                }
            }

            if (specularCalculation)
                function.append("  color.rgb = " + lightingApplyFunction.getFunctionName(renderable, specularCalculation) + "(color.rgb, lighting.diffuse, lighting.specular);");
            else
                function.append("  color.rgb = " + lightingApplyFunction.getFunctionName(renderable, specularCalculation) + "(color.rgb, lighting.diffuse);");
        } else {
            if (specularCalculation)
                function.append("  color.rgb = " + lightingApplyFunction.getFunctionName(renderable, specularCalculation) + "(color.rgb, diffuse, specular);");
            else
                function.append("  color.rgb = " + lightingApplyFunction.getFunctionName(renderable, specularCalculation) + "(color.rgb, diffuse);");
        }

        function.append("  return color;\n" +
                "}\n");

        fragmentShaderBuilder.addFunction("applyPerVertexLighting", function.toString());
    }

    private PerVertexLightingApplyFunction findFirstMatchingLightingApplyFunction(Renderable renderable, boolean hasSpecular) {
        for (PerVertexLightingApplyFunction lightingApplyFunction : lightingApplyFunctions) {
            if (lightingApplyFunction.isProcessing(renderable, hasSpecular))
                return lightingApplyFunction;
        }
        throw new IllegalStateException("Unable to find lighting apply function");
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        long vertexMask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        return renderable.environment != null && hasNormal(vertexMask);
    }

    private boolean hasNormal(long vertexMask) {
        return (vertexMask & VertexAttributes.Usage.Normal) != 0;
    }
}
