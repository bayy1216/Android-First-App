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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class Menu2Fragment extends Fragment {
    RecyclerView recyclerView;
    DataAdapeter adapeter, filteredList;


    ArrayList<SearchData> savelist;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_menu2, container,false);



        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerViewFrag2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        savelist= new ArrayList<>();
        adapeter = new DataAdapeter(savelist,getContext().getApplicationContext());

        try {
            FileInputStream fis = getContext().openFileInput("keysave_List.tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            savelist = (ArrayList<SearchData>) ois.readObject();
            ois.close();
        }catch(Exception e){
            Log.d("test", "프래그2에서 저장 파일 열기 실패");
        }

        Collections.sort(savelist,SearchData.mydata);

        recyclerView.setAdapter(adapeter);




        adapeter.setOnItemClickListener(new DataAdapeter.OnItemClickListener() {
            @Override
            public void onItemClick(DataAdapeter.ViewHolder holder, View view, int position) {
                SearchData data = adapeter.getData(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.siteaddress));
                startActivity(intent);

            }
        });













        return rootview;
    }

    public void searchFilter(DataAdapeter targetAdapter, String filterTitle){
        //filteredList = new DataAdapeter(getContext().getApplicationContext());
        for (int i=0; i<targetAdapter.getItemCount();i++){
            if(targetAdapter.getData(i).getTitle().contains(filterTitle)){
                filteredList.addData(new SearchData(targetAdapter.getData(i).getTitle(),
                        targetAdapter.getData(i).getTime(),
                        targetAdapter.getData(i).getSite(),
                        targetAdapter.getData(i).getViews(),
                        targetAdapter.getData(i).getSiteaddress(),9));

                recyclerView.setAdapter(filteredList);

                System.out.println(i);
            }

        }

    }



}
