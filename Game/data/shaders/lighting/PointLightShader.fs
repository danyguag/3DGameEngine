#version 400 core

in vec3 color;

out vec4 final_color;

void main(void)
{
    final_color = vec4(color,1.0);
}