Lighting applySpecularColor(Lighting lighting) {
  lighting.specular *= u_specularColor.rgb;
  return lighting;
}