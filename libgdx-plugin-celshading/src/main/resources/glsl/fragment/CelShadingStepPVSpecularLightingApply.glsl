vec3 celShadingLightingApply(vec3 color, vec3 diffuse, vec3 specular) {
  return color * floor(0.5 + ((diffuse.r + diffuse.g + diffuse.b) / 3.0) * #shadeCountFlt#) / #shadeCountFlt#;
}
