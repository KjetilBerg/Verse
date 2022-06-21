#shader vertex
#version 330 core
layout (location=0) in vec3 aPos;               //x, y, z   3 float

uniform mat4 uProjection;
uniform mat4 uView;

void main()
{
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#shader fragment
#version 330 core

out vec4 color;

void main()
{
    color = vec4(0.5, 0.5, 0.5, 1.0);
}