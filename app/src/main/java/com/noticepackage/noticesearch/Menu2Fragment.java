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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class Menu2Fragment extends Fragment {
    RecyclerView recyclerView;
    DataAdapeter adapeter, filteredList;

    Handler handler3= new Handler();

    Handler timerhandler = new Handler();
    TextView textView;
    String dataTitle;
    String dataTime;
    ArrayList<SearchData> firstlist;



    private String URL = "https://www.ppomppu.co.kr/zboard/zboard.php?id=money";
    private String CCs = ".list_title";
    final Bundle bundle = new Bundle();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_menu2, container,false);

        EditText inputTxt = (EditText)rootview.findViewById(R.id.inputText);

        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerViewFrag2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        firstlist= new ArrayList<>();

        adapeter = new DataAdapeter(firstlist,getContext().getApplicationContext());
        //filteredList = new DataAdapeter(getContext().getApplicationContext());




        new Thread(){
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect(URL).get();
                    Elements elements = doc.select(".list0");
                    /*Elements contentATag= doc.select(".list1 a");
                    String viewPageUrl = contentATag.first().attr("abs:href");
                    ATag = contentATag.text();
                    System.out.println("contnetTAg:"+ATag);

                    System.out.println("view:"+viewPageUrl);*/
                    for (Element elem : elements){

                        Element elementTitle = elem.select(".list_title").first();
                        Element elementSite = elem.select("td.list_vspace").get(2);
                        String viewPageUrl = elementSite.select("a").attr("abs:href");
                        Element elementTime = elem.select("td.list_vspace").get(3);
                        dataTime = elementTime.text();
                        dataTitle=elementTitle.text();

                        handler3.post(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("###############"+dataTitle);
                                adapeter.addData(new SearchData(dataTitle,dataTime,"뽐뿌","테스트조회수1",viewPageUrl,9));
                            }
                        });
                        try{
                            Thread.sleep(1);
                        }catch (Exception e){}
                    }
                    elements=doc.select(".list1");
                    for (Element elem : elements){

                        Element elementTitle = elem.select(".list_title").first();
                        Element elementSite = elem.select("td.list_vspace").get(2);
                        String viewPageUrl = elementSite.select("a").attr("abs:href");
                        Element elementTime = elem.select("td.list_vspace").get(3);
                        dataTime = elementTime.text();
                        dataTitle=elementTitle.text();

                        handler3.post(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("###############"+dataTitle);
                                adapeter.addData(new SearchData(dataTitle,dataTime,"테스트사이트1","테스트조회수1",viewPageUrl,9));
                            }
                        });
                        try{
                            Thread.sleep(1);
                        }catch (Exception e){}
                    }

                    handler3.post(new Runnable() {
                        @Override
                        public void run() {
                            Collections.sort(firstlist,SearchData.mydata);
                            recyclerView.setAdapter(adapeter);
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        recyclerView.setAdapter(adapeter);




        adapeter.setOnItemClickListener(new DataAdapeter.OnItemClickListener() {
            @Override
            public void onItemClick(DataAdapeter.ViewHolder holder, View view, int position) {
                SearchData data = adapeter.getData(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.siteaddress));
                startActivity(intent);

            }
        });

        /*outputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt= inputTxt.getText().toString();
                searchFilter(adapeter, txt);

            }
        });*/











        Timer timer = new Timer();
        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                // 반복실행할 구문
                System.out.println("타이머작동중!");
                try {
                    Document doc = Jsoup.connect(URL).get();
                    Elements elements = doc.select(".list0");
                    Element elementTitle = elements.select(".list_title").first();
                    String compTitle = elementTitle.text();
                    timerhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("타이틀 뽑아옴!"+compTitle+"\n"+adapeter.getData(0).getTitle());
                            if (!compTitle.equals( adapeter.getData(0).getTitle()) ){
                                System.out.println("다른글 발생"+compTitle);
                                showNoti1(compTitle);
                            }

                        }
                    });
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };


        Button alarmbtn = (Button) rootview.findViewById(R.id.alarmBtn);
        alarmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.schedule(TT, 0, 3000); //Timer 실행
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

    private static String CHANNEL_ID = "channel1";
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
    }

}
