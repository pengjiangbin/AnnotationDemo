package com.jiang.annotaiondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jiang.apt.Code;
import com.jiang.apt.Print;

public class SecondActivity extends AppCompatActivity {

    @Override
    @Print
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        process();
    }

    @Code(author = "pengjiangbin", date = "20180809")
    private void process() {
//        new SecondActivityAutogenerate().message();
    }
}
