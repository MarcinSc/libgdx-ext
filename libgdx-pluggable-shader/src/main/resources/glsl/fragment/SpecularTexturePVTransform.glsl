Lighting applySpecularTexture(Lighting lighting) {
  lighting.specular *= texture2D(u_specularTexture, v_specularUV).rgb;
  return lighting;
}