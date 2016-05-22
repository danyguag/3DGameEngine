#version 400 core

in vec3 nNormal;
in vec2 pass_textureCoords;

out vec4 final_color;

uniform sampler2D textureSampler;
uniform vec3 lightDirection;
uniform vec3 lightColor;

void main(void)
{
	vec4 textureColor = texture(textureSampler, pass_textureCoords);
	float DirectionalColor = max(0.0, dot(normalize(nNormal), lightDirection));


    final_color = textureColor * vec4(lightColor, 1);
}