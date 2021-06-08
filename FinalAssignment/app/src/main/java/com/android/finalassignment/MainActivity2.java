package com.android.finalassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

import static com.android.finalassignment.MainActivity.name;

public class MainActivity2 extends AppCompatActivity {
    EditText description,imageSrc,id,lat,lon,comment;
    TextView chat;
    ImageView imageView;
    Switch aSwitch;
    Button done,next,previous,maps,speak,reader,signout,send;
    DatabaseReference reference,chatReference;
    static int j=1;
    TextSpeech textSpeech;
    private static final int VOICE_REC_RESULT = 543;
    protected static String latitude, longitude;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==VOICE_REC_RESULT && resultCode==RESULT_OK){
            List<String> strings = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            for (String s:strings){
                if (s.equals("map")) {
                    latitude = lat.getText().toString();
                    longitude = lon.getText().toString();
                    startActivity(new Intent(this, MapsActivity.class));
                }
                else if (s.equals("read"))
                    textSpeech.speak(description.getText().toString());
                else if (s.equals("out")){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(this,MainActivity.class);
                    finish();
                    startActivity(intent);
                }
                else {
                    new Thread(()->{
                        try {
                            textSpeech.speak("say \"OUT\" to sign out");
                            Thread.sleep(646);
                            textSpeech.speak("say \"READ\" to activate text to speech");
                            Thread.sleep(646);
                            textSpeech.speak("say \"MAP\" to see location on google maps");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        textSpeech = new TextSpeech(this);
        reference = FirebaseDatabase.getInstance().getReference();
        description = findViewById(R.id.description);
        imageSrc = findViewById(R.id.editTextTextPersonName8);
        id = findViewById(R.id.editTextTextPersonName2);
        lat = findViewById(R.id.editTextTextPersonName5);
        lon = findViewById(R.id.editTextTextPersonName4);
        comment = findViewById(R.id.editTextTextPersonName6);
        imageView = findViewById(R.id.imageView3);
        chat = findViewById(R.id.textView2);
        aSwitch = findViewById(R.id.switch1);
        done = findViewById(R.id.button6);
        next = findViewById(R.id.button3);
        previous = findViewById(R.id.button4);
        maps = findViewById(R.id.button8);
        speak = findViewById(R.id.button9);
        reader = findViewById(R.id.button10);
        signout = findViewById(R.id.button7);
        send = findViewById(R.id.button5);
        updateView(j);
        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (comment.getText().toString().trim().equals(""))
                    send.setEnabled(false);
                else send.setEnabled(true);
            }
        });
        aSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (aSwitch.isChecked()){
                description.setEnabled(true);
                done.setEnabled(true);
                imageSrc.setEnabled(true);
                id.setEnabled(true);
                lat.setEnabled(true);
                lon.setEnabled(true);
                maps.setEnabled(false);
                speak.setEnabled(false);
                reader.setEnabled(false);
                previous.setEnabled(false);
                next.setEnabled(false);
                signout.setEnabled(false);
            }
            else {
                description.setEnabled(false);
                done.setEnabled(false);
                imageSrc.setEnabled(false);
                id.setEnabled(false);
                lat.setEnabled(false);
                lon.setEnabled(false);
                maps.setEnabled(true);
                speak.setEnabled(true);
                reader.setEnabled(true);
                previous.setEnabled(true);
                next.setEnabled(true);
                signout.setEnabled(true);
            }
        });
        chatReference = FirebaseDatabase.getInstance().getReference("chat");
        chatReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                chat.setText("\n"+chat.getText()+"\n"+snapshot.getValue(String.class)+"\n");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void done(View view){
        // adding an image of our choice
        RequestQueue req = Volley.newRequestQueue(this);
        String url = imageSrc.getText().toString();
        ImageRequest imageReq = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
            }
        }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity2.this, "Invalid image URL, try another one!", Toast.LENGTH_LONG).show();
            }
        });
        req.add(imageReq);

        //inserting data in our Realtime Database
        reference = FirebaseDatabase.getInstance().getReference(id.getText().toString());
        reference.child("imagesrc").setValue(imageSrc.getText().toString());
        reference.child("latitude").setValue(lat.getText().toString());
        reference.child("longitude").setValue(lon.getText().toString());
        reference.child("description").setValue(description.getText().toString());
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    public void next(View view){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(String.valueOf(j+1))) {
                    j++;
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                else next.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void previous(View view){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(String.valueOf(j-1))) {
                    j--;
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                else previous.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updateView(int i){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RequestQueue req = Volley.newRequestQueue(getApplicationContext());
                String url = snapshot.child(String.valueOf(i)).child("imagesrc").getValue(String.class);
                ImageRequest imageReq = new ImageRequest(url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity2.this, "Invalid image URL, try another one!", Toast.LENGTH_LONG).show();
                    }
                });
                req.add(imageReq);

                id.setText(String.valueOf(i));
                imageSrc.setText(snapshot.child(String.valueOf(i)).child("imagesrc").getValue(String.class));
                lat.setText(snapshot.child(String.valueOf(i)).child("latitude").getValue(String.class));
                lon.setText(snapshot.child(String.valueOf(i)).child("longitude").getValue(String.class));
                description.setText(snapshot.child(String.valueOf(i)).child("description").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void signout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this,MainActivity.class);
        finish();
        startActivity(intent);
    }
    public void reader(View view){
        textSpeech.speak(description.getText().toString());
    }
    public void speak(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent,VOICE_REC_RESULT);
    }
    public void map(View view){
        latitude = lat.getText().toString();
        longitude = lon.getText().toString();
        startActivity(new Intent(this,MapsActivity.class));
    }
    public void sendit(View view){
        chatReference.push().setValue(name+": "+comment.getText().toString());
        comment.setText("");
    }
}