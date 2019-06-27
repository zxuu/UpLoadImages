package com.zxu.linkmysql;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

public class UpLoadPhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private String path;
    private TextView textView;

    private Handler handlerMy = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_load_photo);

        findViewById(R.id.btn).setOnClickListener(this);
        findViewById(R.id.btn_Login).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        textView = findViewById(R.id.text);
        handlerMy = new Handler();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Login:
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.btn:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        show();
                    }
                }).start();

                break;
            case R.id.btn_2:

                break;
        }
    }

    private void show() {
//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
        HttpPost httpPost = new HttpPost("http://10.0.116.20:8000/getVideos/");
        CloseableHttpClient httpClient = HttpClients.createDefault();


        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pada.mp4";
        FileBody bin = new FileBody(new File(path));

        StringBody myName = new StringBody("zxu", ContentType.TEXT_PLAIN);
        HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file",bin).addPart("myName", myName).build();
        httpPost.setEntity(reqEntity);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                JSONObject jsonpObject = JSON.parseObject(EntityUtils.toString(resEntity));
                List<String> list = JSON.parseArray(jsonpObject.get("result").toString(),String.class);
                handlerMy.post(new uiRunable(list.get(1)));
//                System.out.println("服务器正常返回的数据: " + EntityUtils.toString(resEntity));// httpclient自带的工具类读取返回数据

//                System.out.println(resEntity.getContent());

            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
//                Toast.makeText(this, "上传文件发生异常，请检查服务端异常问题", Toast.LENGTH_SHORT).show();
//                System.out.println("上传文件发生异常，请检查服务端异常问题");
            }
            EntityUtils.consume(resEntity);
            response.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            params.put("method","_POST");
//            params.put("image",file);
//            client.post("http://10.0.116.20:8000/haha/", params, new MyTextListener(handler, 3, 30));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

    }

    public void readImg(View view) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        showImg.setImageBitmap(bitmap);
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    //HashMap map = JSONTOOL.analyze_once_json(msg.obj.toString());
//                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
////                        JSONObject jsonArray = new JSONObject(msg.obj.toString());
////                        for (int i=0; i < jsonArray.length(); i++)    {
////                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            String id = jsonObject.getString("ID");
//                        }

//                    Toast.makeText(UpLoadPhotoActivity.this, (String)map.get("name"), Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                    intent.putExtra("uid",(String) map.get("name"));
//                    startActivity(intent);
//                    LoginActivity.this.finish();
                    break;
                case 400:
                    Toast.makeText(UpLoadPhotoActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(UpLoadPhotoActivity.this, "null", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    private class uiRunable implements Runnable{
        String string;
        public uiRunable(String string) {
            this.string = string;
        }

        @Override
        public void run() {
            textView.setText(string);
        }
    }
}
