package com.noticepackage.noticesearch;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.noticesearch.R;


public class AlarmIntentService extends IntentService {
    public final int NOTIFICATION_ID = 1001;

    public AlarmIntentService() {
        super("AlarmIntentService");
    }
    private void createNotificationChannel() {
        // Android8.0 이상인지 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 채널에 필요한 정보 제공
            String channelName="MyChannel1";

            // 중요도 설정, Android7.1 이하는 다른 방식으로 지원한다.(위에서 설명)
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // 채널 생성
            NotificationChannel channel = new NotificationChannel("channel_id", channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("test", "알람인텐트 시작한다!!!!!!!!!!!!");
        createNotificationChannel();


        new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_MUTABLE);

        Bitmap mLargeIconForNoti = BitmapFactory.decodeResource(this.getResources(),R.drawable.knu);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.knu)
                .setLargeIcon(mLargeIconForNoti)
                .setContentTitle("키워드 게시물 등록")
                .setContentText("새로운 글이 등록되었습니다.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }



}