package com.noticepackage.noticesearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

public class MyReceiver extends BroadcastReceiver {
    private final String[] SWURL={"https://swedu.knu.ac.kr/05_sub/01_sub.html", "https://swedu.knu.ac.kr/05_sub/02_sub.html", "https://swedu.knu.ac.kr/05_sub/10_sub.html", "https://swedu.knu.ac.kr/05_sub/03_sub.html", "https://swedu.knu.ac.kr/05_sub/09_sub.html", "https://swedu.knu.ac.kr/02_sub/04_sub.html"};private final String[] SWname={"sw중심대학지원산업","지역선도대학산업","경북디지털역량교육","소프트웨어교육원새소식","마일리지게시판","sw기초교육"};private final String[] KNUURL = {"https://www.knu.ac.kr/wbbs/wbbs/bbs/btin/list.action?bbs_cde=1&menu_idx=67", "https://www.knu.ac.kr/wbbs/wbbs/bbs/btin/stdList.action?menu_idx=42"};private final String[] CSEname={"컴학","컴학-학사","컴학-심컴"};private final String[] CSEURL ={"http://computer.knu.ac.kr/06_sub/02_sub.html", "http://computer.knu.ac.kr/06_sub/02_sub_2.html", "http://computer.knu.ac.kr/06_sub/02_sub_3.html"};private final String[] KNUname={"경북대","경북대학사공지"};
    String dataTitle; String dataTime; String dataView; String viewPageUrl;
    ArrayList<KeyWord> keylist;
    ArrayList<SearchData> serachdatalist;
    public String dateControl="20220301";
    private boolean[] siteControl= {false, false, false};

    ArrayList<SearchData> savelist;


    long now =System.currentTimeMillis();
    Date nowDate=new Date(now);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년-MM월-dd일/hh시");
    String today=sdf.format(nowDate);

    SimpleDateFormat sdfNow = new SimpleDateFormat("MM월dd일-hh시mm분ss초");
    String todayNow=sdfNow.format(nowDate);

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("test","Receiver 호출 currTime: ."+todayNow);    // 로그 확인용




        try {
            FileInputStream fis = context.openFileInput("keyWord_List.tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            keylist = (ArrayList<KeyWord>) ois.readObject();
            ois.close();
            Log.d("test", "리시버에서 파일 KEYWORD FILE 열기 성공");
        }catch(Exception e){
            Log.d("test", "리시버에서 파일 열기 실패");
        }







        serachdatalist=new ArrayList<SearchData>();
        savelist=new ArrayList<SearchData>();
        //Thread1 t = new Thread1(context);
        //t.start();

        Log.d("test", "Receiver 에서 쓰레드 시작전");
        AlarmSiteTherad th=new AlarmSiteTherad(context);
        th.start();




    }

