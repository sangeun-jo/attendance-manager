package sej.attend.attandentmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ModifyAttend extends AppCompatActivity {

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
    SharedPreferences fine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_check);

        rg = (RadioGroup)findViewById(R.id.radioGroup1);
        wrongWord = findViewById(R.id.wrong_word);
        lateTime =findViewById(R.id.late_time);
        inputFine = findViewById(R.id.input_fine);

        fine = getSharedPreferences("Fine", MODE_PRIVATE); //저장된 벌금 파일

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        today = intent.getStringExtra("today");
        int e_late = intent.getIntExtra("e_late", 0);
        int e_word = intent.getIntExtra("e_word", 0);
        int e_money = intent.getIntExtra("e_money", 0);
        System.out.println("액티비티에서 머니 받음:" + e_money);

        if(e_late != 0){
            lateTime.setText(Integer.toString(e_late));
        }
        if (e_word != 0){
            wrongWord.setText(Integer.toString(e_word));
        }
        if (e_money != 0){
            inputFine.setText(Integer.toString(e_money));
        }

        ActionBar ab = getSupportActionBar();
        ab.setTitle(name+ " 씨의 출결 기록");

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

        int e_state = intent.getIntExtra("e_state", 0);
        System.out.println("상태: " + e_state);
        if(e_state == 2){
            radioGroup.check(R.id.late);
            wrongWord.setVisibility(View.VISIBLE);
            lateTime.setVisibility(View.VISIBLE);
            lateTime.setClickable(true);
        } else if(e_state == 3){
            radioGroup.check(R.id.ab_0);
            wrongWord.setVisibility(View.INVISIBLE);
            lateTime.setVisibility(View.INVISIBLE);
            wrongWord.setClickable(false);
            lateTime.setClickable(false);

        } else if(e_state == 4){
            radioGroup.check(R.id.ab_1);
            wrongWord.setVisibility(View.INVISIBLE);
            lateTime.setVisibility(View.INVISIBLE);
            wrongWord.setClickable(false);
            lateTime.setClickable(false);
        }

        radioGroup.setOnCheckedChangeListener(radioListener);

        ab.setDisplayHomeAsUpEnabled(true); //백버튼

    }

    RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.attend){
                wrongWord.setVisibility(View.VISIBLE);
                lateTime.setVisibility(View.INVISIBLE);
                lateTime.setClickable(false);
                wrongWord.setClickable(true);
                lateTime.setText("");
            } else if(i == R.id.late){
                wrongWord.setVisibility(View.VISIBLE);
                lateTime.setVisibility(View.VISIBLE);
                lateTime.setClickable(true);

            } else if(i == R.id.ab_0){
                wrongWord.setVisibility(View.INVISIBLE);
                lateTime.setVisibility(View.INVISIBLE);
                wrongWord.setClickable(false);
                lateTime.setClickable(false);
                wrongWord.setText("");
                lateTime.setText("");
            }else{
                wrongWord.setVisibility(View.INVISIBLE);
                lateTime.setVisibility(View.INVISIBLE);
                lateTime.setClickable(false);
                wrongWord.setClickable(false);
                wrongWord.setText("");
                lateTime.setText("");
            }
        }
    };

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
                } else if (value.equals("무단결")){
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
                result.putExtra("money", money);

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
