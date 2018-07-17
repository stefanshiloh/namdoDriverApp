package com.pakfull.stefanshiloh.namdodriverapp;

import android.os.CountDownTimer;


public abstract class CountTimeUp extends CountDownTimer {
    private static final long INTERVAL_MS = 1000;
    private final long duration;

    protected CountTimeUp(long durationMs) {
        super(durationMs, INTERVAL_MS);
        this.duration = durationMs;
    }

    public abstract void onTick(int second);

    @Override
    public void onTick(long msUntilFinished) {
        int second = (int) ((duration - msUntilFinished) / 1000);
        onTick(second);
    }

    @Override
    public void onFinish() {
        onTick(duration / 1000);
    }
}