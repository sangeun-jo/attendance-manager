package sej.attend.attandentmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String dbname = "Attend.db";
    private static final int version = 2;

    private static SQLiteHelper INSTANCE;
    private static SQLiteDatabase mDb;

    public static SQLiteHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SQLiteHelper(context.getApplicationContext());
            mDb = INSTANCE.getWritableDatabase();
        }
        return  INSTANCE;
    }


    public void open() {
        if (mDb.isOpen() == false) {
            INSTANCE.onOpen(mDb);
        }
    }

    @Override
    public void close(){
        if(mDb.isOpen() == true) {
            INSTANCE.close();
        }
    }

    public SQLiteHelper(Context context) {
        super(context, dbname, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE attend (date TEXT, name TEXT, state INTEGER, late INTEGER, word INTEGER, fine INTEGER, money INTEGER);");
        db.execSQL("CREATE TABLE profile (regiDate TEXT, name TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS attend");
        db.execSQL("DROP TABLE IF EXISTS profile");
    }

    public void insertAttend(String date, String name, int state, int late, int word, int fine, int money){
        String sql = "INSERT INTO attend VALUES('" + date + "','" + name + "','" +  state + "','" + late + "','" + word + "','" + fine +  "','" + money +  "');";
        mDb.execSQL(sql);
    }

    public void insertProfile(String regiDate, String name){
        String sql = "INSERT INTO profile VALUES('" + regiDate + "','" + name + "');";
        mDb.execSQL(sql);
        insertAttend(regiDate, name, 0, 0, 0, 0, 0);
    }

    public void deleteAll(){ //모든 데이터 삭제
        mDb.execSQL("DELETE FROM attend;");
        mDb.execSQL("DELETE FROM profile;");
    }

    public void deleteAttendAll(){
        mDb.execSQL("DELETE FROM attend;");
    }

    public void deleteProfileAll(){
        mDb.execSQL("DELETE FROM profile;");
    }

    public void deleteStudent(String name){
        mDb.execSQL("DELETE FROM attend WHERE name = '" + name + "';");
        mDb.execSQL("DELETE FROM profile WHERE name = '" + name + "';");
    }


    public void modifyName(String new_name, String old_name){ // 새 이름/ 기존이름
        mDb.execSQL("UPDATE attend SET name = '" + new_name + "' WHERE name = '" + old_name+ "';");
        mDb.execSQL("UPDATE profile SET name = '" + new_name + "' WHERE name = '" + old_name+ "';");
    }

    //학생 프로필 불러오기
    public ArrayList<StudentProfile> loadProfile(){

        ArrayList<StudentProfile> studentProfileList = new ArrayList<>();
        String sql = "SELECT * FROM profile;";
        Cursor cursor = mDb.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            StudentProfile student = new StudentProfile(
                    cursor.getString(0), //등록 날짜
                    cursor.getString(1) //이름
            );
            studentProfileList.add(student);
        }

        cursor.close();

        return studentProfileList;
    }


    //학생 이름 불러오기
    public ArrayList<String>loadNames(){

        ArrayList<String> studentList = new ArrayList<>();
        String sql = "SELECT * FROM profile;";
        Cursor cursor = mDb.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String student = cursor.getString(1);
            studentList.add(student);
        }

        cursor.close();

        return studentList;
    }

    public ArrayList<StudentInfo>loadAttendByName(String name){
        ArrayList<StudentInfo> studentAllData = new ArrayList<>();
        String sql = "SELECT * FROM attend WHERE name = ? ORDER BY date DESC;";
        Cursor cursor = mDb.rawQuery(sql, new String[] {name});
        while (cursor.moveToNext()) {
            StudentInfo student = new StudentInfo(
                    cursor.getString(0), //날짜
                    cursor.getString(1), //이름
                    cursor.getInt(2), // 상태
                    cursor.getInt(3), // 지각 분
                    cursor.getInt(4), // 틀린단어
                    cursor.getInt(5), // 벌금
                    cursor.getInt(6) // 납입
            );
            studentAllData.add(student);
        }
        cursor.close();

        return studentAllData;
    }

    public StudentInfo cash(String date, String name){
        Cursor cursor = null;
        StudentInfo student = new StudentInfo(date, name, 0, 0, 0,0, 0);
        String sql = "SELECT * FROM attend WHERE (date = ? and name = ?);";
        if(cursor != null && cursor.isClosed()){
            cursor.close();
        }
        cursor = mDb.rawQuery(sql, new String[] {date, name});
        if(cursor.getCount() > 0) { //없으면 생성하기
            cursor.moveToNext();
            student.changeState(cursor.getInt(2));
            student.setLateMinutes(cursor.getInt(3));
            student.setWrongWords(cursor.getInt(4));
            student.setFine(cursor.getInt(5));
        }
        cursor.close();
        return student;
    }

    // 오늘 출석 데이터 반환
    public ArrayList<StudentInfo>loadAttendByDate(String today){

        Cursor cursor = null;
        ArrayList<StudentInfo> studentInfoList = new ArrayList<>();
        ArrayList<StudentProfile> pro = loadProfile();


        for(int i = 0; i < pro.size() ; i++){
            String sql = "SELECT * FROM attend WHERE (date = ? and name = ?);";
            //if(cursor != null && cursor.isClosed()){
            //    cursor.close();
            //}
            cursor = mDb.rawQuery(sql, new String[] {today, pro.get(i).getName()});

            if(cursor.getCount() <= 0) { //없으면 생성하기
                insertAttend(today, pro.get(i).getName(), 0, 0, 0, 0, 0);
                StudentInfo student = new StudentInfo(today, pro.get(i).getName(), 0, 0, 0, 0, 0);
                studentInfoList.add(student);
            } else { // 오늘날짜 레코드가 있으면 불러오기
                cursor.moveToNext();
                StudentInfo student = new StudentInfo(today, cursor.getString(1), 0, 0, 0,0, 0);
                student.changeState(cursor.getInt(2));
                student.setLateMinutes(cursor.getInt(3));
                student.setWrongWords(cursor.getInt(4));
                student.setFine(cursor.getInt(5));
                student.setMoney(cursor.getInt(6));
                studentInfoList.add(student);
            }
            cursor.close();
        }

        return studentInfoList;
    }

    public void modifyAttend(StudentInfo studentInfo, SharedPreferences fine, int state, int late, int word, int money){

        String sql = "UPDATE attend SET state = ?, late = ?, word = ?, fine = ?, money = ? WHERE (date = ? and name = ?);";

        int fineForWord = fine.getInt("fineForWord", 100);
        int fineForLate = fine.getInt("fineForLate", 100);
        int free_absent = fine.getInt("free_absent", 10000);
        int plan_absent = fine.getInt("plan_absent", 0);

        //0으로 초기화
        ResetRecode(studentInfo);

        int all_fine = 0;

        if (state == 1) { //출석
            //지각 에딧창 비활성화
            studentInfo.changeState(1);
            studentInfo.chageLateMinutes(0);
        } else if (state == 2) { //지각
            // 지각 에딧창 활성화
            if (late >= 0) { // 0 이상 입력되면
                studentInfo.changeState(2);
                studentInfo.setLateMinutes(late);
                all_fine = all_fine + studentInfo.getLateMinutes() * fineForLate;
            }
        } else if (state == 3) { //무단결
            studentInfo.changeState(3);
            studentInfo.setLateMinutes(0);
            studentInfo.setWrongWords(0);
            all_fine = all_fine + free_absent;
        } else { //예고 결
            studentInfo.changeState(4);
            studentInfo.setLateMinutes(0);
            studentInfo.setWrongWords(0);
            studentInfo.setFine(0);
            all_fine = all_fine + plan_absent;
        }

        if (word >= 0) {
            studentInfo.setWrongWords(word);
            all_fine = all_fine + studentInfo.getWrongWords() * fineForWord;
        }

        studentInfo.setFine(all_fine);

        if (money >= 0) {
            studentInfo.setMoney(money); //낸 돈
            System.out.println("디비 헬퍼에서 저장: " + studentInfo.getMoney());
        }


        mDb.execSQL(sql, new String[] {
                Integer.toString(studentInfo.getState()),
                Integer.toString(studentInfo.getLateMinutes()),
                Integer.toString(studentInfo.getWrongWords()),
                Integer.toString(studentInfo.getFine()),
                Integer.toString(studentInfo.getMoney()),
                studentInfo.getDate(), studentInfo.getName()
        });
    }


    public void ResetRecode(StudentInfo studentInfo){

        String sql = "UPDATE attend SET state = 0, late = 0, word = 0, fine = 0, money = 0 WHERE (date = ? and name = ?);";


        mDb.execSQL(sql, new String[]{
                studentInfo.getDate(), studentInfo.getName()}
                );
    }

}

