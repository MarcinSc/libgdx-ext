Lighting applyPointLights(vec4 pos, vec3 normal, Lighting lighting) {
  for (int i = 0; i < #numPointLights#; i++) {
    vec3 lightDir = u_pointLights[i].position - pos.xyz;
    float dist2 = dot(lightDir, lightDir);
    lightDir *= inversesqrt(dist2);
    float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
    vec3 value = u_pointLights[i].color * (NdotL / (1.0 + dist2));
    lighting.diffuse += value;
  }
  return lighting;
}
