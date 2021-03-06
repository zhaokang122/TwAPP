package com.example.twapp.been;

import android.graphics.Bitmap;

/**
 * Created by lenovo-pc on 2017/9/18.
 */

public class PeopleInfor {
    private String runNum;
    private String peopleNum;
    private String name;
    private String age;
    private String gender;
    private String bedNumber;
    private String result;
    private Bitmap bitmap;
    private String dianLiang;
    private int id;

    public PeopleInfor(String runNum, String peopleNum, String name, String age, String gender, String bedNumber, String dianLiang,int id) {
        this.runNum = runNum;
        this.peopleNum = peopleNum;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bedNumber = bedNumber;
        this.result = result;
        this.dianLiang = dianLiang;
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getRunNum() {
        return runNum;
    }

    public void setRunNum(String runNum) {
        this.runNum = runNum;
    }

    public String getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(String peopleNum) {
        this.peopleNum = peopleNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDianLiang() {
        return dianLiang;
    }

    public void setDianLiang(String dianLiang) {
        this.dianLiang = dianLiang;
    }
}
