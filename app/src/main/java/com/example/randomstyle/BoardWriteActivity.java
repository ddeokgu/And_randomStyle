package com.example.randomstyle;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class BoardWriteActivity extends AppCompatActivity {

    TextView txtMain, txtUserid;
    EditText editTitle, editContents;
    String userid;
    Button btnWrite, btnPhoto1, btnPhoto2;
    ImageView imgPhoto1, imgPhoto2;
    URL url;
    Bitmap bitmap;

    String charset;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String result;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Toast.makeText(BoardWriteActivity.this, "업로드되었습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        txtMain = (TextView) findViewById(R.id.txtMain);
        txtUserid = (TextView) findViewById(R.id.txtUserid);
        btnPhoto1 = (Button) findViewById(R.id.btnPhoto1);
        btnPhoto2 = (Button) findViewById(R.id.btnPhoto2);
        imgPhoto1 = (ImageView) findViewById(R.id.imgPhoto1);
        imgPhoto2 = (ImageView) findViewById(R.id.imgPhoto2);
        btnWrite = (Button) findViewById(R.id.btnWrite);
        editTitle = (EditText) findViewById(R.id.editTitle);
        editContents = (EditText) findViewById(R.id.editContents);


        String str = "오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtMain.setText(content);

        Intent intent = new Intent(this.getIntent());
        userid = intent.getStringExtra("userid");
        txtUserid.setText(userid);


        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        try {
                            //내부메모리에 만들어진 파일
                            String file1 = "/data/data/" + getPackageName() +
                                    "/files/photo.jpg";
                            String file2 = "/data/data/" + getPackageName() +
                                    "/files/screenshot.jpg";
                            //서버에 업로드
                            fileUpload(file1, file2);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
        });
    }


    public void onClick(View v) {
        checkSelfPermission();
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnPhoto1:
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 100);
                break;

            case R.id.btnPhoto2:
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 101);
                break;

        }
    }

    void makeFile(String filename) {
        String path = "/data/data/" + getPackageName() + "/files";
        File dir = new File(path);
        //파일 전용 디렉토리 생성
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File("/data/data/" + getPackageName() +
                "/files/" + filename);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void fileUpload(final String file1, final String file2) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                httpFileUpload(Common.SERVER_URL + "/randomStyle/board/and_board_write.do", file1, file2);
            }
        });
        th.start();
    }

    void httpFileUpload(String urlString, String file1, String file2) {
        String title = editTitle.getText().toString();
        String contents = editContents.getText().toString();
        this.charset = charset;

        try {
            url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("file1", file1);
            conn.setRequestProperty("file2", file2);
            conn.setRequestProperty("title", title);
            conn.setRequestProperty("contents", contents);
            conn.setRequestProperty("userid", userid);

            conn.connect();

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            FileInputStream fis = new FileInputStream(file1);
            //image1 전송
            dos.writeBytes("\r\n--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"file1\";filename=\"" + file1 + "\"" + lineEnd);
            dos.writeBytes("Content-Type: application/octet-stream" + lineEnd);
            dos.writeBytes(lineEnd);
            int bytesAvailable = fis.available();
            int maxBufferSize = 4096;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = fis.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }
            fis.close();

            //image2 전송
            dos = new DataOutputStream(conn.getOutputStream());
            fis = new FileInputStream(file2);
            dos.writeBytes("\r\n--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"file2\";filename=\"" + file2 + "\"" + lineEnd);
            dos.writeBytes("Content-Type: application/octet-stream" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fis.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fis.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }
            fis.close();

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes("\r\n--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"title\"" + lineEnd);
//            dos.writeBytes("Content-Type: text/plain;" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(title + lineEnd);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes("\r\n--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"contents\"" + lineEnd);
//            dos.writeBytes("Content-Type: text/plain;" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(contents + lineEnd);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes("\r\n--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"userid\"" + lineEnd);
//            dos.writeBytes("Content-Type: text/plain;" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(userid);

            Log.i("test", "contents========"+ contents);

            Log.i("test", "title========"+ title);

            Log.i("test", "userid========"+ userid);

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            dos.flush();

            int ch;
            InputStream is = conn.getInputStream();
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            result = b.toString().trim();
            dos.close();
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri photoUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgPhoto1.setImageBitmap(bitmap);
            makeFile("photo.jpg"); //파일 생성
        } else if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            Uri photoUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgPhoto2.setImageBitmap(bitmap);
            makeFile("screenshot.jpg"); //파일 생성
        } else {
            Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_LONG).show();
        }
    }
}











