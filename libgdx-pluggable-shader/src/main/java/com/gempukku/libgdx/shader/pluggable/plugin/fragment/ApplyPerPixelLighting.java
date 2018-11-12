package com.gempukku.libgdx.shader.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel.PerPixelLightingCalculateFunctionCall;

import java.util.LinkedList;
import java.util.List;

public class ApplyPerPixelLighting extends AbstractPluggableFragmentFunctionCall {
    private PluggableFragmentFunctionCall lightDiffuseSource;
    private PluggableFragmentFunctionCall lightSpecularSource;

    private List<PerPixelLightingCalculateFunctionCall> lightWrappers = new LinkedList<PerPixelLightingCalculateFunctionCall>();

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

    public void addLightWrapper(PerPixelLightingCalculateFunctionCall lightWrapper) {
        lightWrappers.add(lightWrapper);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "applyPerPixelLighting";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        boolean hasSpecular = hasSpecularCalculation(renderable);
        lightDiffuseSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
        if (hasSpecular)
            lightSpecularSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
        for (PerPixelLightingCalculateFunctionCall lightWrapper : lightWrappers) {
            if (lightWrapper.isProcessing(renderable, hasSpecular))
                lightWrapper.appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
        }
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addStructure("Lighting",
                "  vec3 diffuse;\n" +
                        "  vec3 specular;\n");
        fragmentShaderBuilder.addVaryingVariable("v_vertexWorldPosition", "vec4");
        fragmentShaderBuilder.addVaryingVariable("v_normal", "vec3");

        boolean specularCalculation = hasSpecularCalculation(renderable);
        lightDiffuseSource.appendFunction(renderable, fragmentShaderBuilder);
        if (specularCalculation)
            lightSpecularSource.appendFunction(renderable, fragmentShaderBuilder);

        StringBuilder function = new StringBuilder();
        function.append("vec4 applyPerPixelLighting(vec4 color) {\n");
        function.append("  vec3 lightDiffuse = " + lightDiffuseSource.getFunctionName(renderable) + "(v_vertexWorldPosition);\n");
        if (specularCalculation)
            function.append("  vec3 lightSpecular = " + lightSpecularSource.getFunctionName(renderable) + "(v_vertexWorldPosition);\n");
        else
            function.append("  vec3 lightSpecular = vec3(0.0);\n");

        if (lightWrappers.size() > 0) {
            function.append("  Lighting lighting = Lighting(lightDiffuse, lightSpecular);\n");

            for (PerPixelLightingCalculateFunctionCall lightWrapper : lightWrappers) {
                if (lightWrapper.isProcessing(renderable, specularCalculation)) {
                    lightWrapper.appendFunction(renderable, fragmentShaderBuilder, specularCalculation);
                    function.append("  lighting = " + lightWrapper.getFunctionName(renderable, specularCalculation) + "(v_vertexWorldPosition, lighting);\n");
                }
            }

            function.append("  color.rgb = color.rgb * lighting.diffuse;\n");
            if (specularCalculation)
                function.append("  color.rgb = color.rgb + lighting.specular;\n");
        } else {
            function.append("  color.rgb = color.rgb * diffuse;\n");
            if (specularCalculation)
                function.append("  color.rgb = color.rgb + specular;\n");
        }

        function.append("  return color;\n" +
                "}\n");

        fragmentShaderBuilder.addFunction("applyPerPixelLightin", function.toString());
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        long vertexMask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        return renderable.environment != null &&
                (hasNormal(vertexMask) || hasTangentAndBiNormal(vertexMask));
    }

    private boolean hasSpecularCalculation(Renderable renderable) {
        return renderable.material.has(TextureAttribute.Specular) || renderable.material.has(ColorAttribute.Specular);
    }

    private boolean hasNormal(long vertexMask) {
        return (vertexMask & VertexAttributes.Usage.Normal) != 0;
    }

    private boolean hasTangentAndBiNormal(long vertexMask) {
        return (vertexMask & VertexAttributes.Usage.Tangent) != 0 && (vertexMask & VertexAttributes.Usage.BiNormal) != 0;
    }
}
