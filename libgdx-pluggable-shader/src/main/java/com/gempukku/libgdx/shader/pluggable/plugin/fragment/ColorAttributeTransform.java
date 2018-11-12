package com.gempukku.libgdx.shader.pluggable.plugin.fragment;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableFragmentFunctionCall;
import com.gempukku.libgdx.shader.pluggable.FragmentShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;

public class ColorAttributeTransform extends AbstractPluggableFragmentFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature colorTransform = PluggableShaderFeatureRegistry.registerFeature();

    public ColorAttributeTransform() {
    }

    public ColorAttributeTransform(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        pluggableShaderFeatures.addFeature(colorTransform);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "getTransformedColor";
    }

    @Override
    public void appendFunction(Renderable renderable, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addVaryingVariable("v_color", "vec4");
        fragmentShaderBuilder.addFunction("getTransformedColor",
                "vec4 getTransformedColor(vec4 color) {\n" +
                        "  color *= v_color;\n" +
                        "  return color;\n" +
                        "}\n");
    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        long mask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        return (mask & (VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.ColorPacked)) != 0;
    }
}
