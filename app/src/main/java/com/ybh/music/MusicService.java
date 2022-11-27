package com.ybh.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {

    private MediaPlayer player;

    //[2]把我们定义的中间人对象 返回
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }
    //服务第一次开启的是调用
    @Override
    public void onCreate() {

        //[1]初始化mediaplayer
        player = new MediaPlayer();

        super.onCreate();
    }


    //当服务销毁的时候调用
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //设置播放音乐指定位置的方法
    public void seekToPosition(int position){
        player.seekTo(position);
    }



    //专门用来播放音乐的
    public void playMusic(){
        System.out.println("音乐播放了");

        //[2]设置要播放的资源  path 可以是本地也可是网络路径
        try {
            player.reset();

            player.setDataSource("/storage/emulated/0/Music/azxyq.mp3");

            //[3]准备播放
            player.prepare();

            //[4]开始播放
            player.start();

            //[5]更新进度条
            updateSeekBar();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //更新进度条的方法
    private void updateSeekBar() {
        //[1]获取当前歌曲总时长
        final int duration = player.getDuration();
        //[2]一秒钟获取一次当前进度
        final Timer timer = new Timer();
        final TimerTask task = new TimerTask() {

            @Override
            public void run() {
                //[3]获取当前歌曲的进度
                int currentPosition = player.getCurrentPosition();

                //[4]创建message对象
                Message msg = Message.obtain();
                //[5]使用msg携带多个数据
                Bundle bundle = new Bundle();
                bundle.putInt("duration", duration);
                bundle.putInt("currentPosition", currentPosition);
                msg.setData(bundle);
                //发送消息 MainActivity的handlemessage方法会执行
                MainActivity.handler.sendMessage(msg);

            }
        };
        //300毫秒后 每隔1秒钟获取一次当前歌曲的进度
        timer.schedule(task, 300, 1000);
        //[3]当歌曲播放完成的时候 把timer 和task 取消
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            //当歌曲播放完成的回调
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.out.println("歌曲播放完成了 ");

                timer.cancel();
                task.cancel();

            }
        });


    }
    //音乐暂停了
    public void pauseMusic(){
        System.out.println("音乐暂停了");
        //暂停
        player.pause();

    }

    //音乐继续播放的方法
    public void  rePlayMusic(){
        System.out.println("音乐继续播放了");

        player.start();

    }

    //[1]定义一个中间人对象(IBinder)
    private class MyBinder extends Binder implements Iservice{

        //调用播放音乐的方法
        @Override
        public void callPlayMusic() {

            playMusic();
        }

        //调用暂停音乐的方法
        @Override
        public void callPauseMusic() {

            pauseMusic();
        }

        //调用继续播放的方法
        @Override
        public void callrePlayMusic() {

            rePlayMusic();
        }

        //调用设置播放指定位置的方法
        @Override
        public void callSeekToPosition(int position) {

            seekToPosition(position);
        }

    }
}
