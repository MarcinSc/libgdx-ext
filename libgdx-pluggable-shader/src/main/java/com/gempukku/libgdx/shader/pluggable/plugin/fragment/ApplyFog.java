package com.gempukku.libgdx.shader.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class ApplyFog extends AbstractPluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature applyFog = PluggableShaderFeatureRegistry.registerFeature();

    public ApplyFog() {
    }

    public ApplyFog(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "applyFog";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(applyFog);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addUniformVariable("u_fogColor", "vec4", false, UniformSetters.fog);
        fragmentShaderBuilder.addVaryingVariable("v_fog", "float");
        fragmentShaderBuilder.addFunction("applyFog",
                "vec4 applyFog(vec4 color) {\n" +
                        "  color.rgb = mix(color.rgb, u_fogColor.rgb, v_fog);\n" +
                        "  return color;\n" +
                        "}\n");
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        return renderable.environment != null && renderable.environment.has(ColorAttribute.Fog);
    }
}
