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
import com.airhockey.android.programs.ColorShaderProgram;

import static android.opengl.GLES10.GL_LINES;
import static android.opengl.GLES10.glLineWidth;
import static android.opengl.GLES20.glDrawArrays;
import static com.airhockey.android.Constants.BYTES_PER_FLOAT;
import static com.airhockey.android.Constants.DATA_SPACE;

public class DataView {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE =
        (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
        * BYTES_PER_FLOAT;
    private static final float[] VERTEX_DATA = {
        // Order of coordinates: X, Y, Z,
        0f, 0.0f, 0f,
        0f,  0.0f, 0f };
    private VertexArray vertexArray;
    private int length;
    public DataView() {
        length = 6;
        vertexArray = new VertexArray(VERTEX_DATA);
    }
    
    public void bindData(ColorShaderProgram colorProgram, float[] data) {
        if(data == null){
            return;
        }
        length = data.length;
        vertexArray = new VertexArray(data);
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, 
            STRIDE);
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            colorProgram.getColorAttributeLocation(), 
            COLOR_COMPONENT_COUNT,
            STRIDE);
    }

    public void draw() {
        glLineWidth(3);
        glDrawArrays(GL_LINES, 0, length*2/DATA_SPACE);
    }
}
