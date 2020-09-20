package com.example.listviewexample;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE students (date TEXT, name TEXT, state INTEGER, late INTEGER, word INTEGER, fine INTEGER, debt INTEGER);");
        db.execSQL("CREATE TABLE names (name TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS students");
        db.execSQL("DROP TABLE IF EXISTS names");
    }

    public void insert(String date, String name, int state, int late, int word, int fine, int debt){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO students VALUES('" + date + "','" + name + "','" +  state + "','" + late + "','" + word + "','" + fine +  "','" + debt +  "');";
        db.execSQL(sql);
        db.close();
    }

    public void insertName(String name){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO names VALUES('" + name +  "');";
        db.execSQL(sql);
        db.close();
    }

    public void deleteAll(){ //모든 데이터 삭제
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM students;");
        db.execSQL("DELETE FROM names;");
        db.close();
    }

    public void delete(String field, String con){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM students WHERE " + field + " = '" + con + "';");
        db.execSQL("DELETE FROM names WHERE " + field + " = '" + con + "';");
        db.close();
    }

    public void modify(String date, String name, int state, int late, int word, int fine, int debt){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "UPDATE students SET state = ?, late = ?, word = ?, fine = ?, debt = ? WHERE (date = ? and name = ?);";

        db.execSQL(sql, new String[] {
                Integer.toString(state),
                Integer.toString(late),
                Integer.toString(word),
                Integer.toString(fine),
                Integer.toString(debt),
                date, name});
        db.close();
    }

    public void updateName(String af_name, String bf_name){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE names SET name = '" + af_name + "' WHERE name = '" + bf_name+ "';");
        db.execSQL("UPDATE students SET name = '" + af_name + "' WHERE name = '" + bf_name+ "';");
        db.close();
    }

    public ArrayList<StudentInfo>loadByName(String name){

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<StudentInfo> todayStudentList = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE name = ? ORDER BY date DESC;";
        Cursor cursor = db.rawQuery(sql, new String[] {name});
        while (cursor.moveToNext()) {
            StudentInfo student = new StudentInfo(
                    cursor.getString(0), //날짜
                    cursor.getString(1), //이름
                    cursor.getInt(2), // 상태
                    cursor.getInt(3), // 지각 분
                    cursor.getInt(4), // 틀린단어
                    cursor.getInt(5), // 벌금
                    cursor.getInt(6) // 납입
            );
            todayStudentList.add(student);
        }

        cursor.close();
        db.close();

        return todayStudentList;
    }
}
