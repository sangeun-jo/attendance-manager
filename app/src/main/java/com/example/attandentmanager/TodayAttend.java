package com.example.attandentmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class TodayAttend extends AppCompatActivity {

    String name;
    String today;

    int state = 0;
    int word = 0;
    int late = 0;
    int money = 0;

    RadioGroup rg;
    EditText wrongWord;
    EditText lateTime;
    EditText inputFine;

    DBHelper dbHelper;

    SharedPreferences fine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_check);

        rg = (RadioGroup)findViewById(R.id.radioGroup1);
        wrongWord = findViewById(R.id.wrong_word);
        lateTime =findViewById(R.id.late_time);
        inputFine = findViewById(R.id.input_fine);



        dbHelper = new DBHelper(this, "Attend.db", null, 2);

        fine = getSharedPreferences("Fine", MODE_PRIVATE); //저장된 벌금 파일

        /* 에딧창 활성, 비활성 코드
        lateTime.setClickable(false);
        lateTime.setFocusable(false);
        lateTime.setFocusableInTouchMode (true);
        lateTime.setFocusable(true);
        */

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        today = intent.getStringExtra("today");

        ActionBar ab = getSupportActionBar();
        ab.setTitle(name+ " 씨의 출결 기록 편집");

        ab.setDisplayHomeAsUpEnabled(true); //백버튼

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.today_attend, menu); //메뉴 XML파일 인플레이션
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.modify_student:
                int id = rg.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(id);
                String value = rb.getText().toString();
                if (value.equals("출석")){
                    state = 1;
                } else if (value.equals("지각")){
                    state = 2;
                } else if (value.equals("무단 결석")){
                    state = 3;
                } else{
                    state = 4;
                }

                if(wrongWord.getText().length() > 0){
                    word = Integer.parseInt(wrongWord.getText().toString());
                }

                if(lateTime.getText().length() > 0){
                    late = Integer.parseInt(lateTime.getText().toString());
                }

                if(inputFine.getText().length() > 0){
                    money = Integer.parseInt(inputFine.getText().toString());
                }

                Intent result = new Intent();
                result.putExtra("state", state);
                result.putExtra("late", late);
                result.putExtra("word", word);
                result.putExtra("inputFine", money);

                setResult(03, result);

                finish();
                return true;

            case android.R.id.home: //뒤로가기
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
