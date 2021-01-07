package com.example.attandentmanager;

public class StudentProfile {
    private String registerDate;
    private String name;
    private boolean itemToggled = false;

    public StudentProfile(String registerDate, String name){
        this.registerDate = registerDate;
        this.name = name;
    }

    public void setRegisterDate(String registerDate){
        this.registerDate = registerDate;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getRegisterDate(){
        return registerDate;
    }

    public String getName(){
        return name;
    }


}
