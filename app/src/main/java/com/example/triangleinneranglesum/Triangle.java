package com.example.triangleinneranglesum;


import android.graphics.Path;
import android.graphics.PointF;

public class Triangle {

    private float x1, y1;
    private float x2, y2;
    private float x3, y3;
    private Path path;
    private PointF centerP;
    private int color;
    private float degrees;
    private String[] angleTexts;
    private boolean canCut;
    private int[] cutNum;

    public Triangle(float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        init(x1, y1, x2, y2, x3, y3, color, true);
    }

    public Triangle(float x1, float y1, float x2, float y2, float x3, float y3, int color, boolean canCut) {
        init(x1, y1, x2, y2, x3, y3, color, canCut);
    }

    private void init(float x1, float y1, float x2, float y2, float x3, float y3, int color, boolean canCut) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.color = color;
        this.centerP = getTriangleCenterPoint(new PointF(x1, y1), new PointF(x2, y2), new PointF(x3, y3));
        this.path = new Path();
        this.path.moveTo(this.x1, this.y1);
        this.path.lineTo(this.x2, this.y2);
        this.path.lineTo(this.x3, this.y3);
        this.angleTexts = new String[3];
        this.canCut = canCut;
        this.cutNum = new int[3];
    }

    /**
     * 已知三角形三个点，中线的交点，中点
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    private PointF getTriangleCenterPoint(PointF p1, PointF p2, PointF p3) {
        // 求任意两条线段的中垂线
        Line l12cv = new Line(getCenterPoint(p1, p2), p3);
        Line l23cv = new Line(getCenterPoint(p2, p3), p1);

        // 任意两条中垂线的交点即为重心
        return getCrossPoint(l12cv, l23cv);
    }

    /**
     * 获取两条直线相交的点
     * @param l1
     * @param l2
     * @return
     */
    public static PointF getCrossPoint(Line l1, Line l2) {
        if (Math.abs(l1.gradient - l2.gradient) < 0.00001) {
            // 斜率相同，平行或重叠
            return null;
        }

        PointF p1 = l1.point1;
        PointF p2 = l1.point2;
        PointF p3 = l2.point1;
        PointF p4 = l2.point2;

        float A1 = p1.y - p2.y;
        float B1 = p2.x - p1.x;
        float C1 = A1 * p1.x + B1 * p1.y;

        float A2 = p3.y - p4.y;
        float B2 = p4.x - p3.x;
        float C2 = A2 * p3.x + B2 * p3.y;

        float det_k = A1 * B2 - A2 * B1;

        float a = B2 / det_k;
        float b = -1 * B1 / det_k;
        float c = -1 * A2 / det_k;
        float d = A1 / det_k;

        float x = a * C1 + b * C2;
        float y = c * C1 + d * C2;

        // 判断交点的坐标x(或y)是否在起始点的x(或y)之间
        if (l1.isDistanceFixed && (x < Math.min(l1.point1.x, l1.point2.x) || x > Math.max(l1.point1.x, l1.point2.x))) {
            return null;
        }
        if (l2.isDistanceFixed && (x < Math.min(l2.point1.x, l2.point2.x) || x > Math.max(l2.point1.x, l2.point2.x))) {
            return null;
        }
        return new PointF(x, y);
    }

    /**
     * 求两点的中点坐标
     *
     * @param p1, p2
     */
    private PointF getCenterPoint(PointF p1, PointF p2) {
        return new PointF((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f);
    }

    public float getDegrees() {
        return degrees;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }

    public int getColor() {
        return color;
    }

    public Path getPath() {
        return path;
    }

    /**
     * 根据中点的移动距离给每个顶点移动相同的距离
     * @param moveX
     * @param moveY
     */
    public void setMove(float moveX, float moveY,int maxH,int maxW) {
        float x1n = x1 + moveX;
        float y1n = y1 + moveY;
        float x2n = x2 + moveX;
        float y2n = y2 + moveY;
        float x3n = x3 + moveX;
        float y3n = y3 + moveY;
        if (x1n>0&&x2n>0&&x3n>0&&y1n>0&&y2n>0&&y3n>0&&x1n<maxW&&x2n<maxW&&x3n<maxW&&y1n<maxH&&y2n<maxH&&y3n<maxH){
            this.x1 = x1 + moveX;
            this.y1 = y1 + moveY;
            this.x2 = x2 + moveX;
            this.y2 = y2 + moveY;
            this.x3 = x3 + moveX;
            this.y3 = y3 + moveY;
            this.centerP = getTriangleCenterPoint(new PointF(x1, y1), new PointF(x2, y2), new PointF(x3, y3));
            this.path.reset();
            this.path.moveTo(this.x1, this.y1);
            this.path.lineTo(this.x2, this.y2);
            this.path.lineTo(this.x3, this.y3);
        }
    }

    /**
     * 3个顶点围绕中心点旋转后的坐标
     * @param angle
     */
    public void setRotate(float angle) {
        PointF p1 = new PointF(x1, y1);
        PointF p2 = new PointF(x2, y2);
        PointF p3 = new PointF(x3, y3);
        this.x1 = calcNewPoint(p1, 90).x;
        this.y1 = calcNewPoint(p1, 90).y;
        this.x2 = calcNewPoint(p2, 90).x;
        this.y2 = calcNewPoint(p2, 90).y;
        this.x3 = calcNewPoint(p3, 90).x;
        this.y3 = calcNewPoint(p3, 90).y;
        this.centerP = getTriangleCenterPoint(new PointF(x1, y1), new PointF(x2, y2), new PointF(x3, y3));
        this.path.reset();
        this.path.moveTo(this.x1, this.y1);
        this.path.lineTo(this.x2, this.y2);
        this.path.lineTo(this.x3, this.y3);
    }

    /**
     * 点围绕中心点旋转后的坐标
     * @param p
     * @param angle
     * @return
     */
    public PointF calcNewPoint(PointF p, float angle) {
        // calc arc
        float l = (float) ((angle * Math.PI) / 180);

        //sin/cos value
        float cosv = (float) Math.cos(l);
        float sinv = (float) Math.sin(l);

        // calc new point
        float newX = (float) ((p.x - centerP.x) * cosv - (p.y - centerP.y) * sinv + centerP.x);
        float newY = (float) ((p.x - centerP.x) * sinv + (p.y - centerP.y) * cosv + centerP.y);
        return new PointF((int) newX, (int) newY);
    }

    /**
     * 点是否在三角形内
     *
     * @param P 点
     * @return
     */
    public boolean isInTriangle(PointF P) {
        /*利用叉乘法进行判断,假设P点就是M点*/
        float a = 0, b = 0, c = 0;

        PointF MA = new PointF(P.x - this.x1, P.y - this.y1);
        PointF MB = new PointF(P.x - this.x2, P.y - this.y2);
        PointF MC = new PointF(P.x - this.x3, P.y - this.y3);

        /*向量叉乘*/
        a = MA.x * MB.y - MA.y * MB.x;
        b = MB.x * MC.y - MB.y * MC.x;
        c = MC.x * MA.y - MC.y * MA.x;

        if ((a <= 0 && b <= 0 && c <= 0) ||
                (a > 0 && b > 0 && c > 0))
            return true;
        return false;
    }

    public float getX(int i) {
        if (i == 0) {
            return x1;
        } else if (i == 1) {
            return x2;
        } else if (i == 2) {
            return x3;
        } else {
            return 0;
        }
    }

    public float getY(int i) {
        if (i == 0) {
            return y1;
        } else if (i == 1) {
            return y2;
        } else if (i == 2) {
            return y3;
        } else {
            return 0;
        }
    }

    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public float getX3() {
        return x3;
    }

    public float getY3() {
        return y3;
    }

    public PointF getCenterP() {
        return centerP;
    }


    public String[] getAngleTexts() {
        return angleTexts;
    }

    public String getAngleIndex() {
        int result = 0;
        for (String s : angleTexts) {
            result += s == null ? 0 : 1;
        }
        return String.valueOf(result + 1);
    }

    public boolean isCanCut() {
        return canCut;
    }

    public PointF getPointF(int index){
        switch (index){
            case 1:
                return new PointF(x1,y1);
            case 2:
                return new PointF(x2,y2);
            case 3:
                return new PointF(x3,y3);
            default:
                return null;
        }
    }

    public int getCutNumAll() {
        int r = 0;
        for (int i : cutNum) {
            r += i;
        }
        return r;
    }

    public boolean getCanCut(int index) {
        return (cutNum[index-1]==0);
    }

    public void addCutNum(int index) {
//        this.cutNum = this.cutNum +1;
        this.cutNum[index-1] = 1;
    }
}
