vec3 celShadingTextureLightingApply(vec3 color, vec3 diffuse, vec3 specular) {
  return color * texture2D(u_celShadingTexture, vec2((diffuse.r + diffuse.g + diffuse.b) / 3.0, 0.0)).rgb;
}
