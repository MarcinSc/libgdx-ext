package com.gempukku.libgdx.shader.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class DiffuseTextureTransform extends AbstractPluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature diffuseTextureTransform = PluggableShaderFeatureRegistry.registerFeature();

    public DiffuseTextureTransform() {
    }

    public DiffuseTextureTransform(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(diffuseTextureTransform);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getTransformedDiffuseTexture";
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addUniformVariable("u_diffuseTexture", "sampler2D", false, UniformSetters.diffuseTexture);
        fragmentShaderBuilder.addVaryingVariable("v_diffuseUV", "vec2");
        fragmentShaderBuilder.addFunction("getTransformedDiffuseTexture",
                "vec4 getTransformedDiffuseTexture(vec4 color) {\n" +
                        "  color *= texture2D(u_diffuseTexture, v_diffuseUV);\n" +
                        "  return color;\n" +
                        "}\n");
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        return renderable.material.has(TextureAttribute.Diffuse) &&
                (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.TextureCoordinates) > 0;
    }
}
