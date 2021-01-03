package com.example.attandentmanager.ui.home;

import android.content.Intent;
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
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.attandentmanager.AttendListViewAdapter;
import com.example.attandentmanager.DBHelper;
import com.example.attandentmanager.MainActivity;
import com.example.attandentmanager.R;
import com.example.attandentmanager.StudentInfo;
import com.example.attandentmanager.StudentProfile;
import com.example.attandentmanager.TodayAttend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    String today;
    ListView listView;
    AttendListViewAdapter adapter;
    ArrayList<StudentInfo> studentInfoList = new ArrayList<>();

    DBHelper dbHelper;

    Menu myMenu;
    MenuInflater myInflater;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true); // 점 세개 메뉴 프레그먼트 보여주기

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //오늘날짜
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        today = sdf.format(Calendar.getInstance(Locale.getDefault()).getTime());

        dbHelper = new DBHelper(getActivity(), "Attend.db", null, 2);

        studentInfoList = dbHelper.loadTodayAttend(today);

        //StudentInfo student = new StudentInfo(today, "조상은", 0, 0, 0,0, 0);
        //StudentInfo student2 = new StudentInfo(today, "홍길동", 0, 0, 0,0, 0);
        //studentInfoList.add(student);
        //studentInfoList.add(student2);

        // 화면에 뿌리기

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
                startActivity(intent);
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
                //옵션메뉴 일시적으로 바꾸기...
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

}