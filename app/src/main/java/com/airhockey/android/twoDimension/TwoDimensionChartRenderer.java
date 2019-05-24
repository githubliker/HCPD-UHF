/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android.twoDimension;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.airhockey.android.R;
import com.airhockey.android.objects.Ordinate;
import com.airhockey.android.objects.OrdinatePoint;
import com.airhockey.android.objects.PointView;
import com.airhockey.android.programs.ColorShaderProgram;
import com.airhockey.android.programs.TextureShaderProgram;
import com.airhockey.android.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES10.GL_LINE_SMOOTH;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

public class TwoDimensionChartRenderer implements Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private float[] pointData;

    private Ordinate ordinate;
    private OrdinatePoint point5,point6;
    private PointView pointView;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;
    private int textureText5,textureText6;

    float r = 1;
    float x = 0f;
    float y = 0.01f;
    float z = 1f;

    public TwoDimensionChartRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.2f, 0.2f, 0.2f, 0f);
        glEnable(GLES20.GL_DEPTH_TEST);

        ordinate = new Ordinate();
        pointView = new PointView();

        initOrdinatePoints();

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);        
        
        texture = TextureHelper.loadTexture(context, R.drawable.surface);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
        glEnable(GL_LINE_SMOOTH);
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1.2f, 10);
        setLookAtM(viewMatrix, 0, x, y, z, 0f, 0f, 0f, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0);

        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, -1.25f, 1.2f);
        scaleM(modelMatrix,0,1,2,1);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        // Draw the table.
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        ordinate.bindData(textureProgram);
        ordinate.draw();

        drawOrdinatePoints();

        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix);
        pointView.bindData(colorProgram,getPointViewData());
        pointView.draw();

    }

    private void initOrdinatePoints(){
        point5 = new OrdinatePoint(1.72f,-0.15f,-1.99f,true);
        textureText5 =  TextureHelper.loadStringTexture("1PF");
        point6 = new OrdinatePoint(1.72f,0.35f,-1.99f,true);
        textureText6 =  TextureHelper.loadStringTexture("1000");

    }
    private void drawOrdinatePoints(){
        textureProgram.setUniforms(modelViewProjectionMatrix, textureText5);
        point5.bindData(textureProgram);
        point5.draw();
        textureProgram.setUniforms(modelViewProjectionMatrix, textureText6);
        point6.bindData(textureProgram);
        point6.draw();
    }


    public void setPointViewData(float[] data){
        pointData = new float[data.length];
        System.arraycopy(data, 0, pointData, 0, data.length);
    }

    public float[] getPointViewData(){
        return pointData;
    }
}