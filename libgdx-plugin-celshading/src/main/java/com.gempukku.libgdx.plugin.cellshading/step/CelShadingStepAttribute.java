package com.gempukku.libgdx.plugin.cellshading.step;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;

public class CelShadingStepAttribute extends IntAttribute {
    public static final String CelShadingCountAlias = "celShadingCount";
    public static final long CelShading = register(CelShadingCountAlias);

    public static CelShadingStepAttribute createAttribute(int celShadingCount) {
        return new CelShadingStepAttribute(celShadingCount);
    }

    private CelShadingStepAttribute(int celShadingCount) {
        super(CelShading, celShadingCount);
    }

    @Override
    public Attribute copy() {
        return new CelShadingStepAttribute(value);
    }
}
