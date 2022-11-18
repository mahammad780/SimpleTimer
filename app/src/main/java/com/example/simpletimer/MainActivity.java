package com.example.simpletimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBar seekBar;
    private TextView time;
    private boolean isworking;
    private Button button;
    private CountDownTimer countDownTimer;
    private int defaultinterval;
    SharedPreferences sharedPreferences;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        time = findViewById(R.id.time);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.reset_timer);
        isworking = true;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        seekBar.setMax(600);
        setIntervalFromSharedPreferences(sharedPreferences);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long progressInMillis = progress * 1000;
                setTimer(progressInMillis);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
    public void start(View view){
        if(isworking){
            button.setBackground(getDrawable(R.drawable.blue_color));
            button.setText("Pause");
            seekBar.setEnabled(false);
            isworking = false;
            countDownTimer = new CountDownTimer(seekBar.getProgress()* 1000,
                    1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setTimer(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if(sharedPreferences.getBoolean("enable_sound",true)){
                        String melodyname = sharedPreferences.getString("timer_melody", "ring_bell");
                        if(melodyname.equals("ring_bell")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),
                                    R.raw.ring_bell);
                            mediaPlayer.start();
                        }
                        else if(melodyname.equals("bike_bell")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),
                                    R.raw.bike_bell);
                            mediaPlayer.start();
                        }
                        else if(melodyname.equals("horror_bell")){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),
                                    R.raw.horror_bell);
                            mediaPlayer.start();
                        }

                    }
                    resetTimer();

                }
            };

            countDownTimer.start();
        }
        else{
            pauseTimer();
        }
    }
    private void setTimer(long millisUntilFinished){
        int minutes = (int)millisUntilFinished/1000/60;
        int seconds = (int)millisUntilFinished/1000 - (minutes*60);

        String minutesString = "";
        String secondsString = "";

        if(minutes<10){
            minutesString = "0" + minutes;
        }
        else{
            minutesString= String.valueOf(minutes);
        }
        if(seconds<10){
            secondsString = "0" + seconds;
        }
        else{
            secondsString= String.valueOf(seconds);
        }
        time.setText(minutesString +":"+ secondsString);
    }

    private void pauseTimer(){
        isworking = true;
        button.setBackground(getDrawable(R.drawable.green_color));
        countDownTimer.cancel();
        button.setText("Resume");
        seekBar.setEnabled(false);
    }

    private void resetTimer(){
        button.setBackground(getDrawable(R.drawable.green_color));
        isworking = true;
        countDownTimer.cancel();
        button.setText("Start");
        seekBar.setEnabled(true);
        setIntervalFromSharedPreferences(sharedPreferences);
    }

    public void reset(View view){
        if(countDownTimer != null){
        resetTimer();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.settings: Intent openSetiings = new Intent(this, SettingsActivity.class);
                startActivity(openSetiings); return true;
            case 0: ;break;
            case R.id.about: Intent openAbout = new Intent(this, AboutActivity.class);
                startActivity(openAbout); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setIntervalFromSharedPreferences(SharedPreferences sharedPreferences){
        defaultinterval = Integer.valueOf(sharedPreferences.getString("default_interval", "10"));
        long defaultintervalinmillis = defaultinterval * 1000;
        setTimer(defaultintervalinmillis);
        seekBar.setProgress(defaultinterval);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("default_interval")){
            setIntervalFromSharedPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

}