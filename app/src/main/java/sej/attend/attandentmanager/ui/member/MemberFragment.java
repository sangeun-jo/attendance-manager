package sej.attend.attandentmanager.ui.member;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import sej.attend.attandentmanager.MainActivity;
import sej.attend.attandentmanager.MemberListViewAdapter;
import sej.attend.attandentmanager.R;
import sej.attend.attandentmanager.SQLiteHelper;
import sej.attend.attandentmanager.StudentInfo;
import sej.attend.attandentmanager.StudentProfile;

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
    ActionMode mActionMode;
    ActionBar ab;
    SharedPreferences fine;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_member, container, false);

        setHasOptionsMenu(true); // 점 세개 메뉴 프레그먼트 보여주기

        dbHelper = new SQLiteHelper(getActivity()).getInstance(getActivity());
        fine = getActivity().getSharedPreferences("Fine", getActivity().MODE_PRIVATE); //저장된 벌금 파일

        ab = ((MainActivity)getActivity()).getSupportActionBar();

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
                if(mActionMode == null) {
                    selectJob(i);
                }
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
                builder.setTitle("멤버 이름 입력");
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
            case R.id.select_student:
                adapter.setIsCheck(true);
                if(mActionMode == null){
                    mActionMode = getActivity().startActionMode(mActionCallback);
                    mActionMode.setTitle("삭제 ");
                    listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE); //선택모드 on
                }
                //액션바 숨기기
                ab.hide();
                adapter.notifyDataSetChanged();
                getActivity().setResult(05);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 통계 얻기
    public int[] calStatistic(ArrayList<StudentInfo> student){

        int allAttend = 0; //출석 횟수
        int allLate = 0; //지각 분
        int allWrong = 0;//틀린단어
        int allFine = 0;  // 벌금
        int allFreeAbsent = 0; // 무단결
        int allPlanAbsent = 0;// 예고결
        int allDebt = 0; // 미납

        int late = fine.getInt("fineForWord", 100);
        int word = fine.getInt("fineForLate", 100);
        int free_absent = fine.getInt("free_absent", 10000);
        int plan_absent = fine.getInt("plan_absent", 0);

        int state = 0;

        for(int i=0; i < student.size(); i++){
            state = student.get(i).getState();
            allWrong += student.get(i).getWrongWords();
            allLate += student.get(i).getLateMinutes();
            allDebt += student.get(i).getDebt();
            if(state == 3){
                allFreeAbsent++;
            }
            if (state == 4){
                allPlanAbsent++;
            }
            if(state == 1){
                allAttend++;
            }

            allFine = allWrong * word + allLate * late + allFreeAbsent * free_absent + allPlanAbsent * plan_absent;

        }

        int[] values = {allAttend, allWrong, allLate, allFreeAbsent, allPlanAbsent, allFine, allDebt};

        return values;
    }

    ActionMode.Callback mActionCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.clicked_select_student, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.select_all:
                    final int checkedCount = studentList.size();

                    //항목을 이미 선택하거나 선택한 경우 제거하거나 선택 취소 후 다시 모두 선택
                    adapter.removeSelection();

                    for (int i = 0; i < checkedCount; i++) {
                        listView.setItemChecked(i, true);
                    }

                    mode.setTitle(checkedCount + "  개 선택됨");
                    return true;
                case R.id.delete_student:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("이 작업은 되돌릴 수 없습니다.");
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                            int count = adapter.getCount();
                            for(int i = count-1;i>=0;i--) {
                                if (checkedItems.get(i)) {
                                    String name = studentList.get(i).getName();
                                    dbHelper.deleteStudent(name);
                                    studentList.remove(i);
                                }
                            }
                            mode.finish();
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.setTitle("삭제 확인"); // dialog  Title
                    alert.show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            adapter.removeSelection();
            adapter.setIsCheck(false);
            listView.clearChoices();
            adapter.notifyDataSetChanged();
            listView.setChoiceMode(AbsListView.CHOICE_MODE_NONE); //선택모드 off
            mActionMode = null;
            ab.show();
        }
    };

    public void selectJob(final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] choices = {"이름 수정", "통계 보기"};
        builder.setItems(choices, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                String selected = choices[i];
                if(selected.equals("이름 수정")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("변경할 이름을 입력하세요");
                    final EditText et = new EditText(getActivity());
                    builder.setView(et);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String name = et.getText().toString();
                            //스튜던트 리스트에서 기존 데이터 수정
                            dbHelper.modifyName(name, studentList.get(pos).getName());
                            studentList.get(pos).setName(name);
                            adapter.setMemberListViewItemList(studentList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    builder.create().show();
                } else {
                    ArrayList<StudentInfo> student = dbHelper.loadAttendByName(studentList.get(pos).getName());
                    int [] statistic = calStatistic(student);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("출결 통계");

                    builder.setMessage("\n총 출석: " + statistic[0] + "회\n" +
                            "\n총 지각: " + statistic[2] + "분\n"+
                            "\n총 결석: " + (statistic[3] + statistic[4]) + "회\n" +
                            "\n총 틀린 단어: " + statistic[1] + "개\n"+
                            "\n총 벌금: " + statistic[5] + "원\n"+
                            "\n총 미납금: " + statistic[6] +"원\n");
                    builder.setNegativeButton("닫기", null);
                    builder.create().show();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}