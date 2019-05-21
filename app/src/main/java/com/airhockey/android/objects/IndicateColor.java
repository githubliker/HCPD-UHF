/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android.objects;

import com.airhockey.android.data.VertexArray;
import com.airhockey.android.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;
import static com.airhockey.android.Constants.BYTES_PER_FLOAT;

public class IndicateColor {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
        + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private float height = 0.5f;

    private VertexArray vertexArray;

    public IndicateColor(float x, float y, float z) {
        float[] VERTEX_DATA = {
                // Order of coordinates: X, Y,Z, S, T
                // Triangle Fan
                x-height*0.5f,    y+height*4,   z, 0.0f, 0.0f,
                x-height*0.5f, y,   z,  0f, 1f,
                x+height*0.5f, y,   z,  1f, 1f,
                x+height*0.5f, y+height*4,  z,  1f, 0f,
                x-height*0.5f, y+height*4,   z, 0.0f, 0.0f,};
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttribPointer(
            0, 
            textureProgram.getPositionAttributeLocation(), 
            POSITION_COMPONENT_COUNT,
            STRIDE);
        
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT, 
            textureProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT, 
            STRIDE);
    }

    public void draw() {                                
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 5);
    }
}
