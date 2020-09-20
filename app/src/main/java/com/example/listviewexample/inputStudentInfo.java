package com.example.listviewexample;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class inputStudentInfo extends Dialog implements View.OnClickListener{

    private EditText lateTime;
    private EditText wrongWord;
    private EditText inputMoney;

    private Button confirm;
    private Button cancel;
    private RadioGroup rg;

    private Context context;

    private myListener myListener;

    public inputStudentInfo(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    interface myListener{
        void onPositiveClicked(int state, int late, int word, int money);
        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(myListener myListener){
        this.myListener = myListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_student_info);

        //init
        lateTime = findViewById(R.id.late_time); // 늦은 시간
        wrongWord = findViewById(R.id.wrong_word_num); // 틀린 단어 개수
        inputMoney = findViewById(R.id.today_input_money); // 오늘 입금한 돈

        confirm = findViewById(R.id.confirm_button); //확인 버튼
        cancel = findViewById(R.id.cancel_button); //취소 버튼

        rg = findViewById(R.id.state);

        //버튼 클릭 리스너 등록
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm_button: //확인 버튼을 눌렀을 때
                //각각의 변수에 EidtText에서 가져온 값을 저장
                int late = -1;
                int word = -1;
                int money = -1;

                if(lateTime.getText().length() > 0){
                    late = Integer.parseInt(lateTime.getText().toString());
                }

                if(wrongWord.getText().length() > 0){
                    word = Integer.parseInt(wrongWord.getText().toString());
                }

                if (inputMoney.getText().length() > 0) {
                    money = Integer.parseInt(inputMoney.getText().toString());
                }

                RadioButton rb = findViewById(rg.getCheckedRadioButtonId());

                int state = 0;

                if( rb.getText().toString().equals("출석") ){
                    state = 1;
                } else if (rb.getText().toString().equals("지각")){
                    state = 2;
                } else if (rb.getText().toString().equals("무단")){
                    state = 3;
                } else{
                    state = 4;
                }

                //인터페이스의 함수를 호출하여 변수에 저장된 값들을 Activity로 전달
                myListener.onPositiveClicked(state, late,word,money);
                dismiss();
                break;

            case R.id.cancel_button: //취소 버튼을 눌렀을 때
                myListener.onNegativeClicked();
                dismiss();
                break;
        }
    }
}

