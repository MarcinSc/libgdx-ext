package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class SetColorVariableCall extends AbstractPluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature colorAttribute = PluggableShaderFeatureRegistry.registerFeature();

    public SetColorVariableCall() {
    }

    public SetColorVariableCall(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(colorAttribute);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "setColorVariable";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addAttributeVariable("a_color", "vec4");
        vertexShaderBuilder.addVaryingVariable("v_color", "vec4");
        vertexShaderBuilder.addFunction("setColorVariable",
                "void setColorVariable() {\n" +
                        "  v_color = a_color;\n" +
                        "}\n");
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        long mask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        return (mask & (VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.ColorPacked)) != 0;
    }
}
