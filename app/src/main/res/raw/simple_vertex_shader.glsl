uniform mat4 u_Matrix;

attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;
const float space = -0.0;
void main()                    
{
    v_Color = a_Color;
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 5.0;

//    if(a_Position.y == 0.0){
//        return;
//    } else {
//        if(a_Position.y<0.2 + space){
//            a_Color=vec4(0.0,1.0,0.0,1.0);
//        }else  if(a_Position.y>= 0.2+space && a_Position.y< 0.3+space){
//            a_Color=vec4(0.4,0.6,0,1.0);
//        }else  if(a_Position.y>=0.3+space && a_Position.y< 0.4+space){
//            a_Color=vec4(0.6,0.4,0,1.0);
//        }else  if(a_Position.y>=0.4 +space && a_Position.y< 0.7+space){
//            a_Color=vec4(0.7,0.3,0,1.0);
//        }else  if(a_Position.y>=0.7+space && a_Position.y< 0.8+space){
//            a_Color=vec4(0.8,0.2,0,1.0);
//        }else  if(a_Position.y>=0.8+space && a_Position.y< 0.999+space){
//            a_Color=vec4(1,0,0,1.0);
//        }else {
//            a_Color=vec4(1.0,1.0,1.0,1.0);
//        }
//        v_Color = a_Color;
//    }

}          