package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class SetNormalVariableCall extends AbstractPluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature colorAttribute = PluggableShaderFeatureRegistry.registerFeature();

    public SetNormalVariableCall() {
    }

    public SetNormalVariableCall(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(colorAttribute);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "setNormalVariable";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addVaryingVariable("v_normal", "vec3");
        vertexShaderBuilder.addFunction("setNormalVariable",
                "void setNormalVariable() {\n" +
                        "  v_normal = normal;\n" +
                        "}\n");
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        return (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.Normal) != 0;
    }
}
