package org.androidtown.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU = 101;
    FrameLayout character, dayTimeBackground, nightTimeBackground;
    RelativeLayout flowerLeft, flowerRight;
    ImageView leftArm, leftEar, rightEar;
    ImageButton evaBtn, selectDietBtn, settingBtn;
    Handler mHandler = new Handler();
    ArmThread armThread;
    EyeThread eyeThread;
    EarThread earThread;
    MouthThread mouthThread;
    ImageSwitcher switcher, switcher_m;
    boolean running;
    Animation armAnim, armDownAnim, lEarAnim, lEarDownAnim, rEarAnim, rEarDownAnim, flowerAnim;
    boolean isArmGoingDown = false;
    boolean isEarGoingDown = false;
    int hightestIndex = 0;
    static DataBase db;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
    SimpleDateFormat hourFormat = new SimpleDateFormat("HH",java.util.Locale.getDefault());
    Date date = new Date();
    String strDate = dateFormat.format(date);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        evaBtn = (ImageButton) findViewById(R.id.evaluationBtn);
        selectDietBtn = (ImageButton) findViewById(R.id.selectDietBtn);
        settingBtn = (ImageButton) findViewById(R.id.settingBtn);
        switcher = (ImageSwitcher) findViewById(R.id.switcher);
        switcher_m = (ImageSwitcher) findViewById(R.id.switcher_mouth);
        character = (FrameLayout) findViewById(R.id.character);
        dayTimeBackground=(FrameLayout)findViewById(R.id.dayTimeBackground);
        nightTimeBackground=(FrameLayout)findViewById(R.id.nightTimeBackground);
        flowerLeft = (RelativeLayout) findViewById(R.id.flowerLayout_left);
        flowerRight = (RelativeLayout) findViewById(R.id.flowerLayout_right);
        leftArm = (ImageView) findViewById(R.id.leftArm);
        final Animation clickAni = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_animation);
        final Animation clickUpAni = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_up_animation);
        armAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.arm_rotate);
        armDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.arm_down_rotate);
        lEarAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.l_ear_rotate);
        lEarDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.l_ear_down_rotate);
        rEarAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.r_ear_rotate);
        rEarDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.r_ear_down_rotate);
        flowerAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flower_rotate);
        leftEar = (ImageView) findViewById(R.id.leftEar);
        rightEar = (ImageView) findViewById(R.id.rightEar);
        SharedPreferences preference = getSharedPreferences("first", MODE_PRIVATE);
        int firstviewshow = preference.getInt("first", 0);
        if (firstviewshow != 1) {
            Intent intent = new Intent(MainActivity.this, Tutorial.class);
            startActivity(intent);
        }
        db = new DataBase(MainActivity.this);
        hightestIndex = db.getHighestIngredient(strDate);
        if(hightestIndex==5)
            hightestIndex=0;
        setHighestIndexImage();
        setBackgroundImage();

        flowerLeft.getChildAt(hightestIndex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                flowerLeft.getChildAt(hightestIndex).startAnimation(flowerAnim);
            }
        });


        flowerRight.getChildAt(hightestIndex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                flowerRight.getChildAt(hightestIndex).startAnimation(flowerAnim);
            }
        });
        character.getChildAt(0).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                final ImageView kkya = (ImageView) findViewById(R.id.kkya);
                v.clearAnimation();
                character.startAnimation(clickAni);
                character.startAnimation(clickUpAni);
                kkya.setVisibility(View.VISIBLE);

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                kkya.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                };

                Timer mTimer = new Timer();
                mTimer.schedule(task, 2000);

            }
        });

        evaBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                evaBtn.startAnimation(clickAni);
                Intent intent = new Intent(getApplicationContext(), Evaluation.class);
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

        selectDietBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                selectDietBtn.startAnimation(clickAni);
                Intent intent = new Intent(getApplicationContext(), SearchFood.class);
                startActivity(intent);
            }
        });

