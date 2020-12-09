package com.example.triangleinneranglesum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CanvasView extends View {

    private Paint oriPaint, changePaint, cutPaint, cutPaint2, anglePaint, anglePaint2;
    private LinkedList<Object> objectList;
    public List<Line> lineList;
    private Random random;
    public final static int STATE_MOVE = 1;
    public final static int STATE_ROTATE = 2;
    public final static int STATE_CUT = 3;
    public final static int STATE_ANGLE = 4;
    private int state = STATE_MOVE;
    private Matrix matrix;
    private CutLine cutLine;
    private PointF cutP1, cutP2;
    private Context context;
    private boolean showNext;


    public CanvasView(Context context) {
        super(context);
        init(context);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        oriPaint = new Paint();
        oriPaint.setColor(getResources().getColor(R.color.color_blue));
        oriPaint.setStyle(Paint.Style.FILL); //设置填充样式
        changePaint = new Paint();
        changePaint.setStyle(Paint.Style.FILL);
        changePaint.setAntiAlias(true);
        cutPaint = new Paint();
        cutPaint.setColor(Color.BLACK);
        cutPaint.setStyle(Paint.Style.STROKE);
        cutPaint.setStrokeWidth(5);
        cutPaint.setPathEffect(new DashPathEffect(new float[]{30, 30}, 0));
        cutPaint.setAntiAlias(true);
        cutPaint2 = new Paint();
        cutPaint2.setColor(Color.WHITE);
        cutPaint2.setStrokeWidth(5);
        cutPaint2.setStyle(Paint.Style.STROKE);
        cutPaint2.setAntiAlias(true);
        anglePaint = new Paint();
        anglePaint.setColor(Color.BLACK);
        anglePaint.setStyle(Paint.Style.FILL);
        anglePaint.setStrokeWidth(15);
        anglePaint.setTextSize(30);
        anglePaint.setAntiAlias(true);
        anglePaint.setTextAlign(Paint.Align.CENTER);
        anglePaint2 = new Paint();
        anglePaint2.setColor(Color.RED);
        anglePaint2.setStyle(Paint.Style.FILL);
        anglePaint2.setAntiAlias(true);
        anglePaint2.setStrokeWidth(15);
        anglePaint2.setDither(true);//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        anglePaint2.setFilterBitmap(true);
        anglePaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        objectList = new LinkedList<>();
        lineList = new ArrayList<>();
        random = new Random();
        matrix = new Matrix();
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Object o : objectList) {
            if (o instanceof Rectangle) {
                canvas.drawRect(((Rectangle) o).getRectF(), oriPaint);
            }
            if (o instanceof Triangle) {
                changePaint.setColor(getContext().getColor(((Triangle) o).getColor()));
                canvas.drawPath(((Triangle) o).getPath(), changePaint);
                for (int i = 0; i < ((Triangle) o).getAngleTexts().length; i++) {
                    if (((Triangle) o).getAngleTexts()[i] != null) {
//                        canvas.drawText(((Triangle) o).getAngleTexts()[i], ((Triangle) o).getX(i), ((Triangle) o).getY(i) - 20, anglePaint);
                        float destX = ((Triangle) o).getCenterP().x - ((Triangle) o).getX(i);
                        float destY = ((Triangle) o).getCenterP().y - ((Triangle) o).getY(i);
                        float n = 1.4f;
                        if (((Triangle) o).isCanCut()) {
                            n = Math.abs(destX) < 102 && Math.abs(destY) < 134 ? 2 : 3;
                        }
                        if (Math.abs(Math.abs(destX)-50)<10&&Math.abs(50-destY)<10){
                            n = 1.0f;
                        }
                        Log.e("BBB", "DESTX" + ((Triangle) o).getAngleTexts()[i] + ":  " + destX + " , " + destY + "-------" + n);
                        canvas.drawText(((Triangle) o).getAngleTexts()[i], ((Triangle) o).getX(i) + destX / n, ((Triangle) o).getY(i) + destY / n, anglePaint);
                        canvas.drawCircle(((Triangle) o).getX(i), ((Triangle) o).getY(i), 40, anglePaint2);
                    }
                }
            }
            if (o instanceof Polygon) {
                changePaint.setColor(((Polygon) o).getColor());
                canvas.drawPath(((Polygon) o).getPath(), changePaint);
            }
        }
        if (cutLine != null) {
            canvas.drawLine(cutLine.getSx(), cutLine.getSy(), cutLine.getEx(), cutLine.getEy(), cutPaint);
        }
        for (Line l : lineList) {
            canvas.drawLine(l.point1.x, l.point1.y, l.point2.x, l.point2.y, cutPaint2);
        }
        /*if (showNext&&context!=null){
            ((MainActivity)context).showNext();
        }*/
    }

    Triangle curTri;
    Rectangle curRect;

    private float pre_X = 0, pre_Y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (state) {
            case STATE_CUT:
                cut(event);
                return true;
            case STATE_MOVE:
                move(event);
                return true;
            case STATE_ROTATE:
                rotate(event);
                return true;
            case STATE_ANGLE:
                angle(event);
                return true;
        }
        return true;
    }

    private void angle(MotionEvent event) {
        float a = event.getX();
        float b = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showNext = true;
                for (Object o : objectList) {
                    if (o instanceof Triangle) {
                        if (Math.abs(a - ((Triangle) o).getX1()) < 40 && Math.abs(b - ((Triangle) o).getY1()) < 40 && ((Triangle) o).getAngleTexts()[0] == null) {
                            if (((Triangle) o).isCanCut()) {
                                ((Triangle) o).getAngleTexts()[0] = "1";
//                                ((Triangle) o).getAngleTexts()[0] = ((Triangle) o).getAngleIndex();
                                invalidate();
                            } else {
                                Toast.makeText(getContext(), "切割后的三角形不能再继续标角！", Toast.LENGTH_SHORT).show();
                            }
                        } else if (Math.abs(a - ((Triangle) o).getX2()) < 40 && Math.abs(b - ((Triangle) o).getY2()) < 40 && ((Triangle) o).getAngleTexts()[1] == null) {
                            if (((Triangle) o).isCanCut()) {
                                ((Triangle) o).getAngleTexts()[1] = "2";
//                                ((Triangle) o).getAngleTexts()[1] = ((Triangle) o).getAngleIndex();
                                invalidate();
                            } else {
                                Toast.makeText(getContext(), "切割后的三角形不能再继续标角！", Toast.LENGTH_SHORT).show();
                            }
                        } else if (Math.abs(a - ((Triangle) o).getX3()) < 40 && Math.abs(b - ((Triangle) o).getY3()) < 40 && ((Triangle) o).getAngleTexts()[2] == null) {
                            if (((Triangle) o).isCanCut()) {
                                ((Triangle) o).getAngleTexts()[2] = "3";
//                                ((Triangle) o).getAngleTexts()[2] = ((Triangle) o).getAngleIndex();
                                invalidate();
                            } else {
                                Toast.makeText(getContext(), "切割后的三角形不能再继续标角！", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!"4".equals(((Triangle) o).getAngleIndex())) {
                            showNext = false;
                        }
                    }
                }
                if (showNext) {
                    ((MainActivity) context).showNext();
                }
                break;
        }
    }

    private void rotate(MotionEvent event) {
        float a = event.getX();
        float b = event.getY();
        for (Object o : objectList) {
            if (o instanceof Triangle) {
                if (((Triangle) o).isInTriangle(new PointF(a, b))) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        /*Matrix matrix = new Matrix();
                        matrix.postRotate(90, ((Triangle) o).getCenterP().x, ((Triangle) o).getCenterP().y);
                        ((Triangle) o).getPath().transform(matrix);
                        ((Triangle) o).setDegrees(((Triangle) o).getDegrees() + 90);*/
                        ((Triangle) o).setRotate(90);
                        objectList.remove(o);
                        objectList.addFirst(o);
                        invalidate();
                    }
                    return;
                }
            }
        }
    }

    private void move(MotionEvent event) {
        float a = event.getX();
        float b = event.getY();
        for (Object o : objectList) {
            if (o instanceof Rectangle) {
                if (((Rectangle) o).isInImage(a, b)) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            pre_X = a;
                            pre_Y = b;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            ((Rectangle) o).setMove(a - pre_X, b - pre_Y);
                            pre_X = a;
                            pre_Y = b;
                            break;
                    }
                    objectList.remove(o);
                    objectList.addFirst(o);
                    invalidate();
                    return;
                }
            }
            if (o instanceof Triangle) {
                if (((Triangle) o).isInTriangle(new PointF(a, b))) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            pre_X = a;
                            pre_Y = b;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            ((Triangle) o).setMove(a - pre_X, b - pre_Y, getHeight(), getWidth());
                            pre_X = a;
                            pre_Y = b;
                            /*Matrix matrix1 = new Matrix();
                            matrix1.postRotate(((Triangle) o).getDegrees(), ((Triangle) o).getCenterP().x, ((Triangle) o).getCenterP().y);
                            ((Triangle) o).getPath().transform(matrix1);*/
                            break;
                        /*case MotionEvent.ACTION_UP:
                            if(!((Triangle) o).isCanCut()&&"2".equals(((Triangle) o).getAngleIndex())){
                                PointF curAngle = ((Triangle) o).getPointF(angleIndex((Triangle) o));
                                for (Object item:objectList) {
                                    if (item instanceof Triangle && !item.equals(o)&& !((Triangle) item).isCanCut() && "2".equals(((Triangle) item).getAngleIndex())){
                                        PointF adsorbAngle = ((Triangle) item).getPointF(angleIndex((Triangle) item));
                                        if (Math.abs(curAngle.x-adsorbAngle.x)<=20&&Math.abs(curAngle.y-adsorbAngle.y)<=20){
                                            ((Triangle) o).setMove(adsorbAngle.x-curAngle.x,adsorbAngle.y-curAngle.y);
                                            return;
                                        }
                                    }
                                }
                            }
                            break;*/
                    }
                    objectList.remove(o);
                    objectList.addFirst(o);
                    invalidate();
                    return;
                }
            }
        }
    }

    private int angleIndex(Triangle triangle) {
        String[] strings = triangle.getAngleTexts();
        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null) {
                return i + 1;
            }
        }
        return 0;
    }

    private void cut(MotionEvent event) {
        float a = event.getX();
        float b = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cutLine = new CutLine();
                cutLine.setSxy(a, b);
                curTri = null;
                curRect = null;
                break;
            case MotionEvent.ACTION_MOVE:
                cutLine.setExy(a, b);
                for (Object o : objectList) {
                    if (o instanceof Triangle) {
                        if (((Triangle) o).isInTriangle(new PointF(a, b)) && ((Triangle) o).isCanCut()) {
                            curTri = (Triangle) o;
                        }
                    }
                }
                for (Object o : objectList) {
                    if (o instanceof Rectangle) {
                        if (((Rectangle) o).isInImage(a, b)) {
                            curRect = (Rectangle) o;
                        }
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (curTri != null) {
                    PointF p12 = getIntersectPoint(cutLine, curTri.getX1(), curTri.getY1(), curTri.getX2(), curTri.getY2());
                    PointF p13 = getIntersectPoint(cutLine, curTri.getX1(), curTri.getY1(), curTri.getX3(), curTri.getY3());
                    PointF p23 = getIntersectPoint(cutLine, curTri.getX2(), curTri.getY2(), curTri.getX3(), curTri.getY3());
                    if (p12 != null && p13 != null && p23 != null) {
                        Toast.makeText(getContext(), "不能切到顶点上哦，重新切一下吧", Toast.LENGTH_SHORT).show();
                    } else if (p12 != null && p13 != null&& curTri.getCanCut(1)) {
                        Triangle t1 = new Triangle(curTri.getX1(), curTri.getY1(), p13.x, p13.y, p12.x, p12.y, getChangeColor(), false);
                        t1.getAngleTexts()[0] = curTri.getAngleTexts()[0];
                        objectList.addLast(t1);
                        lineList.add(new Line(p12, p13));
                        curTri.addCutNum(1);
                    } else if (p12 != null && p23 != null&& curTri.getCanCut(2)) {
                        Triangle t2 = new Triangle(p12.x, p12.y, curTri.getX2(), curTri.getY2(), p23.x, p23.y, getChangeColor(), false);
                        t2.getAngleTexts()[1] = curTri.getAngleTexts()[1];
                        objectList.addLast(t2);
                        lineList.add(new Line(p12, p23));
                        curTri.addCutNum(2);
                    } else if (p13 != null && p23 != null && curTri.getCanCut(3)) {
                        Triangle t3 = new Triangle(p23.x, p23.y, p13.x, p13.y, curTri.getX3(), curTri.getY3(), getChangeColor(), false);
                        t3.getAngleTexts()[2] = curTri.getAngleTexts()[2];
                        objectList.addLast(t3);
                        lineList.add(new Line(p23, p13));
                        curTri.addCutNum(3);
                    }
                    if (curTri.getCutNumAll() == 3) {
                        objectList.remove(curTri);
                        lineList.clear();
                    }
                }
                if (curRect != null) {
                    float k = Math.abs(getSlope(cutLine));
                    if (k <= 1.5 && k >= 0.5) {
                        if (getSlope(cutLine) <= 0) {
                            objectList.addLast(new Triangle(curRect.getL(), curRect.getT(), curRect.getL(), curRect.getB(), curRect.getR(), curRect.getT(), getChangeColor(), false));
                            objectList.addLast(new Triangle(curRect.getL() + 10, curRect.getB(), curRect.getR() + 10, curRect.getT(), curRect.getR() + 10, curRect.getB(), getChangeColor(), false));
                        } else {
                            objectList.addLast(new Triangle(curRect.getL() + 10, curRect.getT(), curRect.getR() + 10, curRect.getT(), curRect.getR() + 10, curRect.getB(), getChangeColor(), false));
                            objectList.addLast(new Triangle(curRect.getL(), curRect.getB(), curRect.getR(), curRect.getB(), curRect.getL(), curRect.getT(), getChangeColor(), false));
                        }
                        objectList.remove(curRect);
                    } else {
                        Toast.makeText(getContext(), "请切割对角线！", Toast.LENGTH_SHORT).show();
                    }
                }
                cutLine = null;
                invalidate();
                break;
        }
    }

    private int[] colors = {R.color.color_purple1,R.color.color_yellow, R.color.color_cyan, R.color.color_green, R.color.color_pink, R.color.color_purple, R.color.color_orange, R.color.color_blue};
    private int colorNum = 0;

    private int getChangeColor() {
//        return 0xff000000 | random.nextInt(0x00ffffff);
        colorNum++;
        return colors[colorNum % colors.length];
    }

    public void addT1() {
        objectList.addFirst(new Triangle(100, 100, 100, 400, 400, 400, getChangeColor()));
        invalidate();
    }

    public void addT2() {
//        objectList.addFirst(new Triangle(400, 100, 350, 300, 550, 300, getChangeColor()));
        objectList.addFirst(new Triangle(200, 100, 100, 400, 400, 400, getChangeColor()));
        invalidate();
    }

    public void addT3() {
//        objectList.addFirst(new Triangle(550, 100, 600, 300, 800, 300, getChangeColor()));
        objectList.addFirst(new Triangle(50, 100, 100, 400, 400, 400, getChangeColor()));
        invalidate();
    }

    public void addR1() {
        objectList.addFirst(new Rectangle(450, 100, 750, 400));
        invalidate();
        ((MainActivity) context).showNext();
    }

    public void addR2() {
        objectList.addFirst(new Rectangle(650, 100, 1050, 400));
        invalidate();
        ((MainActivity) context).showNext();
    }

    public void setState(int state) {
        this.state = state;
        lineList.clear();
    }

    public int getState() {
        return state;
    }

    public void setOnlyOne() {
        Object obj = null;
        for (Object item:objectList) {
            if (item instanceof Triangle && ((Triangle) item).isCanCut() && ((Triangle) item).getAngleIndex().equals("4")){
                obj = item;
                break;
            }
        }
        if (obj != null) {
            objectList.clear();
            objectList.add(obj);
            invalidate();
        }
    }

    /**
     * 获取两条直线相交的点
     */
    public static PointF getIntersectPoint(CutLine cutLine, float x3, float y3, float x4, float y4) {

        double A1 = cutLine.getSy() - cutLine.getEy();
        double B1 = cutLine.getEx() - cutLine.getSx();
        double C1 = A1 * cutLine.getSx() + B1 * cutLine.getSy();

        double A2 = y3 - y4;
        double B2 = x4 - x3;
        double C2 = A2 * x3 + B2 * y3;

        double det_k = A1 * B2 - A2 * B1;

        if (Math.abs(det_k) < 0.00001) {
            return null;
        }

        double a = B2 / det_k;
        double b = -1 * B1 / det_k;
        double c = -1 * A2 / det_k;
        double d = A1 / det_k;

        float x = (float) (a * C1 + b * C2);
        float y = (float) (c * C1 + d * C2);

        // 判断交点的坐标x(或y)是否在起始点的x(或y)之间
        if (x < Math.min(x3, x4) || x > Math.max(x3, x4)) {
            return null;
        }
        if (y < Math.min(y3, y4) || y > Math.max(y3, y4)) {
            return null;
        }
        //强制中点切割
        x = (x3 + x4) / 2;
        y = (y3 + y4) / 2;
        return new PointF(x, y);
    }

    public float getSlope(CutLine cutLine) {
        return (cutLine.getEy() - cutLine.getSy()) / (cutLine.getEx() - cutLine.getSx());
    }

    public void clearAll() {
        objectList.clear();
        lineList.clear();
        invalidate();
    }
}
