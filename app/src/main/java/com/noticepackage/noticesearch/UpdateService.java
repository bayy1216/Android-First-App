package com.noticepackage.noticesearch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class UpdateService extends Service {
    public UpdateService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    //서비스가 가동될떄 호출되는 메서드
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test","서비스 가동");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("test1","Service",NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "test1");
            builder.setSmallIcon(android.R.drawable.ic_menu_search);
            builder.setContentTitle("서비스 가동");
            builder.setContentText("서비스가 가동중이다");
            builder.setAutoCancel(true);

            Notification notification = builder.build();
            //현재 노티피케이션 메시즈를 포그라운드 서비스의 메시지로 등록한다
            startForeground(10,notification);


        }


        GetSiteTherad getSiteTherad = new GetSiteTherad();
        getSiteTherad.start();

        return super.onStartCommand(intent, flags, startId);
    }


    //서비스자 중지될때 호출
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("test","서비스 중지");
    }


    class GetSiteTherad extends Thread{
        @Override
        public void run() {
            for (int i=0;i<10;i++){
                SystemClock.sleep(1000);
                long time = System.currentTimeMillis();
                Log.d("test","Foreground Service Running .... + "+time);
            }
            //작업완료 되면 노티피케이션 없어지게함
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                stopForeground(STOP_FOREGROUND_REMOVE);
                NotificationManager manager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(10);
            }
        }
    }


}