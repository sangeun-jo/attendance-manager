package com.example.listviewexample;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;


// 내부 저장소 관리 클래스
public class InStorageManager {

    private Context contextView;

    public InStorageManager(Context contextView){
        this.contextView = contextView;
    }

    // 학생 이름을 내부 저장소에 추가
    public void addStudent(String studentName) {
        try {
            FileOutputStream fos= contextView.openFileOutput("StudentNames.txt", Context.MODE_APPEND);
            PrintWriter writer= new PrintWriter(fos);

            writer.write(studentName+"\n");
            writer.close();

            Toast.makeText(contextView, "학생이 추가 되었습니다.", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {e.printStackTrace();}

    }

    // 학생 이름 리스트 로드
    public ArrayList<String> loadStudentList() {

        ArrayList<String> studentList = new ArrayList<>();

        try {
            FileInputStream fileInputStream = contextView.openFileInput("StudentNames.txt"); //Data.txt 파일 열기
            InputStreamReader isr= new InputStreamReader(fileInputStream);
            BufferedReader reader= new BufferedReader(isr);

            //학생 이름을 한줄씩 읽어서 studentList 에 추가
            String name = reader.readLine();
            while(true){
                studentList.add(name);
                name = reader.readLine();
                if(name==null) break;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return studentList;
    }



}
