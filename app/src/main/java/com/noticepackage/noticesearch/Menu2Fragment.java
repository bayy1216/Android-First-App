package com.noticepackage.noticesearch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
    public String starfile ="list_star.tmp";
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



        try {
            FileInputStream fis = getContext().openFileInput(starfile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            starlist = (ArrayList<SearchData>) ois.readObject();
            ois.close();
            Log.d("test","star리스트 불러옴");
            adapter.addDatas(starlist);
        }catch(Exception e){
            Log.d("test", "프래그2에서 저장 파일 열기 실패");
        }

        recyclerView.setAdapter(adapter);


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
                SearchData newstardata = adapter.getData(position);

                Log.d("test",newstardata.getTitle());

                starlist.remove(position);
                try {
                    Log.d("test", "저장 시도");
                    FileOutputStream fos = getContext().openFileOutput(starfile, Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);

                    oos.writeObject(starlist);
                    Log.d("test", "저장완료");

                    oos.close();
                }
                catch (Exception e) {
                    Log.d("test","저장 실패");
                    e.printStackTrace();
                }
                recyclerView.setAdapter(adapter);
            }
        });









        return rootview;
    }
/*
    public void searchFilter(DataAdapeter targetAdapter, String filterTitle){
        //filteredList = new DataAdapeter(getContext().getApplicationContext());
        for (int i=0; i<targetAdapter.getItemCount();i++){
            if(targetAdapter.getData(i).getTitle().contains(filterTitle)){
                filteredList.addData(new SearchData(targetAdapter.getData(i).getTitle(),
                        targetAdapter.getData(i).getTime(),
                        targetAdapter.getData(i).getSite(),
                        targetAdapter.getData(i).getViews(),
                        targetAdapter.getData(i).getSiteaddress()));

                recyclerView.setAdapter(filteredList);

                System.out.println(i);
            }
        }
    }

*/


}
