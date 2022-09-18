package com.noticepackage.noticesearch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GetSiteThread extends Thread{
    long now =System.currentTimeMillis();
    Date nowDate=new Date(now);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년-MM월-dd일/hh시");
    String today=sdf.format(nowDate);
    public String recentTimeFile ="recentTime.tmp";
    public String dateControl="20220301";
    public String SearchDataTable="SearchDataTable.db";

    private final String[] SWURL={"https://swedu.knu.ac.kr/05_sub/01_sub.html", "https://swedu.knu.ac.kr/05_sub/02_sub.html",
            "https://swedu.knu.ac.kr/05_sub/10_sub.html", "https://swedu.knu.ac.kr/05_sub/03_sub.html",
            "https://swedu.knu.ac.kr/05_sub/09_sub.html", "https://swedu.knu.ac.kr/02_sub/04_sub.html"};
    private final String[] SWname={"sw중심대학지원산업","지역선도대학산업","경북디지털역량교육","소프트웨어교육원새소식","마일리지게시판","sw기초교육"};
    private final String[] KNUURL = {"https://www.knu.ac.kr/wbbs/wbbs/bbs/btin/list.action?bbs_cde=1&menu_idx=67",
            "https://www.knu.ac.kr/wbbs/wbbs/bbs/btin/stdList.action?menu_idx=42"};
    private final String[] KNUname={"경북대","경북대학사공지"};
    private final String[] CSEname={"컴학","컴학-학사","컴학-심컴","컴학-학부인재"};
    private final String[] CSEURL ={"http://computer.knu.ac.kr/06_sub/02_sub.html", "http://computer.knu.ac.kr/06_sub/02_sub_2.html",
            "http://computer.knu.ac.kr/06_sub/02_sub_3.html","http://computer.knu.ac.kr/06_sub/04_sub.html"};

    String dataTitle; String dataTime; String dataView; String viewPageUrl;



    Menu3Fragment.AddDataHandler addDataHandler;
    Context context;


    GetSiteThread(Context context, Menu3Fragment.AddDataHandler addDataHandler){
        this.context=context;
        this.addDataHandler=addDataHandler;
        Log.d("test","겟사이트스레드 생성");
    }

    @Override
    public void run() {
        DBHelper helper = new DBHelper(context,SearchDataTable);
        SQLiteDatabase db= helper.getWritableDatabase();
        String sql = "insert into SearchDataTable (title, time, site, siteCode, views, siteaddress) values (?, ?, ?, ?, ?, ?)";

        Document doc;
        try {
            addDataHandler.sendEmptyMessage(4);

            int x;
            for (x=0;x<CSEURL.length;x++) {
                addDataHandler.sendEmptyMessage(0);

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
                    }catch (Exception e){}

                    try {
                        Thread.sleep(1);
                    }
                    catch (Exception e) {e.printStackTrace();
                    }
                }
            }

            for (x=0;x<KNUURL.length;x++) {
                addDataHandler.sendEmptyMessage(0);

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
                    }catch (Exception e){}
                    try {
                        Thread.sleep(1);
                    }
                    catch (Exception e) {e.printStackTrace();
                    }
                }
            }
            for (x = 0; x < SWURL.length; x++) {
                addDataHandler.sendEmptyMessage(0);

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
                FileOutputStream fos = context.openFileOutput(recentTimeFile, Context.MODE_PRIVATE);
                DataOutputStream dos = new DataOutputStream(fos);
                dos.writeUTF(today);
                Log.d("test","today-save--"+today);
                dos.close();
            }catch(Exception e){
                Log.d("test","파일로 날짜 저장중 오류");
            }
            Log.d("test","쓰레드 종료!!!");
            addDataHandler.sendEmptyMessage(2);

            Log.d("test","겟사이트 쓰레드 성공");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
