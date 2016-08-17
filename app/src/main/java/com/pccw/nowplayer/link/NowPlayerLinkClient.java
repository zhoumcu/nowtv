package com.pccw.nowplayer.link;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.pccw.nowplayer.link.handler.LinkHandler;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowtv.nmaf.utilities.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Swifty on 2016/3/15.
 */
public class NowPlayerLinkClient {
    private static final String TAG = NowPlayerLinkClient.class.getSimpleName();
    private static NowPlayerLinkClient ourInstance;
    private final Map<String, String> linkHandlerMap = new HashMap<>();
    private ArrayList<LinkHandler> installedHandlers;

    private NowPlayerLinkClient() {
        installedHandlers = new ArrayList<>();
    }

    public static NowPlayerLinkClient getInstance() {
        if (ourInstance == null) {
            ourInstance = new NowPlayerLinkClient();
        }
        return ourInstance;
    }

    public void registerLink(Class<? extends LinkHandler> klass) {
        try {
            String[] actions = (String[]) klass.getMethod("getHooks").invoke(null);
            for (String action : actions) {
                linkHandlerMap.put(action, klass.getCanonicalName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(this.getClass().getSimpleName(), "Fail in register, maybe the handler has no 'getHooks' method?");
        }
    }

    public void executeUrlAction(Context context, String link) {
        executeUrlAction(context, link, null);
    }

    public boolean executeUrlAction(final Context context, final String link, Bundle bundle) {
        Log.i(TAG, "Handling action: " + link);
        try {
            if (TextUtils.isEmpty(link) || context == null) return false;
            String[] action_parts = StringUtils.splitBySeparator(link, ":", new String[2]);
            Class handlerClass;
            try {
                handlerClass = Class.forName(linkHandlerMap.get(action_parts[0]));
            } catch (Exception e) {
                return false;
            }
            if (!LinkHandler.class.isAssignableFrom(handlerClass))
                return false;

            LinkHandler handler = installHandler(handlerClass);
            if (handler != null) {
                return handler.handlerLink(context, link, action_parts[0], action_parts[1], bundle);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private LinkHandler installHandler(Class<? extends LinkHandler> klass) {
        LinkHandler registeredHandler = null;
        for (LinkHandler handler : installedHandlers) {
            if (handler.getClass().equals(klass)) {
                registeredHandler = handler;
                break;
            }
        }
        if (registeredHandler == null) {
            try {
                registeredHandler = klass.getConstructor().newInstance();
                installedHandlers.add(registeredHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return registeredHandler;
    }

    private LinkHandler uninstallHandler(Class<? extends LinkHandler> klass) {
        LinkHandler unregisterdHandler = null;
        for (LinkHandler handler : installedHandlers) {
            if (handler.getClass().equals(klass)) {
                unregisterdHandler = handler;
                break;
            }
        }
        if (unregisterdHandler != null) {
            installedHandlers.remove(unregisterdHandler);
        }
        return unregisterdHandler;
    }
}
