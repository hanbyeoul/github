package com.example.jaey.month;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.jaey.month.Month.CalendarAdapter;
import com.example.jaey.month.Month.DayInfo;
import com.example.jaey.month.Month.MonthAdapter;
import com.example.jaey.month.Month.MonthAddActivity;
import com.example.jaey.month.Month.MonthDB;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<DayInfo> list;
    CalendarAdapter calendarAdapter;
    Calendar c; //현재 날짜를 가져오기 위해 Calendar 사용
    int year, month, day; //년, 월, 일을 저장하는 변수 지정
    TextView dayDate; //사용자가 원하는 년, 월, 일을 저장해 띄우는 TextView
    MonthAdapter monthAdapter;
    MonthDB mhelper;
    ListView  scheduleList;
    String sdate; //DB에서 사용하기 위한 날짜
    PopupWindow window;
    ViewGroup.LayoutParams layoutParmas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_floating_action);

        gridView = (GridView) findViewById(R.id.gridview);

        getCurrentDate();
        setDayDate();  //getCurrentDate로 오늘 날짜를 가져온 뒤, 그 값을 dayDate에 입력(아무 버튼도 누르지 않은 기본 상태)

        //오늘날짜 세팅
        System.currentTimeMillis();
        list = new ArrayList<>();

        // +버튼 클릭 시 일정추가하는 액티비티로 전환
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MonthActivity.this, MonthAddActivity.class));
            }
        });

        //그리드뷰 아이템 클릭 시 일정 표시, 팝업으로 일정을 보여줌
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(window == null) {
                    layoutParmas = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //팝업으로 띄울 커스텀뷰 생성
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.month_show_schedule, null);
                    window  = new PopupWindow(v, 300, 370, true);
                    window.showAtLocation(v, Gravity.CENTER, 0 , 0);
                    //window.setContentView(v);

                    //취소버튼 누르면 팝업창 닫기
                    findViewById(R.id.plan_cancel).setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           window.dismiss();
                       }
                   });

                    //수정버튼 누르면 수정페이지로 넘어가기
                    findViewById(R.id.plan_edit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                }
                //팝업 뷰 이외에도 터치되게 함(터치시 팝업 닫기 위한 코드)
                window.setOutsideTouchable(true);
                //팝업 뷰 터치 가능하게 함
                window.setTouchable(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //이번달의 캘린더를 생성
        c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        getCalendar(c);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //버튼클릭 시 반응
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.month_left: // 이전 달로 이동
                c = getLastMonth(c);
                getCalendar(c);
                break;
            case R.id.month_right: // 다음 달로 이동
                c = getNextMonth(c);
                getCalendar(c);
                break;
            case R.id.day_today: //오늘 날짜 반환
               //   String sToday = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
               //  if (sToday.equals(gridView.getAdapter().getItem(pos) {

                //    holder.tvItemDay.setTextColor(Color.rgb(150, 190, 233));
                //}
                getCurrentDate();
                setDayDate();
                getCalendar(c);
                break;
        }
    }

    public void getCurrentDate() {  //오늘 날짜를 가져오는 함수. Calendar 사용.
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        //day = c.get(Calendar.DAY_OF_MONTH);
    }

    public void setDayDate() {  //TextView에 년, 월, 일을 입력하는 함수
        dayDate = (TextView) findViewById(R.id.day_date);
        dayDate.setText(c.get(Calendar.YEAR) + "년 " + (c.get(Calendar.MONTH) + 1) + "월 ");
    }

    /*달력세팅
    * @param calendar 달력에 보여지는 이번달의 Calendar 객체
    * */
    private void getCalendar(Calendar calendar) {
        int lastMonthStartDay;
        int dayOfMonth;
        int thisMonthLastDay;

        list.clear();

        /*이번달의 시작일의 요일 구하기*/
        dayOfMonth = c.get(Calendar.DAY_OF_WEEK);
        thisMonthLastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        c.add(Calendar.MONTH, -1);
        Log.e("지난달 마지막일", c.get(Calendar.DAY_OF_MONTH) + "");
        //지난달의 마지막 일자를 구함
        lastMonthStartDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        c.add(Calendar.MONTH, 1);
        Log.e("이번달 시작일", calendar.get(Calendar.DAY_OF_MONTH) + "");

        lastMonthStartDay -= (dayOfMonth - 1) - 1;

        //캘린더 년월 표시
        dayDate.setText(c.get(Calendar.YEAR) + "년 " + (c.get(Calendar.MONTH) + 1) + "월 ");

        DayInfo day;
        Log.e("DayOfMonth", dayOfMonth + "");

        /*캘린더에 들어갈 숫자 입력
        * 저번달의 날짜를 회색으로 표시
        * 이번달의 날짜들을 표시
        * 다음달의 날짜들을 표시(총 7*6 =42칸을 기준으로 함)*/
        for (int i = 0; i < dayOfMonth - 1; i++) {
            int date = lastMonthStartDay + i;
            day = new DayInfo();
            day.setDay(Integer.toString(date));
            day.setInMonth(false);
            list.add(day);
        }
        for (int i = 1; i <= thisMonthLastDay; i++) {
            day = new DayInfo();
            day.setDay(Integer.toString(i));
            day.setInMonth(true);
            list.add(day);
        }
        for (int i = 1; i < 42 - (thisMonthLastDay + dayOfMonth - 1) + 1; i++) {
            day = new DayInfo();
            day.setDay(Integer.toString(i));
            day.setInMonth(false);
            list.add(day);
        }
        initCalendarAdapter();
    }

    /*지난달의 Calendar 객체 반환
    * @param calendar
    * @return LastMonthCalendar
    * */
    private Calendar getLastMonth(Calendar calendar) {
        calendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, -1);
        setDayDate();
        return calendar;
    }

    /*다음음달의 Calendar 객체 반환
 * @param calendar
 * @return NextMonthCalendar
 * */
    private Calendar getNextMonth(Calendar calendar) {
        calendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, +1);
        setDayDate();
        return calendar;
    }

    private void initCalendarAdapter() {
        calendarAdapter = new CalendarAdapter(this, R.layout.content_month_item, list);
        gridView.setAdapter(calendarAdapter);
    }

    // MonthAddActivity에서 보낸 intent를 받는 함수. 해당 날짜의 스케줄을 달력에 표시함
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            year = data.getExtras().getInt("cYear");
            month = data.getExtras().getInt("cMonth");
            day = data.getExtras().getInt("cDay");
            sdate = Integer.toString(year) + Integer.toString(month) + Integer.toString(day);
            setSchedule(); // 내용을 보여줌
       }
    }

    //DB에서 가져온 내용 가져오는 함수
    public void setSchedule() {
        scheduleList = (ListView)findViewById(R.id.list);
        SQLiteDatabase db = mhelper.getReadableDatabase();
        Cursor c = db.rawQuery("select _id, content from MonthAddPlan where sdate = '"+ sdate +"';", null);
        while(c.moveToNext()){
            monthAdapter.addItem(c.getInt(0), c.getString(1));
        }
        c.close();
        db.close();
        monthAdapter.notifyDataSetChanged();
    }
}

