package com.pccw.nowplayer.utils;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.pccw.nowplayer.R;
import com.pccw.nowplayer.constant.Constants;


/**
 * Created by Swifty on 5/5/2016.
 */
public class FragmentUtil {

    public enum Animation {
        SLIDEUP,
        SLIDERIGHT,
        FADE,
        NONE,
    }

    public static void changeFragment(AppCompatActivity context, ViewGroup container, Fragment f) {
        if (context != null && container != null && f != null)
            changeFragment(context, container.getId(), f);
    }

    public static void changeFragment(AppCompatActivity context, ViewGroup container, Fragment f, Animation animation) {
        if (context != null && container != null && f != null)
            changeFragment(context, container.getId(), f, null, true, true, animation);
    }

    public static void changeFragment(AppCompatActivity context, int containerId, Fragment f) {
        changeFragment(context, containerId, f, true);
    }

    public static void changeFragment(AppCompatActivity context, int containerId, Fragment f, boolean addToBackStack) {
        changeFragment(context, containerId, f, addToBackStack, true);
    }

    public static void changeFragment(AppCompatActivity context, int containerId, Fragment f, boolean addToBackStack, boolean executePendingTransaction) {
        changeFragment(context, containerId, f, null, addToBackStack, executePendingTransaction, Animation.NONE);
    }

    public static void changeFragment(AppCompatActivity context, int containerId, Fragment f, Bundle args) {
        changeFragment(context, containerId, f, args, true, true, Animation.NONE);
    }

    public static void changeFragment(AppCompatActivity context, int containerId, Fragment f, Bundle args, boolean addToBackStack, boolean executePendingTransaction) {
        changeFragment(context, containerId, f, args, addToBackStack, executePendingTransaction, Animation.NONE);
    }

    public static void changeFragment(AppCompatActivity context, int containerId, Fragment f, Bundle args, boolean addToBackStack, boolean executePendingTransaction, Animation animation) {
        if (f == null || context == null) return;
        try {
            if (args != null) {
                f.setArguments(args);
            } else if (f.getArguments() == null) {
                f.setArguments(new Bundle());
            }
            android.support.v4.app.FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            if (animation == Animation.FADE)
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            else if (animation == Animation.SLIDEUP) {
                transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.fade_out);
            } else if (animation == Animation.SLIDERIGHT) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            }
            transaction.replace(containerId, f, f.getArguments().getString(Constants.FragmentTag));
            if (addToBackStack) {
                transaction.addToBackStack(f.getClass().getSimpleName());
            }
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (executePendingTransaction) {
            context.getSupportFragmentManager().executePendingTransactions();

        }
    }

    public static void removeFragment(FragmentActivity context, Fragment f) {
        context.getSupportFragmentManager().beginTransaction().remove(f).setCustomAnimations(R.anim.fade_in, R.anim.fade_out).commitAllowingStateLoss();
        context.getSupportFragmentManager().executePendingTransactions();
    }

    public static void hideAndShowFragment(FragmentActivity context, Fragment f) {
        context.getSupportFragmentManager().beginTransaction().hide(f).show(f).setCustomAnimations(R.anim.fade_in, R.anim.fade_out).commitAllowingStateLoss();
        context.getSupportFragmentManager().executePendingTransactions();
    }

    public static void detachAndAttachFragment(FragmentActivity context, Fragment f) {
        context.getSupportFragmentManager().beginTransaction().detach(f).attach(f).setCustomAnimations(R.anim.fade_in, R.anim.fade_out).commitAllowingStateLoss();
        context.getSupportFragmentManager().executePendingTransactions();
    }

    public static void clearBackStack(FragmentManager supportFragmentManager) {
        if (supportFragmentManager != null) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public static void popBottomFragment(FragmentManager supportFragmentManager) {
        if (supportFragmentManager != null) {
            for (int i = 0; i < supportFragmentManager.getBackStackEntryCount() - 1; i++) {
                supportFragmentManager.popBackStack();
            }
        }
    }
}