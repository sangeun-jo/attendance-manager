package com.example.listviewexample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.widget.AdapterView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


//일별 삭제 기능
//체크박스로 선택해서 여러 날짜 출석 데이터 지우기
//이미 있는 출석 날짜는 없애기
//오름차순, 내림차순 정렬 메뉴 넣기
//해당 학생 데이터 전체 삭제 기능 추가

public class StudentLog extends AppCompatActivity {

    DBHelper dbHelper;

    final static String dbName = "students.db";
    final static int dbVersion = 2;

    ListView listView;
    ListViewAdapter adapter;
    ArrayList<StudentInfo> studentInfoList = new ArrayList<>();
    Calendar myCalendar = Calendar.getInstance();
    String name;

    int fineForWord = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_log);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        TextView title = findViewById(R.id.log_title);
        title.setText(name + " 학생의 기록");

        listView = findViewById(R.id.log_view);

        dbHelper = new DBHelper(this, dbName, null, dbVersion);
        studentInfoList = dbHelper.loadByName(name); // 이름별로 데이터 불러오기

        statistic(studentInfoList);

        // 최신 날짜 순으로 정렬

        adapter = new ListViewAdapter(studentInfoList);
        listView = findViewById(R.id.log_view);
        listView.setAdapter(adapter);

        //아이템 클릭 이벤트 발생
        listView.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                modifyStudent(i);
            }
        }));


        //데이터 추가 버튼 클릭 시
        Button addStudentInfo = findViewById(R.id.add_student_info);
        addStudentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new DatePickerDialog(StudentLog.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

    }

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            addDateStudent();
        }
    };

    private void addDateStudent() {
        String myFormat = "yyyy-MM-dd";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        Toast toast = Toast.makeText(getApplicationContext(), sdf.format(myCalendar.getTime()) + "일 데이터 추가", Toast.LENGTH_SHORT);
        toast.show();
        dbHelper.insert(sdf.format(myCalendar.getTime()), name, 0, 0,0,0,0);
        adapter = new ListViewAdapter(dbHelper.loadByName(name));
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void modifyStudent(final int i) {
        final inputStudentInfo dialog = new inputStudentInfo(this);
        dialog.setDialogListener(new inputStudentInfo.myListener() {

            @Override
            public void onPositiveClicked(int state, int late, int word, int money) {
                if(state == 1){ //출석
                    //지각 에딧창 비활성화
                    studentInfoList.get(i).changeState(1);
                    studentInfoList.get(i).setLateMinutes(0);
                    studentInfoList.get(i).setFine((studentInfoList.get(i).getLateMinutes()  + studentInfoList.get(i).getWrongWords() ) * fineForWord);
                } else if(state == 2){ //지각
                    // 지각 에딧창 활성화
                    if(late >= 0 ) { // 0 이상 입력되면
                        studentInfoList.get(i).changeState(2);
                        studentInfoList.get(i).setLateMinutes(late);
                        studentInfoList.get(i).setFine(studentInfoList.get(i).getFine() +  studentInfoList.get(i).getLateMinutes() * fineForWord);
                    }
                } else if(state == 3){ //무단결
                    studentInfoList.get(i).changeState(3);
                    studentInfoList.get(i).setLateMinutes(0);
                    studentInfoList.get(i).setWrongWords(0);
                    studentInfoList.get(i).setFine(10000);

                } else{ //예고 결
                    //모든 입력창 비활성화
                    studentInfoList.get(i).changeState(4);
                    studentInfoList.get(i).setLateMinutes(0);
                    studentInfoList.get(i).setWrongWords(0);
                    studentInfoList.get(i).setFine(0);
                    studentInfoList.get(i).setDebt(0);
                }

                if(word >= 0 ) {
                    studentInfoList.get(i).setWrongWords(word);
                    studentInfoList.get(i).setFine(studentInfoList.get(i).getFine() +  studentInfoList.get(i).getWrongWords() * 100);
                }

                if(money >= 0) {
                    studentInfoList.get(i).setDebt(money); //납입 금액
                }

                dbHelper.modify(
                        studentInfoList.get(i).getDate(),
                        studentInfoList.get(i).getName(),
                        studentInfoList.get(i).getState(),
                        studentInfoList.get(i).getLateMinutes(),
                        studentInfoList.get(i).getWrongWords(),
                        studentInfoList.get(i).getFine(),
                        studentInfoList.get(i).getDebt() //입금
                );

                adapter.setListViewItemList(studentInfoList);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onNegativeClicked() { //초기화
                studentInfoList.get(i).changeState(0);
                studentInfoList.get(i).setLateMinutes(0);
                studentInfoList.get(i).setWrongWords(0);
                studentInfoList.get(i).setFine(0);
                studentInfoList.get(i).setDebt(0);

                dbHelper.modify(
                        studentInfoList.get(i).getDate(),
                        studentInfoList.get(i).getName(),
                        studentInfoList.get(i).getState(),
                        studentInfoList.get(i).getLateMinutes(),
                        studentInfoList.get(i).getWrongWords(),
                        studentInfoList.get(i).getFine(),
                        studentInfoList.get(i).getDebt() //입금
                );

                adapter.setListViewItemList(studentInfoList);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void backBtn(){
                dialog.dismiss();
            }
        });




        dialog.show();
    }

    public void statistic(ArrayList<StudentInfo> studentInfoList){
        //지각 분
        int allLate = 0;

        for(int i=0; i < studentInfoList.size(); i++){
            allLate += studentInfoList.get(i).getLateMinutes();
        }

        TextView all_late = findViewById(R.id.all_late_time);
        all_late.setText("총 지각: " + allLate + "분");

        // 무단 결석
        int allAbsent = 0;

        for(int i=0; i < studentInfoList.size(); i++){
            if(studentInfoList.get(i).getState() == 3){
                allAbsent++;
            }
        }

        TextView all_absent = findViewById(R.id.all_absent);
        all_absent.setText("총 무단 결석: " + allAbsent);


        //틀린단어
        int allWrong = 0;

        for(int i=0; i < studentInfoList.size(); i++){
            allWrong += studentInfoList.get(i).getWrongWords();
        }

        TextView all_wrong_word = findViewById(R.id.all_wrong_words);
        all_wrong_word.setText("총 틀린 단어: " + allWrong);


        // 벌금
        int allFine = 0;

        for(int i=0; i < studentInfoList.size(); i++){
            allFine += studentInfoList.get(i).getFine();
        }

        TextView all_fine = findViewById(R.id.all_fine);
        all_fine.setText("총 벌금: " + allFine);


        // 미납
        int allDebt = 0;

        for(int i=0; i < studentInfoList.size(); i++){
            allDebt += studentInfoList.get(i).getDebt();
        }

        int notPaid = allFine - allDebt;

        TextView all_debt = findViewById(R.id.not_paid_fine);
        all_debt.setText("미납: " + notPaid);

    }

    //다이얼로그 조작 후 화면 갱신 부분
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            adapter.notifyDataSetChanged();
        }
    }

}