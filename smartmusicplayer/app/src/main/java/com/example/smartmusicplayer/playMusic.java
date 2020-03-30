package com.example.smartmusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayMusic extends AppCompatActivity {


    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mysongs;
    Thread updateseekbar;
    String sname;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        final SeekBar seekBar=findViewById(R.id.seekbar);
        ImageView forwardBtn=findViewById(R.id.forwardBtn);
        ImageView previousbtn=findViewById(R.id.PreviousBtn);
        final ImageView pauseBtn=findViewById(R.id.pauseBtn);
        final TextView songname=findViewById(R.id.songName);

        setTitle("Now Playing");
        updateseekbar=new Thread(){
            @Override
            public void run() {

                    int totalDuration=mediaPlayer.getDuration();

           int currentPosition=0;

           while (currentPosition<totalDuration){
               try{

                   sleep(500);
                   currentPosition=mediaPlayer.getCurrentPosition();
                   seekBar.setProgress(currentPosition);
               }
               catch (InterruptedException e){
                   e.printStackTrace();
               }
           }
            }
        };

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        mysongs=(ArrayList) bundle.getParcelableArrayList("songs");
        sname=mysongs.get(position).getName();

        String songnamee=intent.getStringExtra("songname");
        songname.setText(songnamee);
        songname.setSelected(true);

        position=bundle.getInt("pos",0);

            Uri uri=Uri.parse(mysongs.get(position).toString());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();


        if(mediaPlayer!=null){
            seekBar.setMax(mediaPlayer.getDuration());
        }

        updateseekbar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.white),PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setMax(mediaPlayer.getDuration());
                if(mediaPlayer.isPlaying()){
                    pauseBtn.setImageResource(0);
                    pauseBtn.setBackgroundResource(0);
                    pauseBtn.setBackgroundResource(R.drawable.play_icone);
                    mediaPlayer.pause();
                }
                else {
                    pauseBtn.setBackgroundResource(R.drawable.pause_icone);
                    mediaPlayer.start();
                }
            }
        });

        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%mysongs.size());

                Uri u=Uri.parse(mysongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mysongs.get(position).getName();
                songname.setText(sname);
                mediaPlayer.start();

            }
        });

        previousbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

                position=((position-1)<0)?(mysongs.size()-1):(position-1);
                Uri u=Uri.parse(mysongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);

                sname=mysongs.get(position).getName();
                songname.setText(sname);
                mediaPlayer.start();

            }
        });
    }
}
