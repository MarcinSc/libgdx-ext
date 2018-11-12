package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class DarkDiffusePPSource extends AbstractPluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature darkDiffuse = PluggableShaderFeatureRegistry.registerFeature();

    public DarkDiffusePPSource() {
    }

    public DarkDiffusePPSource(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(darkDiffuse);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getDarkDiffuse";
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addFunction("getDarkDiffuse",
                "vec3 getDarkDiffuse(vec4 position) {\n" +
                        "  return vec3(0.0);\n" +
                        "}\n");
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        return true;
    }
}
