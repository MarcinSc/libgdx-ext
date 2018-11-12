package com.gempukku.libgdx.demo;

public interface DemoScene {
    void initialize(int width, int height);

    void render(float delta);

    void dispose();
}
