package com.example.hp.project_one;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText etName,etPass;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText)findViewById(R.id.et_name);
        etPass = (EditText)findViewById(R.id.et_pass);
        btn = (Button)findViewById(R.id.btn_login);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int result = 0;
                        try {
                            result = login();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (result == 1){
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else if (result == -2){
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else if (result == -1){
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "不存在该用户！", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
            }
        });

    }

    private int login() throws IOException, JSONException {
        int returnResult = 0;
        
        String user_name = etName.getText().toString();
        String password = etPass.getText().toString();
        if (user_name == null||user_name.length()<=0){
            Looper.prepare();
            Toast.makeText(this, "请输入账号！", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return 0;
        }
        if (password == null || password.length()<=0){
            Looper.prepare();
            Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return 0;
        }
        String strurl = "http://10.0.2.2/project/login.php";

        //strurl = strurl+"?uid="+user_name+"&pwd="+password;

        URL url = new URL(strurl);

        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        String params = "uid="+user_name+"&pwd="+password;
//        Map<String,String> params = new HashMap<String, String>();
//        params.put("uid",user_name);
//        params.put("pwd",password);


//        http.setDoInput(true);
//        http.setDoOutput(true);
        http.setRequestMethod("POST");
//        http.setUseCaches(false);
        OutputStream out = http.getOutputStream();
        //将b.length指定字节数组中的字节写入此输出流。
        out.write(params.getBytes());
        //刷新此输出流并强制写出所有缓冲的输出字节。
        out.flush();
        //关闭此输出流并释放与此流关联的所有系统资源。
        out.close();

        BufferedReader buffer = new BufferedReader(new InputStreamReader(http.getInputStream()));

        String line = "";
        StringBuilder sb = new StringBuilder();
        while (null!=(line=buffer.readLine())){
            sb.append(line);
        }
        String result = sb.toString();
        JSONObject json = new JSONObject(result);
        returnResult = json.getInt("status");
        return returnResult;
    }
}
