Lighting applySpecularColor(vec4 pos, vec3 normal, Lighting lighting) {
  lighting.specular *= u_specularColor.rgb;
  return lighting;
}