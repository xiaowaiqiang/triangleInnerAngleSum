package com.example.triangleinneranglesum;


import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.List;

public class Polygon {
    private Path path;
    private PointF[] pointFS;
    private int color;

    public Polygon(int color, PointF...pointFS) {
        this.path = new Path();
        this.pointFS = pointFS;
        this.color = color;
        for (int i = 0; i < pointFS.length; i++) {
            if (i==0){
                this.path.moveTo(pointFS[i].x,pointFS[i].y);
            }else {
                this.path.lineTo(pointFS[i].x,pointFS[i].y);
            }
        }
    }

    public Path getPath() {
        return path;
    }

    public int getColor() {
        return color;
    }
}
