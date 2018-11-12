package com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.vertex;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class ApplyPVAmbientCubemapDiffuseLight implements PerVertexLightingCalculateFunctionCall {
    // This one depends on the configuration, so separate feature instance per object
    private PluggableShaderFeatureRegistry.PluggableShaderFeature ambientCubemapDiffuseLightTransform = PluggableShaderFeatureRegistry.registerFeature();

    private int numDirectionalLights;
    private int numPointLights;

    public ApplyPVAmbientCubemapDiffuseLight(int numDirectionalLights, int numPointLights) {
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
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder, boolean hasSpecular) {
        vertexShaderBuilder.addArrayUniformVariable("u_ambientCubemap", 6, "vec3", false, new UniformSetters.ACubemap(numDirectionalLights, numPointLights));
        vertexShaderBuilder.addFunction("transformDiffuseLightWithAmbientCubemap",
                "Lighting transformDiffuseLightWithAmbientCubemap(vec4 position, Lighting lighting) {\n" +
                        "  vec3 squaredNormal = normal * normal;\n" +
                        "  vec3 isPositive  = step(0.0, normal);\n" +
                        "  lighting.diffuse += squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +\n" +
                        "    squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +\n" +
                        "    squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);\n" +
                        "  return lighting;\n" +
                        "}\n");
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
