/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android.threeDimension;

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
import static android.opengl.Matrix.frustumM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;
import static com.airhockey.android.Constants.DATA_SPACE;
import static com.airhockey.android.Constants.SAMPLE_GROUP_NUM;
import static com.airhockey.android.Constants.SAMPLE_DATA_NUM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.airhockey.android.R;
import com.airhockey.android.objects.DataView;
import com.airhockey.android.objects.IndicateColor;
import com.airhockey.android.objects.Mallet;
import com.airhockey.android.objects.Ordinate;
import com.airhockey.android.objects.OrdinateBottom;
import com.airhockey.android.objects.OrdinatePoint;
import com.airhockey.android.objects.PointView;
import com.airhockey.android.objects.Table;
import com.airhockey.android.programs.ColorShaderProgram;
import com.airhockey.android.programs.TextureShaderProgram;
import com.airhockey.android.util.MatrixHelper;
import com.airhockey.android.util.TextureHelper;

public class ChartThreeDimenRenderer implements Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private float[] matrixData = new float[SAMPLE_DATA_NUM *DATA_SPACE* SAMPLE_GROUP_NUM];
    private float[] pointData;

    private Table table;
    private Mallet mallet;
    private Ordinate ordinate;
    private OrdinateBottom ordinateBottom;
    private OrdinatePoint point0,point1,point2,point3,point4,point5,point6;
    private IndicateColor indicateColor;
    private DataView dataView;
    private PointView pointView;
    
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;    
    
    private int texture;
    private int textureBottom;
    private int textureText0,textureText1,textureText2,textureText3,textureText4,textureText5,textureText6;
    private int indicateTexture;

    float r = 7.8f;
    float x = -4f;
    float y = 5f;
    float z = (float) Math.sqrt(r*r - x*x - y*y);

    public ChartThreeDimenRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.2f, 0.2f, 0.2f, 0f);
        glEnable(GLES20.GL_DEPTH_TEST);

//        table = new Table();
//        mallet = new Mallet();
        ordinate = new Ordinate();
        ordinateBottom = new OrdinateBottom();
        dataView = new DataView();
        pointView = new PointView();

        initOrdinatePoints();

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);        
        
        texture = TextureHelper.loadTexture(context, R.drawable.surface);
        textureBottom = TextureHelper.loadTexture(context, R.drawable.background);
        indicateTexture = TextureHelper.loadTexture(context, R.drawable.indicate_bg);

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
        glEnable(GL_LINE_SMOOTH);
        //角度越大 距离越小
        MatrixHelper.perspectiveM(projectionMatrix, 35, (float) width
            / (float) height, 1f, 20f);
        setLookAtM(viewMatrix, 0, x, y, z, 0f, 0f, 0f, 0f, 1f, 0f);
//        setLookAtM(viewMatrix, 0, -6f, 2f, 12, 0f, 0f, 0f, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0);

        setIdentityM(modelMatrix, 0);
//        translateM(modelMatrix, 0, -6.8f, -1f, -7f);
//        rotateM(modelMatrix, 0, 30, 0f, 1f, 0f);

        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
