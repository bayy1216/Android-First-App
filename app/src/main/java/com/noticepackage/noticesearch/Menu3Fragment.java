package com.noticepackage.noticesearch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.noticesearch.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class Menu3Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    DataAdapeter adapter;
    ArrayList<SearchData> mlist;
    AddDataHandler addDataHandler;
    GetSiteThread getSiteThread;

    private int tempvalue;
    private int tempaAdder = 100/(12);


    private double tempvalueD;
    private double tempAdderD=100.0/288;


    ProgressBar tempProgress;

    SwipeRefreshLayout swipeRefreshLayout;

    long now =System.currentTimeMillis();
    Date nowDate=new Date(now);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년-MM월-dd일/hh시");
    String today=sdf.format(nowDate);

    int siteCode=0;


    public String recentTimeFile ="recentTime.tmp";
    public String SearchDataTable="SearchDataTable.db";


    MainActivity mainActivity;
    @Override
    public void onAttach(@NonNull Context context) {
        mainActivity = (MainActivity) getActivity();
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_menu3   , container,false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerView_menu3);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);


        tempProgress = (ProgressBar) rootview.findViewById(R.id.progressBar3);
        addDataHandler = new AddDataHandler();

        swipeRefreshLayout = rootview.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);


        getSiteThread = new GetSiteThread(getContext(),addDataHandler);

        initload();

        EditText edText=(EditText)rootview.findViewById(R.id.searchText);
        edText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                searchFilter(edText.getText().toString());
            }
        });



        return rootview;
    }


    class frag3Listener implements DataAdapeter.OnDataClickListener{
        DataAdapeter data_Adapeter;
        MainActivity main_Activity;
        frag3Listener(DataAdapeter adapter,MainActivity mainActivity){
            this.data_Adapeter=adapter;
            this.main_Activity=mainActivity;
        }
        @Override
        public void onItemClick(DataAdapeter.ViewHolder holder, View view, int position) {
            SearchData data = data_Adapeter.getData(position);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.siteaddress));
            startActivity(intent);
        }
        @Override
        public void onStarClick(DataAdapeter.ViewHolder holder, View view, int position) {
            main_Activity.delFrag(2);
            SearchData newstarData = data_Adapeter.getData(position);
            Log.d("test",newstarData.getTitle());

            DBHelper helper = new DBHelper(main_Activity,SearchDataTable);
            SQLiteDatabase db= helper.getWritableDatabase();

            String sql = "update SearchDataTable set star = star+1 where title = ?";
            String[] arg={newstarData.getTitle()};
            db.execSQL(sql,arg);
            db.close();
        }
    }


