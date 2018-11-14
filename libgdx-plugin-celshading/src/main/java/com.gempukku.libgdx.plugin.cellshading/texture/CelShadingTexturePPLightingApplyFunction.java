package com.gempukku.libgdx.plugin.cellshading.texture;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel.PerPixelLightingApplyFunction;

public class CelShadingTexturePPLightingApplyFunction implements PerPixelLightingApplyFunction {
    private CelShadingTextureLightingApplyFunction celShadingTextureLightingApplyFunction = new CelShadingTextureLightingApplyFunction();

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return celShadingTextureLightingApplyFunction.getFunctionName(renderable, hasSpecular);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        celShadingTextureLightingApplyFunction.appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        celShadingTextureLightingApplyFunction.appendPerPixelFunction(renderable, fragmentShaderBuilder, hasSpecular);
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return celShadingTextureLightingApplyFunction.isProcessing(renderable, hasSpecular);
    }
}
