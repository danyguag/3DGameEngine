#version 400 core

in vec3 color;
in vec2 pass_textureCoords;

out vec4 final_color;

uniform sampler2D textureSampler;
uniform vec3 ambientLight;

void main(void)
{
    final_color = texture(textureSampler, pass_textureCoords) * vec4(ambientLight, 0);
}