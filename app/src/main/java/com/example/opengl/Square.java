package com.example.opengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

    //hàm C/C++ định nghĩa cho openGL
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    //số tọa độ trên mỗi đỉnh của mảng
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.5f, 0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f, 0.5f, 0.0f}; // top right
    private final short drawOrder[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    //thiết lập màu cho hình
    float color[] = {0.2f, 0.709803922f, 0.898039216f, 1.0f};

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */

    //thiết lập dữ liệu cho đối tượng sử dung OpenGLES
    public Square() {

        //KHởi tạo bộ đẹm cho tọa độ của hình vuông
        ByteBuffer bb = ByteBuffer.allocateDirect(
                //giá trị của mỗi tọa độ nhân 4 byte trên mỗi float
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        //khởi tạo bộ đệm cho danh sách vẽ
        ByteBuffer dlb = ByteBuffer.allocateDirect(
//giá trị mỗi tọa độ nhân 2byte trên mỗi float
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);


        // chuẩn bị trình tạo đồ họa cho OpenGL
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();// tạo 1 chương trình OpenGL mới
        GLES20.glAttachShader(mProgram, vertexShader);//thêm đỉnh vào chương trình
        GLES20.glAttachShader(mProgram, fragmentShader);//thêm đổ bóng phân mảnh vào chuoosng trình
        GLES20.glLinkProgram(mProgram);//tạo các tệp thực thi cho chương trình OpenGL
    }

    //vẽ
    public void draw(float[] mvpMatrix) {

        //thêm chương trình vào môi trường OPENGL
        GLES20.glUseProgram(mProgram);

        //nhận xử lý đến các vị trí của các đỉnh
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
//cho phép xử lí các đỉnh của hình vuông
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        //chuẩn bị dữ liệu cho các tọa độ
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        //xử lý các fragment trong vColor của các shader
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        //set màu cho hình
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        //xử lý chuyển từ ma trận --> hình
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");
        //dùng phép chiếu và phép biến đổi ma trajand dể biến đổi khung hình
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        //vẽ hình vuông
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        //ngắt không cho vẽ đỉnh trong mảng
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
