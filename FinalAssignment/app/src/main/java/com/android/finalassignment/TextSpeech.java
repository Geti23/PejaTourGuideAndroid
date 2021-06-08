package com.android.finalassignment;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TextSpeech {
    private TextToSpeech textToSpeech;
    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int i) {
            if (i==TextToSpeech.SUCCESS)
                textToSpeech.setLanguage(Locale.US);
        }
    };
    public TextSpeech(Context context){
        textToSpeech = new TextToSpeech(context,onInitListener);
    }
    public void speak(String message){
        textToSpeech.speak(message,TextToSpeech.QUEUE_ADD,null);
    }
}
