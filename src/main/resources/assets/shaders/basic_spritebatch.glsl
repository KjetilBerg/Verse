#shader vertex
#version 330 core
layout (location=0) in vec3 aPos;               //x, y, z   3 float
layout (location=1) in vec4 aColor;             //color     4 bytes
layout (location=2) in vec2 aTexCoords;         //u, v      2 float
//layout (location=3) in uint aTexId;
layout (location=3) in float aTexId;            //id        1 byte
//TODO: the uint version's binary pattern perfectly matches "float value = 1". I believe it is due to https://stackoverflow.com/questions/28014864/why-do-different-variations-of-glvertexattribpointer-exist

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
//flat out uint fTexId;
out float fTexId;

void main()
{
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexId = aTexId;

    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#shader fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
//flat in uint fTexId;
in float fTexId;

uniform sampler2D uTexArray[8]; //TODO: for now, only 8 supported

out vec4 color;

float average(vec4 v)
{
    return (v.x + v.y + v.z + v.a) / 4.0;
}

void main()
{
    color = texture(uTexArray[int(fTexId)], fTexCoords) * fColor;
    //color = color * vec4(0.5, 0.4, 0.6, 1.0); // temp dark test

    /*
    float brightness = average(color);
    if (brightness < 0.55) {
        color.r = brightness;
        color.g = brightness / 1.3;
        color.b = brightness / 2;
    } else {
        color.r = brightness;
        color.g = brightness;
        color.b = brightness;
    }
    */
}