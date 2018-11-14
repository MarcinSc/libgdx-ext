Lighting applySpecularTexture(vec4 pos, vec3 normal, Lighting lighting) {
  lighting.specular *= texture2D(u_specularTexture, v_specularUV).rgb;
  return lighting;
}