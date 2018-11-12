package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class ProjectViewTransform extends AbstractPluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature projectViewTransformFeature = PluggableShaderFeatureRegistry.registerFeature();

    public ProjectViewTransform() {
    }

    public ProjectViewTransform(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(projectViewTransformFeature);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "transformToProjectView";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addUniformVariable("u_projViewTrans", "mat4", true, UniformSetters.projViewTrans);
        vertexShaderBuilder.addFunction("transformToProjectView",
                "vec4 transformToProjectView(vec4 position) {\n" +
                        "  return u_projViewTrans * position;\n" +
                        "}\n");
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        return true;
    }
}
