package com.gempukku.libgdx.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.gempukku.libgdx.demo.scene.RedCubeDemoScene;

import java.util.ArrayList;
import java.util.List;

public class PluggableShaderDemo extends ApplicationAdapter {
    private List<String> demoSceneNames = new ArrayList<String>();
    private List<DemoScene> demoScenes = new ArrayList<DemoScene>();

    private int currentSceneIndex;
    private String currentSceneName;
    private DemoScene currentScene;

    private BitmapFont listFont;
    private BitmapFont titleFont;
    private SpriteBatch spriteBatch;

    private int width;
    private int height;

    private int frameCount;
    private long lastUpdatedTime;

    private int nanoInMs = 1000000;
    private long bestTime = 1000 * 1000000;
    private int fps;

    private long lastProcessedInput;

    public PluggableShaderDemo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private void addDemoScene(String name, DemoScene demoScene) {
        demoSceneNames.add(name);
        demoScenes.add(demoScene);
    }

    @Override
    public void create() {
        listFont = new BitmapFont();
        titleFont = new BitmapFont();
        spriteBatch = new SpriteBatch();

        addDemoScene("Cube - Default shader", new RedCubeDemoScene(RedCubeDemoScene.Mode.DEFAULT));
        addDemoScene("Cube - Pluggable PVL (Per vertex lighting)", new RedCubeDemoScene(RedCubeDemoScene.Mode.PLUGGABLE_PER_VERTEX));
        addDemoScene("Cube - Pluggable PPL (Per pixel lighting)", new RedCubeDemoScene(RedCubeDemoScene.Mode.PLUGGABLE_PER_PIXEL));
        addDemoScene("Cube - Pluggable PVL with cel-shading", new RedCubeDemoScene(RedCubeDemoScene.Mode.CELSHADING_PER_VERTEX));
        addDemoScene("Cube - Pluggable PPL with cel-shading", new RedCubeDemoScene(RedCubeDemoScene.Mode.CELSHADING_PER_PIXEL));

        currentSceneName = demoSceneNames.get(0);
        currentScene = demoScenes.get(0);
        currentScene.initialize(width, height);
    }

    @Override
    public void render() {
        processInput();

        clear();

        long start = System.nanoTime();
        currentScene.render(Gdx.graphics.getDeltaTime());
        bestTime = Math.min(bestTime, System.nanoTime() - start);
        frameCount++;

        long now = System.currentTimeMillis();
        if (now > lastUpdatedTime + 1000) {
            fps = frameCount;

            lastUpdatedTime = now;
            frameCount = 0;
        }

        spriteBatch.begin();
        for (int i = 0; i < demoSceneNames.size(); i++) {
            if (currentSceneIndex == i)
                listFont.setColor(Color.RED);
            else
                listFont.setColor(Color.WHITE);
            listFont.draw(spriteBatch, (i + 1) + ". " + demoSceneNames.get(i), 0, height - 30 - i * 20);
        }
        titleFont.draw(spriteBatch, currentSceneName, 0, height - 5, width, Align.center, false);
        titleFont.draw(spriteBatch, "FPS: " + fps, 0, 20, width, Align.right, false);
        titleFont.draw(spriteBatch, "Best time: " + (1f * bestTime / nanoInMs + "ms"), 0, 40, width, Align.right, false);
        spriteBatch.end();
    }

    private void processInput() {
        long currentTime = System.currentTimeMillis();
        if (lastProcessedInput + 200 < currentTime) {
            int oldIndex = currentSceneIndex;
            int newIndex = oldIndex;

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (oldIndex < demoScenes.size() - 1)
                    newIndex++;
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if (oldIndex > 0)
                    newIndex--;
            }
            if (oldIndex != newIndex) {
                currentScene.dispose();
                currentScene = demoScenes.get(newIndex);
                currentScene.initialize(width, height);
                currentSceneName = demoSceneNames.get(newIndex);
                currentSceneIndex = newIndex;
                lastProcessedInput = currentTime;

                bestTime = 1000 * 1000000;
            }
        }
    }

    private void clear() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        listFont.dispose();
        titleFont.dispose();

        currentScene.dispose();
        spriteBatch.dispose();
    }

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 600;
        config.resizable = false;

        config.foregroundFPS = 0;
        config.vSyncEnabled = false;
        new LwjglApplication(new PluggableShaderDemo(800, 600), config);
    }
}
