#shader vertex
#version 330 core
layout (location=0) in vec3 aPos;               //x, y, z   3 float
layout (location=1) in vec4 aColor;             //color     4 bytes

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;

void main()
{
    fColor = aColor;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}


#shader fragment
#version 330 core

in vec4 fColor;
out vec4 color;

void main()
{
    color = fColor;
}