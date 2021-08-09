package com.example.randomstyle;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randomstyle.DTO.BoardDTO;
import com.example.randomstyle.DTO.CommentsDTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BoardDetailActivity extends AppCompatActivity {

    TextView txtMain, txtUserid, txtNo, txtTitle, txtLikes, txtComments, txtContents;
    EditText editComments;
    RecyclerView rvComments;
    Button btnComments;
    Thread th2;
    ImageView imgBoard,imgLikes, imgShare;
    Bitmap bitmap;



    ArrayList<CommentsDTO> items2;

    RecyclerView.Adapter myAdapter;

    String photo1_url;
    String userid;
    int no;
    int result;

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
        setContentView(R.layout.activity_board_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        txtMain = (TextView) findViewById(R.id.txtMain);
        txtUserid = (TextView) findViewById(R.id.txtUserid);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtNo = (TextView) findViewById(R.id.txtNo);
        txtLikes = (TextView) findViewById(R.id.txtLikes);
        txtComments = (TextView) findViewById(R.id.txtComments);
        txtContents = (TextView) findViewById(R.id.txtContents);
        rvComments = (RecyclerView) findViewById(R.id.rvComments);
        btnComments = (Button) findViewById(R.id.btnComments);
        editComments = (EditText) findViewById(R.id.editComments);
        imgLikes = (ImageView) findViewById(R.id.imgLikes);
        imgBoard = (ImageView) findViewById(R.id.imgBoard);

            String str = "오늘의 랜덤 스타일";
            SpannableString content = new SpannableString(str);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            txtMain.setText(content);

            rvComments.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            rvComments.addItemDecoration(new DividerItemDecoration(rvComments.getContext(), DividerItemDecoration.VERTICAL));
            myAdapter = new MyAdapter();
            rvComments.setAdapter(myAdapter);

            Intent intent = new Intent(this.getIntent());
            no = intent.getIntExtra("no", 0);
            userid = intent.getStringExtra("userid");


            likes_check();

            Log.i(this.getClass().getName(), "photo=====" + photo1_url);


            btnComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    intent = new Intent(BoardDetailActivity.this, CommentsAddActivity.class);
                    intent.putExtra("no", no);
                    intent.putExtra("userid", userid);
                    startActivity(intent);
                }
            });


        imgLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(result == 0) {
                    Log.i(this.getClass().getName(), "Id1 ======" + result);
                    final StringBuilder sb4 = new StringBuilder();
                    Thread th4 = new Thread(new Runnable() {
                        public void run() {
                            try {
                                String page = Common.SERVER_URL + "/randomStyle/board/and_likes_check_insert.do?userid=" + userid + "&b_no=" + no;
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
                                            sb4.append(line + "\n");
                                        }
                                        br.close();
                                    }
                                    conn.disconnect();

                                }
                                likes();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    th4.start();

                } else {

                    final StringBuilder sb5 = new StringBuilder();
                    Thread th5 = new Thread(new Runnable() {

                        public void run() {
                            try {
                                String page = Common.SERVER_URL + "/randomStyle/board/and_likes_check_delete.do?userid=" + userid + "&b_no=" + no;
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
                                            sb5.append(line + "\n");
                                        }
                                        br.close();
                                    }
                                    conn.disconnect();

                                }

                                likes_min();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    th5.start();

                }


            }
        });


        }

        void photo() {

            Thread th9 = new Thread(new Runnable() {
                public void run() {
                    try {
                        String page = Common.SERVER_URL + "/randomStyle/board/"+photo1_url;
                        Log.i(this.getClass().getName(),  "page======" + page);
                        URL url = new URL(page);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true);
                            conn.connect();


                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);

                            Log.i(this.getClass().getName() , "bitmap=======" + bitmap);


                            runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                imgBoard.setImageBitmap(bitmap);

                            }
                        });

                    } catch (MalformedURLException e) {
                        e.printStackTrace();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            th9.start();

        }


        void detail() {

            final StringBuilder sb = new StringBuilder();
            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        String page = Common.SERVER_URL + "/randomStyle/board/and_board_detail.do?no=" + no;
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
                        BoardDTO dto = new BoardDTO();
                        JSONObject row = jArray.getJSONObject(0);

                        Log.i(this.getClass().getName(), "row=========" + row);

                        dto.setUserid(row.getString("userid"));
                        dto.setTitle(row.getString("title"));
                        dto.setLikes(row.getString("likes"));
                        photo1_url = row.getString("photo1_url");
                        Log.i(this.getClass().getName(), "photo1_url=========" + photo1_url);

                        dto.setComments(row.getString("comments"));
                        dto.setContents(row.getString("contents"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                photo();
                                txtTitle.setText(dto.getTitle());
                                txtUserid.setText(dto.getUserid());
                                txtLikes.setText(dto.getLikes());
                                txtComments.setText(dto.getComments());
                                txtContents.setText(dto.getContents());

                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            });

            th.start();

        }

        void likes(){

            final StringBuilder sb6 = new StringBuilder();
            Thread th6 = new Thread(new Runnable() {
                public void run() {
                    try {
                        String page = Common.SERVER_URL + "/randomStyle/board/and_likes.do?userid=" + userid + "&no=" + no;
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
                                    sb6.append(line + "\n");
                                }
                                br.close();
                            }
                            conn.disconnect();
                        }

                        likes_count();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            th6.start();
        }

        void likes_min(){

            final StringBuilder sb7 = new StringBuilder();
            Thread th7 = new Thread(new Runnable() {
                public void run() {
                    try {
                        String page = Common.SERVER_URL + "/randomStyle/board/and_likes_min.do?userid=" + userid + "&no=" + no;
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
                                    sb7.append(line + "\n");
                                }
                                br.close();
                            }
                            conn.disconnect();
                        }
                        likes_count();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            th7.start();


        }

      void likes_count() {
          final StringBuilder sb8 = new StringBuilder();
          Thread th8 = new Thread(new Runnable() {
              public void run() {
                  try {
                      String page = Common.SERVER_URL + "/randomStyle/board/and_likes_count.do?no=" + no;
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
                                  sb8.append(line + "\n");
                              }
                              br.close();
                          }
                          conn.disconnect();

                          int result = Integer.parseInt(sb8.toString().trim());
                          Log.i(this.getClass().getName(), "likes_count_result=========" + result);
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  txtLikes.setText(result+"");
                              }
                          });
                          likes_check();
                      }



                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          });
          th8.start();



      }

      void likes_check() {

          final StringBuilder sb3 = new StringBuilder();
          Thread th3 = new Thread(new Runnable() {
              public void run() {
                  Log.i(this.getClass().getName() , "userid,no=" + userid + no);
                  try {
                      String page = Common.SERVER_URL + "/randomStyle/board/and_likes_check.do?userid=" + userid + "&b_no=" + no;
                      URL url = new URL(page);
                      HttpURLConnection conn = (HttpURLConnection)
                              url.openConnection();
                      if (conn != null) {
                          conn.setConnectTimeout(10000);
                          conn.setRequestMethod("POST");
                          conn.setUseCaches(false);
                          if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                              BufferedReader br =
                                      new BufferedReader(
                                              new InputStreamReader(
                                                      conn.getInputStream(), "utf-8"));
                              while (true) {
                                  String line = br.readLine();
                                  if (line == null) break;
                                  sb3.append(line + "\n");
                              }
                              br.close();
                          }
                          conn.disconnect();
                      }

                      result = Integer.parseInt(sb3.toString().trim());

                      if(result == 0 ) {
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  imgLikes.setImageResource(R.drawable.heart1);
                              }
                          });
                      } else {
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  imgLikes.setImageResource(R.drawable.heart2);
                              }
                          });
                      }


                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          });

          th3.start();

      }
    void comments_delete(int c_no) {

        final StringBuilder sb = new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                Log.i(this.getClass().getName() , "c_no=" + c_no);
                try {
                    String page = Common.SERVER_URL + "/randomStyle/comments/and_comments_delete.do?c_no=" + c_no +"&b_no=" + no;
                    URL url = new URL(page);
                    HttpURLConnection conn = (HttpURLConnection)
                            url.openConnection();
                    if (conn != null) {
                        conn.setConnectTimeout(10000);
                        conn.setRequestMethod("POST");
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
                    comments_list();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        th.start();

    }

    void comments_list() {
        final StringBuilder sb2 = new StringBuilder();
        th2 = new Thread(new Runnable() {

            public void run() {
                try {
                    items2 = new ArrayList<CommentsDTO>();
                    String page = Common.SERVER_URL + "/randomStyle/comments/and_comments_list.do?b_no=" + no;
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
                                sb2.append(line + "\n");
                            }
                            br.close();
                        }
                        conn.disconnect();
                    }

                    comments_count();

                    JSONObject jsonObj = new JSONObject(sb2.toString());
                    JSONArray jArray = (JSONArray) jsonObj.get("comments");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject row = jArray.getJSONObject(i);
                        CommentsDTO dto = new CommentsDTO();
                        dto.setC_no(row.getInt("c_no"));
                        dto.setUserid(row.getString("userid"));
                        dto.setComments(row.getString("comments"));
                        dto.setWrite_date(row.getString("date"));
                        items2.add(dto);
                    }

                    handler.sendEmptyMessage(0);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        th2.start();

    }

    void comments_count(){
            final StringBuilder sb8 = new StringBuilder();
            Thread th8 = new Thread(new Runnable() {
                public void run() {
                    try {
                        String page = Common.SERVER_URL + "/randomStyle/comments/and_comments_count.do?b_no=" + no;
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
                                    sb8.append(line + "\n");
                                }
                                br.close();
                            }
                            conn.disconnect();

                            int result = Integer.parseInt(sb8.toString().trim());
                            Log.i(this.getClass().getName(), "comments_count_result=========" + result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtComments.setText(result+"");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            th8.start();



        }


    @Override
    protected void onResume() {
        super.onResume();
        detail();
        comments_list();

        myAdapter = new MyAdapter();
        rvComments.setAdapter(myAdapter);


        }




    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_row, parent, false);


            return new ViewHolder(rowItem);

        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            holder.txtUserid.setText(items2.get(position).getUserid());
            holder.txtComments.setText(items2.get(position).getComments());
            holder.txtDate.setText(items2.get(position).getWrite_date()+"");
            holder.txtC_no.setText(items2.get(position).getC_no()+"");

        }

        @Override
        public int getItemCount() {
            return items2.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView txtUserid, txtComments, txtDate, txtC_no;



            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                this.txtUserid = view.findViewById(R.id.txtUserid);
                this.txtComments = view.findViewById(R.id.txtComments);
                this.txtDate = view.findViewById(R.id.txtDate);
                this.txtC_no = view.findViewById(R.id.txtC_no);



                txtComments.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int c_no = Integer.parseInt(txtC_no.getText().toString());
                        if(txtUserid.getText().toString().equals(userid)) {
                            AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(BoardDetailActivity.this);
                            myAlertBuilder.setTitle("삭제");
                            myAlertBuilder.setMessage("삭제하시겠습니까?");
                            myAlertBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    comments_delete(c_no);

                                }
                            });
                            myAlertBuilder.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                            myAlertBuilder.show();

                            return true;
                        } else {
                            return false;
                        }

                    }
                });

            }
            @Override
            public void onClick(View v) {

            }
        }

    }

}


