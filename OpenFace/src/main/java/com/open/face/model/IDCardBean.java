package com.open.face.model;

import android.graphics.Bitmap;

/**
 * Created by Jowney on 2018/9/12.
 */

public class IDCardBean {
    private String name;
    private String gender;
    private String nation;
    private String genderCode;
    private String birdthDay;
    private String address;
    //身份证号
    private String identity;
    //签发证机关
    private String signOffice;
    private String startDate;
    private String endDate;
    private Bitmap bmpPhoto;
    private String nationCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    public String getBirdthDay() {
        return birdthDay;
    }

    public void setBirdthDay(String birdthDay) {
        this.birdthDay = birdthDay;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getSignOffice() {
        return signOffice;
    }

    public void setSignOffice(String signOffice) {
        this.signOffice = signOffice;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Bitmap getBmpPhoto() {
        return bmpPhoto;
    }

    public void setBmpPhoto(Bitmap bmpPhoto) {
        this.bmpPhoto = bmpPhoto;
    }

    public String getNationCode() {
        return nationCode;
    }

    public void setNationCode(String nationCode) {
        this.nationCode = nationCode;
    }
}
