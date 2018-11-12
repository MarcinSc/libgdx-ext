package com.gempukku.libgdx.demo.benchmark;

public interface DemoScene {
    void initialize(int width, int height);

    void render(float delta);

    void dispose();
}
