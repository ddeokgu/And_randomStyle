package com.example.randomstyle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RandomActivity extends AppCompatActivity {

    TextView txtMain, txtScreen;
    ImageView imgOuter, imgTop, imgBottom, imgShoes;

    String userid, category;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        txtMain = (TextView) findViewById(R.id.txtMain);
        txtScreen = (TextView) findViewById(R.id.txtScreen);
        imgOuter = (ImageView) findViewById(R.id.imgOuter);
        imgTop = (ImageView) findViewById(R.id.imgTop);
        imgShoes = (ImageView) findViewById(R.id.imgShoes);
        imgBottom = (ImageView) findViewById(R.id.imgBottom);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        String str="오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(),0);
        txtMain.setText(content);

        Intent intent = new Intent(this.getIntent());
        category = intent.getStringExtra("category");
        userid = intent.getStringExtra("userid");
        Log.i("category=============", category);

        random();


    }

    void random() {

        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL + "/randomStyle/random/and_random.do?param=" + category + "&userid=" +userid;
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

                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i(this.getClass().getName(),"sb=========" + jsonObj);

                    if(!jsonObj.isNull("TOP") && jsonObj.getString("TOP") != null) {
                        String Top = jsonObj.getString("TOP");
                        Log.i(this.getClass().getName(),"sb=========" + Top);
                        Top_photo(Top);
                    }
                    if(!jsonObj.isNull("OUTER") && jsonObj.getString("OUTER") != null) {
                        String Outer = jsonObj.getString("OUTER");
                        Log.i(this.getClass().getName(),"sb=========" + Outer);
                        Outer_photo(Outer);
                    }
                    if(!jsonObj.isNull("BOTTOM") &&jsonObj.getString("BOTTOM") != null) {
                        String Bottom = jsonObj.getString("BOTTOM");
                        Log.i(this.getClass().getName(),"sb=========" + Bottom);
                        Bottom_photo(Bottom);
                    }
                    if(!jsonObj.isNull("SHOES") &&jsonObj.getString("SHOES") != null) {
                        String Shoes =  jsonObj.getString("SHOES");
                        Log.i(this.getClass().getName(),"sb=========" + Shoes);
                        Shoes_photo(Shoes);
                    }







                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });

        th.start();

    }

    void Outer_photo(String Outer) {

        Thread th2 = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL + "/randomStyle/items/"+Outer;

                    URL url = new URL(page);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();


                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            imgOuter.setImageBitmap(bitmap);

                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        th2.start();

    }


    void Top_photo(String Top) {

        Thread th3 = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL + "/randomStyle/items/"+ Top;

                    URL url = new URL(page);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            imgTop.setImageBitmap(bitmap);

                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        th3.start();

    }


    void Bottom_photo(String Bottom) {

        Thread th4 = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL + "/randomStyle/items/"+Bottom;

                    URL url = new URL(page);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();


                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            imgBottom.setImageBitmap(bitmap);

                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        th4.start();

    }


    void Shoes_photo(String Shoes) {

        Thread th5 = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL + "/randomStyle/items/"+ Shoes;

                    URL url = new URL(page);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            imgShoes.setImageBitmap(bitmap);

                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        th5.start();

    }


}