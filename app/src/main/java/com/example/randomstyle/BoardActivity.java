package com.example.randomstyle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randomstyle.DTO.BoardDTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity {

    TextView txtMain, txtBoard;
    Button btnWrite;

    ArrayList<BoardDTO> items;
    RecyclerView rv;
    RecyclerView.Adapter myAdapter;

    int no;
    String userid;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myAdapter = new MyAdapter();
            rv.setAdapter(myAdapter);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        txtMain = (TextView) findViewById(R.id.txtMain);
        txtBoard = (TextView) findViewById(R.id.txtBoard);
        btnWrite = (Button) findViewById(R.id.btnWrite);

        String str = "오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtMain.setText(content);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
        myAdapter = new MyAdapter();
        rv.setAdapter(myAdapter);

        Intent intent2 = new Intent(BoardActivity.this.getIntent());
        userid = intent2.getStringExtra("userid");

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardActivity.this, BoardWriteActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

        }


    void list(){

        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<BoardDTO>();
                    String page = Common.SERVER_URL + "/randomStyle/board/and_board_list.do";
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
                        JSONObject row = jArray.getJSONObject(i);
                        BoardDTO dto = new BoardDTO();
                        dto.setNo(row.getInt("no"));
                        dto.setUserid(row.getString("userid"));
                        dto.setTitle(row.getString("title"));
                        dto.setLikes(row.getString("likes"));
                        dto.setComments(row.getString("comments"));
                        dto.setViews(row.getInt("views"));
                        items.add(dto);
                    }

                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_row, parent, false);
            return new ViewHolder(rowItem);

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.txtNo.setText(items.get(position).getNo()+"");
            holder.txtTitle.setText(items.get(position).getTitle());
            holder.txtUserid.setText(items.get(position).getUserid());
            holder.txtLikes.setText(items.get(position).getLikes());
            holder.txtComments.setText(items.get(position).getComments());
            holder.txtViews.setText(items.get(position).getViews()+"");

            holder.txtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    no = items.get(position).getNo();
                    Intent intent = new Intent(BoardActivity.this, BoardDetailActivity.class);
                    intent.putExtra("no", no);
                    intent.putExtra("userid", userid);

                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView txtNo, txtTitle, txtUserid, txtLikes, txtComments, txtViews;

            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                this.txtNo = view.findViewById(R.id.txtNo);
                this.txtTitle = view.findViewById(R.id.txtTitle);
                this.txtUserid = view.findViewById(R.id.txtUserid);
                this.txtLikes = view.findViewById(R.id.txtLikes);
                this.txtComments = view.findViewById(R.id.txtComments);
                this.txtViews = view.findViewById(R.id.txtViews);

            }

            @Override
            public void onClick(View v) {

            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.i(this.getClass().getName(),"hihi");
        list();


    }
}

