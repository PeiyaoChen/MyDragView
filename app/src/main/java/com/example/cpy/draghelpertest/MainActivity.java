package com.example.cpy.draghelpertest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    boolean isShow = false;
    MyDragView dragView;
    View header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt = (Button)findViewById(R.id.show_bt);
        dragView = (MyDragView)findViewById(R.id.dragview);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    dragView.hide();
                    isShow = false;
                } else {
                    dragView.show();
                    isShow = true;
                }
            }
        });
        Button bt2 = (Button)findViewById(R.id.button);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "bt click", Toast.LENGTH_SHORT).show();
            }
        });
        dragView.setHeader(R.id.header);
        final View header = findViewById(R.id.header);
        final int middleHeight = getResources().getDisplayMetrics().heightPixels / 2;
        dragView.setMiddleDisHeight(middleHeight);
        dragView.setOnPositionChangedListener(new MyDragView.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int top) {
                int a = 0;
                if(top < dragView.getHeight() - middleHeight) {
                    a = 255;
                }
                else if(top < dragView.getHeight() - header.getHeight()){
                    a = (int) ((dragView.getHeight() - header.getHeight() - top) / (float)(middleHeight - header.getHeight()) * 255);
                }
                else {
                    a = 0;
                }
                int color = Color.argb(a, 255, 0, 0);
                header.setBackgroundColor(color);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
