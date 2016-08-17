package com.pccw.nowplayer.model.db_orm;

import android.content.Context;
import android.util.Log;

import com.orm.SugarContext;
import com.orm.SugarRecord;
import com.pccw.nowplayer.PlayerApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swifty on 3/23/2016.
 */
public class OrmController {

    /**
     * @param model
     * @return saved id
     */
    public static <T extends SugarRecord> long save(T model) {
        return T.save(model);
    }

    public static void initORM(Context context) {
        SugarContext.init(context);
    }

    public static <T extends SugarRecord> void saveList(List<T> modelList) {
        T.saveInTx(modelList);
    }

    public static <T extends SugarRecord> void saveArray(T[] models) {
        T.saveInTx(models);
    }

    public static <T extends SugarRecord> T findById(Class<T> modelClass, long id) {
        T t = null;
        try {
            t = T.findById(modelClass, id);
        } catch (Exception e) {
            Log.w(PlayerApplication.TAG, e.toString());
        }
        return t;
    }

    public static <T extends SugarRecord> List<T> listAll(Class<T> modelClass) {
        return listAll(modelClass, null);
    }

    public static <T extends SugarRecord> List<T> listAll(Class<T> modelClass, String order) {
        List<T> ts = null;
        try {
            ts = T.listAll(modelClass, order);
        } catch (Exception e) {
            Log.w(PlayerApplication.TAG, e.toString());
        } finally {
            if (ts == null) ts = new ArrayList<>();
        }
        return ts;
    }

    public static <T extends SugarRecord> boolean delete(T model) {
        return T.delete(model);
    }

    public static <T extends SugarRecord> int deleteAll(Class<T> kclass) {
        return T.deleteAll(kclass);
    }
}
