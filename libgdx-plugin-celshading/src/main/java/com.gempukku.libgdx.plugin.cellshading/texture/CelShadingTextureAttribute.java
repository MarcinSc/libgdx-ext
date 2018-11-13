package com.gempukku.libgdx.plugin.cellshading.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.MathUtils;

public class CelShadingTextureAttribute extends Attribute {
    public static final String CelShadingTextureAlias = "celShadingTexture";
    public static final long CelShading = register(CelShadingTextureAlias);

    public final TextureDescriptor<Texture> textureDescription;
    public float offsetU;
    public float offsetV;
    public float scaleU;
    public float scaleV;

    public static CelShadingTextureAttribute createAttribute(Texture texture) {
        return new CelShadingTextureAttribute(texture);
    }

    public static CelShadingTextureAttribute createAttribute(TextureRegion textureRegion) {
        return new CelShadingTextureAttribute(textureRegion);
    }

    private CelShadingTextureAttribute() {
        super(CelShading);
        this.offsetU = 0.0F;
        this.offsetV = 0.0F;
        this.scaleU = 1.0F;
        this.scaleV = 1.0F;
        this.textureDescription = new TextureDescriptor<Texture>();
    }

    private CelShadingTextureAttribute(Texture texture) {
        this();
        this.textureDescription.texture = texture;
    }

    private CelShadingTextureAttribute(TextureRegion textureRegion) {
        this();
        this.textureDescription.texture = textureRegion.getTexture();
        this.offsetU = textureRegion.getU();
        this.offsetV = textureRegion.getV();
        this.scaleU = textureRegion.getU2() - this.offsetU;
        this.scaleV = textureRegion.getV2() - this.offsetV;
    }

    @Override
    public Attribute copy() {
        CelShadingTextureAttribute copy = new CelShadingTextureAttribute();
        copy.textureDescription.set(textureDescription);
        copy.offsetU = offsetU;
        copy.offsetV = offsetV;
        copy.scaleU = scaleU;
        copy.scaleV = scaleV;
        return copy;
    }

    @Override
    public int compareTo(Attribute o) {
        if (this.type != o.type) {
            return this.type < o.type ? -1 : 1;
        } else {
            TextureAttribute other = (TextureAttribute) o;
            int c = this.textureDescription.compareTo(other.textureDescription);
            if (c != 0) {
                return c;
            } else if (!MathUtils.isEqual(this.scaleU, other.scaleU)) {
                return this.scaleU > other.scaleU ? 1 : -1;
            } else if (!MathUtils.isEqual(this.scaleV, other.scaleV)) {
                return this.scaleV > other.scaleV ? 1 : -1;
            } else if (!MathUtils.isEqual(this.offsetU, other.offsetU)) {
                return this.offsetU > other.offsetU ? 1 : -1;
            } else if (!MathUtils.isEqual(this.offsetV, other.offsetV)) {
                return this.offsetV > other.offsetV ? 1 : -1;
            } else {
                return 0;
            }
        }
    }
}
