package com.gempukku.libgdx.plugin.cellshading;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.CommonShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.GLSLFragmentReader;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

import java.util.Collections;

public class CelShadingDiffuseTransform {
    private int shadeCountMaximum;
    // Separate due to shade count
    private PluggableShaderFeatureRegistry.PluggableShaderFeature[] celShadingFeatures;

    public CelShadingDiffuseTransform(int shadeCountMaximum) {
        this.shadeCountMaximum = shadeCountMaximum;
        this.celShadingFeatures = new PluggableShaderFeatureRegistry.PluggableShaderFeature[shadeCountMaximum];
    }

    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "applyCelShading";
    }

    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        int celShadeCount = getCelShadeCount(renderable);
        PluggableShaderFeatureRegistry.PluggableShaderFeature celShadingFeature = celShadingFeatures[celShadeCount - 1];
        if (celShadingFeature == null) {
            celShadingFeature = PluggableShaderFeatureRegistry.registerFeature();
            celShadingFeatures[celShadeCount - 1] = celShadingFeature;
        }
        pluggableShaderFeatures.addFeature(celShadingFeature);
    }

    public void appendPerPixelFunction(Renderable renderable, CommonShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        int shadeCount = getCelShadeCount(renderable);
        String shadeCountFlt = shadeCount + ".0";
        fragmentShaderBuilder.addFunction("applyCelShading",
                GLSLFragmentReader.getFragment("CelShadingDiffusePPTransform",
                        Collections.singletonMap("shadeCountFlt", shadeCountFlt)));
    }

    public void appendPerVertexFunction(Renderable renderable, CommonShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        int shadeCount = getCelShadeCount(renderable);
        String shadeCountFlt = shadeCount + ".0";
        fragmentShaderBuilder.addFunction("applyCelShading",
                GLSLFragmentReader.getFragment("CelShadingDiffusePVTransform",
                        Collections.singletonMap("shadeCountFlt", shadeCountFlt)));
    }

    private int getCelShadeCount(Renderable renderable) {
        return Math.min(shadeCountMaximum, renderable.material.get(CelShadingAttribute.class, CelShadingAttribute.CelShading).value);
    }

    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return renderable.material.has(CelShadingAttribute.CelShading);
    }
}
