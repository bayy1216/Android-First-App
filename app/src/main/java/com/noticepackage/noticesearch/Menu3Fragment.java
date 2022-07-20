package com.noticepackage.noticesearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Menu3Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    DataAdapeter adapter;
    ArrayList<SearchData> mlist;

    AddDataHandler addDataHandler;
    SearchSiteTherad searchSiteTherad;


    String dataTitle; String dataTime; String dataView; String viewPageUrl;

    private final String[] SWURL={"https://swedu.knu.ac.kr/05_sub/01_sub.html",
            "https://swedu.knu.ac.kr/05_sub/02_sub.html",
            "https://swedu.knu.ac.kr/05_sub/10_sub.html",
            "https://swedu.knu.ac.kr/05_sub/03_sub.html",
            "https://swedu.knu.ac.kr/05_sub/09_sub.html",
            "https://swedu.knu.ac.kr/02_sub/04_sub.html"};
    private final String[] SWname={"sw중심대학지원산업","지역선도대학산업","경북디지털역량교육","소프트웨어교육원새소식","마일리지게시판","sw기초교육"};

    private final String[] KNUURL = {"https://www.knu.ac.kr/wbbs/wbbs/bbs/btin/list.action?bbs_cde=1&menu_idx=67",
                            "https://www.knu.ac.kr/wbbs/wbbs/bbs/btin/stdList.action?menu_idx=42"};
    private final String[] KNUname={"경북대","경북대학사공지"};

    private final String[] CSEname={"컴학","컴학-학사","컴학-심컴","컴학-학부인재"};
    private final String[] CSEURL ={"http://computer.knu.ac.kr/06_sub/02_sub.html",
            "http://computer.knu.ac.kr/06_sub/02_sub_2.html",
            "http://computer.knu.ac.kr/06_sub/02_sub_3.html","http://computer.knu.ac.kr/06_sub/04_sub.html"};


    private int tempvalue;
    private int tempaAdder = 100/(12);
    public String dateControl="20220301";

    private boolean[] siteControl= {false, false, false};



    ProgressBar tempProgress;


    public String savelist ="list_save.tmp";

    SwipeRefreshLayout swipeRefreshLayout;

    long now =System.currentTimeMillis();
    Date nowDate=new Date(now);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년-MM월-dd일/hh시");
    String today=sdf.format(nowDate);

    int siteCode=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_menu3   , container,false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerView_menu3);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        mlist = new ArrayList<>();
        adapter = new DataAdapeter(mlist,getContext().getApplicationContext());

        Collections.sort(mlist,SearchData.mydata);
        recyclerView.setAdapter(adapter);

        tempProgress = (ProgressBar) rootview.findViewById(R.id.progressBar3);
        addDataHandler = new AddDataHandler();
        searchSiteTherad = new SearchSiteTherad();


        swipeRefreshLayout = rootview.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);


        try {
            SearchSiteImproved s=new SearchSiteImproved();
            s.start();
        }catch(Exception e){
            Log.d("tt","html에서 실패");
            loadCurrentGame();
        }

        try {
            FileInputStream fis1 = getContext().openFileInput("permission_save.tmp");
            DataInputStream dis1 = new DataInputStream(fis1);
            siteCode=dis1.readInt();
            Log.d("test","code"+siteCode);
            dis1.close();

            if(siteCode%2==1) siteControl[0]=true;
            if((siteCode/2)%2==1) siteControl[1]=true;
            if((siteCode/4)%2==1) siteControl[2]=true;

            for(int x=0;x<siteControl.length;x++){
                if(siteControl[x]){
                    adapter.removeSite(x);
                    Log.d("test",x+"를 삭제해야함");
                    recyclerView.setAdapter(adapter);
                }
            }



        } catch (Exception e) {
            Log.d("test","사이트code에서 오픈 실패");
        }







        adapter.setOnItemClickListener(new DataAdapeter.OnItemClickListener() {
            @Override
            public void onItemClick(DataAdapeter.ViewHolder holder, View view, int position) {

                SearchData data = adapter.getData(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.siteaddress));
                startActivity(intent);
            }
        });

        return rootview;
    }

    class SearchSiteImproved extends Thread {
        @Override
        public void run() {
            Document doc;
            try {
                doc = Jsoup.connect("http://13.209.154.237:58740/").get();
                Elements elements = doc.select("tbody tr");

                for (Element elem : elements) {
                    dataTitle = elem.select("td.title").text();
                    viewPageUrl = elem.select("td.href").text();
                    dataTime = elem.select("td.date").text();
                    dataView = elem.select("td.hit").text();
                    String site=elem.select("td.name").text();


                    SearchData newAddData = new SearchData(dataTitle, dataTime, site, "조회수:" + dataView, viewPageUrl, 0);
                    Message msg = new Message();
                    msg.what=1;
                    msg.obj=newAddData;
                    addDataHandler.sendMessage(msg);


                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                saveCurrentGame();
                addDataHandler.sendEmptyMessage(2);
                Log.d("test","html에서 쓰레드 종료!!!");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    class SearchSiteTherad extends Thread{
        @Override
        public void run() {
            Document doc;
            try {
                tempvalue = 0;
                int x;
                for (x = 0; x < SWURL.length; x++) {
                    tempvalue += tempaAdder;
                    addDataHandler.sendEmptyMessage(0);

                    if(siteControl[0]) continue;//설정에서 체크되있으면 사이트안받기

                    doc = Jsoup.connect(SWURL[x]).get();
                    Elements elements = doc.select("tbody tr");

                    for (Element elem : elements) {
                        dataTitle = elem.select("td.l").first().text();
                        viewPageUrl = elem.select("a").attr("abs:href");
                        dataTime = elem.select("td").get(4).text();
                        dataView = elem.select("td").get(5).text();
                        String ss = dataTime.replaceAll("[^\\d]", "");
                        if (Integer.parseInt(ss) < Integer.parseInt(dateControl)) {
                            continue;
                        }

                        SearchData newAddData= new SearchData(dataTitle, dataTime, SWname[x], "조회수:" + dataView, viewPageUrl,0);
                        Message msg = new Message();
                        msg.what=1;
                        msg.obj=newAddData;
                        addDataHandler.sendMessage(msg);

                        try {
                            Thread.sleep(1);
                        }
                        catch (Exception e) {e.printStackTrace();
                        }
                    }
                }

                for (x=0;x<CSEURL.length;x++) {
                    tempvalue += tempaAdder;
                    addDataHandler.sendEmptyMessage(0);

                    if(siteControl[1]) continue;//설정에서 체크되있으면 사이트안받기
                    doc = Jsoup.connect(CSEURL[x]).get();
                    Elements cseElements = doc.select("tbody tr");


                    for (Element elem : cseElements) {
                        dataTitle = elem.select("a").first().ownText();
                        viewPageUrl = elem.select("a").attr("abs:href");

                        dataTime = elem.select("td.bbs_date").first().text();
                        dataView = elem.select("td.bbs_hit").first().ownText();
                        String ss = dataTime.replaceAll("[^\\d]", "");
                        if (Integer.parseInt(ss) < Integer.parseInt(dateControl)) {
                            continue;
                        }

                        SearchData newAddData= new SearchData(dataTitle, dataTime, CSEname[x], "조회수:" + dataView, viewPageUrl,1);
                        Message msg = new Message();
                        msg.what=1;
                        msg.obj=newAddData;
                        addDataHandler.sendMessage(msg);

                        try {
                            Thread.sleep(1);
                        }
                        catch (Exception e) {e.printStackTrace();
                        }
                    }
                }

                for (x=0;x<KNUURL.length;x++) {
                    tempvalue += tempaAdder;
                    addDataHandler.sendEmptyMessage(0);

                    if(siteControl[2]) continue;//설정에서 체크되있으면 사이트안받기

                    doc = Jsoup.connect(KNUURL[x]).get();
                    Elements knuElements = doc.select("tbody tr");

                    for (Element elem : knuElements) {

                        dataTitle = elem.select("a").first().ownText();
                        if(x==0) viewPageUrl = elem.select("a").attr("abs:href");
                        else{//url이 자바관련 이라서 학사공지탭으로 이동
                            viewPageUrl="https://www.knu.ac.kr/wbbs/wbbs/bbs/btin/stdList.action?menu_idx=42";
                        }
                        dataTime = elem.select("td.date").first().text();
                        dataView = elem.select("td.hit").first().ownText();
                        String ss = dataTime.replaceAll("[^\\d]", "");
                        if (Integer.parseInt(ss) < Integer.parseInt(dateControl)){
                            continue;
                        }

                        SearchData newAddData= new SearchData(dataTitle, dataTime, KNUname[x], "조회수:" + dataView, viewPageUrl,2);
                        Message msg = new Message();
                        msg.what=1;
                        msg.obj=newAddData;
                        addDataHandler.sendMessage(msg);

                        try {
                            Thread.sleep(1);
                        }
                        catch (Exception e) {e.printStackTrace();
                        }
                    }
                }



                saveCurrentGame();
                addDataHandler.sendEmptyMessage(2);

                Log.d("test","쓰레드 종료!!!");

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    class AddDataHandler extends Handler{
        //개발자가  발생시킨 쓰레드에서 화면에 관련된 처리를 하기위해
        //작업을 요청하면 자동으로 호출되는 메서드. 메인쓰레드에서 하게된다.
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 0:
                    tempProgress.setProgress(tempvalue);
                    break;
                case 1:
                    SearchData x = (SearchData) msg.obj;
                    adapter.addData(x);
                    break;
                case 2:
                    tempProgress.setVisibility(View.GONE);
                    //Collections.sort(mlist,SearchData.mydata);
                    //adapter.Deduplication();
                    recyclerView.setAdapter(adapter);
                    break;
            }



        }
    }

    public void saveCurrentGame() {
        try {
            Collections.sort(mlist, SearchData.mydata);
            adapter.Deduplication();//저정하는데 sort하고 writeObjcet하고 충돌해서 임시로 일단 이렇게
            //해봤는데 일단 돌아가긴하는데 왜이런지는 잘 모르겠음(쓰레드에서)화면구성요소 접근한거가 작동이되는거

            Log.d("test", "저장 시도");
            FileOutputStream fos = getContext().openFileOutput(savelist, Context.MODE_PRIVATE);

            // FileOutputStream fos = new FileOutputStream("myfile1.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(mlist);
            Log.d("test", "저장완료");

            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeUTF(today);
            Log.d("test","today-save--"+today);
            oos.close();
            dos.close();
        }
        catch (Exception e) {
            Log.d("test","저장 실패");
            e.printStackTrace(); }
    }


    public void loadCurrentGame() {
        try {

            FileInputStream fis = getContext().openFileInput(savelist);
            //FileInputStream fis = new FileInputStream("t.tmp");
            Log.d("test","이미 있어서 불러오기 시작");
            ObjectInputStream ois = new ObjectInputStream(fis);


            mlist = (ArrayList<SearchData>) ois.readObject();
            Log.d("test","mlist 불러오기 성공");

            DataInputStream dis = new DataInputStream(fis);
            String savedDay = dis.readUTF();
            Log.d("test","마지막 저장된 day : "+savedDay);

            tempProgress.setVisibility(View.GONE);
            adapter.addDatas(mlist);

            if(today.compareTo(savedDay)!=0){//마지막갱신후 1시간 경과시 새로고침
                Log.d("test",today+"!="+savedDay);
                tempProgress.setVisibility(View.VISIBLE);
                searchSiteTherad.start();
            }
            ois.close();
            dis.close();

        } catch (Exception e) {
            Log.d("test","오픈(로드) 실패");
            searchSiteTherad.start();//사이트 가져오기 (업데이트)
        }
    }

    @Override
    public void onRefresh() {
        //새로 고침 코드
        updateLayoutView();

        //새로 고침 완
        swipeRefreshLayout.setRefreshing(false);
    }

    // 당겨서 새로고침 했을 때 뷰 변경 메서드
    public void updateLayoutView(){
        if(searchSiteTherad.isAlive()){
            searchSiteTherad.interrupt();
        }
        searchSiteTherad = new SearchSiteTherad();
        Log.d("test","swap->update");
        tempProgress.setVisibility(View.VISIBLE);
        searchSiteTherad.start();

    }






}
