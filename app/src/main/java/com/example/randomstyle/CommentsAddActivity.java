package com.example.randomstyle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommentsAddActivity extends AppCompatActivity {

    TextView txtMain;
    EditText editComments;
    Button btnAdd;
    int no;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        editComments = (EditText) findViewById(R.id.editComments);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        txtMain = (TextView) findViewById(R.id.txtMain);

        String str = "오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtMain.setText(content);

        Intent intent = new Intent(this.getIntent());
        no = intent.getIntExtra("no", 0);
        userid = intent.getStringExtra("userid");


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final StringBuilder sb = new StringBuilder();
                Thread th3 = new Thread(new Runnable() {
                    String comments = editComments.getText().toString();
                    public void run() {
                        try {
                            String page = Common.SERVER_URL + "/randomStyle/comments/and_comments_write.do";
                            URL url = new URL(page);
                            HttpURLConnection conn = (HttpURLConnection)
                                    url.openConnection();
                            String param = "userid=" + userid +"&comments=" + comments + "&b_no=" + no;
                            Log.i(this.getClass().getName(), "row=========" + param);
                            if (conn != null) {
                                conn.setConnectTimeout(10000);
                                conn.setRequestMethod("POST");
                                conn.setUseCaches(false);
                                conn.getOutputStream().write(param.getBytes("utf-8"));
                                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    BufferedReader br =
                                            new BufferedReader(
                                                    new InputStreamReader(
                                                            conn.getInputStream(), "utf-8"));
                                    while (true) {
                                        String line = br.readLine();
                                        if (line == null) break;
                                        sb.append(line + "\n");
                                    }
                                    br.close();
                                }
                                conn.disconnect();
                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        finish();
                    }

                });
                th3.start();

            }

        });

    }
}