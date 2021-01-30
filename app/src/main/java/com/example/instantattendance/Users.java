package com.example.instantattendance;

import java.io.Serializable;
import java.util.List;

public class Users implements Serializable {
    public String FName,LName,Title;

    public List<String> Sections;

    public Users(String FName,String LName,String Title, List<String> Sections) {
        this.FName = FName;
        this.LName = LName;
        this.Title = Title;
        this.Sections = Sections;
    }






    public void Users(Users user){
        this.FName = user.FName;
        this.LName = user.LName;
        this.Title = user.Title;
        this.Sections = user.Sections;
}

    public Users(){}

}
