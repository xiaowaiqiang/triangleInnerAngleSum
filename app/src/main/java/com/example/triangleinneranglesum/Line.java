package com.example.triangleinneranglesum;

import android.graphics.PointF;

class Line {
    // 两点决定一条直线
    PointF point1, point2;
    // 是否固定长度（point1、point2分别为起点和终点）
    boolean isDistanceFixed;

    // y = gradient * x + constC;
    // 斜率
    float gradient;
    // 常量
    float constC;

    /**
     * 已知两个点
     *
     * @param isDistanceFixed true: 线段；false: 直线
     */
    public Line(PointF point1, PointF point2, boolean isDistanceFixed) {
        this.point1 = point1;
        this.point2 = point2;
        this.isDistanceFixed = isDistanceFixed;

        this.gradient = (point1.y - point2.y) / (point1.x - point2.x);
        this.constC = point1.y - this.gradient * point1.x;
    }

    /**
     * 线段
     */
    public Line(PointF point1, PointF point2) {
        this.point1 = point1;
        this.point2 = point2;
        this.isDistanceFixed = true;

        this.gradient = (point1.y - point2.y) / (point1.x - point2.x);
        this.constC = point1.y - this.gradient * point1.x;
    }

    /**
     * 已知斜率和常量
     */
    public Line(float gradient, float constC) {
        this.gradient = gradient;
        this.constC = constC;


        this.isDistanceFixed = false;
        this.point1 = new PointF(0, constC);
        this.point2 = new PointF(10, 10 * gradient + constC);
    }

    /**
     * 已知一点和斜率
     */
    public Line(PointF point, float gradient) {
        this.point1 = point;
        this.gradient = gradient;

        this.isDistanceFixed = false;
        this.constC = point1.y - this.gradient * point1.x;
        float x = this.point1.x + 10;
        this.point2 = new PointF(x, this.gradient * x + this.constC);
    }
}
