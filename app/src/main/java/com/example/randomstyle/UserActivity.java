package com.example.randomstyle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.randomstyle.DTO.BoardDTO;
import com.example.randomstyle.DTO.ClothDTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {


    TextView txtMain, txtBoard;
    String userid;
    Button btnWrite,btnItems;
    ArrayList<BoardDTO> items;
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
        setContentView(R.layout.activity_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        txtMain = (TextView) findViewById(R.id.txtMain);
        txtBoard = (TextView) findViewById(R.id.txtBoard);
        btnWrite = (Button) findViewById(R.id.btnWrite);
        btnItems = (Button) findViewById(R.id.btnItems);
        rv = findViewById(R.id.rv);



        String str = "오늘의 랜덤 스타일";
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txtMain.setText(content);

        Intent intent = new Intent(this.getIntent());
        userid = intent.getStringExtra("userid");

        board_list_userid();

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(UserActivity.this, BoardWriteActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });


        btnItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(UserActivity.this, ItemsActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
        myAdapter = new UserActivity.MyAdapter();
        rv.setAdapter(myAdapter);


    }

    void board_list_userid() {

        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<BoardDTO>();
                    String page = Common.SERVER_URL + "/randomStyle/board/and_board_list_userid.do?userid=" + userid;
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
                    Log.i("test" , "sendData============"+ jArray);

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

    class MyAdapter extends RecyclerView.Adapter<UserActivity.MyAdapter.ViewHolder> {

        @Override
        public UserActivity.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_row_userid, parent, false);

            return new UserActivity.MyAdapter.ViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(UserActivity.MyAdapter.ViewHolder holder, int position) {
            holder.txtNo.setText(items.get(position).getNo()+"");
            holder.txtTitle.setText(items.get(position).getTitle());
            holder.txtUserid.setText(items.get(position).getUserid());
            holder.txtLikes.setText(items.get(position).getLikes());
            holder.txtComments.setText(items.get(position).getComments());

            holder.txtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int no = items.get(position).getNo();
                    Intent intent = new Intent(UserActivity.this, BoardDetailActivity.class);
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
            private Button btnDelete;

            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                this.txtNo = view.findViewById(R.id.txtNo);
                this.txtTitle = view.findViewById(R.id.txtTitle);
                this.txtUserid = view.findViewById(R.id.txtUserid);
                this.txtLikes = view.findViewById(R.id.txtLikes);
                this.txtComments = view.findViewById(R.id.txtComments);
                this.txtViews = view.findViewById(R.id.txtViews);
                this.btnDelete = view.findViewById(R.id.btnDelete);

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String no = txtNo.getText().toString();
                        board_delete(no);
                    }
                });

            }

            @Override
            public void onClick(View v) {

            }
        }
    }

    void board_delete(String no) {
        AlertDialog.Builder myAlertBuilder =
                new AlertDialog.Builder(UserActivity.this);
        myAlertBuilder.setMessage("삭제하시겠습니까?");
        myAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final StringBuilder sb = new StringBuilder();
                Thread th = new Thread(new Runnable() {
                    public void run() {
                        try {
                            String page = Common.SERVER_URL + "/randomStyle/board/and_board_delete.do?no="+no;
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
                            board_list_userid();

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

