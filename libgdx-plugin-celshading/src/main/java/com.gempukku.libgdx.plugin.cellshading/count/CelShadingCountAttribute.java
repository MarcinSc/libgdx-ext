package com.gempukku.libgdx.plugin.cellshading.count;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;

public class CelShadingCountAttribute extends IntAttribute {
    public static final String CelShadingCountAlias = "celShadingCount";
    public static final long CelShading = register(CelShadingCountAlias);

    public static CelShadingCountAttribute createAttribute(int celShadingCount) {
        return new CelShadingCountAttribute(celShadingCount);
    }

    private CelShadingCountAttribute(int celShadingCount) {
        super(CelShading, celShadingCount);
    }

    @Override
    public Attribute copy() {
        return new CelShadingCountAttribute(value);
    }
}
