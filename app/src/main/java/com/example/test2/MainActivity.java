package com.example.test2;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "phpquerytest";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_text1 = "text1";
    private static final String TAG_text2 = "text2";
    private static final String TAG_text3 = "text3";
    String loginId, loginPwd;

    ArrayList<HashMap<String, String>> mArrayList;
    EditText mEditTextSearchKeyword1, mEditTextSearchKeyword2;
    String mJsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextSearchKeyword1 = (EditText) findViewById(R.id.usrusr);
        mEditTextSearchKeyword2 = (EditText) findViewById(R.id.passwrd);
        SharedPreferences auto = getSharedPreferences("auto", AppCompatActivity.MODE_PRIVATE);

        loginId = auto.getString("inputId",null);
        loginPwd = auto.getString("inputPwd",null);
        System.out.println("auto : "+loginId);
        System.out.println("auto1 : "+loginPwd);
        if(loginPwd!=null && loginId != null) {
            GetData task = new GetData();
            task.execute(loginId, loginPwd);
        }
        Button button_search = (Button) findViewById(R.id.logiin);
        button_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mArrayList.clear();


                GetData task = new GetData();
                task.execute( mEditTextSearchKeyword1.getText().toString(), mEditTextSearchKeyword2.getText().toString());
            }
        });


        mArrayList = new ArrayList<>();


    }


    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            if (result.compareTo("error") == 0){
                alert.setMessage("아이디와 비밀번호가 틀렸습니다!");
                alert.show();
            }
            else if(result.compareTo("input") == 0){
                alert.setMessage("아이디를 입력해 주세요!");
                alert.show();
            }
            else {

                mJsonString = result;
                showResult();

            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];

            String serverURL = "http://dotdotdot.ga/login_m1.php";
            String postParameters = "country=" + searchKeyword1 + "&name=" + searchKeyword2;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){
                HashMap<String,String> hashMap = new HashMap<>();
                JSONObject jObject = jsonArray.getJSONObject(i);

                String text1 = jObject.optString(TAG_text1);
                String text2 = jObject.optString(TAG_text2);
                String text3 = jObject.optString(TAG_text3);

                hashMap.put(TAG_text1, text1);
                hashMap.put(TAG_text2, text2);
                hashMap.put(TAG_text3, text3);

                Intent intent = new Intent(MainActivity.this,SubActivity.class);

                SharedPreferences auto = getSharedPreferences("auto", AppCompatActivity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = auto.edit();

                autoLogin.putString("inputId", mEditTextSearchKeyword1.getText().toString());
                autoLogin.putString("inputPwd", mEditTextSearchKeyword2.getText().toString());
                autoLogin.commit();

                intent.putExtra("text_1",text1);
                intent.putExtra("text_2",text2);
                intent.putExtra("text_3",text3);

                startActivity(intent);
                mArrayList.add(hashMap);
            }

        } catch (JSONException e) {
            System.out.println("showResult : " + e);
        }

    }

}