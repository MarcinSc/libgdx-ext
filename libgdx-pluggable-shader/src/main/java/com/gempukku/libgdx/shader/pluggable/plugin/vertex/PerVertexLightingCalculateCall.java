package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.PluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.vertex.PerVertexLightingCalculateFunctionCall;

import java.util.LinkedList;
import java.util.List;

public class PerVertexLightingCalculateCall extends AbstractPluggableVertexFunctionCall {
    private PluggableVertexFunctionCall lightDiffuseSource;
    private PluggableVertexFunctionCall lightSpecularSource;

    private List<PerVertexLightingCalculateFunctionCall> lightWrappers = new LinkedList<PerVertexLightingCalculateFunctionCall>();

    public PerVertexLightingCalculateCall() {
    }

    public PerVertexLightingCalculateCall(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    public void setLightDiffuseSource(PluggableVertexFunctionCall lightDiffuseSource) {
        this.lightDiffuseSource = lightDiffuseSource;
    }

    public void setLightSpecularSource(PluggableVertexFunctionCall lightSpecularSource) {
        this.lightSpecularSource = lightSpecularSource;
    }

    public void addLightWrapper(PerVertexLightingCalculateFunctionCall lightWrapper) {
        lightWrappers.add(lightWrapper);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "calculatePerVertexLighting";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        boolean hasSpecular = lightSpecularSource.isProcessing(renderable);
        lightDiffuseSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
        if (hasSpecular)
            lightSpecularSource.appendShaderFeatures(renderable, pluggableShaderFeatures);
        for (PerVertexLightingCalculateFunctionCall lightWrapper : lightWrappers) {
            if (lightWrapper.isProcessing(renderable, hasSpecular))
                lightWrapper.appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
        }
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addStructure("Lighting",
                "  vec3 diffuse;\n" +
                        "  vec3 specular;\n");

        vertexShaderBuilder.addVaryingVariable("v_lightDiffuse", "vec3");
        boolean specularCalculation = lightSpecularSource.isProcessing(renderable);
        if (specularCalculation)
            vertexShaderBuilder.addVaryingVariable("v_lightSpecular", "vec3");

        lightDiffuseSource.appendFunction(renderable, vertexShaderBuilder);
        if (specularCalculation)
            lightSpecularSource.appendFunction(renderable, vertexShaderBuilder);

        StringBuilder sb = new StringBuilder();
        sb.append("vec4 calculatePerVertexLighting(vec4 position) {\n");
        sb.append("  vec3 lightDiffuse = " + lightDiffuseSource.getFunctionName(renderable) + "(position);\n");
        if (specularCalculation)
            sb.append("  vec3 lightSpecular = " + lightSpecularSource.getFunctionName(renderable) + "(position);\n");
        else
            sb.append("  vec3 lightSpecular = vec3(0.0);\n");
        sb.append("  Lighting lighting = Lighting(lightDiffuse, lightSpecular);\n");

        for (PerVertexLightingCalculateFunctionCall lightWrapper : lightWrappers) {
            if (lightWrapper.isProcessing(renderable, specularCalculation)) {
                lightWrapper.appendFunction(renderable, vertexShaderBuilder, specularCalculation);
                sb.append("  lighting = " + lightWrapper.getFunctionName(renderable, specularCalculation) + "(position, lighting);\n");
            }
        }

        sb.append("  v_lightDiffuse = lighting.diffuse;\n");
        if (specularCalculation)
            sb.append("  v_lightSpecular = lighting.specular;\n");
        sb.append("  return position;\n");
        sb.append("}\n");

        vertexShaderBuilder.addFunction("calculatePerVertexLighting", sb.toString());
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        long vertexMask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        return renderable.environment != null &&
                (hasNormal(vertexMask) || hasTangentAndBiNormal(vertexMask));
    }

    private boolean hasNormal(long vertexMask) {
        return (vertexMask & VertexAttributes.Usage.Normal) != 0;
    }

    private boolean hasTangentAndBiNormal(long vertexMask) {
        return (vertexMask & VertexAttributes.Usage.Tangent) != 0 && (vertexMask & VertexAttributes.Usage.BiNormal) != 0;
    }
}
