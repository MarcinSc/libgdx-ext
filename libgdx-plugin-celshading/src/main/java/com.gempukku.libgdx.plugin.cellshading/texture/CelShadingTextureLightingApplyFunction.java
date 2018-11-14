package com.gempukku.libgdx.plugin.cellshading.texture;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.BasicShader;
import com.gempukku.libgdx.shader.UniformRegistry;
import com.gempukku.libgdx.shader.pluggable.CommonShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.GLSLFragmentReader;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class CelShadingTextureLightingApplyFunction {
    private final static UniformRegistry.UniformSetter celShadingTexture = new UniformRegistry.UniformSetter() {
        @Override
        public void set(BasicShader shader, int location, Renderable renderable, Attributes combinedAttributes) {
            final int unit = shader.getContext().textureBinder.bind(((CelShadingTextureAttribute) (combinedAttributes
                    .get(CelShadingTextureAttribute.CelShading))).textureDescription);
            shader.setUniform(location, unit);
        }
    };

    private static PluggableShaderFeatureRegistry.PluggableShaderFeature celShadingFeature = PluggableShaderFeatureRegistry.registerFeature();

    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "celShadingTextureLightingApply";
    }

    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        pluggableShaderFeatures.addFeature(celShadingFeature);
    }

    public void appendPerPixelFunction(Renderable renderable, CommonShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        fragmentShaderBuilder.addUniformVariable("u_celShadingTexture", "sampler2D", false, celShadingTexture);
        String fragmentName = hasSpecular ? "CelShadingTexturePPSpecularLightingApply" : "CelShadingTexturePPLightingApply";
        fragmentShaderBuilder.addFunction("celShadingTextureLightingApply",
                GLSLFragmentReader.getFragment(fragmentName));
    }

    public void appendPerVertexFunction(Renderable renderable, CommonShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        fragmentShaderBuilder.addUniformVariable("u_celShadingTexture", "sampler2D", false, celShadingTexture);
        String fragmentName = hasSpecular ? "CelShadingTexturePVSpecularLightingApply" : "CelShadingTexturePVLightingApply";
        fragmentShaderBuilder.addFunction("celShadingTextureLightingApply",
                GLSLFragmentReader.getFragment(fragmentName));
    }

    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return renderable.material.has(CelShadingTextureAttribute.CelShading);
    }
}
