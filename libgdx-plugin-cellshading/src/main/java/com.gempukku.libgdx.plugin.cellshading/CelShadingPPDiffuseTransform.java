package com.gempukku.libgdx.plugin.cellshading;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel.PerPixelLightingCalculateFunctionCall;

public class CelShadingPPDiffuseTransform implements PerPixelLightingCalculateFunctionCall {
    private CelShadingDiffuseTransform celShadingDiffuseTransform;

    public CelShadingPPDiffuseTransform(int shadeCountMaximum) {
        celShadingDiffuseTransform = new CelShadingDiffuseTransform(shadeCountMaximum);
    }

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return celShadingDiffuseTransform.getFunctionName(renderable, hasSpecular);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        celShadingDiffuseTransform.appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        celShadingDiffuseTransform.appendPerPixelFunction(renderable, fragmentShaderBuilder, hasSpecular);
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return celShadingDiffuseTransform.isProcessing(renderable, hasSpecular);
    }
}
