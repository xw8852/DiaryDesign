package com.msx7.android.diarydesign.ui;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msx7.android.diarydesign.R;
import com.msx7.android.diarydesign.ui.widget.DragGridView;
import com.msx7.android.diarydesign.ui.widget.DragGridView.DragAdapter;

public class SplashActivity extends Activity {

    DragGridView mDragGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mDragGridView = (DragGridView) findViewById(R.id.dragGridView1);
        mDragGridView.setAdapter(new GridAdapter());
        mDragGridView.setDrag(true);
    }

    class GridAdapter extends DragAdapter<String> {
        ArrayList<String> arrayList = new ArrayList<String>();

        public GridAdapter() {
            super();
            for (int i = 0; i < 100; i++) {
                arrayList.add("" + i);
            }
        }

        @Override
        public String[] getData() {
            return arrayList.toArray(new String[arrayList.size()]);
        }

        @Override
        public void setData(String[] arr) {
            arrayList.clear();
            arrayList.addAll(Arrays.asList(arr));
        }

        @Override
        public void enableEdit(boolean isEdit) {

        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public String getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            ((TextView) convertView).setText(getItem(position));
            convertView.setBackgroundResource(R.drawable.shape_dotted_box);
            return convertView;
        }

    }
}
