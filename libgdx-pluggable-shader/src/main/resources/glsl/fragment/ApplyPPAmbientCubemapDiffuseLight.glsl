Lighting transformDiffuseLightWithAmbientCubemap(vec4 position, vec3 normal, Lighting lighting) {
  vec3 squaredNormal = normal * normal;
  vec3 isPositive  = step(0.0, normal);
  lighting.diffuse += squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +
    squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +
    squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);
  return lighting;
}
