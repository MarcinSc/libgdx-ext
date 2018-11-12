Lighting applySpecularColor(vec4 pos, Lighting lighting) {
  lighting.specular *= u_specularColor.rgb;
  return lighting;
}