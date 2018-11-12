package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class PerPixelLightingCall extends AbstractPluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature perPixelLighting = PluggableShaderFeatureRegistry.registerFeature();

    public PerPixelLightingCall() {
    }

    public PerPixelLightingCall(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "storeVertexWorldPosition";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(perPixelLighting);
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addVaryingVariable("v_vertexWorldPosition", "vec4");
        StringBuilder sb = new StringBuilder();
        sb.append("vec4 storeVertexWorldPosition(vec4 position) {\n" +
                "  v_vertexWorldPosition = position;\n" +
                "  return position;\n" +
                "}\n");

        vertexShaderBuilder.addFunction("storeVertexWorldPosition", sb.toString());
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
