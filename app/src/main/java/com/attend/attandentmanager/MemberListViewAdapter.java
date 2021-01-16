package com.attend.attandentmanager;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class MemberListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    public ArrayList<StudentProfile> listViewItemList;
    private SparseBooleanArray mSelectedItemsIds;
    public boolean isCheck = false;

    // ListViewAdapter의 생성자
    public MemberListViewAdapter(ArrayList<StudentProfile> listViewItemList){
        this.listViewItemList = listViewItemList;
    }

    public void setMemberListViewItemList(ArrayList<StudentProfile> listViewItemList) {
        this.listViewItemList = listViewItemList;
    }

    public void setIsCheck(boolean isCheck){
        this.isCheck = isCheck;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.member_listview, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        //TextView date = convertView.findViewById(R.id.date);
        TextView registerDate = convertView.findViewById(R.id.regi_date);
        TextView studentName = convertView.findViewById(R.id.student_name) ;
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        //TextView studentState = convertView.findViewById(R.id.student_state);
        //TextView wrongWords = convertView.findViewById(R.id.wrong_words);
        //TextView fine = convertView.findViewById(R.id.fine);
        //TextView debt = convertView.findViewById(R.id.debt);


        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        StudentProfile listViewItem = listViewItemList.get(position);


        // 아이템 내 각 위젯에 데이터 반영
        //date.setText(listViewItem.getDate());
        registerDate.setText(listViewItem.getRegisterDate());
        studentName.setText(listViewItem.getName());
        if(isCheck == true){
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

        /*
        if(listViewItem.getState() == 1){
            studentState.setText("출석");
        } else if(listViewItem.getState() == 2){
            studentState.setText("지각: " + listViewItem.getLateMinutes() + "분");
        } else if(listViewItem.getState() == 3){
            studentState.setText("결석");
        } else if(listViewItem.getState() == 4){
            studentState.setText("예고결석");
        } else {
            studentState.setText("미체크");
        }
        */
        //wrongWords.setText("틀린 단어 개수: " + listViewItem.getWrongWords());

        //fine.setText("벌금: " + listViewItem.getFine() + "원");
        //debt.setText("입금: " + listViewItem.getDebt() + "원");

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

}