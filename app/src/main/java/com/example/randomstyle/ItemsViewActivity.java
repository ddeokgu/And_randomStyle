package com.example.randomstyle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ItemsViewActivity extends AppCompatActivity {


    TextView txtMain;
    ImageView imgCloth;
    Button btnDelete;

    String photo1_url;
    String no;
    String userid;

    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_view);

        txtMain = (TextView) findViewById(R.id.txtMain);
        imgCloth = (ImageView) findViewById(R.id.imgCloth);
        btnDelete = (Button) findViewById(R.id.btnDelete);


        String str = "오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtMain.setText(content);

        Intent intent = new Intent(ItemsViewActivity.this.getIntent());
        no = intent.getStringExtra("no");
        userid = intent.getStringExtra("userid");
        Log.i("no==============", no);
        detail();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_cloth();
            }
        });

    }

    void detail() {

        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL + "/randomStyle/cloth/and_cloth_detail.do?no=" + no;
                    URL url = new URL(page);
                    HttpURLConnection conn = (HttpURLConnection)
                            url.openConnection();
                    if (conn != null) {
                        conn.setConnectTimeout(10000);
                        conn.setUseCaches(false);
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

                    photo1_url = sb.toString();
                    Log.i(this.getClass().getName(), "photo1_url========="+photo1_url);

                    photo();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });

        th.start();

    }


    void photo() {

        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL +"/randomStyle/items/"+photo1_url;
                    Log.i(this.getClass().getName(), "page======" + page);
                    URL url = new URL(page);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();


                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                    Log.i(this.getClass().getName(), "bitmap=======" + bitmap);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            imgCloth.setImageBitmap(bitmap);

                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        th.start();

    }


    void delete_cloth() {
        AlertDialog.Builder myAlertBuilder =
                new AlertDialog.Builder(ItemsViewActivity.this);
        myAlertBuilder.setMessage("삭제하시겠습니까?");
        myAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final StringBuilder sb = new StringBuilder();
                Thread th = new Thread(new Runnable() {
                    public void run() {
                        try {
                            String page = Common.SERVER_URL + "/randomStyle/cloth/cloth_delete.do?no="+no+"&userid=" +userid;
                            URL url = new URL(page);
                            HttpURLConnection conn = (HttpURLConnection)
                                    url.openConnection();

                            if (conn != null) {
                                conn.setConnectTimeout(10000);
                                conn.setUseCaches(false);
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
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                th.start();
            }
        });
        myAlertBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        myAlertBuilder.show();
    }
}