Lighting applyCelShading(Lighting lighting) {
  lighting.diffuse.r = texture2D(u_celShadingTexture, lighting.diffuse.r, 0);
  lighting.diffuse.g = texture2D(u_celShadingTexture, lighting.diffuse.g, 0);
  lighting.diffuse.b = texture2D(u_celShadingTexture, lighting.diffuse.b, 0);
  return lighting;
}
