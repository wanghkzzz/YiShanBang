package com.benben.yishanbang.utils;


import android.os.CountDownTimer;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 获取验证码倒计时
 * Created by Administrator on 2016/8/19 0019.
 */
public class TimerUtil {

    private TextView tv;

    public TimerUtil(TextView tv) {
        this.tv = tv;
    }

    public void timers() {
        timer.start();
    }

    public CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tv.setEnabled(false);
            tv.setText((millisUntilFinished / 1000) + "秒");
        }

        @Override
        public void onFinish() {
            tv.setEnabled(true);
            tv.setText("获取验证码");
        }
    };


    /**
     * 获取随机验证码
     */
    public static String getNum() {
        StringBuilder sb = new StringBuilder();
        //随机生成6位数  发送到聚合
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int a = random.nextInt(10);
            sb.append(a);
        }
        return sb.toString();
    }

    public static String times(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }
}
