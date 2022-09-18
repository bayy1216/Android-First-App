package com.noticepackage.noticesearch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import java.io.ObjectOutputStream;
import java.util.Collections;

public class Menu4Fragment extends Fragment {

    private Button btnSetting;
    MainActivity mainActivity;
    private AdView mAdView;
    Switch switch_Alarm;
    int code;
    @Override
    public void onAttach(@NonNull Context context) {
        mainActivity = (MainActivity) getActivity();
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_menu4, container,false);

        btnSetting = (Button) rootview.findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                bundle.putString("name","프레그먼트4에서 보내는거");//번들에 넘길 값 저장
                mainActivity.fragBtnClick(bundle);
                mainActivity.changeFrag(5);

            }
        });

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = (AdView) rootview.findViewById(R.id.adView_frag4);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);








        try {
            FileInputStream fis = getContext().openFileInput("permission_save.tmp");
            DataInputStream dis = new DataInputStream(fis);
            code = dis.readInt();
            Log.d("test", "저장된code" + code);
            dis.close();
        }catch(Exception e){
            Log.d("test", "실패");
        }

        switch_Alarm =(Switch) rootview.findViewById(R.id.switch4);
        if((code/8)%2==1) switch_Alarm.setChecked(true);

        Intent xx= new Intent(getActivity(), MyReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, xx, PendingIntent.FLAG_MUTABLE);


        switch_Alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    code += Math.pow(2, 3);
                    if(alarmManager!=null) {//알람메니저 시작하게함 myreceiver로
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                System.currentTimeMillis(),
                                AlarmManager.INTERVAL_HOUR, pendingIntent);
                        Toast.makeText(getContext(), "Alarm start", Toast.LENGTH_SHORT).show();
                        Log.d("test","alarmManger호출 ");
                    }
                    else{
                        Log.d("test","null이라는데요");
                    }
                }
                else {
                    code -= Math.pow(2, 3);
                    if (pendingIntent != null && alarmManager != null) {//key모두 제거시 알람매니저 취소
                        alarmManager.cancel(pendingIntent);
                        Log.d("test", "alarmManger 취소하였음");
                    }
                }

                try {
                    Log.d("test", "Code1을 저장해보자"+code);
                    FileOutputStream fos = getContext().openFileOutput("permission_save.tmp", Context.MODE_PRIVATE);
                    DataOutputStream dos = new DataOutputStream(fos);
                    dos.writeInt(code);
                    Log.d("test", "파일열고 code1 저장완료");
                    dos.close();
                }catch (Exception e) {
                    Log.d("test","저장 실패");
                    e.printStackTrace();
                }
            }
        });


        return rootview;
    }



}
