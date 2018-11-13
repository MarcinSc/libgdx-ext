package com.gempukku.libgdx.plugin.cellshading.count;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.CommonShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.GLSLFragmentReader;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

import java.util.Collections;

public class CelShadingCountDiffuseTransform {
    private int shadeCountMaximum;
    // Separate due to shade count
    private PluggableShaderFeatureRegistry.PluggableShaderFeature[] celShadingCountFeatures;

    public CelShadingCountDiffuseTransform(int shadeCountMaximum) {
        this.shadeCountMaximum = shadeCountMaximum;
        this.celShadingCountFeatures = new PluggableShaderFeatureRegistry.PluggableShaderFeature[shadeCountMaximum];
    }

    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "applyCelShadingCount";
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

    public void appendPerPixelFunction(Renderable renderable, CommonShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        int shadeCount = getCelShadeCount(renderable);
        String shadeCountFlt = shadeCount + ".0";
        fragmentShaderBuilder.addFunction("applyCelShadingCount",
                GLSLFragmentReader.getFragment("CelShadingCountDiffusePPTransform",
                        Collections.singletonMap("shadeCountFlt", shadeCountFlt)));
    }

    public void appendPerVertexFunction(Renderable renderable, CommonShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        int shadeCount = getCelShadeCount(renderable);
        String shadeCountFlt = shadeCount + ".0";
        fragmentShaderBuilder.addFunction("applyCelShadingCount",
                GLSLFragmentReader.getFragment("CelShadingCountDiffusePVTransform",
                        Collections.singletonMap("shadeCountFlt", shadeCountFlt)));
    }

    private int getCelShadeCount(Renderable renderable) {
        return Math.min(shadeCountMaximum, renderable.material.get(CelShadingCountAttribute.class, CelShadingCountAttribute.CelShading).value);
    }

    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return renderable.material.has(CelShadingCountAttribute.CelShading) && !hasSpecular;
    }
}
