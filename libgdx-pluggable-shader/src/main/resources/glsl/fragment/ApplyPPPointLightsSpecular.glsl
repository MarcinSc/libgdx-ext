Lighting applyPointLights(vec4 pos, Lighting lighting) {
  vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);
  for (int i = 0; i < #numPointLights#; i++) {
    vec3 lightDir = u_pointLights[i].position - pos.xyz;
    float dist2 = dot(lightDir, lightDir);
    lightDir *= inversesqrt(dist2);
    float NdotL = clamp(dot(v_normal, lightDir), 0.0, 1.0);
    vec3 value = u_pointLights[i].color * (NdotL / (1.0 + dist2));
    lighting.diffuse += value;
    float halfDotView = max(0.0, dot(v_normal, normalize(lightDir + viewVec)));
    lighting.specular += value * pow(halfDotView, u_shininess);
  }
  return lighting;
}
