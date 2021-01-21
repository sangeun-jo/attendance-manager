package sej.attend.attandentmanager;

public class StudentInfo {

    public final static int NOT_CHECKED = 0;
    public final static int ATTENDANCE = 1;
    public final static int LATE = 2;
    public final static int ABSENT = 3;
    public final static int REPORT_ABSENT = 4;

    private String date;
    private String name;
    private int state = NOT_CHECKED;
    private int lateMinutes = 0;
    private int wrongWordsNum = 0;
    private int fine = 0;
    private int money = 0;

    public StudentInfo(){}

    public StudentInfo(String date, String name, int state, int late, int word, int fine, int money){
        this.date = date;
        this.name = name;
        this.state = state;
        lateMinutes = late;
        wrongWordsNum = word;
        this.fine = fine;
        this.money = money;
    }

    public  void changeName(String name) { this.name = name; }

    public void changeState(int state){
        this.state = state;
    }

    public void setWrongWords(int num){
        wrongWordsNum = num;
    }

    public void setLateMinutes(int m){
        lateMinutes = m;
    }

    public void setFine(int fine){ this.fine = fine; }

    public void setMoney(int money) { this.money = money; }

    public String getDate() { return  this.date; }

    public String getName() {
        return name ;
    }

    public int getState(){
        return state;
    }

    public int getWrongWords(){
         return wrongWordsNum;
    }

    public int getLateMinutes(){
        return lateMinutes;
    }

    public int getFine(){ return fine; }

    public int getMoney(){ return money; }

    public void chageLateMinutes(int i) {
    }
}