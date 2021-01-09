package com.example.attandentmanager.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.example.attandentmanager.AttendListViewAdapter;
import com.example.attandentmanager.MainActivity;
import com.example.attandentmanager.R;
import com.example.attandentmanager.SQLiteHelper;
import com.example.attandentmanager.StudentInfo;
import com.example.attandentmanager.ModifyAttend;

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

    //DBHelper dbHelper;
    SQLiteHelper dbHelper;

    SharedPreferences fine;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true); // 점 세개 메뉴 프레그먼트 보여주기

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //오늘날짜
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        today = sdf.format(Calendar.getInstance(Locale.getDefault()).getTime());

        fine = getActivity().getSharedPreferences("Fine", getActivity().MODE_PRIVATE); //저장된 벌금 파일

        // 액션바
        ActionBar ab = ((MainActivity)getActivity()).getSupportActionBar();
        ab.setTitle(today + " 출결");

        dbHelper = new SQLiteHelper(getActivity()).getInstance(getActivity());
        dbHelper.open();

        studentInfoList = dbHelper.loadAttendByDate(today);

        //어댑터
        adapter = new AttendListViewAdapter(studentInfoList);
        listView = rootView.findViewById(R.id.home_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //새 액티비티 열기
                Intent intent = new Intent(getActivity(), ModifyAttend.class);
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
        inflater.inflate(R.menu.home_main, menu);
    }

    // 점 세게 메뉴 중 하나가 클릭되었을 때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.attend_all:
                break;
            case R.id.reset_today_attend:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateList(int state, int late, int word, int money){
        //insertDB(index, state, late, word, money);
        adapter.setListViewItemList(studentInfoList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 03){
            int state = data.getIntExtra("state", 0);
            int late = data.getIntExtra("late", 0);
            int word = data.getIntExtra("word", 0);
            int money = data.getIntExtra("money", 0);

            dbHelper.modifyAttend(studentInfoList.get(index), fine, state, late, word, money);
            studentInfoList = dbHelper.loadAttendByDate(today);
            adapter.setListViewItemList(studentInfoList);
            adapter.notifyDataSetChanged();
        }

        //프레그먼트간 통신
        if(requestCode == 05){
            studentInfoList = dbHelper.loadAttendByDate(today);
            adapter.setListViewItemList(studentInfoList);
            adapter.notifyDataSetChanged();
        }
    }
}