    public void savedata(Context context){
        try {
            FileOutputStream fos = context.openFileOutput("keysave_List.tmp", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(savelist);

            Log.d("test", "keysave FILE 저장완료");
            oos.close();
        }catch(Exception e){
            Log.d("test", "keyword실패");
        }
    }


    class Thread1 extends Thread{
        Context context;
        public Thread1(Context context){
            this.context=context;
        }
        @Override
        public void run() {
            Document doc;
            try {
                int x;
                for (x = 0; x < SWURL.length; x++) {
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
                        SearchData newAddData= new SearchData(dataTitle, dataTime, SWname[x], "조회수:" + dataView, viewPageUrl);
                        serachdatalist.add(newAddData);
                        try {
                            Thread.sleep(1);
                        }
                        catch (Exception e) {e.printStackTrace();
                        }
                    }
                }

                for (x=0;x<CSEURL.length;x++) {
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
                        SearchData newAddData= new SearchData(dataTitle, dataTime, CSEname[x], "조회수:" + dataView, viewPageUrl);
                        serachdatalist.add(newAddData);
                        try {
                            Thread.sleep(1);
                        }
                        catch (Exception e) {e.printStackTrace();
                        }
                    }
                }

                for (x=0;x<KNUURL.length;x++) {
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
                        SearchData newAddData= new SearchData(dataTitle, dataTime, KNUname[x], "조회수:" + dataView, viewPageUrl);
                        serachdatalist.add(newAddData);
                        try {
                            Thread.sleep(1);
                        }
                        catch (Exception e) {e.printStackTrace();
                        }
                    }
                }
                Log.d("test","Receiver에서 html받아옴");

                Collections.sort(serachdatalist,SearchData.mydata);
                for(int i=0; i<serachdatalist.size()-1;i++){//같은 제목이면 삭제한다.
                    int iplus=i+1;
                    while( serachdatalist.get(i).time.equals(serachdatalist.get(iplus).time) ){
                        if ( serachdatalist.get(i).title.equals(serachdatalist.get(iplus).title) ) {
                            serachdatalist.remove(iplus);
                        }else {
                            iplus++;
                        }
                        if(iplus>=serachdatalist.size())break;
                    }
                }
                String tempday=serachdatalist.get(0).getTime();
                int dayMarking=serachdatalist.size();
                for(int i=0; i<serachdatalist.size();i++){//최근날짜만 저장하게한다
                    if(!serachdatalist.get(i).getTime().equals(tempday)) {
                        dayMarking = i;
                        Log.d("test","삭제한 index:"+i+"해당날자:"+serachdatalist.get(i).getTime());
                        break;
                    }
                }

                for(int b=0;b< dayMarking;b++){
                    Log.d("test","사이트:"+serachdatalist.get(b).getSite()+"/제목:"+serachdatalist.get(b).getTitle()+"날짜:"+serachdatalist.get(b).getTime());
                }



                for(int a=0;a<keylist.size();a++){
                    for(int b=0;b<dayMarking;b++){
                        if(serachdatalist.get(b).getTitle().contains(keylist.get(a).getWord())){
                            Intent alarmIntentServiceIntent = new Intent(context, AlarmIntentService.class);
                            context.startService(alarmIntentServiceIntent);
                            Log.d("test", "알람인텐트 호출");
                            break;
                        }
                    }
                }




                Collections.sort(savelist,SearchData.mydata);
                savedata(context);
            }
            catch (IOException e) {
                Log.d("test","서비스 쓰레드 오류발생");
                e.printStackTrace();
            }
        }
    }

    class AlarmSiteTherad extends Thread{

        Context context;
        AlarmSiteTherad(Context context){
            this.context=context;
        }
        @Override
        public void run() {
            Log.d("test", "Receiver 에서 쓰레드 시작");
            DBHelper helper = new DBHelper(context,"SearchDataTable.db");
            SQLiteDatabase db= helper.getWritableDatabase();
            String sql = "insert into SearchDataTable (title, time, site, siteCode, views, siteaddress) values (?, ?, ?, ?, ?, ?)";
            Log.d("test","db열었음");
            Document doc;
            try {
                int x;
                for (x=0;x<CSEURL.length;x++) {


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

                        String [] arg1={dataTitle, dataTime, CSEname[x], "CSE","조회수:" + dataView, viewPageUrl};
                        try {
                            db.execSQL(sql, arg1);
                            Intent alarmIntentServiceIntent = new Intent(context, AlarmIntentService.class);
                            context.startService(alarmIntentServiceIntent);
                            Log.d("test", "알람인텐트 호출");
                        }catch (Exception e){}

                        try {
                            Thread.sleep(1);
                        }
                        catch (Exception e) {e.printStackTrace();
                        }
                    }
                }

                for (x=0;x<KNUURL.length;x++) {

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

                        String [] arg1={dataTitle, dataTime, KNUname[x], "KNU","조회수:" + dataView, viewPageUrl};
                        try {
                            db.execSQL(sql, arg1);
                            Intent alarmIntentServiceIntent = new Intent(context, AlarmIntentService.class);
                            context.startService(alarmIntentServiceIntent);
                        }catch (Exception e){}
                        try {
                            Thread.sleep(1);
                        }
                        catch (Exception e) {e.printStackTrace();
                        }
                    }
                }
                for (x = 0; x < SWURL.length; x++) {
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
                        String [] arg1={dataTitle, dataTime, SWname[x], "SW","조회수:" + dataView, viewPageUrl};
                        try {
                            db.execSQL(sql, arg1);
                            Intent alarmIntentServiceIntent = new Intent(context, AlarmIntentService.class);
                            context.startService(alarmIntentServiceIntent);
                        }catch (Exception e){}

                        try {
                            Thread.sleep(1);
                        }
                        catch (Exception e) {e.printStackTrace();
                        }
                    }
                }
                db.close();

                try{
                    FileOutputStream fos = context.openFileOutput("recentTime.tmp", Context.MODE_PRIVATE);
                    DataOutputStream dos = new DataOutputStream(fos);
                    dos.writeUTF(today);
                    Log.d("test","RecentFILEtoday-save--"+today);
                    dos.close();
                }catch(Exception e){
                    Log.d("test","RecentFILE 저장중 오류");
                }
                Log.d("test","쓰레드 종료!!!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}