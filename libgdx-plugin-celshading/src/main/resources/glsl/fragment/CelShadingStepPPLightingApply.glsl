vec3 celShadingLightingApply(vec4 position, vec3 color, vec3 diffuse) {
  return color * floor(0.5 + ((diffuse.r + diffuse.g + diffuse.b) / 3.0) * #shadeCountFlt#) / #shadeCountFlt#;
}
