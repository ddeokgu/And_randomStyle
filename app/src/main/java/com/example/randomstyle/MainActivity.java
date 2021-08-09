package com.example.randomstyle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {


    TextView txtMain;
    Button btnUser;
    public static String userid;
    CheckBox checkOuter,checkTop,checkBottom,checkShoes;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMain = (TextView) findViewById(R.id.txtMain);
        btnUser = (Button) findViewById(R.id.btnUser);
        checkOuter = (CheckBox) findViewById(R.id.checkOuter);
        checkTop = (CheckBox) findViewById(R.id.checkTop);
        checkBottom = (CheckBox) findViewById(R.id.checkBottom);
        checkShoes = (CheckBox) findViewById(R.id.checkShoes);
        imageView = (ImageView) findViewById(R.id.imageView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        String str="오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(),0);
        txtMain.setText(content);

        Intent intent = new Intent(this.getIntent());
        userid = intent.getStringExtra("userid");
        btnUser.setText(userid);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = sendCheck(checkOuter, checkTop, checkBottom, checkShoes);
                Log.i("category=========123" , category);
                if(category == "") {
                    Toast.makeText(MainActivity.this, "카테고리를 선택하세요.",Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, RandomActivity.class);
                    intent.putExtra("userid", userid);
                    intent.putExtra("category", category);
                    startActivity(intent);
                }
            }
        });

    }
    public void Board(View v){
        Intent intent = null;
        intent = new Intent(this, BoardActivity.class);
        intent.putExtra("userid", userid);
        startActivity(intent);
    }
    public void User(View v) {
        Intent intent = null;
        intent = new Intent(this, UserActivity.class);
        intent.putExtra("userid", userid);
        startActivity(intent);
    }
    public void onBackPressed() {
        //super.onBackPressed();
    }
    public void btn_logout(View v) {
        new AlertDialog.Builder(this)
                .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .show();
    }

    private String sendCheck(CheckBox checkOuter, CheckBox checkTop, CheckBox checkBottom, CheckBox checkShoes) {
        String checked = "";
    if(checkOuter.isChecked()){
        checked += (checkOuter.getText().toString()+",");
    }
    if (checkTop.isChecked()) {
        checked += (checkTop.getText().toString()+",");

    }
    if (checkBottom.isChecked()) {
        checked += (checkBottom.getText().toString()+",");
    }
    if (checkShoes.isChecked()) {
        checked += (checkShoes.getText().toString());
    }
    String[] result = checked.split(",");
    String category = "";

    for (int i = 0; i<result.length; i++) {
        if (i == result.length-1) {
            category += result[i];
        } else {
            category +=(result[i]+",");
        }
    }
    return category;
    }


}