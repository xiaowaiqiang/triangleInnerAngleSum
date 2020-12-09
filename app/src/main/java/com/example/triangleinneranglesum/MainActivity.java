package com.example.triangleinneranglesum;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvT1, mIvT2, mIvT3, mIvR1, mIvR2,mIvNext;
    private CanvasView canvasView;
    private RadioGroup radioGroup;
    private RadioButton mRbCut,mRbRotate,mRbMove;
    private String curType;
    private int curNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvT1 = findViewById(R.id.iv_t1);
        mIvT2 = findViewById(R.id.iv_t2);
        mIvT3 = findViewById(R.id.iv_t3);
        mIvR1 = findViewById(R.id.iv_r1);
        mIvR2 = findViewById(R.id.iv_r2);
        canvasView = findViewById(R.id.cv);
        mRbCut = findViewById(R.id.rb_cut);
        mRbMove = findViewById(R.id.rb_move);
        mRbRotate = findViewById(R.id.rb_rotate);
        mIvNext = findViewById(R.id.iv_next);
        mIvT1.setOnClickListener(this);
        mIvT2.setOnClickListener(this);
        mIvT3.setOnClickListener(this);
        mIvR1.setOnClickListener(this);
        mIvR2.setOnClickListener(this);
        radioGroup = findViewById(R.id.radio_group);
        radioGroup.check(R.id.rb_move);
        curType = "";
        curNum = 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_t1:
                canvasView.addT1();
                break;
            case R.id.iv_t2:
                canvasView.addT2();
                break;
            case R.id.iv_t3:
                if (curType == "" || curType.equals("t3")) {
                    curType = "t3";
                    if (curNum < 6) {
                        canvasView.addT3();
                        curNum++;
                    }
                }
                break;
            case R.id.iv_r1:
                if (curType == "") {
                    canvasView.addR1();
                    curType = "r1";
                }
                break;
            case R.id.iv_r2:
                if (curType == "") {
                    canvasView.addR2();
                    curType = "r2";
                }
                break;
        }
    }

    public void rotate(View view) {
        canvasView.setState(CanvasView.STATE_ROTATE);
    }

    public void move(View view) {
        canvasView.setState(CanvasView.STATE_MOVE);
    }

    public void cut(View view) {
        canvasView.setState(CanvasView.STATE_CUT);
        canvasView.setOnlyOne();
    }

    public void angle(View view) {
        canvasView.setState(CanvasView.STATE_ANGLE);
    }

    public void clear(View view) {
        canvasView.clearAll();
        curType = "";
        curNum = 0;
        mIvNext.setVisibility(View.GONE);
        mRbCut.setVisibility(View.GONE);
        mRbMove.setVisibility(View.GONE);
        mRbRotate.setVisibility(View.GONE);
        mIvNext.setVisibility(View.GONE);
        canvasView.setState(CanvasView.STATE_MOVE);
        radioGroup.check(R.id.rb_move);
    }

    public void next(View view) {
        mRbCut.setVisibility(View.VISIBLE);
        mRbMove.setVisibility(View.VISIBLE);
        mRbRotate.setVisibility(View.VISIBLE);
    }

    public void showNext(){
        mIvNext.setVisibility(View.VISIBLE);
    }
}
