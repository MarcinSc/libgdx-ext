Lighting applyCelShading(Lighting lighting) {
  lighting.diffuse.r = floor(0.5 + lighting.diffuse.r * #shadeCountFlt#) / #shadeCountFlt#;
  lighting.diffuse.g = floor(0.5 + lighting.diffuse.g * #shadeCountFlt#) / #shadeCountFlt#;
  lighting.diffuse.b = floor(0.5 + lighting.diffuse.b * #shadeCountFlt#) / #shadeCountFlt#;
  return lighting;
}
