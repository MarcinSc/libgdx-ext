package com.gempukku.libgdx.shader.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class WhiteColorSource extends AbstractPluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature whiteColor = PluggableShaderFeatureRegistry.registerFeature();

    public WhiteColorSource() {
    }

    public WhiteColorSource(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(whiteColor);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getWhite";
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addFunction("getWhite",
                "vec4 getWhite() {\n" +
                        "  return vec4(1.0);\n" +
                        "}\n");
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        return true;
    }
}
