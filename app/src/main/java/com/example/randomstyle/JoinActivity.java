package com.example.randomstyle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.randomstyle.DTO.MemberDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {
    TextView txtMain, txtJoin, txtName,txtId,txtPwd,txtPwdCheck,txtPhone,
            txtEmail,txtAt,txtAddress,txtZipcode,txtAddress2,txtIdCheckResult,txtPwdCheckResult;
    EditText editName,editId,editPwd, editPwdCheck,
                    editTel, editEId, editEAddress,editAddress,editAddress2;
    Button btnIdCheck, btnPwdCheck, btnEmailCheck, btnZipcode, btnJoin;
    Spinner spiPhone, spiEmail;
    WebView webView;


    Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        txtMain = (TextView) findViewById(R.id.txtMain);
        txtJoin = (TextView) findViewById(R.id.txtJoin);
        txtName = (TextView) findViewById(R.id.txtName);
        txtId = (TextView) findViewById(R.id.txtId);
        txtPwd = (TextView) findViewById(R.id.txtPwd);
        txtPwdCheck = (TextView) findViewById(R.id.txtPwdCheck);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtZipcode = (TextView) findViewById(R.id.txtZipcode);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtAddress2 = (TextView) findViewById(R.id.txtAddress2);
        txtAt = (TextView) findViewById(R.id.txtAt);
        txtIdCheckResult = (TextView) findViewById(R.id.txtIdCheckResult);
        txtPwdCheckResult = (TextView) findViewById(R.id.txtPwdCheckResult);


        editName = (EditText) findViewById(R.id.editName);
        editId = (EditText) findViewById(R.id.editId);
        editPwd = (EditText) findViewById(R.id.editPwd);
        editPwdCheck = (EditText) findViewById(R.id.editPwdCheck);
        editEId = (EditText) findViewById(R.id.editEId);
        editTel = (EditText) findViewById(R.id.editTel);
        editEAddress = (EditText) findViewById(R.id.editEAddress);
        editAddress = (EditText) findViewById(R.id.editAddress);
        editAddress2 = (EditText) findViewById(R.id.editAddress2);

        btnIdCheck = (Button) findViewById(R.id.btnIdCheck);
        btnPwdCheck = (Button) findViewById(R.id.btnPwdCheck);
        btnEmailCheck = (Button) findViewById(R.id.btnEmailCheck);
        btnZipcode = (Button) findViewById(R.id.btnZipcode);
        btnJoin = (Button) findViewById(R.id.btnJoin);

        spiPhone = (Spinner) findViewById(R.id.spiPhone);
        spiEmail = (Spinner) findViewById(R.id.spiEmail);
        webView = (WebView) findViewById(R.id.webView);



        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        String str="오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(),0);
        txtMain.setText(content);

        btnZipcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinActivity.this, ZipcodeActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        String at="@";
        txtAt.setText(at);

        spiEmail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        editEAddress.setText("");
                        break;
                    case 1:
                        editEAddress.setText("naver.com");
                        break;
                    case 2:
                        editEAddress.setText("gmail.com");
                        break;
                    case 3:
                        editEAddress.setText("hanmail.net");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnIdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = editId.getText().toString();
                if (userid.equals("")) {
                    Toast.makeText(JoinActivity.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String page = Common.SERVER_URL + "/randomStyle/member/and_id_check.do";
                                URL url = new URL(page);
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                String param = "userid=" + userid;
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
                                int result = Integer.parseInt(sb.toString().trim());
                                Log.i(this.getClass().getName(), "result==========" + result);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result != 0) {
                                            txtIdCheckResult.setTextColor(0xAAef484a);
                                            txtIdCheckResult.setText("사용할 수 없는 아이디입니다.");
                                        } else {
                                            txtIdCheckResult.setTextColor(0xAA1e6de0);
                                            txtIdCheckResult.setText("사용 가능한 아이디입니다.");
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    th.start();

                }
            }
        });

        btnPwdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pwd1 = editPwd.getText().toString();
                String pwd2 = editPwdCheck.getText().toString();

                if (pwd1.equals("") || pwd2.equals("")) {
                    Toast.makeText(JoinActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pwd1.equals(pwd2)) {
                                txtPwdCheckResult.setTextColor(0xAA1e6de0);
                                txtPwdCheckResult.setText("비밀번호 일치");
                            } else {
                                txtPwdCheckResult.setTextColor(0xAAef484a);
                                txtPwdCheckResult.setText("비밀번호를 확인해주세요.");
                            }
                        }
                    });

                }
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           String userid = editId.getText().toString();
           String passwd = editPwd.getText().toString();
           String name = editName.getText().toString();
           String phone = spiPhone.getSelectedItem().toString();
           String tel = editTel.getText().toString();
           String zipcode = txtZipcode.getText().toString();
           String email =  editEId.getText().toString()+"@"+editEAddress.getText().toString();
           String address1 = editAddress.getText().toString();
           String address2 = editAddress2.getText().toString();


                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String page = Common.SERVER_URL + "/randomStyle/member/and_sign_up.do";
                            URL url = new URL(page);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            StringBuilder sb = new StringBuilder();
                            String param = "name=" + name + "&userid=" + userid + "&passwd=" + passwd
                                    + "&phone=" + phone + "&tel=" + tel + "&email=" + email + "&zipcode=" + zipcode
                                    + "&address1=" + address1 + "&address2=" + address2;
                            Log.i(this.getClass().getName(), "param=========" + param);
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
                            String result = sb.toString();
                            Log.i(this.getClass().getName(), "result==========" + result);

                            Intent intent = null;
                            intent = new Intent(JoinActivity.this, MainActivity.class);
                            intent.putExtra("userid" , userid);
                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                th.start();


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100) {
            if(resultCode == 100) {
                txtZipcode.setText(data.getStringExtra("zipcode"));
                editAddress.setText(data.getStringExtra("address"));
            }
        }
    }
}

