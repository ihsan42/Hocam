package com.egitimyazilim.iletisim.hocam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PositionActivity extends AppCompatActivity {

    SharedPreferences preferences;
    int positionNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        Button buttonManeger=(Button)findViewById(R.id.buttonManeger);
        Button buttonTeacher=(Button)findViewById(R.id.buttonTeacher);
        Button buttonParent=(Button)findViewById(R.id.buttonParent);

        preferences=getSharedPreferences("position",MODE_PRIVATE);
        positionNo=preferences.getInt("positionNo",-1);

        if(positionNo==0){
            startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            finish();
        }else if(positionNo==1){
            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
            intent.putExtra("position",1);
            startActivity(intent);
            finish();
        }

        buttonManeger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putInt("positionNo",0).commit();
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();
            }
        });

        buttonTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putInt("positionNo",1).commit();
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                intent.putExtra("position",1);
                startActivity(intent);
                finish();
            }
        });

        buttonParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putInt("positionNo",2).commit();
            }
        });
    }
}
