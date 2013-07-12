package com.msx7.android.diarydesign.ui;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

    class GridAdapter extends DragAdapter {
        ArrayList<String> arrayList = new ArrayList<String>();

        public GridAdapter() {
            super();
            for (int i = 0; i < 20; i++) {
                arrayList.add("" + i);
            }
        }

        @Override
        public void enableEdit(boolean isEdit) {

        }

        @Override
        public void insertPostion(int dragPosition, int insertPosition) {
            Log.d("MSG", "insertPostion-------->" +dragPosition+","+insertPosition);
            String[] arr=arrayList.toArray(new String[arrayList.size()]);
            String[] arr1=new String[arrayList.size()];
            if (dragPosition > insertPosition) {
                System.arraycopy(arr, 0, arr1, 0, insertPosition);
                System.arraycopy(arr, insertPosition, arr1, insertPosition+1, dragPosition-insertPosition);
                if(dragPosition+1<arr1.length)
                System.arraycopy(arr, dragPosition+1, arr1, dragPosition+1, arr1.length-1-dragPosition);
                arr1[insertPosition]=arr[dragPosition];
            } else {
                System.arraycopy(arr, 0, arr1, 0, dragPosition);
                System.arraycopy(arr, dragPosition+1, arr1, dragPosition, insertPosition-dragPosition);
                if(dragPosition+1<arr1.length)
                System.arraycopy(arr, dragPosition+1, arr1, dragPosition+1, arr1.length-1-dragPosition);
                arr1[insertPosition]=arr[dragPosition];
            }
            Log.d("MSG", "insertPostion-------->" +Arrays.toString(arr1));
            arrayList.clear();
            arrayList.addAll(Arrays.asList(arr1));
            notifyDataSetChanged();
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
            ((TextView) convertView).setText(position + "");
            return convertView;
        }

    }
}
