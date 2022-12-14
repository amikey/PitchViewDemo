package com.example.pitchviewdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private LinearLayout ll;
    private RecyclerView rv;
    private PitchLineScrollLayout mPitchLine;

    // RecycleView适配器
    private PitchRecyclerViewAdapter adapter;
    // 滑动动画
    private ScrollAnimat animation;
    // RecyclerView的长度
    private int rvWidth;

    // 模拟音乐时长
    private long musicDuration = 100000;

    // 音高数据
    private List<PitchLineData> correctPitchDataList;

    private List<PitchLineView> correctPitchLineViewList;

    // 实际音高得分显示控件
    private ScoreLineView mScoreLineView;

    private List<Integer> realPitchDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewAndData();
    }

    private void initViewAndData() {
        ll = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        rv = new RecyclerView(this) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                return true;
            }
        };

        mScoreLineView = new ScoreLineView(this);
        ll.addView(mScoreLineView);

//        ll.addView(rv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(manager);
        correctPitchDataList = new ArrayList<>();
        correctPitchLineViewList = new ArrayList<>();
        realPitchDataList = new ArrayList<>();
        initData();
        adapter = new PitchRecyclerViewAdapter(this);
        adapter.setCorrectPitchData(correctPitchDataList);
        rv.setAdapter(adapter);

        // 添加自定义音高线布局
        mPitchLine = new PitchLineScrollLayout(this);
        initPitchLine();
        ll.addView(mPitchLine);

        setContentView(ll);
        startScrollAnimat();

        // 模拟得分操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < realPitchDataList.size(); i ++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mScoreLineView.setScore(realPitchDataList.get(i));
                }
            }
        }).start();
    }

    private void initPitchLine() {
        for(int i = 0; i < correctPitchLineViewList.size() - 1; i++) {
            mPitchLine.addView(correctPitchLineViewList.get(i));
        }
    }

    private void initData() {
        if (correctPitchDataList != null) {
            // 插入模拟数据
            PitchLineData data1 = new PitchLineData();
            data1.lineLength = 1000;
            data1.needSing = false;
            correctPitchDataList.add(data1);

            // PitchLineScrollLayout测试样例
            PitchLineView viewStart = new PitchLineView(this);
            viewStart.setData(data1);
            correctPitchLineViewList.add(viewStart);

            for (int i = 0; i < 30; i++) {

                // PitchLineScrollLayout测试样例
                PitchLineView v = new PitchLineView(this);

                PitchLineData d = new PitchLineData();
                d.heightY = i % 5 * 50;
                d.lineLength = i * 50;
                d.needSing = i % 4 != 0;
                correctPitchDataList.add(d);
                realPitchDataList.add(d.heightY);

                v.setData(d);
                correctPitchLineViewList.add(v);
            }
            PitchLineData data2 = new PitchLineData();
            data2.lineLength = 1000;
            data2.needSing = false;
            correctPitchDataList.add(data2);

            // PitchLineScrollLayout测试样例
            PitchLineView viewEnd = new PitchLineView(this);
            viewEnd.setData(data2);
            correctPitchLineViewList.add(viewEnd);

            rvWidth = 0;
            for (int i = 0; i < correctPitchDataList.size(); i++) {
                rvWidth += correctPitchDataList.get(i).lineLength;
            }
        }
    }

    private void startScrollAnimat() {
        animation = new ScrollAnimat(rv, musicDuration);
        animation.startAnimation(rvWidth);

        // 自定义布局开始滑动
        mPitchLine.post(new Runnable() {
            @Override
            public void run() {
                mPitchLine.startScroll();
            }
        });
    }
}
