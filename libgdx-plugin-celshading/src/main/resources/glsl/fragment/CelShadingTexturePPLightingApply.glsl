vec3 celShadingTextureLightingApply(vec4 position, vec3 color, vec3 diffuse) {
  return color * texture2D(u_celShadingTexture, vec2((diffuse.r + diffuse.g + diffuse.b) / 3.0, 0.0)).rgb;
}
