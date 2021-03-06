package com.gempukku.libgdx.plugin.cellshading.step;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.vertex.PerVertexLightingApplyFunction;

public class CelShadingStepPVLightingApplyFunction implements PerVertexLightingApplyFunction {
    private CelShadingStepLightingApplyFunction celShadingStepLightingApplyFunction;

    public CelShadingStepPVLightingApplyFunction(int shadeCountMaximum) {
        celShadingStepLightingApplyFunction = new CelShadingStepLightingApplyFunction(shadeCountMaximum);
    }

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return celShadingStepLightingApplyFunction.getFunctionName(renderable, hasSpecular);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        celShadingStepLightingApplyFunction.appendShaderFeatures(renderable, pluggableShaderFeatures, hasSpecular);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        celShadingStepLightingApplyFunction.appendPerVertexFunction(renderable, fragmentShaderBuilder, hasSpecular);
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return celShadingStepLightingApplyFunction.isProcessing(renderable, hasSpecular);
    }
}
