package com.example.attandentmanager.ui.last;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.attandentmanager.R;

public class LastFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true); // 점 세개 메뉴 프레그먼트

        View rootView = inflater.inflate(R.layout.fragment_last, container, false);

        return rootView;
    }

    // 점 세개
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.today_attandent, menu);
    }

    // 점 세게 메뉴 중 하나가 클릭되었을 때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.select_student :
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}