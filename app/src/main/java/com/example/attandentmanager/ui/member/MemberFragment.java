package com.example.attandentmanager.ui.member;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.attandentmanager.MemberListViewAdapter;
import com.example.attandentmanager.R;
import com.example.attandentmanager.SQLiteHelper;
import com.example.attandentmanager.StudentProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MemberFragment extends Fragment {

    ListView listView;
    MemberListViewAdapter adapter;
    SQLiteHelper dbHelper;
    ArrayList<StudentProfile> studentList = new ArrayList<>();
    String today;
    CheckBox checkBox;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_member, container, false);

        setHasOptionsMenu(true); // 점 세개 메뉴 프레그먼트 보여주기

        dbHelper = new SQLiteHelper(getActivity()).getInstance(getActivity());

        //오늘날짜
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        today = sdf.format(Calendar.getInstance(Locale.getDefault()).getTime());

        //학생 프로필 불러오기
        studentList = dbHelper.loadProfile();

        //화면에 뿌려주기
        adapter = new MemberListViewAdapter(studentList);
        listView = rootView.findViewById(R.id.member_listeiw);
        checkBox = rootView.findViewById(R.id.checkBox);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        return rootView;
    }
    // 점 세개
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.member_manage, menu);
    }

    // 점 세게 메뉴 중 하나가 클릭되었을 때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_student: //학생 추가
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("학생 이름 입력");
                final EditText et = new EditText(getActivity());
                builder.setView(et);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = et.getText().toString();
                        dbHelper.insertProfile(today, name); //이름 중복 문제 해결 필요
                        StudentProfile profile = new StudentProfile(today, name);
                        studentList.add(profile);
                        adapter.setMemberListViewItemList(studentList) ;
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.create().show();
                break;
            case R.id.delete_student:
                SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                int count = adapter.getCount();
                for(int i = count-1;i>=0;i--){
                    if(checkedItems.get(i)){
                        String name = studentList.get(i).getName();
                        dbHelper.deleteStudent(name);
                        studentList.remove(i);
                    }
                }
                adapter.notifyDataSetChanged();
                listView.clearChoices();

                getActivity().setResult(05);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}