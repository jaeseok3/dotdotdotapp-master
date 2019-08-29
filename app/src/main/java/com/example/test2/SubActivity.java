package com.example.test2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.Locale;

public class SubActivity extends AppCompatActivity {
    Intent intent;
    SpeechRecognizer mRecognizer;
    TextView textView;
    private TextToSpeech tts;              // TTS 변수 선언
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    String resum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        textView = (TextView) findViewById(R.id.textView1);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    // 언어를 선택한다.
                    System.out.println("성공");
                    tts.setLanguage(Locale.KOREAN);
                    tts.setSpeechRate((float) 1.1);
                    tts.setPitch((float)1.0);
                }
                tts.speak("이 어플은 시각장애인을 위해 만들어진 닽닽닽 어플입니다. 읽을 책을 말해주세요! 로그아웃을 원하시면 로그아웃이라고 말씀하시면 됩니다.",TextToSpeech.QUEUE_FLUSH, null);

            }

        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO
                );
            }
        }

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(recognitionListener);

        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecognizer.startListening(intent);

            }
        }, 12000);

    }

    private final RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            textView.setText("Ready");
        }

        @Override
        public void onBeginningOfSpeech() {
            textView.setText("Beginning");
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            textView.setText("Buffer");
        }

        @Override
        public void onEndOfSpeech() {
            textView.setText("End");
        }

        @Override
        public void onError(int i) {
            tts.speak("인식에 실패했습니다. 다시 말씀해 주세요.",TextToSpeech.QUEUE_FLUSH, null);
            mRecognizer.setRecognitionListener(recognitionListener);

            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecognizer.startListening(intent);

                }
            }, 4000);
        }

        @Override
        public void onResults(Bundle bundle) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            resum = rs[0];
            if((resum.compareTo("1번") == 0) ||(resum.compareTo("일본")==0)){
                textView.setText("1번");
                tts.speak("1번 나무를 블루투스 로 전송합니다.",TextToSpeech.QUEUE_FLUSH, null);
                Intent intent = new Intent(getApplicationContext(), Bluetooth.class);
                intent.putExtra("name", "나무");
                startActivity(intent);
            }
            else if((resum.compareTo("2번") == 0) ||(resum.compareTo("이번")==0)){
                textView.setText("2번");
                tts.speak("2번 아프니까 청춘이다 를 블루투스로 전송합니다.",TextToSpeech.QUEUE_FLUSH, null);
                Intent intent = new Intent(getApplicationContext(), Bluetooth.class);
                intent.putExtra("name", "아프니까 청춘이다");
                startActivity(intent);
            }
            else if((resum.compareTo("3번") == 0) ||(resum.compareTo("삼번")==0)){
                textView.setText("3번!!");
                tts.speak("3번 나의 라임 오렌지 나무 를 블루투스로 전송합니다.",TextToSpeech.QUEUE_FLUSH, null);
                Intent intent = new Intent(getApplicationContext(), Bluetooth.class);
                intent.putExtra("name", "나의 라임 오렌지 나무");
                startActivity(intent);
            }
            else if((resum.compareTo("로그아웃") == 0) ||(resum.compareTo("로가웃")==0)){
                textView.setText("로그아웃");
                tts.speak("로그아웃 되었습니다.",TextToSpeech.QUEUE_FLUSH, null);
                finish();
            }
            else{
                tts.speak("인식에 실패했습니다. 다시 말씀해 주세요.",TextToSpeech.QUEUE_FLUSH, null);
                mRecognizer.setRecognitionListener(recognitionListener);

                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecognizer.startListening(intent);
                    }
                }, 5000);
            }

            System.out.println(resum);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }

    };
}