//        settingBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
//                startActivity(intent);
//            }
//        });

        switcher.setFactory(new ViewSwitcher.ViewFactory()

        {
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setBackgroundColor(0x00000000);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        switcher_m.setFactory(new ViewSwitcher.ViewFactory()

        {
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setBackgroundColor(0x00000000);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });

        startAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MENU) {
            Toast.makeText(getApplicationContext(), "요청 코드: " + requestCode + " 결과 코드: " + resultCode, Toast.LENGTH_LONG).show();

            if (resultCode == RESULT_OK) {
                String msg = data.getExtras().getString("msg");
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startAnimation() {
        switcher.setVisibility(View.VISIBLE);
        switcher_m.setVisibility(View.VISIBLE);
        eyeThread = new EyeThread();
        armThread = new ArmThread();
        earThread = new EarThread();
        mouthThread = new MouthThread();
        eyeThread.start();
        armThread.start();
        earThread.start();
        mouthThread.start();
    }


    private void stopAnimation() {
        running = false;
        try {
            eyeThread.join();
            armThread.join();
            earThread.join();
        } catch (InterruptedException e) {
        }
        switcher.setVisibility(View.INVISIBLE);
        switcher_m.setVisibility(View.INVISIBLE);
    }

    class EarThread extends Thread {

        @Override
        public void run() {
            running = true;
            while (running) {
                synchronized (this) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isEarGoingDown) {
                                leftEar.startAnimation(lEarAnim);
                                rightEar.startAnimation(rEarAnim);
                                isEarGoingDown = false;
                            } else {
                                leftEar.startAnimation(lEarDownAnim);
                                rightEar.startAnimation(rEarDownAnim);
                                isEarGoingDown = true;
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }


    class ArmThread extends Thread {

        @Override
        public void run() {
            running = true;
            while (running) {
                synchronized (this) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isArmGoingDown) {
                                leftArm.startAnimation(armDownAnim);
                                isArmGoingDown = false;
                            } else {
                                leftArm.startAnimation(armAnim);
                                isArmGoingDown = true;
                            }
                        }
                    });
                    try {
                        Thread.sleep(1300);
                    } catch (Exception e) {

                    }
                }

            }
        }
    }

    class MouthThread extends Thread {

        int m_duration;
        final int image_m_Id[] = {R.drawable.mouth_open, R.drawable.mouth_close};
        int m_currentIndex = 0;

        @Override
        public void run() {
            running = true;
            while (running) {
                synchronized (this) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            switcher_m.setImageResource(image_m_Id[m_currentIndex]);
                        }
                    });
                    m_currentIndex++;
                    if (m_currentIndex == image_m_Id.length) {
                        m_currentIndex = 0;
                    }
                    try {
                        m_duration = getRandomTime(3000, 5000);
                        Thread.sleep(m_duration);
                    } catch (InterruptedException e) {
                    }
                }
            }   //while end

        }
    }

    class EyeThread extends Thread {
        int duration = 100;
        final int imageId[] = {R.drawable.eye_open, R.drawable.eye_close, R.drawable.eye_open, R.drawable.eye_close};
        int currentIndex = 0;

        @Override
        public void run() {
            running = true;
            while (running) {
                synchronized (this) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            switcher.setImageResource(imageId[currentIndex]);
                        }
                    });
                    currentIndex++;
                    if (currentIndex == imageId.length) {
                        currentIndex = 0;
                        try {
                            int sleepTime = getRandomTime(4000, 6000);
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                        }
                    }
                    try {
                        Thread.sleep(duration);
                    } catch (InterruptedException e) {
                    }
                }
            }   //while end

        }   //run end
    }

    public int getRandomTime(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }

    public static DataBase getDBInstance() {
        return db;
    }

    public void setHighestIndexImage() {
        for (int i = 0; i < flowerLeft.getChildCount(); i++) {
            flowerLeft.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < flowerRight.getChildCount(); i++) {
            flowerRight.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        flowerLeft.getChildAt(hightestIndex).setVisibility(View.VISIBLE);
        flowerRight.getChildAt(hightestIndex).setVisibility(View.VISIBLE);
    }

    public void setBackgroundImage() {
        String hour = hourFormat.format(date);
        int cHour = Integer.parseInt(hour);
        if(cHour>=18 || cHour <=6){
            nightTimeBackground.setVisibility(View.VISIBLE);
            dayTimeBackground.setVisibility(View.INVISIBLE);
        }
        else{
            nightTimeBackground.setVisibility(View.INVISIBLE);
            dayTimeBackground.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBackgroundImage();
        hightestIndex = db.getHighestIngredient(strDate);
        if(hightestIndex==5)
            hightestIndex=0;
        setHighestIndexImage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setBackgroundImage();
        hightestIndex = db.getHighestIngredient(strDate);
        if(hightestIndex==5)
            hightestIndex=0;
        setHighestIndexImage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAnimation();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
