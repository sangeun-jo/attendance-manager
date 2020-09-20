package com.example.listviewexample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


//개선점(꼭 필요한 건 * 붙임)
// 오른쪽 상단에 톱니바퀴 모양 만들기 *
// 단어 1개 틀렸을 때 벌금 변경할 수 있도록 하기(현재 100원) *
// 기록 엑셀 내보내기 기능 만들기
// 톱니바퀴 메뉴
// [메뉴 종류] 벌금 액수 조정 (초기값 100원) * / 컬러 변겅 / 엑셀로 데이터 내보내기 / 데이터 초기화 * / 사용법 *
// 출석 체크 되어 있을 때 지각 입력창 비활성화 하기 *
// 출결 편집 다이얼로그 창 오른쪽 상단에 작게 X 버튼 만들기 *
// 앱 실행 시 인트로 만들기
// 디자인 수정
// 틀린 단어 개수 > 틀린 개수로 만들기


// 만든 계기
// 스터디 모임에서 출결 관리를 쉽게 하기 위하여 만들었습니다.
// 개인적으로 사용하기 위해 만들었지만 혹시 필요한 기능이나 건의 사항이 있으시면 아래 메일로 문의해주시기 바랍니다.
// 메일 주소:



public class MainActivity extends AppCompatActivity{

    DBHelper dbHelper;

    final static String dbName = "students.db";
    final static int dbVersion = 2;

    String today;
    ListView listview;
    ListViewAdapter adapter;

    ArrayList<String> stuStrList = new ArrayList<>();

    ArrayList<StudentInfo> studentInfoList = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 데이터 테이블 가져오기
        dbHelper = new DBHelper(this, dbName, null, dbVersion);

        //dbHelper.insertName("홍길동"); 이름 추가하는 법
        adapter = new ListViewAdapter(studentInfoList);

        // names.db 에서 이름 불러오기
        SQLiteDatabase db = dbHelper.getReadableDatabase(); //db 열기
        String sql = "SELECT * FROM names;";
        Cursor cursor = db.rawQuery(sql, null);

        while(cursor.moveToNext()){
            String name = cursor.getString(0);
            stuStrList.add(name);
            //System.out.println("name: " + name);
        }

        cursor.close();
        db.close();

        // 메인뷰 세팅
        today = sdf.format(Calendar.getInstance(Locale.getDefault()).getTime());
        mainView();

        // 학생 리스트 중 아무나 눌리면
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectJob(i);
            }
        });

    }

    // 날짜 데이터를 기준으로 학생 정보를 메인화면에 뿌려줌
    public void mainView(){
        SQLiteDatabase db = dbHelper.getReadableDatabase(); //db 열기

        // 오늘 데이터 검색
        String sql = "SELECT * FROM students WHERE date = '" + today + "';";
        Cursor cursor = db.rawQuery(sql, null);

        // 테스트 코드
        System.out.println("cursor.getCount: " + cursor.getCount());
        System.out.println("stuStrList.size(): " + stuStrList.size());

        // 데이터가 없으면 생성
        if(cursor.getCount() <= 0) {
            for(int i = 0; i < stuStrList.size() ; i++){
                dbHelper.insert(today, stuStrList.get(i), 0, 0, 0, 0, 0);
                StudentInfo student = new StudentInfo(today, stuStrList.get(i), 0, 0, 0, 0, 0);
                studentInfoList.add(student);
            }
        } else { // 오늘날짜 레코드가 있으면 불러오기
            System.out.println(today + " 데이터 있음. 불러옴");
            while (cursor.moveToNext()){
                StudentInfo student = new StudentInfo(today, cursor.getString(1), 0, 0, 0,0, 0);
                student.changeState(cursor.getInt(2));
                student.setLateMinutes(cursor.getInt(3));
                student.setWrongWords(cursor.getInt(4));
                student.setFine(cursor.getInt(5));
                studentInfoList.add(student);
            }
        }

        listview = findViewById(R.id.listview1);
        listview.setAdapter(adapter);
    }

    // 작업 선택 창
    public void selectJob(final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] choices = {"출결편집", "타임라인", "이름수정", "학생삭제"};
        builder.setItems(choices, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                String selected = choices[i];
                if(selected.equals("출결편집")) {
                    modifyStudent(pos);
                } else if(selected.equals("타임라인")){
                    Intent intent = new Intent(getApplicationContext(), StudentLog.class);
                    intent.putExtra("name", studentInfoList.get(pos).getName());
                    startActivity(intent);
                } else if(selected.equals("이름수정")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("변경할 이름을 입력하세요");
                    final EditText et = new EditText(MainActivity.this);
                    builder.setView(et);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String name = et.getText().toString();
                            //스튜던트 리스트에서 기존 데이터 수정
                            dbHelper.updateName(name, studentInfoList.get(pos).getName());
                            studentInfoList.get(pos).changeName(name);
                            adapter.setListViewItemList(studentInfoList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    builder.create().show();
                } else{
                    String name = studentInfoList.get(pos).getName();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(name + " 학생을 삭제하시겠습니까?");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //스튜던트 리스트에서 기존 데이터 삭제
                            dbHelper.delete("name", studentInfoList.get(pos).getName());
                            studentInfoList.remove(pos);
                            adapter.setListViewItemList(studentInfoList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    builder.create().show();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 각 학생 일일 정보 수정창(모듈화 하기)
    public void modifyStudent(final int i) {
        final inputStudentInfo dialog = new inputStudentInfo(this);
        dialog.setDialogListener(new inputStudentInfo.myListener() {

            @Override
            public void onPositiveClicked(int state, int late, int word, int money) {
                if(state == 1){ //출석
                    //지각 에딧창 비활성화
                    studentInfoList.get(i).changeState(1);
                    studentInfoList.get(i).setLateMinutes(0);
                    studentInfoList.get(i).setFine((studentInfoList.get(i).getLateMinutes()  + studentInfoList.get(i).getWrongWords() ) *100);
                } else if(state == 2){ //지각
                    // 지각 에딧창 활성화
                    if(late >= 0 ) { // 0 이상 입력되면
                        studentInfoList.get(i).changeState(2);
                        studentInfoList.get(i).setLateMinutes(late);
                        studentInfoList.get(i).setFine(studentInfoList.get(i).getFine() +  studentInfoList.get(i).getLateMinutes() * 100);
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
                dialog.dismiss(); //이게 뭐지??
            }


        });

        dialog.show();
    }

    public void addStudent(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("학생 이름 입력");
        final EditText et = new EditText(MainActivity.this);
        builder.setView(et);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = et.getText().toString();
                dbHelper.insertName(name); //이름 중복 문제는 나중에 해결하기
                dbHelper.insert(today, name, 0, 0, 0, 0, 0);
                StudentInfo student = new StudentInfo(today, name, 0, 0, 0, 0, 0);
                studentInfoList.add(student);
                adapter.setListViewItemList(studentInfoList);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("취소", null);
        builder.create().show();
    }
}