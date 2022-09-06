package com.noticepackage.noticesearch;

import com.example.noticesearch.R;

import java.io.Serializable;
import java.util.Comparator;

public class SearchData implements Serializable {
    String title;
    String time;
    String site;
    String views;
    String siteaddress;

    int imageResid;

    public int getImageResid() {
        return imageResid;
    }

    public void setImageResid(int imageResid) {
        this.imageResid = imageResid;
    }

    public SearchData(String title, String time, String site, String views, String siteaddress) {
        this.title = title;
        this.time = time;
        this.site = site;
        this.views = views;
        this.siteaddress = siteaddress;
        this.imageResid= R.drawable.star;
    }

    public SearchData(String title, String time, String site, String views, String siteaddress, int imageResid) {
        this.title = title;
        this.time = time;
        this.site = site;
        this.views = views;
        this.siteaddress = siteaddress;
        this.imageResid= imageResid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getSiteaddress() {
        return siteaddress;
    }

    public void setSiteaddress(String siteaddress) {
        this.siteaddress = siteaddress;
    }



    public int compareTo(SearchData o){
       return Integer.parseInt(this.time)-Integer.parseInt(o.getTime());
    }

    public static Comparator<SearchData> mydata = new Comparator<SearchData>() {
        @Override
        public int compare(SearchData o1, SearchData o2) {
            return o2.getTime().compareTo(o1.getTime());
        }
    };

}
