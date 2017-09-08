package com.example.hungn.jsonthoitiet;

/**
 * Created by hungn on 05/29/17.
 */

public class thoitiet {
    private String tv_city, tv_nhietdo, tv_thoitiet, imgtt;

    public thoitiet(String tv_city, String tv_nhietdo, String tv_thoitiet, String imgtt) {
        this.tv_city = tv_city;
        this.tv_nhietdo = tv_nhietdo;
        this.tv_thoitiet = tv_thoitiet;
        this.imgtt = imgtt;
    }

    public String getTv_city() {
        return tv_city;
    }

    public void setTv_city(String tv_city) {
        this.tv_city = tv_city;
    }

    public String getTv_nhietdo() {
        return tv_nhietdo;
    }

    public void setTv_nhietdo(String tv_nhietdo) {
        this.tv_nhietdo = tv_nhietdo;
    }

    public String getTv_thoitiet() {
        return tv_thoitiet;
    }

    public void setTv_thoitiet(String tv_thoitiet) {
        this.tv_thoitiet = tv_thoitiet;
    }

    public String getImgtt() {
        return imgtt;
    }

    public void setImgtt(String imgtt) {
        this.imgtt = imgtt;
    }
}
