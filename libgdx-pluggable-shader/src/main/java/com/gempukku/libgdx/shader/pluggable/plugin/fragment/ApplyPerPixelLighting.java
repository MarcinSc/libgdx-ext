package com.gempukku.libgdx.shader.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel.PerPixelLightingApplyFunction;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel.PerPixelLightingCalculateFunctionCall;

import java.util.LinkedList;
import java.util.List;

public class ApplyPerPixelLighting extends AbstractPluggableFragmentFunctionCall {
    private PluggableFragmentFunctionCall lightDiffuseSource;
    private PluggableFragmentFunctionCall lightSpecularSource;
    private PluggableFragmentFunctionCall normalSource;
    private List<PerPixelLightingApplyFunction> lightingApplyFunctions = new LinkedList<PerPixelLightingApplyFunction>();

    private List<PerPixelLightingCalculateFunctionCall> lightWrappers = new LinkedList<PerPixelLightingCalculateFunctionCall>();
    private List<PluggableFragmentFunctionCall> normalWrappers = new LinkedList<PluggableFragmentFunctionCall>();

    public ApplyPerPixelLighting() {
    }

    public ApplyPerPixelLighting(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    public void setLightDiffuseSource(PluggableFragmentFunctionCall lightDiffuseSource) {
        this.lightDiffuseSource = lightDiffuseSource;
    }

    public void setLightSpecularSource(PluggableFragmentFunctionCall lightSpecularSource) {
        this.lightSpecularSource = lightSpecularSource;
    }

    public void setNormalSource(PluggableFragmentFunctionCall normalSource) {
        this.normalSource = normalSource;
    }

    public void addNormalWrapper(PluggableFragmentFunctionCall normalWrapper) {
        this.normalWrappers.add(normalWrapper);
    }

    public void addLightWrapper(PerPixelLightingCalculateFunctionCall lightWrapper) {
        lightWrappers.add(lightWrapper);
    }

    public void addLightingApplyFunction(PerPixelLightingApplyFunction lightingApplyFunction) {
        this.lightingApplyFunctions.add(lightingApplyFunction);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "applyPerPixelLighting";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        boolean hasSpecular = lightSpecularSource.isProcessing(renderable);
        lightDiffuseSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
        if (hasSpecular)
            lightSpecularSource.appendShaderFeatures(renderable, pluggableShaderFeatures);

        normalSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
        for (PluggableFragmentFunctionCall normalWrapper : normalWrappers) {
            if (normalWrapper.isProcessing(renderable))
                normalWrapper.appendShaderFeatures(renderable, pluggableShaderFeatures);
        }

        for (PerPixelLightingCalculateFunctionCall lightWrapper : lightWrappers) {
            if (lightWrapper.isProcessing(renderable, hasSpecular))
                lightWrapper.appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
        }

        findFirstMatchingLightingApplyFunction(renderable, hasSpecular).appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addStructure("Lighting",
                "  vec3 diffuse;\n" +
                        "  vec3 specular;\n");
        fragmentShaderBuilder.addVaryingVariable("v_vertexWorldPosition", "vec4");

        boolean specularCalculation = lightSpecularSource.isProcessing(renderable);
        lightDiffuseSource.appendFunction(renderable, fragmentShaderBuilder);
        if (specularCalculation)
            lightSpecularSource.appendFunction(renderable, fragmentShaderBuilder);

        normalSource.appendFunction(renderable, fragmentShaderBuilder);

        StringBuilder function = new StringBuilder();
        function.append("vec4 applyPerPixelLighting(vec4 color) {\n");

        function.append("  vec3 normal = " + normalSource.getFunctionName(renderable) + "(v_vertexWorldPosition);\n");
        for (PluggableFragmentFunctionCall normalWrapper : normalWrappers) {
            if (normalWrapper.isProcessing(renderable)) {
                normalWrapper.appendFunction(renderable, fragmentShaderBuilder);
                function.append("  normal = " + normalWrapper.getFunctionName(renderable) + "(v_vertexWorldPosition, normal);\n");
            }
        }

        function.append("  vec3 lightDiffuse = " + lightDiffuseSource.getFunctionName(renderable) + "(v_vertexWorldPosition);\n");
        if (specularCalculation)
            function.append("  vec3 lightSpecular = " + lightSpecularSource.getFunctionName(renderable) + "(v_vertexWorldPosition);\n");
        else
            function.append("  vec3 lightSpecular = vec3(0.0);\n");

        PerPixelLightingApplyFunction lightingApplyFunction = findFirstMatchingLightingApplyFunction(renderable, specularCalculation);
        lightingApplyFunction.appendFunction(renderable, fragmentShaderBuilder, specularCalculation);

        if (lightWrappers.size() > 0) {
            function.append("  Lighting lighting = Lighting(lightDiffuse, lightSpecular);\n");

            for (PerPixelLightingCalculateFunctionCall lightWrapper : lightWrappers) {
                if (lightWrapper.isProcessing(renderable, specularCalculation)) {
                    lightWrapper.appendFunction(renderable, fragmentShaderBuilder, specularCalculation);
                    function.append("  lighting = " + lightWrapper.getFunctionName(renderable, specularCalculation) + "(v_vertexWorldPosition, normal, lighting);\n");
                }
            }

            if (specularCalculation)
                function.append("  color.rgb = " + lightingApplyFunction.getFunctionName(renderable, specularCalculation) + "(v_vertexWorldPosition, color.rgb, lighting.diffuse, lighting.specular);");
            else
                function.append("  color.rgb = " + lightingApplyFunction.getFunctionName(renderable, specularCalculation) + "(v_vertexWorldPosition, color.rgb, lighting.diffuse);");
        } else {
            if (specularCalculation)
                function.append("  color.rgb = " + lightingApplyFunction.getFunctionName(renderable, specularCalculation) + "(v_vertexWorldPosition, color.rgb, diffuse, specular);");
            else
                function.append("  color.rgb = " + lightingApplyFunction.getFunctionName(renderable, specularCalculation) + "(v_vertexWorldPosition, color.rgb, diffuse);");
        }

        function.append("  return color;\n" +
                "}\n");

        fragmentShaderBuilder.addFunction("applyPerPixelLighting", function.toString());
    }

    private PerPixelLightingApplyFunction findFirstMatchingLightingApplyFunction(Renderable renderable, boolean hasSpecular) {
        for (PerPixelLightingApplyFunction lightingApplyFunction : lightingApplyFunctions) {
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
