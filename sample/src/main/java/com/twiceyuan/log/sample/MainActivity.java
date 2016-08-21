package com.twiceyuan.log.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.twiceyuan.log.L;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void printLog(View view) {
        L.i("一条普通的日志，类型 INFO");
        L.v("一条普通的日志，类型 VERBOSE");
        L.w("一条普通的日志，类型 WARNING");
        L.e("一条普通的日志，类型 ERROR");
        L.wtf("一条普通的日志，类型 WTF");
    }

    public void globalShowCodePosition(View view) {
        boolean isShow = L.getDefaultLevelLogger().getShowPath();
        if (isShow) {
            L.getDefaultLevelLogger().setShowPath(false);
            L.i("已关闭全局显示代码位置");
        } else {
            L.getDefaultLevelLogger().setShowPath(true);
            L.i("已打开全局显示代码位置");
        }
    }

    public void tempShowCodePosition(View view) {
        new Runnable() {
            @Override
            public void run() {
                L.showPath().i("临时显示代码位置的日志");
            }
        }.run();
    }

    public void printJSONLog(View view) {
        L.json("{\"status\": 200,\"data\": {\"key\": \"value\"}}");
    }

    public void printTagLog(View view) {
        L.tag("~Logger~").i("自定义 Tag 的日志");
    }

    public void logTurnOn(View view) {
        L.setGlobalToggle(true);
    }

    public void logTurnOff(View view) {
        L.setGlobalToggle(false);
    }

    public void customLogger(View view) {
        L.Logger logger = L.createLogger()
                .setShowPath(true)
                .setTag("CustomLog");
        logger.i("自定义 Logger");
        logger.e("自定义 Logger");
    }
}
