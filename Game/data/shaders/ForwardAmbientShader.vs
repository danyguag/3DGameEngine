#version 400 core

in vec3 position;

in vec2 texCoords;
out vec2 pass_textureCoords;

uniform mat4 transform;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void)
{
	gl_Position = projectionMatrix * viewMatrix * transform * vec4(position,1.0);
	pass_textureCoords = texCoords;
}
