package com.gempukku.libgdx.shader.pluggable;


import com.gempukku.libgdx.shader.pluggable.plugin.fragment.*;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel.*;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.vertex.DiffusePVAttributeSource;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.vertex.SpecularColorPVTransform;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.vertex.SpecularPVAttributeSource;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.vertex.SpecularTexturePVTransform;
import com.gempukku.libgdx.shader.pluggable.plugin.initializer.CullFaceInitializer;
import com.gempukku.libgdx.shader.pluggable.plugin.initializer.DepthTestInitializer;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.*;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.vertex.*;

public class PluggableShaderUtil {
    private PluggableShaderUtil() {
    }

    public static PluggableShaderBuilder createDefaultPluggableShaderBuilder() {
        return createPerVertexLightingPluggableShaderBuilder();
    }

    public static PluggableShaderBuilder createPerVertexLightingPluggableShaderBuilder() {
        DefaultPluggableShaderBuilder defaultPluggableShaderBuilder = new DefaultPluggableShaderBuilder();

        // Initializers
        defaultPluggableShaderBuilder.addRenderInitializer(new CullFaceInitializer());
        defaultPluggableShaderBuilder.addRenderInitializer(new DepthTestInitializer());

        // Vertex shader
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new SetColorVariableCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new BlendingAttributeCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new SkinningCalculateCall(12));
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new NormalCalculateCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new SetNormalVariableCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new TextureCooridnateAttributesCall());

        defaultPluggableShaderBuilder.setPositionSource(new AttributePositionSource());
        defaultPluggableShaderBuilder.addPositionProcessor(new ApplySkinningTransform());
        defaultPluggableShaderBuilder.addPositionProcessor(new WorldTransform());
        defaultPluggableShaderBuilder.addPositionProcessor(new CalculateFog());

        // Lighting vertex calculations
        PerVertexLightingCalculateCall perVertexLightingCalculateCall = new PerVertexLightingCalculateCall();
        perVertexLightingCalculateCall.setLightDiffuseSource(new DarkDiffusePVSource());
        perVertexLightingCalculateCall.setLightSpecularSource(new DarkSpecularPVSource());

        perVertexLightingCalculateCall.addLightWrapper(new ApplyPVAmbientCubemapDiffuseLight(2, 5));
        perVertexLightingCalculateCall.addLightWrapper(new ApplyPVDirectionalLights(2));
        perVertexLightingCalculateCall.addLightWrapper(new ApplyPVPointLights(5));

        defaultPluggableShaderBuilder.addPositionProcessor(perVertexLightingCalculateCall);

        defaultPluggableShaderBuilder.addPositionProcessor(new ProjectViewTransform());

        // Fragment shader
        defaultPluggableShaderBuilder.setColorSource(new WhiteColorSource());
        defaultPluggableShaderBuilder.addColorProcessor(new ColorAttributeTransform());
        defaultPluggableShaderBuilder.addColorProcessor(new DiffuseColorTransform());
        defaultPluggableShaderBuilder.addColorProcessor(new DiffuseTextureTransform());

        //Lighting fragment calculations

        ApplyPerVertexLighting perVertexLightingApplyCall = new ApplyPerVertexLighting();
        perVertexLightingApplyCall.setLightDiffuseSource(new DiffusePVAttributeSource());
        perVertexLightingApplyCall.setLightSpecularSource(new SpecularPVAttributeSource());
        perVertexLightingApplyCall.addLightWrapper(new SpecularTexturePVTransform());
        perVertexLightingApplyCall.addLightWrapper(new SpecularColorPVTransform());

        defaultPluggableShaderBuilder.addColorProcessor(perVertexLightingApplyCall);

        defaultPluggableShaderBuilder.addColorProcessor(new ApplyFog());
        defaultPluggableShaderBuilder.addColorProcessor(new BlendingAttributeTransform());

        return defaultPluggableShaderBuilder;
    }

    public static PluggableShaderBuilder createPerPixelLightingPluggableShaderBuilder() {
        DefaultPluggableShaderBuilder defaultPluggableShaderBuilder = new DefaultPluggableShaderBuilder();

        // Initializers
        defaultPluggableShaderBuilder.addRenderInitializer(new CullFaceInitializer());
        defaultPluggableShaderBuilder.addRenderInitializer(new DepthTestInitializer());

        // Vertex shader
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new SetColorVariableCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new BlendingAttributeCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new SkinningCalculateCall(12));
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new NormalCalculateCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new SetNormalVariableCall());
        defaultPluggableShaderBuilder.addAdditionalVertexCall(new TextureCooridnateAttributesCall());

        defaultPluggableShaderBuilder.setPositionSource(new AttributePositionSource());
        defaultPluggableShaderBuilder.addPositionProcessor(new ApplySkinningTransform());
        defaultPluggableShaderBuilder.addPositionProcessor(new WorldTransform());
        defaultPluggableShaderBuilder.addPositionProcessor(new PerPixelLightingCall());
        defaultPluggableShaderBuilder.addPositionProcessor(new CalculateFog());

        defaultPluggableShaderBuilder.addPositionProcessor(new ProjectViewTransform());

        // Fragment shader
        defaultPluggableShaderBuilder.setColorSource(new WhiteColorSource());
        defaultPluggableShaderBuilder.addColorProcessor(new ColorAttributeTransform());
        defaultPluggableShaderBuilder.addColorProcessor(new DiffuseColorTransform());
        defaultPluggableShaderBuilder.addColorProcessor(new DiffuseTextureTransform());

        // Lighting fragment calculations
        ApplyPerPixelLighting applyPerPixelLighting = new ApplyPerPixelLighting();
        applyPerPixelLighting.setLightDiffuseSource(new DarkDiffusePPSource());
        applyPerPixelLighting.setLightSpecularSource(new DarkSpecularPPSource());

        applyPerPixelLighting.addLightWrapper(new ApplyPPAmbientCubemapDiffuseLight(2, 5));
        applyPerPixelLighting.addLightWrapper(new ApplyPPDirectionalLights(2));
        applyPerPixelLighting.addLightWrapper(new ApplyPPPointLights(5));
        applyPerPixelLighting.addLightWrapper(new SpecularTexturePPTransform());
        applyPerPixelLighting.addLightWrapper(new SpecularColorPPTransform());

        defaultPluggableShaderBuilder.addColorProcessor(applyPerPixelLighting);

        defaultPluggableShaderBuilder.addColorProcessor(new ApplyFog());
        defaultPluggableShaderBuilder.addColorProcessor(new BlendingAttributeTransform());

        return defaultPluggableShaderBuilder;
    }
}
