#version 140

in vec2 textureCoords;

out vec4 final_color;

uniform vec3 color;

void main(void)
{
	final_color = vec4(color, 1);
}