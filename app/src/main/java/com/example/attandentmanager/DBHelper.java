package com.example.attandentmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE attend (date TEXT, name TEXT, state INTEGER, late INTEGER, word INTEGER, fine INTEGER, debt INTEGER);");
        db.execSQL("CREATE TABLE profile (regiDate TEXT, name TEXT);");
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS attend");
        db.execSQL("DROP TABLE IF EXISTS profile");
        db.close();
    }

    public void insertAttend(String date, String name, int state, int late, int word, int fine, int debt){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO attend VALUES('" + date + "','" + name + "','" +  state + "','" + late + "','" + word + "','" + fine +  "','" + debt +  "');";
        db.execSQL(sql);
        db.close();
    }


    public void insertProfile(String regiDate, String name){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO profile VALUES('" + regiDate + "','" + name + "');";
        db.execSQL(sql);
        db.close();
        insertAttend(regiDate, name, 0, 0, 0, 0, 0);
    }

    public void deleteAll(){ //모든 데이터 삭제
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM attend;");
        db.execSQL("DELETE FROM profile;");
        db.close();
    }

    public void deleteAttendAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM attend;");
        db.close();
    }

    public void deleteProfileAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM profile;");
        db.close();
    }

    public void deleteAttend(String field, String con){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM attend WHERE " + field + " = '" + con + "';");
        db.close();
    }

    public void deleteProfile(String field, String con){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM profile WHERE " + field + " = '" + con + "';");
        db.close();
    }

    public void modifyAttend(String date, String name, int state, int late, int word, int fine, int debt){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "UPDATE attend SET state = ?, late = ?, word = ?, fine = ?, debt = ? WHERE (date = ? and name = ?);";

        db.execSQL(sql, new String[] {
                Integer.toString(state),
                Integer.toString(late),
                Integer.toString(word),
                Integer.toString(fine),
                Integer.toString(debt),
                date, name});
        db.close();
    }

    public void modifyName(String af_name, String bf_name){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE names SET name = '" + af_name + "' WHERE name = '" + bf_name+ "';");
        db.close();
    }

    //학생 프로필 불러오기
    public ArrayList<StudentProfile>loadProfile(){

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<StudentProfile> studentProfileList = new ArrayList<>();
        String sql = "SELECT * FROM profile;";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            StudentProfile student = new StudentProfile(
                    cursor.getString(0), //등록 날짜
                    cursor.getString(1) //이름
            );
            studentProfileList.add(student);
        }

        cursor.close();
        db.close();

        return studentProfileList;
    }


    //학생 이름 불러오기
    public ArrayList<String>loadNames(){

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> studentList = new ArrayList<>();
        String sql = "SELECT * FROM profile;";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String student = cursor.getString(1);
            studentList.add(student);
        }

        cursor.close();
        db.close();

        return studentList;
    }

    // 오늘 출석 데이터 반환
    public ArrayList<StudentInfo>loadTodayAttend(String today){

        Cursor cursor = null;
        ArrayList<StudentInfo> studentInfoList = new ArrayList<>();
        ArrayList<StudentProfile> pro = loadProfile();

        SQLiteDatabase db = getWritableDatabase();

        for(int i = 0; i < pro.size() ; i++){
            String sql = "SELECT * FROM attend WHERE (date = ? and name = ?);";


            if(cursor != null && cursor.isClosed()){
                cursor.close();
            }
            cursor = db.rawQuery(sql, new String[] {today, pro.get(i).getName()});
            if(cursor.getCount() <= 0) {
                insertAttend(today, pro.get(i).getName(), 0, 0, 0, 0, 0);
                StudentInfo student = new StudentInfo(today, pro.get(i).getName(), 0, 0, 0, 0, 0);
                studentInfoList.add(student);
            } else { // 오늘날짜 레코드가 있으면 불러오기
                //System.out.println(today + " 데이터 있음. 불러옴");
                cursor.moveToNext();
                StudentInfo student = new StudentInfo(today, cursor.getString(1), 0, 0, 0,0, 0);
                student.changeState(cursor.getInt(2));
                student.setLateMinutes(cursor.getInt(3));
                student.setWrongWords(cursor.getInt(4));
                student.setFine(cursor.getInt(5));
                studentInfoList.add(student);
            }
            cursor.close();
        }

        db.close();

        return studentInfoList;
    }



}
