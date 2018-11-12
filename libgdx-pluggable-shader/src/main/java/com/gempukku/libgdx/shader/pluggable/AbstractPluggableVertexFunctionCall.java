package com.gempukku.libgdx.shader.pluggable;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Predicate;

public abstract class AbstractPluggableVertexFunctionCall implements PluggableVertexFunctionCall {
    private Predicate<Renderable> additionalPredicate;

    public AbstractPluggableVertexFunctionCall() {
    }

    public AbstractPluggableVertexFunctionCall(Predicate<Renderable> additionalPredicate) {
        this.additionalPredicate = additionalPredicate;
    }

    @Override
    public boolean isProcessing(Renderable renderable) {
        return (additionalPredicate == null || additionalPredicate.evaluate(renderable)) && isProcessingForRenderable(renderable);
    }

    protected abstract boolean isProcessingForRenderable(Renderable renderable);
}
