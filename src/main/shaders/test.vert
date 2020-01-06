#version 330 core
layout(location=0) in vec3 vert;


void main(){
    gl_Position.xyz = vert;
    gl_Position.w = 1.0;
}