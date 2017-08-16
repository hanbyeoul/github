package com.example.jaey.month.Month;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jaey.month.R;

import java.util.ArrayList;

/*MonthActivity 와 MonthAddActivity 를 연결해주는 어댑터*/
class MonthListView {
    private int mId;
    private String content;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}

public class MonthAdapter extends BaseAdapter{
    private ArrayList<MonthListView> mList = new ArrayList<>();

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public MonthListView getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        final TextView text;

        //content_
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.month_list_item, parent, false);
        }

        //ListView에 들어갈 해당 textview를 가져와서 데이터 넣음
        text = (TextView)convertView.findViewById(R.id.list_text);

        //month_list_item에서 position에 위치한 데이터 참조 획득
        MonthListView monList = getItem(position);

        //아이템 내 textview에 데이터 반영
        text.setText(monList.getContent());

        return convertView;
    }

    //아이템 데이터 추가를 위한 함수
    public void addItem(int mid, String tv){
        MonthListView item = new MonthListView();
        item.setContent(tv);
        item.setmId(mid);
        mList.add(item);
    }

    public void clearItem() {
        mList.clear();
    }
}
