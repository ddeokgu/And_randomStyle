package com.example.randomstyle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.RadioAccessSpecifier;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.randomstyle.DTO.ClothDTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ItemsActivity extends AppCompatActivity {

    TextView txtMain, txtItems, txtCNo, txtCCategory, txtCPhoto_url;
    Button btnAdd, btnDelete;

    String userid;
    ArrayList<ClothDTO> list;
    RecyclerView rv;
    RecyclerView.Adapter myAdapter;




    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myAdapter.notifyDataSetChanged();
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        txtMain = (TextView) findViewById(R.id.txtMain);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        rv = findViewById(R.id.rv);

        String str = "오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtMain.setText(content);


        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
        myAdapter = new ItemsActivity.MyAdapter();
        rv.setAdapter(myAdapter);

        Intent intent = new Intent(ItemsActivity.this.getIntent());
        userid = intent.getStringExtra("userid");


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(ItemsActivity.this, ItemsAddActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });


    }

    class MyAdapter extends RecyclerView.Adapter<ItemsActivity.MyAdapter.ViewHolder> {

        @Override
        public ItemsActivity.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_row, parent, false);

            return new ItemsActivity.MyAdapter.ViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(ItemsActivity.MyAdapter.ViewHolder holder, int position) {
            holder.txtNo.setText(list.get(position).getNo()+"");
            holder.txtCategory.setText(list.get(position).getCategory());
            holder.txtPhoto_url.setText(list.get(position).getPhoto_url());

            holder.txtPhoto_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ItemsActivity.this, ItemsViewActivity.class);
                    intent.putExtra("no", holder.txtNo.getText().toString());
                    intent.putExtra("userid", userid);
                    startActivity(intent);
                }
                });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView txtNo, txtCategory, txtPhoto_url;
            public CheckBox checkBox;

            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                this.txtNo = view.findViewById(R.id.txtNo);
                this.txtCategory = view.findViewById(R.id.txtCategory);
                this.txtPhoto_url = view.findViewById(R.id.txtPhoto_url);

            }
            @Override
            public void onClick(View v) {

            }
        }
    }

    void cloth_list() {

        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    list = new ArrayList<ClothDTO>();
                    Log.i("test","userid=========" + userid);
                    String page = Common.SERVER_URL + "/randomStyle/cloth/and_cloth_all_list.do?userid="+userid;
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
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData");

                    for (int i = 0; i < jArray.length(); i++) {
                        ClothDTO dto = new ClothDTO();
                        JSONObject row = jArray.getJSONObject(i);
                        dto.setNo(row.getInt("no"));
                        dto.setCategory(row.getString("category"));
                        dto.setPhoto_url(row.getString("photo_url"));
                        list.add(dto);
                    }

                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        cloth_list();
    }
}


