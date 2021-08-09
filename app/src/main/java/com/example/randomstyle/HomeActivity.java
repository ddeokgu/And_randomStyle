package com.example.randomstyle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HomeActivity extends AppCompatActivity {
    TextView txtMain, txtId, txtPwd, txtLoginResult;
    EditText editId, editPwd;
    ImageView imgMain;
    Button btnLogin, btnJoin;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        txtMain = (TextView) findViewById(R.id.txtMain);
        txtId = (TextView) findViewById(R.id.txtId);
        txtPwd = (TextView) findViewById(R.id.txtPwd);
        txtLoginResult = (TextView) findViewById(R.id.txtLoginResult);
        editId = (EditText) findViewById(R.id.editId);
        editPwd = (EditText) findViewById(R.id.editPwd);
        imgMain = (ImageView) findViewById(R.id.imgMain);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnJoin = (Button) findViewById(R.id.btnJoin);

        String str = "오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtMain.setText(content);


    }

    public void Join(View v) {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }

    public void Login(View v) {

        String id = editId.getText().toString();
        String pwd = editPwd.getText().toString();
        if (id == "") {
            Toast.makeText(this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
        } else if (pwd == "") {
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String page = Common.SERVER_URL + "/randomStyle/member/and_login_check.do";
                    URL url = new URL(page);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    String param = "userid=" + editId.getText().toString()
                            + "&passwd=" + editPwd.getText().toString();
                    StringBuilder sb = new StringBuilder();
                    if (conn != null) {
                        conn.setConnectTimeout(10000);
                        conn.setRequestMethod("POST");
                        conn.setUseCaches(false);
                        conn.getOutputStream().write(param.getBytes("utf-8"));
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(
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
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    String name = jsonObj.getString("name");

                    if (name != null && !name.equals("null")) {
                        String userid = editId.getText().toString();
                        Intent intent = null;
                        intent = new Intent(HomeActivity.this, MainActivity.class);
                        intent.putExtra("userid",userid);
                        startActivity(intent);
                    } else {
                        result = "아이디 또는 비밀번호가 일치하지 않습니다.";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtLoginResult.setText(result);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        th.start();



    }
}