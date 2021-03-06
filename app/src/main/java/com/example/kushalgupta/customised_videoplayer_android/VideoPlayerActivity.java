package com.example.kushalgupta.customised_videoplayer_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by kushalgupta on 04/04/18.
 */
public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl, MediaPlayer.OnCompletionListener, TextToSpeech.OnInitListener {

    SurfaceView videoSurface;
    MediaPlayer player, player2;
    VideoControllerView controller;
    TextView dura, dura2, NoOfSets, middleCount;
    Boolean count;
    int screenTime, screenTime2;
    CountDownTimer countDownTimer;
    public static final String TAG = "chla";
    ProgressDialog progressDialog;
    Button skipIntroBtn;
    int noOfSets = 2;
    String videoName;
    int IntroReal;
    int videoNo;
    int currentSet = 0;
    int tottalReps = 4;
    private TextToSpeech tts;
    int flag = 0;

    CheckBox soundOn;
    Boolean isSoundOn = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        dura = findViewById(R.id.duration);
        dura2 = findViewById(R.id.duration2);
        skipIntroBtn = findViewById(R.id.btn_skip_intro);
        skipIntroBtn.setOnClickListener(skipIntriListner);
        NoOfSets = findViewById(R.id.no_of_sets);
        middleCount = findViewById(R.id.countInBetweenScreen);
        tts = new TextToSpeech(this, this);
        soundOn = findViewById(R.id.muteCheckBox);

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        soundOn.setChecked(isSoundOn);
        soundOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    player.setVolume(1, 1);
                    isSoundOn = true;
                }
                else {
                    player.setVolume(0,0);
                    isSoundOn = false;
                }
            }
        });

        // player = new MediaPlayer();
        controller = new VideoControllerView(this);
        startNext();
//
//        try {
//
//            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//             //player.setDataSource(this, Uri.parse("http://dl2.n3.22.cdn.perfectgirls.net/mp4/HkW0SBQNq1yMVYiNUuiiMA==,1523648541/526/180/526180-full.mp4"));
//
//            player.setDataSource(this, Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
//            player.setOnPreparedListener(this);
////player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
////    @Override
////    public void onCompletion(MediaPlayer mediaPlayer) {
////
////       startNext();
////
////    }
////});
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hideNavAndStatus();
    }

    private void hideNavAndStatus(){
        //hides navigationbar and statusbar
        videoSurface.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void showFeedBack(){
        //cleaning up
        if(player!=null)
            player.stop();

        controller.hide();
        controller.setEnabled(false);

        tts.stop();
        tts.shutdown();

        //removing all views and placing feedback view in its place
        LinearLayout linearLayout = findViewById(R.id.video_container);
        linearLayout.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.feed_back,linearLayout,false);
        linearLayout.addView(view);

        Button restart = (Button)view.findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        NumberPicker numberPicker = (NumberPicker)view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        numberPicker.setValue(4);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //set value here
            }
        });

        final Button comment = (Button) view.findViewById(R.id.commentButton);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(VideoPlayerActivity.this);
                alert.setMessage("Write a comment");
                final EditText edittext = new EditText(VideoPlayerActivity.this);
                alert.setView(edittext);
                alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
                alert.show();

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        player.pause();
        dura.setVisibility(View.INVISIBLE);
        dura2.setVisibility(View.INVISIBLE);
        NoOfSets.setVisibility(View.INVISIBLE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        return false;
    }


    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback


    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: 1");
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer), IntroReal, noOfSets, videoName);
        player.start();
        // player.setLooping(true);
        if(progressDialog!=null)
            progressDialog.dismiss();
        //dura.setVisibility(View.VISIBLE);
        Log.d(TAG, "onPrepared: " + getDuration());
        int gy = getDuration();
        if (IntroReal == 1) {
            if (videoNo == 0 || videoNo == 1) {
                gy = gy * noOfSets;
            } else if (videoNo == 2) {
                gy = gy * tottalReps;
                noOfSets = tottalReps;
            }
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(gy, 1000) {                     //geriye sayma

            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                if (IntroReal == 0) {
                    dura.setVisibility(View.VISIBLE);
                    dura2.setVisibility(View.GONE);
                    NoOfSets.setVisibility(View.INVISIBLE);
                    NoOfSets.setText("Remaining Sets : " + String.valueOf(noOfSets));
                    dura.setText(videoName + "\n" + "Starts in " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    skipIntroBtn.setVisibility(View.VISIBLE);
                } else if (IntroReal == 1) {
                    if (videoNo == 0 || videoNo == 1) {
                        dura.setVisibility(View.GONE);
                        skipIntroBtn.setVisibility(View.GONE);
                        dura2.setVisibility(View.VISIBLE);
                        NoOfSets.setVisibility(View.VISIBLE);
                        NoOfSets.setText("Remaining Sets : " + String.valueOf(noOfSets));
                        dura.setText(videoName + "\n" + "Starts in " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                        dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    } else {
                        dura.setVisibility(View.GONE);
                        skipIntroBtn.setVisibility(View.GONE);
                        dura2.setVisibility(View.VISIBLE);
                        NoOfSets.setVisibility(View.VISIBLE);
                        //   NoOfSets.setText("Remaining Sets : " + String.valueOf(noOfSets));
                        // dura.setText(videoName + "\n" + "Starts in " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                        // dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                        dura2.setText(String.valueOf(currentSet) + "/" + String.valueOf(tottalReps));
                        NoOfSets.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));


                    }

                }


            }

            public void onFinish() {
                dura.setText("00:00:00");
                dura2.setText("00:00:00");
                if (IntroReal == 0 && videoNo == 0) {
                    // if (flag != 1) {
                    IntroReal = 1;
                    videoNo = 0;
                    //  }
                    // flag = 0;
                } else if (IntroReal == 1 && videoNo == 0) {
                    IntroReal = 0;
                    videoNo = 1;
                } else if (IntroReal == 0 && videoNo == 1) {
                    IntroReal = 1;
                    videoNo = 1;
                } else if (IntroReal == 1 && videoNo == 1) {
                    IntroReal = 0;
                    videoNo = 2;
                } else if (IntroReal == 0 && videoNo == 2) {
                    IntroReal = 1;
                    videoNo = 2;
                } else if(IntroReal==1 &&videoNo==2){
                    cancel();
                }
                startNext();
            }
        }.start();
    }
