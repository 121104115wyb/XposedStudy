package com.zxl.xposedstudy;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 参考地址:
 * https://blog.csdn.net/niubitianping/article/details/52571438
 * https://blog.csdn.net/happyghh/article/details/78857585
 * https://www.jianshu.com/p/3adea67bc2d7
 * 最有价值:
 * https://mp.weixin.qq.com/s/MB1hj0GKW3yOE5I6sC66fw
 */
public class MainActivity extends AppCompatActivity {
//    private String myname = "default--myname";
//    private TextView textView;
//    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        textView = findViewById(R.id.textview);
//
//        System.out.println("-------->onCreate");
    }


    public void clickMe(View view) {

        //当前activity启动另外一个app的activity
        Intent intent = new Intent();
        //前提：知道要跳转应用的包名、类名
//        ComponentName componentName = new ComponentName("com.zxl.hookby", "com.zxl.hookby.Main2Activity");
//        intent.setComponent(componentName);

        intent.setClassName("com.zxl.hookby", "com.zxl.hookby.Main2Activity");
        startActivity(intent);




//        if (view.getId() == R.id.mybtn) {
//
//            textView.setText(getTTAd());
//            setData("按钮点击"+ ++count);
//            Toast.makeText(this, myname, Toast.LENGTH_SHORT).show();
//        }
    }


//    public String getTTAd() {
//        return "广告加载成功";
//    }
//
//
//    public void setData(String s) {
//
//        myname = TextUtils.isEmpty(s) ? "我是备注" : s;
//    }


}
