package com.pccw.nowplayer.utils;

import android.os.AsyncTask;
import android.os.Looper;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;

import java.util.List;


/**
 * Created by kriz on 18/5/2016.
 */
public class PromiseUtils {
    public static Promise<Void, Void, Void> firstly() {
        return resolve(null);
    }

    public static <D, F, P> Promise<D, F, P> firstly(DonePipe<Void, D, F, P> task) {
        return firstly().then(task);
    }

    public static <D> Promise<D, Throwable, Void> firstlyInBackground(Function<Void, D> task) {
        return firstly(PromiseUtils.<Void, D>inBackground(task));
    }

    public static <D, F, P> Promise<D, F, P> ignoreError(Promise<D, F, P> promise) {
        return ignoreError(promise, false);
    }

    public static <D, F, P> Promise<D, F, P> ignoreError(Promise<D, F, P> promise, boolean returnsToMainThread) {

        final DeferredObject<D, F, P> deferred = new DeferredObject<>();

        promise.always(new AlwaysCallback<D, F>() {
            @Override
            public void onAlways(Promise.State state, D resolved, F rejected) {
                deferred.resolve(resolved);
            }
        });

        Promise<D, F, P> ret = deferred;
        if (returnsToMainThread) ret = new AndroidDeferredObject<>(ret);
        return ret;
    }

    public static <IN, OUT> Promise<OUT, Throwable, Void> inBackground(final Function<IN, OUT> task, final IN input) {

        final DeferredObject<OUT, Throwable, Void> deferred = new DeferredObject<>();

        if (isMainThread()) {

            // send to background
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        OUT output = task.invoke(input);
                        deferred.resolve(output);
                    } catch (Throwable throwable) {
                        deferred.reject(throwable);
                    }
                    return null;
                }
            }.execute();
        } else {

            // already on main thread
            try {
                OUT output = task.invoke(input);
                deferred.resolve(output);
            } catch (Throwable throwable) {
                deferred.reject(throwable);
            }
        }

        return deferred;
    }

    public static <D_IN, D> DonePipe<D_IN, D, Throwable, Void> inBackground(final Function<D_IN, D> task) {
        return new DonePipe<D_IN, D, Throwable, Void>() {
            @Override
            public Promise<D, Throwable, Void> pipeDone(D_IN result) {
                return inBackground(task, result);
            }
        };
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static <R, D, F, P> DonePipe<R, D, F, P> next(final Promise<D, F, P> task) {
        return new DonePipe<R, D, F, P>() {
            @Override
            public Promise<D, F, P> pipeDone(R result) {
                return task;
            }
        };
    }

    public static Promise nil() {
        return resolve(null);
    }

    public static <D> Promise<D, Throwable, Void> promise(Task<D> task) {
        final Deferred<D, Throwable, Void> deferred = new DeferredObject<>();
        task.run(deferred);
        return new AndroidDeferredObject<>(deferred);
    }

    public static <T> org.osito.androidpromise.deferred.Promise<T> promiseWithValue(final T value) {
        org.osito.androidpromise.deferred.Deferred<T> deferred = org.osito.androidpromise.deferred.Deferred.newDeferred();
        deferred.resolve(value);
        return deferred;
    }

    public static <D, F, P> Promise<D, F, P> reject(F exception) {
        return new AndroidDeferredObject<>(new DeferredObject<D, F, P>().reject(exception));
    }

    public static <D, F, P> Promise<D, F, P> resolve(D result) {
        return new AndroidDeferredObject<>(new DeferredObject<D, F, P>().resolve(result));
    }

    public static <D_IN, D, F, P> DonePipe<D_IN, D, F, P> resolveAs(final D result) {
        return new DonePipe<D_IN, D, F, P>() {

            @Override
            public Promise<D, F, P> pipeDone(D_IN ignored) {
                return PromiseUtils.resolve(result);
            }
        };
    }

    public static Promise<MultipleResults, OneReject, MasterProgress> when(List<Promise> promises, final boolean ignoreIndividualError, boolean resolveOnMainThread) {

        if (Is.empty(promises)) return PromiseUtils.resolve(null);

        Promise[] tasks = new Promise[promises.size()];

        for (int i = 0; i < promises.size(); i++) {

            if (ignoreIndividualError) {
                tasks[i] = ignoreError(promises.get(i), false);
            } else {
                tasks[i] = promises.get(i);
            }
        }

        DeferredManager dm = new DefaultDeferredManager();
        Promise<MultipleResults, OneReject, MasterProgress> ret = dm.when(tasks);
        if (resolveOnMainThread) ret = new AndroidDeferredObject<>(ret);
        return ret;
    }

    public interface Function<IN, OUT> {
        OUT invoke(IN input) throws Throwable;
    }

    public interface Task<D> {
        void run(final Deferred<D, Throwable, Void> deferred);
    }
}
