package com.pccw.nowplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kriz on 17/12/2015.
 */
public class DrawableUtils {
    public static Map<String, String> AliasMap = new HashMap<>();

    public static void addAlias(String name, String alias) {
        AliasMap.put(alias, name);
    }

    public static Bitmap changeColorTinting(Context context, int drawable_id, int color) {
        Drawable drawable = context.getResources().getDrawable(drawable_id);
        if (!(drawable instanceof BitmapDrawable)) return null;

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (bitmap == null) return null;

        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(bitmap);
        Paint p = new Paint();
        p.setColor(color);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        //p.setColorFilter(new LightingColorFilter(color, 10));
        c.drawRect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1, p);
        return bitmap;
    }

    public static Drawable changeDrawableColorTinting(Context context, int drawable_id, int color) {
        if (drawable_id == 0) return null;
        if (color == 0) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return context.getResources().getDrawable(drawable_id, context.getTheme());
            } else {
                return context.getResources().getDrawable(drawable_id);
            }
        }
        return new BitmapDrawable(context.getResources(), changeColorTinting(context, drawable_id, color));
    }

    public static Drawable getDrawable(Context ctx, int resourceId) {
        if (resourceId == 0) return null;
        Drawable ret = null;
        try {
            ret = ctx.getResources().getDrawable(resourceId);
        } catch (Exception ex) {
        }
        return ret;
    }

    public static Drawable getDrawable(Context ctx, String uri) {
        return getDrawable(ctx, getResourceId(ctx, uri));
    }

    public static Drawable getDrawable(Context ctx, int backupColor, String uri) {
        Drawable ret = getDrawable(ctx, uri);
        if (ret == null) {
            Uri u = TypeUtils.toUri(uri);
            if (u != null && u.getScheme().startsWith("http")) {
                ret = new NetworkBitmapDrawable(ctx, backupColor, u);
            }
        }
        return ret;
    }

    /**
     * Looks up a drawable resource ID by name. Name alias is supported. If the prefix "local:" is
     * found, the prefix is striped first.
     *
     * @param ctx
     * @param name of resource. Could be an alias. Could be prefixed by "local:".
     * @return resource ID of the drawable or 0 if not found.
     */
    public static int getResourceId(Context ctx, String name) {
        return getResourceId(ctx, name, "drawable");
    }

    /**
     * Looks up defType resource ID by name. Name alias is supported. If the prefix "local:" is
     * found, the prefix is striped first.
     *
     * @param ctx
     * @param name    of resource. Could be an alias. Could be prefixed by "local:".
     * @param defType resource type
     * @return resource ID or 0 if not found.
     */
    public static int getResourceId(Context ctx, String name, String defType) {
        if (ctx == null || name == null || name.isEmpty()) return 0;

        // lookup alias map for the real name
        String real_name = AliasMap.get(name);
        if (real_name != null) name = real_name;

        // remove prefix if any
        String prefix = "local:";
        if (name.startsWith(prefix))
            name = name.substring(prefix.length());

        // lookup the drawable id
        return ctx.getResources().getIdentifier(name, defType, ctx.getPackageName());
    }

    public static void loadImage(ImageView target, String src, boolean togglesVisibility, int placeHolderDrawableId) {
        loadImage(target, src, togglesVisibility, placeHolderDrawableId, null);
    }

    /**
     * Load image into image view. Supporint local resource name lookup.
     *
     * @param target                image view
     * @param src                   image source string. Local resource format "local:resource_name". Otherwise, try to load as web url.
     * @param togglesVisibility     update the visibility depending on if src is resolved or not
     * @param placeHolderDrawableId a image for placeholder and error status
     */
    public static void loadImage(ImageView target, String src, boolean togglesVisibility, int placeHolderDrawableId, Callback callback) {

        if (target == null || TextUtils.isEmpty(src)) return;
        Context ctx = target.getContext();
        Picasso picasso = Picasso.with(ctx);

        boolean empty = true;

        if (src != null) {
            int res = getResourceId(ctx, src);
            if (res != 0) {
                if (togglesVisibility) target.setVisibility(View.VISIBLE);
                RequestCreator requestCreator = picasso.load(res);
                if (placeHolderDrawableId != 0) {
                    requestCreator.placeholder(placeHolderDrawableId).error(placeHolderDrawableId);
                }
                requestCreator.into(target, callback);
                empty = false;
            } else {
                Uri uri = TypeUtils.toUri(src);
                if (uri != null) {
                    if (togglesVisibility) target.setVisibility(View.VISIBLE);
                    RequestCreator requestCreator = picasso.load(uri);
                    if (placeHolderDrawableId != 0) {
                        requestCreator.placeholder(placeHolderDrawableId).error(placeHolderDrawableId);
                    }
                    requestCreator.into(target, callback);
                    empty = false;
                }
            }
        }
        if (empty) {
            picasso.cancelRequest(target);
            target.setImageBitmap(null);
            if (togglesVisibility) target.setVisibility(View.GONE);
        }
    }

    /**
     * Load image into target. Supporint local resource name lookup.
     *
     * @param ctx    Context reference
     * @param src    image source string. Local resource format "local:resource_name". Otherwise, try to load as web url.
     * @param target target
     */
    public static void loadImage(Context ctx, String src, Target target) {

        if (target == null || TextUtils.isEmpty(src)) return;
        Picasso picasso = Picasso.with(ctx);

        boolean empty = true;

        if (src != null) {
            int res = getResourceId(ctx, src);
            if (res != 0) {
                picasso.load(res).into(target);
                empty = false;
            } else {
                Uri uri = TypeUtils.toUri(src);
                if (uri != null) {
                    picasso.load(uri).into(target);
                    empty = false;
                }
            }
        }
        if (empty) {
            picasso.cancelRequest(target);
        }
    }

    /**
     * apply color tinting to the specified imageview.
     *
     * @param iv                      ImageView to apply tinting to.
     * @param defaultDrawableResource the drawable resource for default state.
     * @param color                   color of tint to apply.
     * @param state                   intended state when tinting appears.
     */
    public static void setImageTinting(ImageView iv, int defaultDrawableResource, int color, int state) {
        StateListDrawable stateListDrawable;

        if (iv.getDrawable() instanceof StateListDrawable) {
            stateListDrawable = (StateListDrawable) iv.getDrawable();
        } else {
            stateListDrawable = new StateListDrawable();
        }

        Bitmap imageCopy = tintImageWithColor(iv.getContext(), defaultDrawableResource, color);

        stateListDrawable.addState(new int[]{state}, new BitmapDrawable(iv.getContext().getResources(), imageCopy));
        stateListDrawable.addState(new int[]{}, iv.getContext().getResources().getDrawable(defaultDrawableResource));
        iv.setImageDrawable(stateListDrawable);
    }

    public static void setImageTinting(ImageView iv, int defaultDrawableResource, int color) {
        Bitmap imageCopy = tintImageWithColor(iv.getContext(), defaultDrawableResource, color);
        iv.setImageBitmap(imageCopy);
    }

    public static Bitmap tintImageWithColor(Context context, int drawableResId, int color) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        Bitmap ret = null;
        if (imageBitmap != null) {
            ret = tintImageWithColor(imageBitmap, color);
            imageBitmap.recycle();
        }
        return ret;
    }

    public static Bitmap tintImageWithColor(Bitmap imageBitmap, int color) {
        if (imageBitmap == null) return null;
        Bitmap imageCopy = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(imageCopy);
        Paint p = new Paint();
        p.setColor(color);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        c.drawRect(0, 0, imageCopy.getWidth() , imageCopy.getHeight(), p);
        return imageCopy;
    }
}
