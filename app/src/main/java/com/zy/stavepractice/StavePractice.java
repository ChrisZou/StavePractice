package com.zy.stavepractice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Random;

public class StavePractice extends Activity {
    private ImageView staveImg = null;
    private int imgIndex = 0;
    private boolean highStave = false;
	private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    private int rightNum = 0, wrongNum = 0;
    private TextView rightNumView, wrongNumView, timerView;
    private RadioGroup typeGroup;

    private boolean mRunning;

    private static final int LOW_STAVE = 1;
    private static final int HIGH_STAVE = 2;
    private static final int ALL_STAVE = 3;

    private int staveType = ALL_STAVE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        staveImg = (ImageView) this.findViewById(R.id.staveImg);

        rightNumView = (TextView) findViewById(R.id.rightNum);
        wrongNumView = (TextView) findViewById(R.id.wrongNum);
        timerView = (TextView) findViewById(R.id.timer);
		mSoundBox = (CheckBox) findViewById(R.id.main_sound);

        mRunning = true;

        typeGroup = (RadioGroup) findViewById(R.id.typeGroup);
        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                case R.id.lowRadio:
                    staveType = LOW_STAVE;
                    break;
                case R.id.highRadio:
                    staveType = HIGH_STAVE;
                    break;
                case R.id.allRadio:
                    staveType = ALL_STAVE;
                    break;
                default:
                    staveType = ALL_STAVE;
                    break;
                }
            }
        });

        btn1 = (Button) findViewById(R.id.b1);
        btn2 = (Button) findViewById(R.id.b2);
        btn3 = (Button) findViewById(R.id.b3);
        btn4 = (Button) findViewById(R.id.b4);
        btn5 = (Button) findViewById(R.id.b5);
        btn6 = (Button) findViewById(R.id.b6);
        btn7 = (Button) findViewById(R.id.b7);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(1);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(2);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(3);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(4);
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(5);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(6);
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check(7);
            }
        });
        nextImg();
        startTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, 0, "关于音谱练习V1.1");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            AlertDialog.Builder builder = new Builder(StavePractice.this);
            builder.setMessage("如果您喜欢这个程序，或者对该程序有任何改进的意见，欢迎联系作者 邹小创：happystriving@126.com, 谢谢！");
            builder.setTitle("联系作者");
            builder.setPositiveButton("确认", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    StavePractice.this.finish();
                }
            });
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int secAll = msg.arg1;
            int min = secAll / 60;
            int sec = secAll % 60;
            String strMin = (min >= 10) ? ("" + min) : ("0" + min);
            String strSec = (sec >= 10) ? ("" + sec) : ("0" + sec);
            String timeStr = "已用时间 " + strMin + ":" + strSec;
            timerView.setText(timeStr);
        }
    };
    
    final Runnable mRunnable = new Runnable() {
        private int count = 0;
        @Override
        public void run() {
            while (mRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("ERROR", "Thread Interrupted");
                }
                count++;
                Message msg = mHandler.obtainMessage();
                msg.arg1 = count;
                mHandler.sendMessage(msg);
            }
        }
    };

    private void startTimer() {
        Thread thread = new Thread(mRunnable);
        thread.start();
    }

    // check是否点击了正确的按钮
    protected void check(int i) {
        if (i % 7 == imgIndex % 7) {
            rightNum++;
            playVoice();
            nextImg();
        } else {
            wrongNum++;
            notifyWrong();
        }
        updateStatisticData(); // 更新显示统计数据
    }

	CheckBox mSoundBox;
    private void playVoice() {
		if (mSoundBox.isChecked()) {
			MediaPlayer mp = MediaPlayer.create(this, getAudioFile());
			mp.start();
		}
    }

    private void notifyWrong() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    // 更新显示统计数据
    private void updateStatisticData() {
        rightNumView.setText(" " + rightNum + "   ");
        wrongNumView.setText(" " + wrongNum + "   ");
    }

    // 随机显示下一张图片
    private void nextImg() {
        Random rand = new Random();
        // 判断是显示什么类型的音符
        if (staveType == LOW_STAVE) {
            highStave = false;
        } else if (staveType == HIGH_STAVE) {
            highStave = true;
        } else { // 如果是全部类型，则随机产生一种类型
            highStave = rand.nextBoolean();
        }

        imgIndex = (highStave) ? (rand.nextInt(15) + 1) : (rand.nextInt(8) + 8);
        String imgName = (highStave) ? ("h_" + imgIndex) : ("l_" + imgIndex);
        Log.v("db", imgName);
        int imgId = getImg(highStave, imgIndex);
        staveImg.setImageResource(imgId);
    }

    // 根据imgIndex得到下一张图片的Id
    private int getAudioFile() {
        if (highStave) {
            switch (imgIndex) {
            case 1:
                return R.raw.h_1;
            case 2:
                return R.raw.h_2;
            case 3:
                return R.raw.h_3;
            case 4:
                return R.raw.h_4;
            case 5:
                return R.raw.h_5;
            case 6:
                return R.raw.h_6;
            case 7:
                return R.raw.h_7;
            case 8:
                return R.raw.h_8;
            case 9:
                return R.raw.h_9;
            case 10:
                return R.raw.h_10;
            case 11:
                return R.raw.h_11;
            case 12:
                return R.raw.h_12;
            case 13:
                return R.raw.h_13;
            case 14:
                return R.raw.h_14;
            case 15:
                return R.raw.h_15;
            }
        } else {
            switch (imgIndex) {
            /*
             * case 1: return R.raw.l_1; case 2: return R.raw.l_2; case 3:
             * return R.raw.l_3; case 4: return R.raw.l_4; case 5: return
             * R.raw.l_5; case 6: return R.raw.l_6; case 7: return R.raw.l_7;
             */
            case 8:
                return R.raw.l_8;
            case 9:
                return R.raw.l_9;
            case 10:
                return R.raw.l_10;
            case 11:
                return R.raw.l_11;
            case 12:
                return R.raw.l_12;
            case 13:
                return R.raw.l_13;
            case 14:
                return R.raw.l_14;
            case 15:
                return R.raw.l_15;
            }
        }
        return R.raw.h_1;
    }

    // 根据imgIndex得到下一张图片的Id
    private int getImg(boolean highStave2, int imgIndex2) {

        if (highStave2) {
            switch (imgIndex2) {
            case 1:
                return R.drawable.h_1;
            case 2:
                return R.drawable.h_2;
            case 3:
                return R.drawable.h_3;
            case 4:
                return R.drawable.h_4;
            case 5:
                return R.drawable.h_5;
            case 6:
                return R.drawable.h_6;
            case 7:
                return R.drawable.h_7;
            case 8:
                return R.drawable.h_8;
            case 9:
                return R.drawable.h_9;
            case 10:
                return R.drawable.h_10;
            case 11:
                return R.drawable.h_11;
            case 12:
                return R.drawable.h_12;
            case 13:
                return R.drawable.h_13;
            case 14:
                return R.drawable.h_14;
            case 15:
                return R.drawable.h_15;
            }
        } else {
            switch (imgIndex2) {
            case 1:
                return R.drawable.l_1;
            case 2:
                return R.drawable.l_2;
            case 3:
                return R.drawable.l_3;
            case 4:
                return R.drawable.l_4;
            case 5:
                return R.drawable.l_5;
            case 6:
                return R.drawable.l_6;
            case 7:
                return R.drawable.l_7;
            case 8:
                return R.drawable.l_8;
            case 9:
                return R.drawable.l_9;
            case 10:
                return R.drawable.l_10;
            case 11:
                return R.drawable.l_11;
            case 12:
                return R.drawable.l_12;
            case 13:
                return R.drawable.l_13;
            case 14:
                return R.drawable.l_14;
            case 15:
                return R.drawable.l_15;
            }
        }
        return R.drawable.h_1;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_HOME:
            // return true;
        case KeyEvent.KEYCODE_BACK:
            /*
             * if (rightNum >= 30) { break; } else { return true; }
             */
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mRunning = false;
        super.onDestroy();
    }
}