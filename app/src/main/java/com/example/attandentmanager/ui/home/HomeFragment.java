package com.example.attandentmanager.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.attandentmanager.AttendListViewAdapter;
import com.example.attandentmanager.DBHelper;
import com.example.attandentmanager.R;
import com.example.attandentmanager.StudentInfo;
import com.example.attandentmanager.TodayAttend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    int index;
    String today;
    ListView listView;
    AttendListViewAdapter adapter;
    ArrayList<StudentInfo> studentInfoList = new ArrayList<>();

    DBHelper dbHelper;

    SharedPreferences fine;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true); // 점 세개 메뉴 프레그먼트 보여주기

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //오늘날짜
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        today = sdf.format(Calendar.getInstance(Locale.getDefault()).getTime());

        dbHelper = new DBHelper(getActivity(), "Attend.db", null, 2);

        studentInfoList = dbHelper.loadTodayAttend(today);

        //어댑터
        adapter = new AttendListViewAdapter(studentInfoList);
        listView = rootView.findViewById(R.id.listview1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //새 액티비티 열기
                Intent intent = new Intent(getActivity(), TodayAttend.class);
                intent.putExtra("name", studentInfoList.get(i).getName());
                intent.putExtra("today", today);
                index = i;
                //HomeFragment hf = new HomeFragment();
                startActivityForResult(intent, 1000);
            }
        });

        return rootView;

    }

    // 점 세개
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.today_attandent, menu);
        //myInflater = inflater;
        //myMenu = menu;
    }

    // 점 세게 메뉴 중 하나가 클릭되었을 때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.select_student :
                //옵션메뉴 일시적으로 바꾸기
                //myInflater.inflate(R.menu.main, myMenu);
                //Toast.makeText(getActivity(), "학생 선택", Toast.LENGTH_SHORT).show();

                //ActionBar ab = ((MainActivity) getActivity()).getActionbar();
                //ab.setTitle("학생 삭제");

                break;

            case R.id.add_student:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateList(int state, int late, int word, int money){
        insertDB(index, state, late, word, money);
        adapter.setListViewItemList(studentInfoList);
        adapter.notifyDataSetChanged();
    }


    public void insertDB(int i, int state, int late, int word, int money) {

        fine = getActivity().getSharedPreferences("Fine", getActivity().MODE_PRIVATE); //저장된 벌금 파일

        int fineForWord = fine.getInt("fineForWord", 100);
        int fineForLate = fine.getInt("fineForLate", 100);
        int free_absent = fine.getInt("free_absent", 10000);
        int plan_absent = fine.getInt("plan_absent", 0);

        studentInfoList.get(i).setDate(today);
        if (state == 1) { //출석
            //지각 에딧창 비활성화
            studentInfoList.get(i).changeState(1);
            studentInfoList.get(i).chageLateMinutes(0);
            studentInfoList.get(i).setFine((studentInfoList.get(i).getLateMinutes() + studentInfoList.get(i).getWrongWords()) * fineForWord);
        } else if (state == 2) { //지각
            // 지각 에딧창 활성화
            if (late >= 0) { // 0 이상 입력되면
                studentInfoList.get(i).changeState(2);
                studentInfoList.get(i).setLateMinutes(late);
                studentInfoList.get(i).setFine(studentInfoList.get(i).getFine() + studentInfoList.get(i).getLateMinutes() * fineForLate);
            }
        } else if (state == 3) { //무단결
            studentInfoList.get(i).changeState(3);
            studentInfoList.get(i).setLateMinutes(0);
            studentInfoList.get(i).setWrongWords(0);
            studentInfoList.get(i).setFine(free_absent);
        } else { //예고 결
            //모든 입력창 비활성화
            studentInfoList.get(i).changeState(4);
            studentInfoList.get(i).setLateMinutes(0);
            studentInfoList.get(i).setWrongWords(0);
            studentInfoList.get(i).setFine(0);
            studentInfoList.get(i).setDebt(plan_absent);
        }

        if (word >= 0) {
            studentInfoList.get(i).setWrongWords(word);
            studentInfoList.get(i).setFine(studentInfoList.get(i).getFine() + studentInfoList.get(i).getWrongWords() * fineForWord);
        }

        if (money >= 0) {
            studentInfoList.get(i).setDebt(money); //납입 금액
        }

        dbHelper.modifyAttend(
                studentInfoList.get(i).getDate(),
                studentInfoList.get(i).getName(),
                studentInfoList.get(i).getState(),
                studentInfoList.get(i).getLateMinutes(),
                studentInfoList.get(i).getWrongWords(),
                studentInfoList.get(i).getFine(),
                studentInfoList.get(i).getDebt()
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 03){
            // System.out.println("프레그먼트에서 응답수신!!!");

            int state = data.getIntExtra("state", 0);
            int late = data.getIntExtra("late", 0);
            int word = data.getIntExtra("word", 0);
            int money = data.getIntExtra("money", 0);

            insertDB(index, state, late, word, money);
            adapter.setListViewItemList(studentInfoList);
            adapter.notifyDataSetChanged();

        }
    }

}