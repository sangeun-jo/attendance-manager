package com.example.attandentmanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangeFine extends Dialog implements View.OnClickListener{

    private EditText editLate;
    private EditText editWord;
    private EditText editFreeAbsent;
    private EditText editPlanAbsent;

    private Button vBtn;

    private Context context;

    private myListener myListener;

    public ChangeFine(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    public interface myListener{
        void onPositiveClicked(int late, int word, int free_absent, int plan_absent);
    }

    //호출할 리스너 초기화
    public void setDialogListener(myListener myListener){
        this.myListener = myListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_fine);

        //init
        editLate = findViewById(R.id.edit_for_late); // 늦은 시간
        editWord = findViewById(R.id.edit_for_word); // 틀린 단어 개수
        editFreeAbsent = findViewById(R.id.edit_for_free_absent); // 무단 결석 벌금
        editPlanAbsent = findViewById(R.id.edit_for_plan_absent); // 예고 결석 벌금

        vBtn = findViewById(R.id.vButton); //뒤로가기 X 버튼

        //버튼 클릭 리스너 등록
        vBtn.setOnClickListener(this); //각 액티비티에서 리스너 생성시 기본 함수로 등록
        //액티비티에서 Override 하여 액티비티마다 다르게 동작시키게 할 수 있음

    }

    @Override
    public void onClick(View v) {
        int late = -1;
        int word = -1;
        int free_absent = -1;
        int plan_absent = -1;

        if(editLate.getText().length() > 0){
            late = Integer.parseInt(editLate.getText().toString());
        }

        if(editWord.getText().length() > 0){
            word = Integer.parseInt(editWord.getText().toString());
        }

        if (editFreeAbsent.getText().length() > 0) {
            free_absent = Integer.parseInt(editFreeAbsent.getText().toString());
        }

        if (editPlanAbsent.getText().length() > 0) {
            plan_absent = Integer.parseInt(editPlanAbsent.getText().toString());
        }

        //인터페이스의 함수를 호출하여 변수에 저장된 값들을 Activity로 전달
        myListener.onPositiveClicked(late,word,free_absent, plan_absent);
    }




}
