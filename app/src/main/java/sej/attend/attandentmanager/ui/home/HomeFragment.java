package sej.attend.attandentmanager.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import sej.attend.attandentmanager.AttendListViewAdapter;
import sej.attend.attandentmanager.MainActivity;
import sej.attend.attandentmanager.R;
import sej.attend.attandentmanager.SQLiteHelper;
import sej.attend.attandentmanager.StudentInfo;
import sej.attend.attandentmanager.ModifyAttend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    int index;
    ListView listView;
    AttendListViewAdapter adapter;
    ArrayList<StudentInfo> studentInfoList = new ArrayList<>();

    SQLiteHelper dbHelper;

    SharedPreferences fine;
    SharedPreferences prefs;

    String today;
    String selected;
    String h_selected;
    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat h_sdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true); // 점 세개 메뉴 프레그먼트 보여주기

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //오늘날짜

        today = sdf.format(Calendar.getInstance(Locale.getDefault()).getTime());
        h_selected = h_sdf.format(Calendar.getInstance(Locale.getDefault()).getTime());

        fine = getActivity().getSharedPreferences("Fine", getActivity().MODE_PRIVATE); //저장된 벌금 파일
        prefs = getActivity().getSharedPreferences("Pref", getActivity().MODE_PRIVATE);
        // 액션바
        ActionBar ab = ((MainActivity)getActivity()).getSupportActionBar();
        ab.setTitle(h_selected + " 출결");

        dbHelper = new SQLiteHelper(getActivity()).getInstance(getActivity());
        dbHelper.open();

        checkFirstRun();

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
                intent.putExtra("selected", selected);

                intent.putExtra("e_state", studentInfoList.get(i).getState());
                intent.putExtra("e_late", studentInfoList.get(i).getLateMinutes());
                intent.putExtra("e_word", studentInfoList.get(i).getWrongWords());
                intent.putExtra("e_money", studentInfoList.get(i).getMoney());
                index = i;
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            /*
            case R.id.attend_all:
                break;
            case R.id.reset_today_attend:
                break;

             */
            case R.id.select_date:
                new DatePickerDialog(getActivity(),
                        myDatePicker, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
        return super.onOptionsItemSelected(item);
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
            studentInfoList = dbHelper.loadAttendByDate(selected);
            adapter.setListViewItemList(studentInfoList);
            adapter.notifyDataSetChanged();
        }

        if(requestCode == 05){
            studentInfoList = dbHelper.loadAttendByDate(today);
            adapter.setListViewItemList(studentInfoList);
            adapter.notifyDataSetChanged();
        }
    }

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            selected = sdf.format(myCalendar.getTime());
            h_selected = h_sdf.format(myCalendar.getTime());
            showSelectedAttend();
        }
    };

    public void showSelectedAttend(){
        ActionBar ab = ((MainActivity)getActivity()).getSupportActionBar();
        ab.setTitle(h_selected + " 출결");
        studentInfoList = dbHelper.loadAttendByDate(selected);
        adapter.setListViewItemList(studentInfoList);
        adapter.notifyDataSetChanged();
    }


    //앱 초기 실행 확인
    public void checkFirstRun(){
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(
                    "\n출결벌 어플은 소규모 단어스터디를 위한 어플입니다.\n" +
                    "틀린 단어, 지각, 결석에 각각 벌금이 있습니다.\n" +
                    "초기 벌금은 틀린 단어 1개 100원, 지각 1분 100원, 무단 결석 10000원, 예고결석 0원으로 설정되어 있으며, 설정 메뉴에서 변경할 수 있습니다. \n" +
                    "기타 사용법은 설정 메뉴에서 확인하세요.");
            builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog alert = builder.create();
            alert.setTitle("출결벌 어플에 관하여"); // dialog  Title
            alert.show();

            dbHelper.insertProfile(today, "김영희");
            dbHelper.insertProfile(today, "이철수");
            prefs.edit().putBoolean("isFirstRun", false).apply();
        }
    }


}