package com.gempukku.libgdx.shader.pluggable.plugin.vertex;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;
import com.gempukku.libgdx.shader.UniformSetters;
import com.gempukku.libgdx.shader.pluggable.AbstractPluggableVertexFunctionCall;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatureRegistry;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderFeatures;
import com.gempukku.libgdx.shader.pluggable.VertexShaderBuilder;

public class NormalCalculateCall extends AbstractPluggableVertexFunctionCall {
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature normalCalculationWithSkinning = PluggableShaderFeatureRegistry.registerFeature();
    private static PluggableShaderFeatureRegistry.PluggableShaderFeature normalCalculationNoSkinning = PluggableShaderFeatureRegistry.registerFeature();

    public NormalCalculateCall() {
    }

    public NormalCalculateCall(Predicate<Renderable> additionalPredicate) {
        super(additionalPredicate);
    }

    @Override
    public String getFunctionName(Renderable renderable) {
        return "calculateNormal";
    }

    @Override
    public void appendShaderFeatures(Renderable renderable, PluggableShaderFeatures pluggableShaderFeatures) {
        if (hasSkinning(renderable))
            pluggableShaderFeatures.addFeature(normalCalculationWithSkinning);
        else
            pluggableShaderFeatures.addFeature(normalCalculationNoSkinning);
    }

    @Override
    public void appendFunction(Renderable renderable, VertexShaderBuilder vertexShaderBuilder) {
        boolean hasSkinning = hasSkinning(renderable);

        vertexShaderBuilder.addAttributeVariable("a_normal", "vec3");
        vertexShaderBuilder.addVariable("normal", "vec3");
        if (!hasSkinning)
            vertexShaderBuilder.addUniformVariable("u_normalMatrix", "mat3", false, UniformSetters.normalMatrix);

        if (hasSkinning)
            vertexShaderBuilder.addFunction("calculateNormal",
                    "void calculateNormal() {\n" +
                            "  normal = normalize((u_worldTrans * skinning * vec4(a_normal, 0.0)).xyz);\n" +
                            "}\n");
        else
            vertexShaderBuilder.addFunction("calculateNormal",
                    "void calculateNormal() {\n" +
                            "  normal = normalize(u_normalMatrix * a_normal);\n" +
                            "}\n");

    }

    @Override
    protected boolean isProcessingForRenderable(Renderable renderable) {
        return (renderable.meshPart.mesh.getVertexAttributes().getMask() & VertexAttributes.Usage.Normal) != 0;
    }

    private boolean hasSkinning(Renderable renderable) {
        return renderable.bones != null && renderable.bones.length > 0;
    }
}
