package com.noticepackage.noticesearch;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noticesearch.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Menu1Fragment extends Fragment {

    private AdView mAdView;
    EditText textAlarm;
    Button btnAlarm;


    RecyclerView recyclerView;
    KeyWordAdapter keyWordAdapter;
    ArrayList<KeyWord> keylist;

    long now =System.currentTimeMillis();
    Date nowDate=new Date(now);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String today=sdf.format(nowDate);

    SimpleDateFormat sdfNow = new SimpleDateFormat("MM월dd일-hh시mm분ss초");
    String todayNow=sdfNow.format(nowDate);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_menu1, container,false);
        btnAlarm = (Button) rootview.findViewById(R.id.btn_alarm_start);
        textAlarm = (EditText) rootview.findViewById(R.id.alarm_keyword);

        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerView_menu1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        keylist = new ArrayList<>();
        keyWordAdapter = new KeyWordAdapter(keylist,getContext().getApplicationContext());

        try {
            FileInputStream fis = getContext().openFileInput("keyWord_List.tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            keylist = (ArrayList<KeyWord>) ois.readObject();
            keyWordAdapter.setList(keylist);
            ois.close();
        }catch(Exception e){
            Log.d("test", "keyList 열기 실패");
        }
        recyclerView.setAdapter(keyWordAdapter);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = (AdView) rootview.findViewById(R.id.adView_frag1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);











        Intent xx= new Intent(getActivity(), MyReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, xx, PendingIntent.FLAG_MUTABLE);

        if (pendingIntent != null && alarmManager != null && keylist.isEmpty()) {//key모두 제거시 알람매니저 취소
            alarmManager.cancel(pendingIntent);
            Log.d("test", "alarmManger 취소하였음");
        }

        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyWordAdapter.addKeyWord(new KeyWord(textAlarm.getText().toString(),today));
                try {
                    FileOutputStream fos = getContext().openFileOutput("keyWord_List.tmp", Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(keylist);

                    Log.d("test", "keyListFILE 저장완료");
                    oos.close();
                }catch(Exception e){
                    Log.d("test", "keyListFILE 저장실패");
                }
                recyclerView.setAdapter(keyWordAdapter);
                if(alarmManager!=null) {//알람메니저 시작하게함 myreceiver로
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis(),
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                    Toast.makeText(getContext(), "Alarm start,not implemented", Toast.LENGTH_SHORT).show();
                    Log.d("test","alarmManger호출 currTime: "+todayNow);
                }
                else{
                    Log.d("test","null이라는데요");
                }
            }
        });

        keyWordAdapter.setOnItemClickListener(new KeyWordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(KeyWordAdapter.ViewHolder holder, View view, int position) {
                keyWordAdapter.removeData(position);
                recyclerView.setAdapter(keyWordAdapter);
                try {
                    FileOutputStream fos = getContext().openFileOutput("keyWord_List.tmp", Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(keylist);

                    Log.d("test", "keyListFILE 저장완료");
                    oos.close();
                }catch(Exception e){
                    Log.d("test", "keyListFILE 저장실패");
                }

            }
        });


        return rootview;
    }







    /*class NetworkThread extends Thread{
        @Override
        public void run() {
            try{
                //서버에접속
                final Socket socket = new Socket("172.16.102.74",55555); //172.16.102.74

                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                DataInputStream dis = new DataInputStream(is);
                DataOutputStream dos = new DataOutputStream(os);

                final int data1= dis.readInt();
                final String str1=dis.readUTF();

                dos.writeInt(200);
                dos.writeUTF("클라이언트에서 보낸 문자");





                text1.append("data1 :" + data1 + "str1 : "+str1);

                socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }*/

    /*private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";
    public void showNoti1(String input){
        NotificationManager manger = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manger.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            ));
            builder = new NotificationCompat.Builder(getActivity(),CHANNEL_ID);
        }

        builder.setContentTitle("간단알림");
        builder.setContentText("새로운 글입니다 : "+input);
        builder.setSmallIcon(android.R.drawable.ic_menu_view);

        Notification noti = builder.build();
        manger.notify(1,noti);
    }*/

}
