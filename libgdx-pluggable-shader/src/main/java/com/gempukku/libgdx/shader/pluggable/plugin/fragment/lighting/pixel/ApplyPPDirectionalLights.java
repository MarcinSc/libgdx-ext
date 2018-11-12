package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.shader.BasicShader;
import com.gempukku.libgdx.shader.UniformRegistry;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.LightingUtils;

public class ApplyPPDirectionalLights implements PerPixelLightingCalculateFunctionCall {
    private int numDirectionalLights;
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature variableShininess = PluggableShaderFeatureRegistry.registerFeature();
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature constantShininess = PluggableShaderFeatureRegistry.registerFeature();
    // Separate due to config
    private PluggableShaderFeatureRegistry.PluggableShaderFeature applyDirectionalLights = PluggableShaderFeatureRegistry.registerFeature();

    public ApplyPPDirectionalLights(int numDirectionalLights) {
        this.numDirectionalLights = numDirectionalLights;
    }

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "applyDirectionalLights";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        pluggableShaderFeatures.addFeature(applyDirectionalLights);
        boolean hasShininess = renderable.material.has(FloatAttribute.Shininess);
        if (hasSpecular) {
            if (hasShininess)
                pluggableShaderFeatures.addFeature(variableShininess);
            else
                pluggableShaderFeatures.addFeature(constantShininess);
        }
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder, boolean hasSpecular) {
        fragmentShaderBuilder.addStructure("DirectionalLight",
                "  vec3 color;\n" +
                        "  vec3 direction;\n");
        fragmentShaderBuilder.addStructArrayUniformVariable("u_dirLights", new String[]{"color", "direction"}, numDirectionalLights, "DirectionalLight", false,
                new UniformRegistry.StructArrayUniformSetter() {
                    @Override
                    public void set(BasicShader shader, int startingLocation, int[] fieldOffsets, int structSize, Renderable renderable, Attributes combinedAttributes) {
                        final DirectionalLightsAttribute dla = combinedAttributes.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
                        final Array<DirectionalLight> dirs = dla == null ? null : dla.lights;

                        for (int i = 0; i < numDirectionalLights; i++) {
                            int location = startingLocation + i * structSize;
                            if (dirs != null && i < dirs.size) {
                                DirectionalLight directionalLight = dirs.get(i);

                                shader.setUniform(location, directionalLight.color.r, directionalLight.color.g,
                                        directionalLight.color.b);
                                shader.setUniform(location + fieldOffsets[1], directionalLight.direction.x,
                                        directionalLight.direction.y, directionalLight.direction.z);
                            } else {
                                shader.setUniform(location, 0f, 0f, 0f);
                                shader.setUniform(location + fieldOffsets[1], 0f, 0f, 0f);
                            }
                        }
                    }
                });

        if (hasSpecular) {
            fragmentShaderBuilder.addUniformVariable("u_cameraPosition", "vec4", true, UniformSetters.cameraPosition);
            boolean hasShininess = hasShininess(renderable);
            if (hasShininess)
                fragmentShaderBuilder.addUniformVariable("u_shininess", "float", false, UniformSetters.shininess);
            else
                fragmentShaderBuilder.addUniformVariable("u_shininess", "float", false, LightingUtils.defaultShininessSetter);
        }

        StringBuilder function = new StringBuilder();
        function.append("Lighting applyDirectionalLights(vec4 pos, Lighting lighting) {\n");
        if (hasSpecular)
            function.append("  vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);\n");
        function.append("  for (int i = 0; i < " + numDirectionalLights + "; i++) {\n" +
                "    vec3 lightDir = -u_dirLights[i].direction;\n" +
                "    float NdotL = clamp(dot(v_normal, lightDir), 0.0, 1.0);\n" +
                "    vec3 value = u_dirLights[i].color * NdotL;\n" +
                "    lighting.diffuse += value;\n");
        if (hasSpecular) {
            function.append("    float halfDotView = max(0.0, dot(v_normal, normalize(lightDir + viewVec)));\n" +
                    "    lighting.specular += value * pow(halfDotView, u_shininess);\n");
        }
        function.append("  }\n");
        function.append("  return lighting;\n");
        function.append("}\n");

        fragmentShaderBuilder.addFunction("applyDirectionalLights", function.toString());
    }

    private boolean hasShininess(Renderable renderable) {
        return renderable.material.has(FloatAttribute.Shininess);
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return numDirectionalLights > 0 && (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.Normal) > 0;
    }
}
