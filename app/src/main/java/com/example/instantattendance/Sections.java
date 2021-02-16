package com.example.instantattendance;

import java.util.List;

public class Sections {
    private String Course_Code,Course_Name,Section_ID;
    private List<String> Days;
    private List<String> Regesitered_Student;

    public Sections(String course_Code, String course_Name, String section_ID, List<String> days, List<String> regesitered_Student) {
        Course_Code = course_Code;
        Course_Name = course_Name;
        Section_ID = section_ID;
        Days = days;
        Regesitered_Student = regesitered_Student;
    }
    public Sections(Sections s){
        this.Course_Code = s.Course_Code;
        this.Course_Name = s.Course_Name;
        this.Days = s.Days;
        this.Section_ID = s.Section_ID;
        this.Regesitered_Student = s.Regesitered_Student;
    }
    public Sections(){

    }

    public List<String> getRegesitered_Student() {
        return Regesitered_Student;
    }

    public void setRegesitered_Student(List<String> regesitered_Student) {
        Regesitered_Student = regesitered_Student;
    }

    public String getCourse_Code() {
        return Course_Code;
    }

    public String getSection_ID() {
        return Section_ID;
    }

    public void setSection_ID(String section_ID) {
        Section_ID = section_ID;
    }

    public void setCourse_Code(String course_Code) {
        Course_Code = course_Code;
    }

    public String getCourse_Name() {
        return Course_Name;
    }

    public void setCourse_Name(String course_Name) {
        Course_Name = course_Name;
    }

    public List<String> getDays() {
        return Days;
    }

    public void setDays(List<String> days) {
        Days = days;
    }
}
