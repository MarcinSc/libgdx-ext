package com.gempukku.libgdx.plugin.cellshading.step;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.GLSLFragmentReader;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

import java.util.Collections;

public class CelShadingStepLightingApplyFunction {
    private int shadeCountMaximum;
    // Separate due to shade count
    private PluggableShaderFeatureRegistry.PluggableShaderFeature[] celShadingCountFeatures;

    public CelShadingStepLightingApplyFunction(int shadeCountMaximum) {
        this.shadeCountMaximum = shadeCountMaximum;
        this.celShadingCountFeatures = new PluggableShaderFeatureRegistry.PluggableShaderFeature[shadeCountMaximum];
    }

    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "celShadingLightingApply";
    }

    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        int celShadeCount = getCelShadeCount(renderable);
        PluggableShaderFeatureRegistry.PluggableShaderFeature celShadingFeature = celShadingCountFeatures[celShadeCount - 1];
        if (celShadingFeature == null) {
            celShadingFeature = PluggableShaderFeatureRegistry.registerFeature();
            celShadingCountFeatures[celShadeCount - 1] = celShadingFeature;
        }
        pluggableShaderFeatures.addFeature(celShadingFeature);

    }

    public void appendPerVertexFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        int shadeCount = getCelShadeCount(renderable);
        String shadeCountFlt = shadeCount + ".0";
        String fragmentName = hasSpecular ? "CelShadingStepPVSpecularLightingApply" : "CelShadingStepPVLightingApply";
        fragmentShaderBuilder.addFunction("celShadingLightingApply",
                GLSLFragmentReader.getFragment(fragmentName,
                        Collections.singletonMap("shadeCountFlt", shadeCountFlt)));

    }

    public void appendPerPixelFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        int shadeCount = getCelShadeCount(renderable);
        String shadeCountFlt = shadeCount + ".0";
        String fragmentName = hasSpecular ? "CelShadingStepPPSpecularLightingApply" : "CelShadingStepPPLightingApply";
        fragmentShaderBuilder.addFunction("celShadingLightingApply",
                GLSLFragmentReader.getFragment(fragmentName,
                        Collections.singletonMap("shadeCountFlt", shadeCountFlt)));

    }

    private int getCelShadeCount(Renderable renderable) {
        return Math.min(shadeCountMaximum, renderable.material.get(CelShadingStepAttribute.class, CelShadingStepAttribute.CelShading).value);
    }

    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return renderable.material.has(CelShadingStepAttribute.CelShading);
    }
}
