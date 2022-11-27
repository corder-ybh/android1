package com.ybh.music;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import java.sql.BatchUpdateException;

public class MainActivity extends AppCompatActivity {
    private Iservice iservice; // 这个就是我们定义的中间人对象
    private MyConn conn;
    private static SeekBar sbar;
    private boolean isRefuse;

    public  static Handler handler = new Handler(){
        //当 接收到消息该方法执行
        public void handleMessage(android.os.Message msg) {
            //[1]获取msg 携带的数据
            Bundle data = msg.getData();
            //[2]获取当前进度和总进度
            int duration = data.getInt("duration");
            int currentPosition = data.getInt("currentPosition");

            //[3]设置seekbar的最大进度和当前进度
            sbar.setMax(duration);  //设置进度条的最大值
            sbar.setProgress(currentPosition);//设置当前进度



        };
    };

    private class MyConn implements ServiceConnection {

        // 当连接成功时候调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取我们定义的中间人对象
            iservice = (Iservice) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    }

    // 带回授权结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 检查是否有权限
            if (Environment.isExternalStorageManager()) {
                isRefuse = false;
                // 授权成功
            } else {
                isRefuse = true;
                // 授权失败
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R  && !isRefuse) {// android 11  且 不是已经被拒绝
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1024);
            }
        }

        String[] PERMISSIONS = {"Manifest.permission.WRITE_EXTERNAL_STORAGE","Manifest.permission.MANAGE_EXTERNAL_STORAGE"};
        int PERMISSION_CODE = 123;
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                PERMISSIONS[0]) != PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_CODE);
        }

        sbar = (SeekBar)findViewById(R.id.seekBar1);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);

        conn = new MyConn();

        bindService(intent, conn, BIND_AUTO_CREATE);

        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    //设置播放的位置
                    iservice.callSeekToPosition(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void button_play_click(View v) {
        // 调用播放音乐的方法
        iservice.callPlayMusic();
    }

    public void button_stop_click(View v) {
        // 调用暂停音乐的方法
        iservice.callPauseMusic();
    }

    public void button_continue_click(View v) {
        // 调用继续播放
        iservice.callrePlayMusic();
    }

    // 当Activity销毁的时候调用
    @Override
    protected void onDestroy() {
        // 在Activity销毁的时候 取消绑定服务
        unbindService(conn);

        super.onDestroy();
    }
}