package com.example.randomstyle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class UploadActivity extends AppCompatActivity {
    Button btn;
    EditText edit_entry;
    FileInputStream fis;
    URL url;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    String result;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //EditText에 업로드 결과를 출력
            edit_entry.setText(result);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_upload);
        edit_entry =
                (EditText) findViewById(R.id.edit_entry);
        makeFile();
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    //서버에 업로드할 파일 경로
                    String file = "/data/data/" + getPackageName()
                            + "/files/test.txt";
                    fileUpload(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    void makeFile() {
        String path = "/data/data/" + getPackageName() + "/files";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File("/data/data/" + getPackageName()
                + "/files/test.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);//파일출력스트림
            String str = "hello android";
            fos.write(str.getBytes()); //스트링을 바이트배열로 변환
            fos.close(); //스트림 닫기
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void fileUpload(final String file) {
        Thread th = new Thread(new Runnable() {
            public void run() {
                // 업로드를 처리할 웹서버의 url
                httpFileUpload(Common.SERVER_URL
                        + "/mobile/upload/android_upload.jsp", file);
            }
        });
        th.start();
    }
    void httpFileUpload(String urlString, String file) {
        try {
            //sdcard의 파일입력스트림 생성
            fis = new FileInputStream(file);
            //URL 객체 생성
            url = new URL(urlString);
            //서버 URL에 접근하여 연결을 확립시킴
            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();
            //커넥션을 통해 입출력이 가능하도록 설정
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false); //캐쉬 사용 안함
            //post 방식으로 업로드 처리
            conn.setRequestMethod("POST");
            //post 방식으로 넘길 자료의 정보 설정
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            //파일업로드이므로 다양한 파일포맷을 지원하기 위해
            // DataOutputStream 객체 생성
            DataOutputStream dos =
                    new DataOutputStream(
                            conn.getOutputStream());
            //파일에 저장할 내용 기록
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes(
"Content-Disposition: form-data;name=\"file\";filename=\"" + file + "\""
        + lineEnd);
            dos.writeBytes(lineEnd);
            int bytesAvailable = fis.available();
            int maxBufferSize = 1024;
            //Math.min(값1,값2) => 작은 값을 리턴
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            //버퍼로 사용할 바이트 배열 생성
            byte[] buffer = new byte[bufferSize];
            //파일입력스트림을 통해 내용을 읽어서 버퍼에 저장
            int bytesRead =
                    fis.read(buffer, 0, bufferSize);
            //내용이 있으면
            while (bytesRead > 0) {
                //버퍼의 사이즈만큼 읽어서 버퍼의 내용을 데이터출력스트림에 기록
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            //파일 입력스트림 닫기
            fis.close();
            //데이터 출력 스트림을 비움
            dos.flush();
            int ch;
            // url 커넥션으로 결과값을 받아서 스트링버퍼에 저장
            InputStream is = conn.getInputStream();
            StringBuffer b = new StringBuffer();
            //1바이트씩 읽어서 내용이 있으면 스트링버퍼에 추가
            //더이상 내용이 없으면 -1을 리턴
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            //바이트 배열을 스트링으로 변환
            result = b.toString().trim();
            //데이터출력스트림을 닫음
            dos.close();
            handler.sendEmptyMessage(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}