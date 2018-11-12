package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.GLSLFragmentReader;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class ApplyPPAmbientCubemapDiffuseLight implements PerPixelLightingCalculateFunctionCall {
    // This one depends on the configuration, so separate feature instance per object
    private PluggableShaderFeatureRegistry.PluggableShaderFeature ambientCubemapDiffuseLightTransform = PluggableShaderFeatureRegistry.registerFeature();

    private int numDirectionalLights;
    private int numPointLights;

    public ApplyPPAmbientCubemapDiffuseLight(int numDirectionalLights, int numPointLights) {
        this.numDirectionalLights = numDirectionalLights;
        this.numPointLights = numPointLights;
    }

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "transformDiffuseLightWithAmbientCubemap";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        pluggableShaderFeatures.addFeature(ambientCubemapDiffuseLightTransform);
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        fragmentShaderBuilder.addArrayUniformVariable("u_ambientCubemap", 6, "vec3", false, new UniformSetters.ACubemap(numDirectionalLights, numPointLights));
        fragmentShaderBuilder.addFunction("transformDiffuseLightWithAmbientCubemap", GLSLFragmentReader.getFragment("ApplyPPAmbientCubemapDiffuseLight"));
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        if (renderable.environment == null)
            return false;
        if (renderable.environment.has(ColorAttribute.AmbientLight)
                || renderable.material.has(ColorAttribute.AmbientLight))
            return true;
        int pointLightCount = getPointLightCount(renderable.environment) + getPointLightCount(renderable.material);
        if (pointLightCount > numPointLights)
            return true;
        int directionalLightCount = getDirectionalLightCount(renderable.environment) + getDirectionalLightCount(renderable.material);
        return directionalLightCount > numDirectionalLights;
    }

    private int getDirectionalLightCount(Attributes attributes) {
        DirectionalLightsAttribute directionalLightsAttribute = attributes.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
        if (directionalLightsAttribute == null)
            return 0;
        return directionalLightsAttribute.lights.size;
    }

    private int getPointLightCount(Attributes attributes) {
        PointLightsAttribute pointLightsAttribute = attributes.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        if (pointLightsAttribute == null)
            return 0;
        return pointLightsAttribute.lights.size;
    }
}
