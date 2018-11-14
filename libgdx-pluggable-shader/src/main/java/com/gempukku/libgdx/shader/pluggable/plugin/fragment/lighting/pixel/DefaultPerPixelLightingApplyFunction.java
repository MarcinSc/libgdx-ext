package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class DefaultPerPixelLightingApplyFunction implements PerPixelLightingApplyFunction {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature defaultPerVertexLightinApplyFunction = PluggableShaderFeatureRegistry.registerFeature();

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "defaultApplyLighting";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        pluggableShaderFeatures.addFeature(defaultPerVertexLightinApplyFunction);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        if (hasSpecular) {
            fragmentShaderBuilder.addFunction("defaultApplyLighting",
                    "vec3 defaultApplyLighting(vec4 position, vec3 color, vec3 diffuse, vec3 specular) {\n" +
                            "  return color * diffuse + specular;\n" +
                            "}\n");
        } else {
            fragmentShaderBuilder.addFunction("defaultApplyLighting",
                    "vec3 defaultApplyLighting(vec4 position, vec3 color, vec3 diffuse) {\n" +
                            "  return color * diffuse;\n" +
                            "}\n");
        }
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return true;
    }
}
