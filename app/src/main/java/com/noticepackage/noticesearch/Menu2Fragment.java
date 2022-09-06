package com.noticepackage.noticesearch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.noticesearch.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class Menu2Fragment extends Fragment {
    RecyclerView recyclerView;
    DataAdapeter adapter;


    ArrayList<SearchData> starlist;
    public String SearchDataTable="SearchDataTable.db";
    private AdView mAdViewFrag2;


    MainActivity mainActivity;
    @Override
    public void onAttach(@NonNull Context context) {
        mainActivity = (MainActivity) getActivity();
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_menu2, container,false);


        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerViewFrag2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        starlist= new ArrayList<>();
        adapter = new DataAdapeter(starlist,getContext().getApplicationContext());


        loadCurrentStar();


        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdViewFrag2 = (AdView) rootview.findViewById(R.id.adView_frag2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdViewFrag2.loadAd(adRequest);





        adapter.setOnDataClickListener(new DataAdapeter.OnDataClickListener() {
            @Override
            public void onItemClick(DataAdapeter.ViewHolder holder, View view, int position) {
                SearchData data = adapter.getData(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.siteaddress));
                startActivity(intent);
            }
            @Override
            public void onStarClick(DataAdapeter.ViewHolder holder, View view, int position) {
                mainActivity.delFrag(3);
                SearchData newstardata = adapter.getData(position);
                Log.d("test",newstardata.getTitle());
                String title=newstardata.getTitle();

                starlist.remove(position);
                recyclerView.setAdapter(adapter);

                DBHelper helper = new DBHelper(mainActivity,SearchDataTable);
                SQLiteDatabase db= helper.getWritableDatabase();

                String sql = "update SearchDataTable set star = star+1 where title = ?";

                String[] args={title};
                db.execSQL(sql,args);
                db.close();

            }
        });

        return rootview;
    }

    public void loadCurrentStar() {
        Log.d("test","loadCurrentStar호출");
        DBHelper helper = new DBHelper(mainActivity,SearchDataTable);
        SQLiteDatabase db= helper.getWritableDatabase();


        //"select 컬럼명들 from 테이블명 where 조건절 group by 기준컬럼 having 조건절 order by 컬럼명"
        String sql="select * from SearchDataTable order by time DESC";

        //쿼리실행
        Cursor c =db.rawQuery(sql,null);

        //선택된 로우 끝까지 반복하며 데이터
        while(c.moveToNext()){
            int star_pos=c.getColumnIndex("star");
            int star=c.getInt(star_pos);
            if(star%2==0) continue;
            //가져올 컬럼의 인덱스 번호를 가져옴
            int title_pos=c.getColumnIndex("title");
            int time_pos=c.getColumnIndex("time");
            int site_pos=c.getColumnIndex("site");
            int viewsa_pos=c.getColumnIndex("views");
            int siteaddress_pos=c.getColumnIndex("siteaddress");
            //컬럼 인덱스번호를 통해데이터를 가져옴
            String title=c.getString(title_pos);
            String time=c.getString(time_pos);
            String site=c.getString(site_pos);
            String views=c.getString(viewsa_pos);
            String siteaddress=c.getString(siteaddress_pos);

            SearchData newData= new SearchData(title,time,site,views,siteaddress,R.drawable.star2);
            Log.d("test","star데이터이름="+title);
            starlist.add(newData);
        }
        recyclerView.setAdapter(adapter);
        db.close();
    }






}
