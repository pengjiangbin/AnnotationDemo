package com.jiang.annotaiondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jiang.annotaiondemo.inject.ContentView;
import com.jiang.annotaiondemo.inject.InjectUtil;
import com.jiang.annotaiondemo.inject.ViewInject;
import com.jiang.annotaiondemo.user.AnnotationProcessor;
import com.jiang.annotaiondemo.user.User;
import com.jiang.apt.Code;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    @ViewInject(id = R.id.btn_one, clickable = true)
    Button btnOne;
    @ViewInject(id = R.id.btn_two)
    Button btnTwo;
    @ViewInject(id = R.id.btn_three, clickable = true)
    Button btnThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectUtil.init(this);
        findViewById(R.id.btn_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                AnnotationProcessor.init(user);
                Log.d(TAG, user.toString());
            }
        });


    }



    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: " + v.toString());
    }
}
