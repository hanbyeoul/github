package com.example.jaey.month.Month;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.jaey.month.R;

import java.util.Calendar;

public class MonthAddActivity extends Activity {
    int year_s, month_s, day_s; // 일정 시작 관련 년,월,일
    int year_f, month_f, day_f;  // 일정 종료 관련 년,월,일
    static final int DIALOG_ID_1 = 0; //시작 datepicker dialog_id
    static final int DIALOG_ID_2 = 1; //종료 datepicker dialog_id

    int hour_s, min_s; // 일정 시작 관련 시, 분
    int hour_f, min_f; // 일정 종료 관련 시, 분
    static final int DIALOG_ID_3 = 2; //시작 timepicker dialog_id
    static final int DIALOG_ID_4 = 3; //종료 timepicker dialog_id

    Button startDate, finishDate, startTime, finishTime;
    Calendar c;
    MonthDB mhelper;
    Intent intent;
    String estr; //edittext(입력내용)를 DB에 넣기위한 변수
    String mds, mts, mdf, mtf; // 시작 날짜/시각, 종료 날짜/시각을 DB에 넣기 위한 변수
    EditText edit;
    MonthAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_add_schedule);

        edit = (EditText)findViewById(R.id.month_edit);

        //오늘 날짜로 picker 기본값 세팅
        getCurrent();
        showDatePickerDialogClick();
        showTimePickerDialogClick();

        //ok버튼 누르면 database에 저장 후 이전 month화면으로 돌아감
        findViewById(R.id.month_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("cYear", year_s);
                intent.putExtra("cMonth", month_s);
                intent.putExtra("cDay", day_s);
                setResult(RESULT_OK, intent);
                finish();

                /* 작동이 안되서 우선 주석 처리
                SQLiteDatabase db = mhelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                estr = edit.getText().toString();
                if(estr.length() == 0)
                    Toast.makeText(MonthAddActivity.this, "일정이 입력되지 않아서 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
                else values.put("MonthListView",estr);
                db.insert("monthAddPlan",null,values);
                db.close();
                */
            }
        });

    }


    /*DatePickerDialog 작동관련함수
    * 버튼 클릭시 datePicker가 표시되고, 선택한 날짜가 반영됨*/
    public void showDatePickerDialogClick() {
        startDate = (Button) findViewById(R.id.month_start);
        finishDate = (Button) findViewById(R.id.month_finish);

        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID_1);
            }
        });
        finishDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID_2);
            }
        });
        setDayDate();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ID_1:
                return new DatePickerDialog(this, spickerListener, year_s, month_s - 1, day_s);
            case DIALOG_ID_2:
                return new DatePickerDialog(this, fpickerListener, year_f, month_f - 1, day_f);
            case DIALOG_ID_3:
                return new TimePickerDialog(this, tspickerListener, hour_s, min_s, false);
            case DIALOG_ID_4:
                return new TimePickerDialog(this, tfpickerListener, hour_f, min_f, false);
            default:
                return null;
        }
    }

    //Picker를 사용해 선택한 날짜로 year_s, month_s, day_s를 갱신(시작 날짜)
    private DatePickerDialog.OnDateSetListener spickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_s = year;
            month_s = month + 1;
            day_s = dayOfMonth;
            setDayDate();
        }
    };

    //Picker를 사용해 선택한 날짜로 year_f, month_f, day_f를 갱신(종료 날짜)
    private DatePickerDialog.OnDateSetListener fpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                year_f = year;
                month_f = month + 1;
                day_f = dayOfMonth;
                setDayDate();
        }
    };



    /*TimePicker 작동 관련 함수
    * 버튼 클릭시 time 선택할 수 있게 picker 나옴*/
    public void showTimePickerDialogClick() {
        startTime = (Button) findViewById(R.id.month_start_time);
        finishTime = (Button) findViewById(R.id.month_finish_time);

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID_3);
            }
        });
        finishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID_4);
            }
        });
        setTime();
    }


    //Picker를 사용해 선택한 시간으로 시작 time 갱신
    private TimePickerDialog.OnTimeSetListener tspickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_s = hourOfDay;
            min_s = minute;
            setTime();
        }
    };
    //Picker를 사용해 선택한 시간으로 종료 time 갱신
    private TimePickerDialog.OnTimeSetListener tfpickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_f = hourOfDay;
            min_f = minute;
            setTime();
        }
    };

    public void getCurrent() {
        c = Calendar.getInstance();
        year_s = c.get(Calendar.YEAR);
        month_s = c.get(Calendar.MONTH) + 1;
        day_s = c.get(Calendar.DAY_OF_MONTH);
        hour_s = c.get(Calendar.HOUR_OF_DAY);
        min_s = c.get(Calendar.MINUTE);

        year_f = year_s;
        month_f = month_s;
        day_f = day_s;

        hour_f = hour_s;
        min_f = min_s;
    }

    //버튼에 년,월,일을 입력하는 함수.
    public void setDayDate() {
        startDate.setText(year_s + "년 " + month_s + "월 " + day_s + "일"); //시작날짜 표시
        finishDate.setText(year_f + "년 " + month_f + "월 " + day_f + "일"); //종료날짜 표시

        //데이터베이스에 사용하기 위해 String 타입으로 변수 mds(month_day_start),mdf(month_day_finish) 지정
        int syear = year_s; int smonth = month_s; int sday= day_s;
        mds =  Integer.toString(syear) + Integer.toString(smonth) + Integer.toString(sday);

        int fyear = year_f; int fmonth = month_f; int fday= day_f;
        mdf =  Integer.toString(fyear) + Integer.toString(fmonth) + Integer.toString(fday);
    }

    //버튼에 시간을 입력하는 함수
    public void setTime() {
        startTime.setText(String.format("%02d:%02d",hour_s ,min_s)); //시작시각 표시
        finishTime.setText(String.format("%02d:%02d",hour_f ,min_f)); //종료시각 표시

        //데이터베이스에 사용하기 위해 String 타입으로 변수 mts(month_time_start),mtf(month_time_finish) 지정
        int shour = hour_s; int sminute = min_s;
        mts =  Integer.toString(shour) + Integer.toString(sminute);

        int fhour = hour_f; int fminute = min_f;
        mtf =  Integer.toString(fhour) + Integer.toString(fminute);
    }
}

