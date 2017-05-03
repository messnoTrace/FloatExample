package com.notrace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button suspend;

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }
    private void initUI() {
        suspend = (Button)findViewById(R.id.suspend);
        suspend.setOnClickListener(new suspendListener());
        text = (TextView)findViewById(R.id.text);
        text.setText(MyWindowManager.getUsedPercentValue(getApplicationContext()));
    }

    public class suspendListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,WindowService.class);
            startService(intent);
            finish();
        }
    }
}
