package com.example.attandentmanager.ui.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.example.attandentmanager.ChangeFine;
import com.example.attandentmanager.ModifyAttend;
import com.example.attandentmanager.R;
import com.example.attandentmanager.StudentProfile;

public class SettingFragment extends Fragment {
    SharedPreferences fine;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);


        String [] str = {
                "버전 2.0.0",
                "데이터 초기화",
                "벌금 액수 변경",
                "사용법",
                "문의"
        };
        ArrayAdapter adt = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, str);
        ListView listView = (ListView) rootView.findViewById(R.id.setting_list);
        listView.setAdapter(adt);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 2){ //벌금 바꾸기
                    changeFine();
                }
            }
        });


        return rootView;
    }

    public void changeFine() {
        final ChangeFine dialog = new ChangeFine(getActivity());
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


        });

        dialog.show();

    }
}