//        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        // Draw the table.
        textureProgram.useProgram();
        textureProgram.setUniforms(viewProjectionMatrix, texture);
        ordinate.bindData(textureProgram);
        ordinate.draw();

        textureProgram.setUniforms(viewProjectionMatrix, textureBottom);
        ordinateBottom.bindData(textureProgram);
        ordinateBottom.draw();
        drawOrdinatePoints();

        colorProgram.useProgram();
        colorProgram.setUniforms(viewProjectionMatrix);
        dataView.bindData(colorProgram, getDataViewMatrix());
        dataView.draw();

        pointView.bindData(colorProgram,getPointViewData());
        pointView.draw();

    }

    private void initOrdinatePoints(){
//        point0 = new OrdinatePoint(-2.0f,0.01f,2.2f,false);
//        textureText0 =  TextureHelper.loadStringTexture("0");
//        point1 = new OrdinatePoint(-1f,0.01f,2.2f,false);
//        textureText1 =  TextureHelper.loadStringTexture("90");
//        point2 = new OrdinatePoint(0.0f,0.01f,2.2f,false);
//        textureText2 =  TextureHelper.loadStringTexture("180");
//        point3 = new OrdinatePoint(1f,0.01f,2.2f,false);
//        textureText3 =  TextureHelper.loadStringTexture("270");
//        point4 = new OrdinatePoint(2f,0.01f,2.2f,false);
//        textureText4 =  TextureHelper.loadStringTexture("360");
        point5 = new OrdinatePoint(1.7f,0,-1.99f,true);
        textureText5 =  TextureHelper.loadStringTexture("1pf");
        point6 = new OrdinatePoint(1.72f,0.5f,-1.99f,true);
        textureText6 =  TextureHelper.loadStringTexture("1000");

//        indicateColor = new IndicateColor(2.2f,0,-2f);
    }
    private void drawOrdinatePoints(){
//        textureProgram.setUniforms(modelViewProjectionMatrix, textureText0);
//        point0.bindData(textureProgram);
//        point0.draw();
//        textureProgram.setUniforms(modelViewProjectionMatrix, textureText1);
//        point1.bindData(textureProgram);
//        point1.draw();
//        textureProgram.setUniforms(modelViewProjectionMatrix, textureText2);
//        point2.bindData(textureProgram);
//        point2.draw();
//        textureProgram.setUniforms(modelViewProjectionMatrix, textureText3);
//        point3.bindData(textureProgram);
//        point3.draw();
//        textureProgram.setUniforms(modelViewProjectionMatrix, textureText4);
//        point4.bindData(textureProgram);
//        point4.draw();
        textureProgram.setUniforms(viewProjectionMatrix, textureText5);
        point5.bindData(textureProgram);
        point5.draw();
        textureProgram.setUniforms(viewProjectionMatrix, textureText6);
        point6.bindData(textureProgram);
        point6.draw();

//        textureProgram.setUniforms(modelViewProjectionMatrix, indicateTexture);
//        indicateColor.bindData(textureProgram);
//        indicateColor.draw();
    }

    public  void setDataViewMatrix(float[] data){
        System.arraycopy(data, 0, matrixData, 0, data.length);
    }
    public float[] getDataViewMatrix(){
        return matrixData;
    }

    public void setPointViewData(float[] data){
        pointData = new float[data.length];
        System.arraycopy(data, 0, pointData, 0, data.length);
    }

    public float[] getPointViewData(){
        return pointData;
    }
    float maxX = r;
    float maxY = 8;
    int isBack = 1;
    public void setAngle(float anglex,float angley) {
//        mAngleX = mAngleX +anglex;
//        mAngleY = mAngleY +angley;
//        Log.e("角度分量"," x "+anglex + "   y "+angley);
//        if(Math.abs(anglex) >Math.abs(angley)){
//            rotateM(modelMatrix, 0, mAngleX,    0f, 1f, 0f);
//        } else {
//            rotateM(modelMatrix, 0, mAngleY, 10f, 0f, 0f);
//        }
        maxX = (float) Math.sqrt(r*r - y*y);
        if(isBack == 1){
            x = x -anglex;
            if(x < -maxX){
                x = -maxX;
                isBack = -1;
            } else if(x > maxX){
                x = maxX;
                isBack = -1;
            }
        }else{
            x = x +anglex;
            if(x < -maxX){
                x = -maxX;
                isBack = 1;
            } else if(x > maxX){
                x = maxX;
                isBack = 1;
            }
        }

//        y = y +angley;
//        maxY = (float) Math.sqrt(r*r - x*x);
//        if( y < -2){
//            y = -2;
//        } else if(y >= maxY-1){
//            y = maxY-1;
//        }
        double result = r*r - x*x - y*y;
        if(result  >0 ){
            z = (float) Math.sqrt(r*r - x*x - y*y)* isBack;
        } else {
            z = 0;
        }
        Log.e("角度分量"," x "+x + "   y "+y +"  z "+z);
        setLookAtM(viewMatrix, 0, x, y, z, 0f, 0f, 0f, 0f, 1f, 0f);
        multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0);
//        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

    }
}