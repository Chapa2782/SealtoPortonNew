package com.sealtosoft.porton.sealtoporton;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sealtosoft.porton.Activities.MainActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SpashActivity extends AppCompatActivity {

    ImageView imagen;
    Animation animation,animation2;
    TextView text;
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    int TIME_LAP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spash);
        imagen = findViewById(R.id.imagenLogo);
        text = findViewById(R.id.texto);
        animation = AnimationUtils.loadAnimation(SpashActivity.this,R.anim.scale);
        animation2 = AnimationUtils.loadAnimation(SpashActivity.this,R.anim.alpha);
        imagen.startAnimation(animation);
        text.setAnimation(animation2);
        text.setVisibility(View.VISIBLE);
        showSystemUI();
        MediaPlayer mediaPlayer = MediaPlayer.create(SpashActivity.this,R.raw.chorus);
        pref = getSharedPreferences("Preferencias",MODE_PRIVATE);
        edit = pref.edit();
        int inicios = pref.getInt("Inicios",0);
        Log.d("Consola",String.valueOf(inicios));
        if(inicios <= 3){
            TIME_LAP = 11000;
            mediaPlayer.start();
        }else{
            TIME_LAP = 0;
        }
        inicios++;
        edit.putInt("Inicios",inicios);
        edit.commit();
        Log.d("Consola",String.valueOf(inicios));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SpashActivity.this, MainActivity.class));
                finish();
            }
        },TIME_LAP);
    }
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


}
