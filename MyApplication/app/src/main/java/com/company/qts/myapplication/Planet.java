package com.company.qts.myapplication;

/**
 * Created by MyPC on 10/08/2017.
 */
public class Planet {

    private String name;
    private Integer distance;
    private String descr;
    private int idImg;



    public Planet(String name, Integer distance) {
        this.name = name;
        this.distance = distance;
    }

    public Planet(String name, String descr) {
        this.name = name;
        this.descr = descr;
    }

    public Planet(String name, Integer distance, String descr, int idImg) {
        this.name = name;
        this.distance = distance;
        this.descr = descr;
        this.idImg = idImg;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getDistance() {
        return distance;
    }
    public void setDistance(Integer distance) {
        this.distance = distance;
    }
    public int getIdImg() {
        return idImg;
    }
    public void setIdImg(int idImg) {
        this.idImg = idImg;
    }


}
