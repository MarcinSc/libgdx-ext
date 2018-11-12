package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.GLSLFragmentReader;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class SpecularColorPPTransform implements PerPixelLightingCalculateFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature specularTexture = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "applySpecularColor";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        pluggableShaderFeatures.addFeature(specularTexture);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        fragmentShaderBuilder.addUniformVariable("u_specularColor", "vec4", false, UniformSetters.specularColor);

        fragmentShaderBuilder.addFunction("applySpecularColor", GLSLFragmentReader.getFragment("SpecularColorPPTransform"));
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return hasSpecular && renderable.material.has(ColorAttribute.Specular);
    }
}
