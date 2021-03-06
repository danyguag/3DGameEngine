#version 400 core

in vec2 pass_textureCoords;

out vec4 final_color;

uniform sampler2D textureSampler;

void main(void)
{
	vec4 textureColor = texture(textureSampler,pass_textureCoords);

    final_color = textureColor;
}