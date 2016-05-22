#version 140

in vec2 textureCoords;

out vec4 outColor;

uniform sampler2D sampler;

void main(void)
{
	outColor = texture(sampler, vec2(textureCoords.x, textureCoords.y));
}