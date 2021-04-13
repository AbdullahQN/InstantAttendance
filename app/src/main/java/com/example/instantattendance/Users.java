package com.example.instantattendance;

import java.io.Serializable;
import java.util.List;

public class Users implements Serializable {
    public String FName,LName,UserType;

    public List<String> Sections;

    public Users(String FName,String LName,String UserType, List<String> Sections) {
        this.FName = FName;
        this.LName = LName;
        this.UserType = UserType;
        this.Sections = Sections;
    }






    public void Users(Users user){
        this.FName = user.FName;
        this.LName = user.LName;
        this.UserType = user.UserType;
        this.Sections = user.Sections;
}

    public Users(){}

}