// End MediaPlayer.OnPreparedListener


    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        int dur = player.getDuration();
//        dura.setText(Integer.toString(dur));
        return dur;
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {

        dura.setVisibility(View.INVISIBLE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        dura2.setVisibility(View.GONE);
        NoOfSets.setVisibility(View.INVISIBLE);
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        //https://drive.google.com/file/d/19-QXY7dFSHGzDMgmQ2KFQnySfGznjnC1/view?usp=sharing
        player.start();
//        dura.setVisibility(View.VISIBLE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(screenTime, 1000) {                     //geriye sayma

            public void onTick(long millisUntilFinished) {

                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;

                if (IntroReal == 0) {
                    dura.setVisibility(View.VISIBLE);
                    dura2.setVisibility(View.GONE);
                    NoOfSets.setVisibility(View.INVISIBLE);
                    NoOfSets.setText("Remaining Sets : " + String.valueOf(noOfSets));
                    dura.setText(videoName + "\n" + "Starts in " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    skipIntroBtn.setVisibility(View.VISIBLE);
                } else if (IntroReal == 1) {
                    if (videoNo == 0 || videoNo == 1) {
                        dura.setVisibility(View.GONE);
                        skipIntroBtn.setVisibility(View.GONE);
                        dura2.setVisibility(View.VISIBLE);
                        NoOfSets.setVisibility(View.VISIBLE);
                        NoOfSets.setText("Remaining Sets : " + String.valueOf(noOfSets));
                        dura.setText(videoName + "\n" + "Starts in " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                        dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    } else {
                        dura.setVisibility(View.GONE);
                        skipIntroBtn.setVisibility(View.GONE);
                        dura2.setVisibility(View.VISIBLE);
                        NoOfSets.setVisibility(View.VISIBLE);
                        //   NoOfSets.setText("Remaining Sets : " + String.valueOf(noOfSets));
                        // dura.setText(videoName + "\n" + "Starts in " + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                        // dura2.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                        dura2.setText(String.valueOf(currentSet) + "/" + String.valueOf(tottalReps));
                        NoOfSets.setText("Total Time Remaining : \n" + f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));


                    }
                }
            }

            public void onFinish() {
                dura.setText("00:00:00");
                if (IntroReal == 0 && videoNo == 0) {

                    IntroReal = 1;
                    videoNo = 0;

                } else if (IntroReal == 1 && videoNo == 0) {
                    IntroReal = 0;
                    videoNo = 1;
                } else if (IntroReal == 0 && videoNo == 1) {
                    IntroReal = 1;
                    videoNo = 1;
                } else if (IntroReal == 1 && videoNo == 1) {
                    IntroReal = 0;
                    videoNo = 2;
                } else if (IntroReal == 0 && videoNo == 2) {
                    IntroReal = 1;
                    videoNo = 2;
                } else if(IntroReal==1 && videoNo==2){
                    cancel();
                    return;
                }
                startNext();
            }
        }.start();
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    public void toggleFullScreen() {
    }

    @Override
    public void setOnScreenTime(int time) {
        // dura.setText(time);
        if (IntroReal == 1) {
            int tempTotalDuration = getDuration();
            int ku = tempTotalDuration - time;
            screenTime = tempTotalDuration * noOfSets - ku + 1000;


        } else {
            screenTime = time + 1000;
        }

    }

    @Override
    public void nextVideo() {
        if (player != null) {

            if (IntroReal == 0 && videoNo == 0) {
                IntroReal = 1;
                videoNo = 0;
            } else if (IntroReal == 1 && videoNo == 0) {
                IntroReal = 0;
                videoNo = 1;
            } else if (IntroReal == 0 && videoNo == 1) {
                IntroReal = 1;
                videoNo = 1;
            } else if (IntroReal == 1 && videoNo == 1) {
                IntroReal = 0;
                videoNo = 2;
            } else if (IntroReal == 0 && videoNo == 2) {
                IntroReal = 1;
                videoNo = 2;
            } else if (IntroReal == 1 && videoNo == 2) {
//                IntroReal = 0;
//                videoNo = 0;
                showFeedBack();
                return;
            }
            startNext();


        }
        //  player.stop();
//            player.reset();
//           // player.release();
//         //   player = null;
//
////player=new MediaPlayer();
//            try {
//
//              // Uri u=Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//                //player=MediaPlayer.create(this,u);
//                player = new MediaPlayer();
//                SurfaceHolder videoHolder = videoSurface.getHolder();
//                videoHolder.addCallback(this);
//
//                controller = new VideoControllerView(this);
//                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                player.setDataSource(this, Uri.parse("http://www.html5videoplayer.net/videos/toystory.mp4"));
//                player.setOnPreparedListener(this);
////                player.prepareAsync();
//
//
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
    }


    @Override
    public void prevVideo() {
        if (player != null) {

            if (IntroReal == 0 && videoNo == 0) {
                IntroReal = 0;
                videoNo = 2;
            } else if (IntroReal == 1 && videoNo == 0) {
                IntroReal = 0;
                videoNo = 2;
            } else if (IntroReal == 0 && videoNo == 1) {
                IntroReal = 0;
                videoNo = 0;
            } else if (IntroReal == 1 && videoNo == 1) {
                IntroReal = 0;
                videoNo = 0;
            } else if (IntroReal == 0 && videoNo == 2) {
                IntroReal = 0;
                videoNo = 1;
            } else if (IntroReal == 1 && videoNo == 2) {
                IntroReal = 0;
                videoNo = 1;
            }
            startNext();


        }
    }

    public void startNext() {
        if (player == null) {
            player = new MediaPlayer();
            try {
                //   player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                videoName = "Stack PushUp intro";
                IntroReal = 0;
                videoNo = 0;
                //player.setDataSource("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupintro);
                player.setDataSource(this,video);

                // player.setOnPreparedListener(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }if(flag == 1) {
//        else if (IntroReal == 0 && videoNo == 0) {
//                player.reset();
//                try {
//                    videoName = "Big Buck Bunny";
//                    IntroReal = 1;
//                    videoNo = 0;
//                    if (countDownTimer != null) {
//                        countDownTimer.cancel();
//                    }
//                    player.setDataSource("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//                    player.prepareAsync();
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            flag =0;
//        }
        } else if (IntroReal == 1 && videoNo == 0) {
            player.reset();
            try {
                videoName = "Stack PushUp";
                IntroReal = 1;
                videoNo = 0;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);
                player.setDataSource(this,video);
                player.prepareAsync();


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (IntroReal == 0 && videoNo == 1) {
            player.reset();
            try {
                videoName = "Superman PushUp intro";
                IntroReal = 0;
                videoNo = 1;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.supermanpushupintro);
                player.setDataSource(this,video);
                player.prepareAsync();


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (IntroReal == 1 && videoNo == 1) {
            player.reset();
            try {
                videoName = "Superman Pushup";
                IntroReal = 1;
                videoNo = 1;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.supermanpushup);
                player.setDataSource(this,video);
                player.prepareAsync();


            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (IntroReal == 0 && videoNo == 2) {
            player.reset();
            try {
                videoName = "Stack PushUp";
                IntroReal = 0;
                videoNo = 2;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stackpushupsingle);
                player.setDataSource(this,video);
                player.prepareAsync();


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (IntroReal == 1 && videoNo == 2) {
            player.reset();
            try {
                videoName = "SuperMan PushUp";
                IntroReal = 1;
                videoNo = 2;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.supermanpushup);
                player.setDataSource(this,video);
                player.prepareAsync();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Loading ... ");

                progressDialog.setCancelable(false);
                progressDialog.show();
                progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        hideNavAndStatus();
                    }
                });

            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (IntroReal == 1) {
            if (videoNo == 0 || videoNo == 1) {
                if (noOfSets > 0) {
                    player.seekTo(0);
                    player.start();
                    noOfSets--;
                } else {

                    if (videoNo == 0) {
                        IntroReal = 0;
                        videoNo = 1;
                        noOfSets = 2;
                        startNext();
                    }

                    if (videoNo == 1) {
                        IntroReal = 0;
                        videoNo = 2;
                        noOfSets = 2;
                        currentSet = 0;
                        startNext();
                    }


                }
            } else if (videoNo == 2) {
                currentSet = currentSet + 1;
                flag = 1;
                if (currentSet <= tottalReps) {
                    if (currentSet != 4) {
                        player.seekTo(0);
                        player.start();
                        noOfSets--;
                    } else {
                        showFeedBack();

//                        player.reset();
//                        try {
//                            videoName = "Big Buck Bunny";
//                            IntroReal = 1;
//                            videoNo = 0;
//                            currentSet = 0;
//                            noOfSets = 2;
//                            if (countDownTimer != null) {
//                                countDownTimer.cancel();
//                            }
//                            player.setDataSource("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//                            player.prepareAsync();
//
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }


                    }
                    middleCount.setVisibility(View.VISIBLE);
                    middleCount.setText(String.valueOf(currentSet));
                    if(isSoundOn) {
                        tts.speak(String.valueOf(currentSet), TextToSpeech.QUEUE_FLUSH, null);
                    }
                    YoYo.with(Techniques.ZoomIn).duration(2000).playOn(middleCount);
                    YoYo.with(Techniques.FadeOut).duration(1000).delay(2000).playOn(middleCount);
                    // middleCount.setVisibility(View.INVISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            middleCount.setVisibility(View.INVISIBLE);

                        }
                    }, 3000);
                }

//                } else {
//                    IntroReal = 0;
//                    videoNo = 0;
//                    currentSet = 0;
//                    noOfSets = 2;
//                    startNext();
//                }
            }

        }

        if (IntroReal == 0 && videoNo == 0) {
            IntroReal = 1;
            videoNo = 0;
            startNext();

        }

        if (IntroReal == 0 && videoNo == 1) {
            IntroReal = 1;
            videoNo = 1;
            startNext();
        }
        if (IntroReal == 0 && videoNo == 2) {
            IntroReal = 1;
            videoNo = 2;
            startNext();
        }
        //    startNext();


    }

    private View.OnClickListener skipIntriListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dura.setVisibility(View.INVISIBLE);
            if (videoNo == 0) {
                IntroReal = 1;
                videoNo = 0;
            } else if (videoNo == 1) {
                IntroReal = 1;
                videoNo = 1;
            } else if (videoNo == 2) {
                IntroReal = 1;
                videoNo = 2;
            }
            startNext();
        }
    };


    @Override
    public void onInit(int i) {

        if (i == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}

// End VideoMediaController.MediaPlayerControl


