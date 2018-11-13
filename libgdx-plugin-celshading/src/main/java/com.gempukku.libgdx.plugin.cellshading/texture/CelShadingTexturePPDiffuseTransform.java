package com.gempukku.libgdx.plugin.cellshading.texture;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel.PerPixelLightingCalculateFunctionCall;

public class CelShadingTexturePPDiffuseTransform implements PerPixelLightingCalculateFunctionCall {
    private CelShadingTextureDiffuseTransform celShadingCountDiffuseTransform = new CelShadingTextureDiffuseTransform();

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return celShadingCountDiffuseTransform.getFunctionName(renderable, hasSpecular);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        celShadingCountDiffuseTransform.appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        celShadingCountDiffuseTransform.appendPerPixelFunction(renderable, fragmentShaderBuilder, hasSpecular);
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return celShadingCountDiffuseTransform.isProcessing(renderable, hasSpecular);
    }
}
