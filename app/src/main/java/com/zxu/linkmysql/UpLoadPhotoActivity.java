package com.zxu.linkmysql;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.zxu.linkmysql.bean.Urine;
import com.zxu.linkmysql.bean.student;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.params.HttpClientParams;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.EntityTemplate;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class UpLoadPhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UpLoadPhotoActivity";
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
        findViewById(R.id.btn_urine).setOnClickListener(this);
        textView = findViewById(R.id.text_test);
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
            case R.id.btn_urine:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        checkUrine();
                    }
                }).start();

                break;
        }
    }

    private void show() {
        HttpPost httpPost = new HttpPost("http://10.0.116.20:8080/deleteByName");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("name", "wer"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                //服务器正常返回的数据

//                Log.d(TAG, "服务器正常返回的数据: " + EntityUtils.toString(resEntity));// httpclient自带的工具类读取返回数据

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
    }
    private void checkUrine(){
        HttpPost httpPost = new HttpPost("http://10.0.116.20:8080/urine/");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
//                JSONObject jsonpObject = JSON.parseObject(EntityUtils.toString(resEntity));
                List<Urine> list = JSON.parseArray(EntityUtils.toString(resEntity), Urine.class);
                handlerMy.post(new uiRunable(list.get(0).getCateUrine()+'\n'+list.get(1).getCateUrine()));
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
    }
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
