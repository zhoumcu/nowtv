package com.pccw.nowplayer.utils;

/**
 * Created by Kriz on 22/1/2016.
 */
public class TaskStatus {
    protected boolean cancelled;
    protected boolean failed;
    protected boolean finished;
    protected boolean inProgress;
    protected long lastStart;

    public void cancelled() {
        inProgress = false;
        finished = false;
        failed = false;
        cancelled = true;
    }

    public void failed() {
        inProgress = false;
        finished = false;
        failed = true;
        cancelled = false;
    }

    public void finished() {
        inProgress = false;
        finished = true;
        failed = false;
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public boolean lastTrialOver(long duration) {
        long now = System.currentTimeMillis();
        boolean ret = now >  lastStart + duration;
        return ret;
    }

    public boolean shouldStart() {
        return !finished && !inProgress;
    }

    public void started() {
        inProgress = true;
        finished = false;
        failed = false;
        cancelled = false;
        lastStart = System.currentTimeMillis();
    }
}
