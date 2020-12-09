package com.example.triangleinneranglesum;


import android.graphics.RectF;

public class Rectangle {

    private float l, t, r, b;
    private float x, y;
    private RectF rectF;

    public Rectangle(float l, float t, float r, float b) {
        this.l = l;
        this.t = t;
        this.r = r;
        this.b = b;
        this.rectF = new RectF();
        this.rectF.set(l, t, r, b);
        this.x = this.rectF.centerX();
        this.y = this.rectF.centerY();
    }

    public RectF getRectF() {
        return rectF;
    }

    public void setMove(float moveX, float moveY) {
        float l1 = l + moveX;
        float t1 = t + moveY;
        float r1 = r + moveX;
        float b1 = b + moveY;
        if (l1>0&&t1>0&&b1<720&&r1<1140){
            this.l = l1;
            this.t = t1;
            this.r = r1;
            this.b = b1;
            this.rectF.set(l, t, r, b);
        }
    }

    public boolean isInImage(float x, float y) {
        if (this.rectF.contains(x, y)) {
            return true;
        }
        return false;
    }

    public float getL() {
        return l;
    }

    public float getT() {
        return t;
    }

    public float getR() {
        return r;
    }

    public float getB() {
        return b;
    }
}
