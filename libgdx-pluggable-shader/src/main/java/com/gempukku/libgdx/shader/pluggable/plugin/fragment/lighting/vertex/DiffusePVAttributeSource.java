package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class DiffusePVAttributeSource extends AbstractPluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature diffuseAttributeSource = PluggableShaderFeatureRegistry.registerFeature();

    public DiffusePVAttributeSource() {
    }

    public DiffusePVAttributeSource(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getDiffuseVaryingValue";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(diffuseAttributeSource);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addVaryingVariable("v_lightDiffuse", "vec3");
        fragmentShaderBuilder.addFunction("getDiffuseVaryingValue",
                "vec3 getDiffuseVaryingValue() {\n" +
                        "  return v_lightDiffuse;\n" +
                        "}\n");
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        return true;
    }
}
