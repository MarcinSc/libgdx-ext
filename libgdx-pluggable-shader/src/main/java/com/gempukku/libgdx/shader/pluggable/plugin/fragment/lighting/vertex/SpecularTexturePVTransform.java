package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.vertex;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.GLSLFragmentReader;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class SpecularTexturePVTransform implements PerVertexLightingWrapperFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature specularTexture = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "applySpecularTexture";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        pluggableShaderFeatures.addFeature(specularTexture);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        fragmentShaderBuilder.addUniformVariable("u_specularTexture", "sampler2D", false, UniformSetters.specularTexture);
        fragmentShaderBuilder.addVaryingVariable("v_specularUV", "vec2");

        fragmentShaderBuilder.addFunction("applySpecularTexture", GLSLFragmentReader.getFragment("SpecularTexturePVTransform"));
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return hasSpecular && renderable.material.has(TextureAttribute.Specular);
    }
}
