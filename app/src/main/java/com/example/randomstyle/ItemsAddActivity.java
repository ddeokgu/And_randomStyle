package com.example.randomstyle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ItemsAddActivity extends AppCompatActivity {

    TextView txtMain;
    Button btnAdd;
    String userid;
    Uri uri;
    File file1;
    RadioButton radioOuter,radioTop,radioBottom,radioShoes;
    RadioGroup RG;
    ImageView imgItems;

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String category;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        txtMain = (TextView) findViewById(R.id.txtMain);
        RG = (RadioGroup) findViewById(R.id.RG);
        radioOuter = (RadioButton) findViewById(R.id.radioOuter);
        radioTop = (RadioButton) findViewById(R.id.radioTop);
        radioBottom = (RadioButton) findViewById(R.id.radioBottom);
        radioShoes = (RadioButton) findViewById(R.id.radioShoes);
        imgItems = (ImageView) findViewById(R.id.imgItems);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        String str = "오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtMain.setText(content);

        Intent intent = new Intent(this.getIntent());
        userid = intent.getStringExtra("userid");

        RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.radioOuter) {
                    category = radioOuter.getText().toString();
                }
                if(checkedId == R.id.radioTop) {
                    category = radioTop.getText().toString();
                }
                if(checkedId == R.id.radioBottom) {
                    category = radioBottom.getText().toString();
                }
                if(checkedId == R.id.radioShoes) {
                    category = radioShoes.getText().toString();
                }

                Log.i("category========" , category);
                Log.i("category========" , userid);
            }
        });





        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String page = Common.SERVER_URL + "/randomStyle/cloth/and_cloth_insert.do";
                            URL url = new URL(page);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            conn.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary="+boundary);
                            conn.setRequestMethod("POST");
                            conn.setDoInput(true);
                            conn.setDoOutput(true);
                            conn.setUseCaches(false);
                            conn.setConnectTimeout(10000);

                            OutputStream outputStream = conn.getOutputStream();
                            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "utf-8"), true);

                            writer.append("--" + boundary).append(lineEnd);
                            writer.append("Content-Disposition: form-data; name=\"userid\"").append(lineEnd);
                            writer.append("Content-Type: text/plain; charset=utf-8").append(lineEnd);
                            writer.append(lineEnd);
                            writer.append(userid).append(lineEnd);
                            writer.flush();

                            writer.append("--" + boundary).append(lineEnd);
                            writer.append("Content-Disposition: form-data; name=\"category\"").append(lineEnd);
                            writer.append("Content-Type: text/plain; charset=utf-8").append(lineEnd);
                            writer.append(lineEnd);
                            writer.append(category).append(lineEnd);
                            writer.flush();

                            writer.append("--" + boundary).append(lineEnd);
                            writer.append("Content-Disposition: form-data; name=\"file1\"; filename=\""+file1.getName()+"\"").append(lineEnd);
                            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file1.getName())).append(lineEnd);
                            writer.append("Content-Transfer-Encoding: binary").append(lineEnd);
                            writer.append(lineEnd);
                            writer.flush();


                            FileInputStream inputStream = new FileInputStream(file1);

                            byte[] buffer1 = new byte[(int)file1.length()];


                            int bytesRead = -1;
                            while((bytesRead = inputStream.read(buffer1)) != -1){
                                outputStream.write(buffer1, 0, bytesRead);
                            }
                            outputStream.flush();


                            inputStream.close();

                            writer.append(lineEnd);
                            writer.flush();

                            writer.append("--" + boundary + "--").append(lineEnd);
                            writer.close();

                            int responseCode = conn.getResponseCode();
                            Log.e("BoardWriteActivity.java", "성공기원: "+responseCode );

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    finish();
                    }
                });
                th.start();


            }
        });

    }

    public void onClick(View v) {
        checkSelfPermission();
        Intent intent = new Intent();
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
        }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            int length = permissions.length;
            for(int i = 0; i < length; i++) {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("test" , "권한허용:" + permissions[i]);
                }
            }
        }
    }

    public void checkSelfPermission(){
        String temp = "";
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";

        }

        if (TextUtils.isEmpty(temp) == false) {
            ActivityCompat.requestPermissions(this, temp.trim().split(" "), 1);
        } else {
            Toast.makeText(this, "권한을 모두 허용" , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();
            Cursor cursor = null;

            try{
                String[] proj = {MediaStore.Images.Media.DATA};

                assert uri != null;
                cursor = getContentResolver().query(uri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                String filePath = cursor.getString(column_index);
                file1 = new File(filePath);

            }finally{
                if(cursor != null){
                    cursor.close();
                }
            }

            imgItems.setImageURI(uri);
        }

    }
}