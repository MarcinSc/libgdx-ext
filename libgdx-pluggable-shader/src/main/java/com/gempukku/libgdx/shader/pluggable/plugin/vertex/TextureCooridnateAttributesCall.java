package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

// TODO: This should be split into two classes
public class TextureCooridnateAttributesCall extends AbstractPluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature diffuseTextureAttribute = PluggableShaderFeatureRegistry.registerFeature();
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature specularTextureAttribute = PluggableShaderFeatureRegistry.registerFeature();

    public TextureCooridnateAttributesCall() {
    }

    public TextureCooridnateAttributesCall(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        if (isProcessingDiffuse(renderable))
            pluggableShaderFeatures.addFeature(diffuseTextureAttribute);
        if (isProcessingSpecular(renderable))
            pluggableShaderFeatures.addFeature(specularTextureAttribute);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "setTextureCoordinates";
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        vertexShaderBuilder.addAttributeVariable("a_texCoord0", "vec2");
        boolean processingDiffuse = isProcessingDiffuse(renderable);
        boolean processingSpecular = isProcessingSpecular(renderable);
        if (processingDiffuse) {
            vertexShaderBuilder.addUniformVariable("u_diffuseUVTransform", "vec4", false, UniformSetters.diffuseUVTransform);
            vertexShaderBuilder.addVaryingVariable("v_diffuseUV", "vec2");
        }
        if (processingSpecular) {
            vertexShaderBuilder.addUniformVariable("u_specularUVTransform", "vec4", false, UniformSetters.specularUVTransform);
            vertexShaderBuilder.addVaryingVariable("v_specularUV", "vec2");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("void setTextureCoordinates() {\n");
        if (processingDiffuse)
            sb.append("  v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;\n");
        if (processingSpecular)
            sb.append("  v_specularUV = u_specularUVTransform.xy + a_texCoord0 * u_specularUVTransform.zw;\n");
        sb.append("}\n");

        vertexShaderBuilder.addFunction("setTextureCoordinates", sb.toString());
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        return (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.TextureCoordinates) > 0
                && (isProcessingDiffuse(renderable) || isProcessingSpecular(renderable));
    }

    private boolean isProcessingDiffuse(Renderable renderable) {
        return renderable.material.has(TextureAttribute.Diffuse);
    }

    private boolean isProcessingSpecular(Renderable renderable) {
        return renderable.material.has(TextureAttribute.Specular);
    }
}
