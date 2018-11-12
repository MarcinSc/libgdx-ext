package com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.shader.BasicShader;
import com.gempukku.libgdx.shader.UniformRegistry;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.GLSLFragmentReader;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.LightingUtils;

import java.util.Collections;

public class ApplyPPPointLights implements PerPixelLightingCalculateFunctionCall {
    private int numPointLights;
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature variableShininess = PluggableShaderFeatureRegistry.registerFeature();
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature constantShininess = PluggableShaderFeatureRegistry.registerFeature();
    // Separate due to config
    private PluggableShaderFeatureRegistry.PluggableShaderFeature applyPointLights = PluggableShaderFeatureRegistry.registerFeature();

    public ApplyPPPointLights(int numPointLights) {
        this.numPointLights = numPointLights;
    }

    @Override
    public String getFunctionName(Renderable renderable, boolean hasSpecular) {
        return "applyPointLights";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures, boolean hasSpecular) {
        pluggableShaderFeatures.addFeature(applyPointLights);
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
        fragmentShaderBuilder.addStructure("PointLight",
                "  vec3 color;\n" +
                        "  vec3 position;\n");
        fragmentShaderBuilder.addStructArrayUniformVariable("u_pointLights", new String[]{"color", "position"}, numPointLights, "PointLight", false,
                new UniformRegistry.StructArrayUniformSetter() {
                    @Override
                    public void set(BasicShader shader, int startingLocation, int[] fieldOffsets, int structSize, Renderable renderable, Attributes combinedAttributes) {
                        final PointLightsAttribute pla = combinedAttributes.get(PointLightsAttribute.class, PointLightsAttribute.Type);
                        final Array<PointLight> points = pla == null ? null : pla.lights;

                        for (int i = 0; i < numPointLights; i++) {
                            int location = startingLocation + i * structSize;
                            if (points != null && i < points.size) {
                                PointLight pointLight = points.get(i);

                                shader.setUniform(location, pointLight.color.r * pointLight.intensity,
                                        pointLight.color.g * pointLight.intensity, pointLight.color.b * pointLight.intensity);
                                shader.setUniform(location + fieldOffsets[1], pointLight.position.x, pointLight.position.y,
                                        pointLight.position.z);
                            } else {
                                shader.setUniform(location, 0f, 0f, 0f);
                                shader.setUniform(location + fieldOffsets[1], 0f, 0f, 0f);
                            }
                        }
                    }
                });
        if (hasSpecular) {
            fragmentShaderBuilder.addUniformVariable("u_cameraPosition", "vec4", true, UniformSetters.cameraPosition);
            boolean hasShininess = renderable.material.has(FloatAttribute.Shininess);
            if (hasShininess)
                fragmentShaderBuilder.addUniformVariable("u_shininess", "float", false, UniformSetters.shininess);
            else
                fragmentShaderBuilder.addUniformVariable("u_shininess", "float", false, LightingUtils.defaultShininessSetter);
        }

        fragmentShaderBuilder.addFunction("applyPointLights",
                GLSLFragmentReader.getFragment(hasSpecular ? "ApplyPPPointLightsSpecular" : "ApplyPPPointLightsNoSpecular",
                        Collections.singletonMap("numPointLights", String.valueOf(numPointLights))));
    }

    @Override
    public boolean isProcessing(Renderable renderable, boolean hasSpecular) {
        return numPointLights > 0 && (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.Normal) > 0;
    }
}
