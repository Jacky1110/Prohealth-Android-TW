package com.v7idea.healthkit;

import android.app.Application;

public class Air extends Application{
    //姓名 名字 手機 密碼 電子郵件
    //生日 身高 體重 性別 血型
    private String LastName;
    private String FirstName;
    private String PhoneNumber;
    private String Password;
    private String Email;
    private String Birthday;
    private String Height;
    private String Weight;
    private String Gender;
    private String BloodType;

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public void setBloodType(String bloodType) {
        BloodType = bloodType;
    }

    public String getBirthday() {
        return Birthday;
    }

    public String getBloodType() {
        return BloodType;
    }

    public String getEmail() {
        return Email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getGender() {
        return Gender;
    }

    public String getHeight() {
        return Height;
    }

    public String getLastName() {
        return LastName;
    }

    public String getPassword() {
        return Password;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getWeight() {
        return Weight;
    }
}
