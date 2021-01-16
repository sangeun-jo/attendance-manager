package com.example.attandentmanager.ui.setting;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.IntentCompat;
import androidx.fragment.app.Fragment;

import com.example.attandentmanager.ChangeFine;
import com.example.attandentmanager.R;
import com.example.attandentmanager.SQLiteHelper;

public class SettingFragment extends Fragment {

    SQLiteHelper dbHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        dbHelper = new SQLiteHelper(getActivity()).getInstance(getActivity());


        String [] str = {
                "데이터 초기화",
                "벌금 액수 변경",
                "사용법"
        };
        ArrayAdapter adt = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, str);
        ListView listView = (ListView) rootView.findViewById(R.id.setting_list);
        listView.setAdapter(adt);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final EditText et = new EditText(getActivity());
                    builder.setView(et);
                    builder.setMessage("이 작업은 되돌릴 수 없습니다.\n데이터 삭제를 원하시면 '전체삭제'를 입력하세요.");
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String deleteConfirm = et.getText().toString();
                            if(deleteConfirm.equals("confirm") || deleteConfirm.equals("전체삭제")) {
                                dbHelper.deleteAll();
                                SharedPreferences prefs = getActivity().getSharedPreferences("Pref", getActivity().MODE_PRIVATE);
                                prefs.edit().putBoolean("isFirstRun", true).apply();
                            }
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.setTitle("삭제 확인"); // dialog  Title
                    alert.show();
                } else if(i == 1){ //벌금 바꾸기
                    changeFine();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(
                            "\n1. 출결 메뉴 색상 의미\n" +
                            "   회색: 출결 상태\n" +
                            "   파란색: 틀린 단어 개수\n" +
                            "   빨간색: 해당 날짜의 벌금\n" +
                            "\n2. 다른 날짜 출결 관리\n" +
                            "   우측 상단 달력 아이콘을 선택하여 이동하세요.\n" +
                            "\n3. 멤버 관리\n" +
                            "   멤버 추가: 멤버 메뉴로 이동 > 우측 상단 점 세개 클릭 > 멤버 추가\n"  +
                            "   멤버 삭제: 멤버 메뉴로 이동 > 우측 상단 점 세개 클릭 > 멤버 선택 > 삭제 모드 진입\n" +
                            "   ※멤버 이름 옆 회색 날짜는 등록일입니다\n" +
                            "\n4. 벌금 액수 변경\n" +
                            "   초기 벌금은 단어 1개 100원, 지각 1분 100원, 무단 결석 10000원, 예고 결석 0원으로 설정되어 있습니다. 변경 시점 이전의 내역에 대해서는 적용되지 않습니다.\n" +
                            "\n5. 데이터 초기화에 관하여\n" +
                            "   출결 데이터는 사용자의 폰에 저장되므로, 어플 삭제, 데이터 초기화 시 복구할 수 없습니다.\n" );

                    builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.setTitle("사용법"); // dialog  Title
                    alert.show();
                }
            }
        });


        return rootView;
    }

    public void changeFine() {
        final ChangeFine dialog = new ChangeFine(getActivity());
        dialog.setContentView(R.layout.change_fine);
        dialog.setTitle("벌금 액수 변경");
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams)params);
        dialog.setDialogListener(new ChangeFine.myListener() {

            @Override
            public void onPositiveClicked(int late, int word, int free_absent, int plan_absent) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Fine",getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("fineForWord", word);
                editor.putInt("fineForLate", late);
                editor.putInt("free_absent", free_absent);
                editor.putInt("plan_absent", plan_absent);
                editor.commit();
                dialog.dismiss();
            }

            @Override
            public void onNegativeClicked() {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}
