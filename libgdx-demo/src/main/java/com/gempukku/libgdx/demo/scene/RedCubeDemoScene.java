package com.gempukku.libgdx.demo.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.gempukku.libgdx.demo.DemoScene;
import com.gempukku.libgdx.plugin.cellshading.step.CelShadingStepAttribute;
import com.gempukku.libgdx.plugin.cellshading.step.CelShadingStepPPLightingApplyFunction;
import com.gempukku.libgdx.plugin.cellshading.step.CelShadingStepPVLightingApplyFunction;
import com.gempukku.libgdx.plugin.cellshading.texture.CelShadingTextureAttribute;
import com.gempukku.libgdx.plugin.cellshading.texture.CelShadingTexturePPLightingApplyFunction;
import com.gempukku.libgdx.plugin.cellshading.texture.CelShadingTexturePVLightingApplyFunction;
import com.gempukku.libgdx.shader.pluggable.DefaultPluggableShaderBuilder;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderProvider;
import com.gempukku.libgdx.shader.pluggable.PluggableShaderUtil;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.*;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.pixel.*;
import com.gempukku.libgdx.shader.pluggable.plugin.fragment.lighting.vertex.*;
import com.gempukku.libgdx.shader.pluggable.plugin.initializer.CullFaceInitializer;
import com.gempukku.libgdx.shader.pluggable.plugin.initializer.DepthTestInitializer;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.*;
import com.gempukku.libgdx.shader.pluggable.plugin.vertex.lighting.vertex.*;

public class RedCubeDemoScene implements DemoScene {
    public enum Mode {
        DEFAULT, PLUGGABLE_PER_VERTEX, PLUGGABLE_PER_PIXEL,
        CELSHADING_PV_STEP, CELSHADING_PP_STEP,
        CELSHADING_PV_TEXTURE, CELSHADING_PP_TEXTURE
    }

    private Camera camera;
    private ModelBatch modelBatch;
    private Model cubeModel;
    private ModelInstance cube;
    private Model baseModel;
    private ModelInstance base;
    private Environment environment;
    private Mode mode;
    private Texture celShadingTexture;

    public RedCubeDemoScene(Mode mode) {
        this.mode = mode;
    }

    private ShaderProvider createShaderProvider() {
        if (this.mode == Mode.PLUGGABLE_PER_VERTEX)
            return new PluggableShaderProvider(PluggableShaderUtil.createPerVertexLightingPluggableShaderBuilder());
        else if (this.mode == Mode.PLUGGABLE_PER_PIXEL)
            return new PluggableShaderProvider(PluggableShaderUtil.createPerPixelLightingPluggableShaderBuilder());
        else if (this.mode == Mode.CELSHADING_PV_STEP)
            return createCellShadingPVLShaderProvider(new CelShadingStepPVLightingApplyFunction(10));
        else if (this.mode == Mode.CELSHADING_PP_STEP)
            return createCellShadingPPLShaderProvider(new CelShadingStepPPLightingApplyFunction(10));
        else if (this.mode == Mode.CELSHADING_PV_TEXTURE)
            return createCellShadingPVLShaderProvider(new CelShadingTexturePVLightingApplyFunction());
        else if (this.mode == Mode.CELSHADING_PP_TEXTURE)
            return createCellShadingPPLShaderProvider(new CelShadingTexturePPLightingApplyFunction());

        return null;
    }

    private ShaderProvider createCellShadingPVLShaderProvider(PerVertexLightingApplyFunction lightingApplyFunction) {
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

        perVertexLightingApplyCall.addLightingApplyFunction(lightingApplyFunction);
        perVertexLightingApplyCall.addLightingApplyFunction(new DefaultPerVertexLightingApplyFunction());

        defaultPluggableShaderBuilder.addColorProcessor(perVertexLightingApplyCall);

        defaultPluggableShaderBuilder.addColorProcessor(new ApplyFog());
        defaultPluggableShaderBuilder.addColorProcessor(new BlendingAttributeTransform());

        return new PluggableShaderProvider(defaultPluggableShaderBuilder);
    }

    private ShaderProvider createCellShadingPPLShaderProvider(PerPixelLightingApplyFunction lightingApplyFunction) {
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
        applyPerPixelLighting.setNormalSource(new VaryingNormalSource());

        applyPerPixelLighting.addLightWrapper(new ApplyPPAmbientCubemapDiffuseLight(2, 5));
        applyPerPixelLighting.addLightWrapper(new ApplyPPDirectionalLights(2));
        applyPerPixelLighting.addLightWrapper(new ApplyPPPointLights(5));
        applyPerPixelLighting.addLightWrapper(new SpecularTexturePPTransform());
        applyPerPixelLighting.addLightWrapper(new SpecularColorPPTransform());

        applyPerPixelLighting.addLightingApplyFunction(lightingApplyFunction);
        applyPerPixelLighting.addLightingApplyFunction(new DefaultPerPixelLightingApplyFunction());

        defaultPluggableShaderBuilder.addColorProcessor(applyPerPixelLighting);

        defaultPluggableShaderBuilder.addColorProcessor(new ApplyFog());
        defaultPluggableShaderBuilder.addColorProcessor(new BlendingAttributeTransform());

        return new PluggableShaderProvider(defaultPluggableShaderBuilder);
    }

    @Override
    public void initialize(int width, int height) {
        modelBatch = new ModelBatch(createShaderProvider());

        celShadingTexture = new Texture(Gdx.files.internal("texture/celShadingTexture.png"));
        camera = new PerspectiveCamera(75, width, height);

        ModelBuilder modelBuilder = new ModelBuilder();
        cubeModel = modelBuilder.createBox(1, 1, 1,
                new Material(
                        ColorAttribute.createDiffuse(1, 0, 0, 1),
                        CelShadingStepAttribute.createAttribute(10),
                        CelShadingTextureAttribute.createAttribute(celShadingTexture)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        cube = new ModelInstance(cubeModel);
        cube.transform.rotate(1f, 1f, 0, 50);

        float yPos = -0.8f;
        baseModel = modelBuilder.createRect(
                -2, yPos, -2,
                -2, yPos, 2,
                2, yPos, 2,
                2, yPos, -2,
                0, 1, 0,
                new Material(
                        ColorAttribute.createDiffuse(1, 1, 1, 1)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        base = new ModelInstance(baseModel);

        camera.position.set(0, 0, 2);
        camera.up.set(0, 1, 0);
        camera.lookAt(0, 0, 0);
        camera.update();

        PointLight pointLight = new PointLight();
        pointLight.set(Color.WHITE, 1, 1, 2.5f, 8f);

        environment = new Environment();

        environment.add(pointLight);
    }

    @Override
    public void render(float delta) {
        cube.transform.rotate(0, 1, 0, 20 * delta);

        modelBatch.begin(camera);
        modelBatch.render(cube, environment);
        modelBatch.render(base, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        celShadingTexture.dispose();
        cubeModel.dispose();
        baseModel.dispose();
        modelBatch.dispose();
    }
}
