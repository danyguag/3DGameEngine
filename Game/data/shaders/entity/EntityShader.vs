#version 400 core

in vec3 position;

in vec2 texCoords;
out vec2 pass_textureCoords;

uniform mat4 transform;

void main(void)
{
	gl_Position = transform * vec4(position,1.0);
	pass_textureCoords = texCoords;
}
