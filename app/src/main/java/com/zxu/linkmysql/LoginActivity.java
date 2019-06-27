package com.zxu.linkmysql;


import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    Button login_btn;
    EditText uid;
    EditText psw;
    EditText id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uid = (EditText)findViewById(R.id.uid);
        psw = (EditText)findViewById(R.id.psw);
        id = (EditText)findViewById(R.id.id);

        login_btn = (Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        switch (v.getId()){
            case R.id.login_btn:
//                LinK();

                //Toast.makeText(this, "ooo", Toast.LENGTH_SHORT).show();
                //parseJSONWithJSONObject("http://192.168.43.6:8000/android_user/");
                params.put("method", "_POST");
                params.put("name",uid.getText().toString());
                params.put("age", psw.getText().toString());
                params.put("id", id.getText().toString());

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cover.jpg");
                try {
                    params.put("img_url",file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                client.post("http://10.0.116.20:8000/android_user/", params, new MyTextListener(this.handler, 3, 30));

        }
    }


    private void parseJSONWithJSONObject(String s) {

        JSONObject object = JSON.parseObject(s);
        Toast.makeText(this, object.toJSONString(), Toast.LENGTH_SHORT).show();
    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
//                    Toast.makeText(LoginActivity.this, (String)map.get("name"), Toast.LENGTH_LONG).show();
                    break;
                case 400:
                    Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(LoginActivity.this, "null", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };
}
