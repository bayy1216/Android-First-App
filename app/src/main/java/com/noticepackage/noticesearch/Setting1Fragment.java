package com.noticepackage.noticesearch;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.noticesearch.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class Setting1Fragment extends Fragment{

    Switch switch1;
    Switch switch2;
    Switch switch3;
    int code;

    Button btnBack;

    MainActivity mainActivity;
    @Override
    public void onAttach(@NonNull Context context) {
        mainActivity = (MainActivity) getActivity();
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_setting1, container,false);

        switch1=(Switch) rootview.findViewById(R.id.switch1);
        switch2=(Switch) rootview.findViewById(R.id.switch2);
        switch3=(Switch) rootview.findViewById(R.id.switch3);
        btnBack=(Button) rootview.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.changeFrag(4);
            }
        });



        try {
            FileInputStream fis = getContext().openFileInput("permission_save.tmp");
            DataInputStream dis = new DataInputStream(fis);
            code = dis.readInt();
            Log.d("test", "저장된code" + code);
            dis.close();
        }catch(Exception e){
            Log.d("test", "실패");
        }


        if((code/4)%2==1) switch3.setChecked(true);
        if((code/2)%2==1) switch2.setChecked(true);
        if(code%2==1) switch1.setChecked(true);

        switch1.setOnCheckedChangeListener(new MyLisener(0));
        switch2.setOnCheckedChangeListener(new MyLisener(1));
        switch3.setOnCheckedChangeListener(new MyLisener(2));
        return rootview;
    }

    class MyLisener implements Switch.OnCheckedChangeListener{
        int index;
        public MyLisener(int index){
            this.index=index;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mainActivity.delFrag(3);

            if (isChecked) code += Math.pow(2, index);
            else code -= Math.pow(2, index);

            try {
                Log.d("test", "Code1을 저장해보자"+code);
                FileOutputStream fos = getContext().openFileOutput("permission_save.tmp", Context.MODE_PRIVATE);
                Log.d("test", "파일열었다");
                DataOutputStream dos = new DataOutputStream(fos);
                dos.writeInt(code);
                dos.close();
            }catch (Exception e) {
                Log.d("test","저장 실패");
                e.printStackTrace(); }
            }


    }




}