/*
    class SearchSiteImproved extends Thread {
        @Override
        public void run() {
            Document doc;
            try {
                doc = Jsoup.connect("http://13.209.154.237:58740/").get();
                Elements elements = doc.select("tbody tr");
                tempvalueD = 0;
                for (Element elem : elements) {
                    tempvalueD += tempAdderD;
                    addDataHandler.sendEmptyMessage(3);

                    dataTitle = elem.select("td.title").text();
                    viewPageUrl = elem.select("td.href").text();
                    dataTime = elem.select("td.date").text();
                    dataView = elem.select("td.hit").text();
                    String site=elem.select("td.name").text();

                    SearchData newAddData = new SearchData(dataTitle, dataTime, site, "조회수:" + dataView, viewPageUrl);
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
                addDataHandler.sendEmptyMessage(2);
                Log.d("test","html에서 쓰레드 종료!!!");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
*/
    class AddDataHandler extends Handler{
        //개발자가  발생시킨 쓰레드에서 화면에 관련된 처리를 하기위해
        //작업을 요청하면 자동으로 호출되는 메서드. 메인쓰레드에서 하게된다.
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case 0:
                    tempvalue += tempaAdder;
                    tempProgress.setProgress(tempvalue);
                    break;
                case 1:
                    SearchData x = (SearchData) msg.obj;
                    adapter.addData(x);
                    break;
                case 2:
                    tempProgress.setVisibility(View.GONE);
                    loadCurrentGame();
                    break;
                case 3:
                    tempProgress.setProgress((int)tempvalueD);
                    break;
                case 4:
                    tempvalue=0;
                    break;
            }
        }
    }

    public void initload(){//최근 시간 알아보고 1시간 안지났으면 그대로 오픈하고 아니면 Thread로 검색
        try{
            FileInputStream fis = getContext().openFileInput(recentTimeFile);
            DataInputStream dis = new DataInputStream(fis);
            String savedDay = dis.readUTF();
            Log.d("test","마지막 저장된 day : "+savedDay);
            if(today.compareTo(savedDay)!=0){//마지막갱신후 1시간 경과시 새로고침
                loadCurrentGame();
                Log.d("test",today+"!="+savedDay);
                tempProgress.setVisibility(View.VISIBLE);
                getSiteThread.start();
            }
            else{
                Log.d("test","1시간 안지나서 불러옴");
                loadCurrentGame();
            }
            dis.close();
        }catch(Exception e){
            Log.d("test","파일로 시간확인중 오류발생->파일만든다");
            try{
                tempProgress.setVisibility(View.VISIBLE);
                FileOutputStream fos = getContext().openFileOutput(recentTimeFile, Context.MODE_PRIVATE);
                DataOutputStream dos = new DataOutputStream(fos);
                dos.writeUTF(today);
                Log.d("test","today-save--"+today);
                dos.close();
                getSiteThread.start();
            }catch(Exception ee){
                Log.d("test","파일로 첫설치 날짜 저장중 오류");
            }
        }
    }


    public void loadCurrentGame() {
        Log.d("test","loadCurrentGame호출");
        DBHelper helper = new DBHelper(mainActivity,SearchDataTable);
        SQLiteDatabase db= helper.getWritableDatabase();
        mlist=new ArrayList<>();
        adapter = new DataAdapeter(mlist,getContext().getApplicationContext());
        adapter.setOnDataClickListener(new frag3Listener(adapter,mainActivity));

        try {
            FileInputStream fis = getContext().openFileInput(recentTimeFile);
            DataInputStream dis = new DataInputStream(fis);
            siteCode = dis.readInt();
            dis.close();
        }catch(Exception e){
            Log.d("test", "사이트코드 파일 안열림");
        }

        String exceptSite_3="";
        String exceptSite_1="";
        String exceptSite_2="";
        if((siteCode/4)%2==1) exceptSite_3+="'KNU',";
        if((siteCode/2)%2==1) exceptSite_2+="'CSE',";
        if(siteCode%2==1) exceptSite_1+="'SW',";
        String exceptSite=exceptSite_3+exceptSite_2+exceptSite_1+"'N'";

        //"select 컬럼명들 from 테이블명 where 조건절 group by 기준컬럼 having 조건절 order by 컬럼명"
        String sql="select * from SearchDataTable where siteCode NOT IN ("+exceptSite+")order by time DESC";
        //쿼리실행
        Cursor c =db.rawQuery(sql,null);
        Log.d("test","DB에서 사이트제외 select");
        //선택된 로우 끝까지 반복하며 데이터
        while(c.moveToNext()){
            //가져올 컬럼의 인덱스 번호를 가져옴
            int title_pos=c.getColumnIndex("title");
            int time_pos=c.getColumnIndex("time");
            int site_pos=c.getColumnIndex("site");
            int viewsa_pos=c.getColumnIndex("views");
            int siteaddress_pos=c.getColumnIndex("siteaddress");
            int star_pos=c.getColumnIndex("star");

            //컬럼 인덱스번호를 통해데이터를 가져옴
            String title=c.getString(title_pos);
            String time=c.getString(time_pos);
            String site=c.getString(site_pos);
            String views=c.getString(viewsa_pos);
            String siteaddress=c.getString(siteaddress_pos);
            int star=c.getInt(star_pos);
            SearchData newData = new SearchData(title, time, site, views, siteaddress);
            if(star%2==1) newData.setImageResid(R.drawable.star2);
            if(star==0)//DB에서 star이 0이면 list에0으로 추가
                newData.setStar(0);
            else if(star==-2)
                newData.setStar(-2);

            mlist.add(newData);
        }
        db.execSQL("update SearchDataTable set star = 2 where star <= 0");
        db.close();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onRefresh() {
        updateLayoutView(getSiteThread);//새로 고침 코드
        swipeRefreshLayout.setRefreshing(false);//새로 고침 완
    }

    public void updateLayoutView(GetSiteThread th){// 당겨서 새로고침 했을 때 뷰 변경 메서드
        try{
            if(th.isAlive()){
                th.interrupt();
            }
            th = new GetSiteThread(getContext(),addDataHandler);
            Log.d("test","swap->update");
            tempProgress.setVisibility(View.VISIBLE);
            th.start();
        } catch(Exception e){

        }
    }
    /*
    public void updateLayoutViewImproved(SearchSiteImproved th){
        try{
            if(th.isAlive()){
                th.interrupt();
            }
            th = new SearchSiteImproved();
            Log.d("test","swap->update");
            tempProgress.setVisibility(View.VISIBLE);

            th.start();
        } catch(Exception e){
            updateLayoutView(getSiteThread);
        }
    }*/

    public void searchFilter(String filterTitle){
        Log.d("test","searchFilter호출:"+filterTitle);
        DBHelper helper = new DBHelper(mainActivity,SearchDataTable);
        SQLiteDatabase db= helper.getWritableDatabase();

        String sql="select * from SearchDataTable where title LIKE ('%"+filterTitle+"%')order by time DESC";
        //쿼리실행
        Cursor c =db.rawQuery(sql,null);
        mlist=new ArrayList<>();
        adapter = new DataAdapeter(mlist,getContext().getApplicationContext());
        adapter.setOnDataClickListener(new frag3Listener(adapter,mainActivity));
        Log.d("test","mlist 초기화");
        //선택된 로우 끝까지 반복하며 데이터
        while(c.moveToNext()){
            //가져올 컬럼의 인덱스 번호를 가져옴
            int title_pos=c.getColumnIndex("title");
            int time_pos=c.getColumnIndex("time");
            int site_pos=c.getColumnIndex("site");
            int viewsa_pos=c.getColumnIndex("views");
            int siteaddress_pos=c.getColumnIndex("siteaddress");
            int star_pos=c.getColumnIndex("star");

            //컬럼 인덱스번호를 통해데이터를 가져옴
            String title=c.getString(title_pos);
            String time=c.getString(time_pos);
            String site=c.getString(site_pos);
            String views=c.getString(viewsa_pos);
            String siteaddress=c.getString(siteaddress_pos);
            int star=c.getInt(star_pos);
            SearchData newData = new SearchData(title, time, site, views, siteaddress);
            if(star%2==1) newData.setImageResid(R.drawable.star2);
            Log.d("test",filterTitle+" IN "+title);
            mlist.add(newData);
        }
        db.close();
        recyclerView.setAdapter(adapter);
    }







}
