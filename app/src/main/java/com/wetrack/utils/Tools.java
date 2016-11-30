package com.wetrack.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.wetrack.BaseApplication;
import com.wetrack.R;

public class Tools {
    private static int screenW = -1, screenH = -1;

    public static int getScreenW() {
        if (screenW < 0) {
            initScreenDisplayParams();
        }
        return screenW;
    }

    public static int getScreenH() {
        if (screenH < 0) {
            initScreenDisplayParams();
        }
        return screenH;
    }

    private static void initScreenDisplayParams() {
        DisplayMetrics dm = BaseApplication.getContext().getResources()
                .getDisplayMetrics();
        screenW = dm.widthPixels;
        screenH = dm.heightPixels;
    }

    private static Context mainContext = null;

    public static void setMainContext(Context context) {
        mainContext = context;
    }

    public static Context getMainContext() {
        return mainContext;
    }

    public static Bitmap getMarkerFromBitmap(Bitmap portrait) {
        Bitmap bm = BitmapFactory.decodeResource(
                mainContext.getResources(), R.drawable.marker_back);
        Bitmap mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        portrait = Bitmap.createScaledBitmap(
                portrait,
                mutableBitmap.getWidth(),
                portrait.getHeight() * mutableBitmap.getWidth() / portrait.getWidth(),
                false);
        portrait = getCroppedBitmap(portrait);

        Paint paint = new Paint();
        paint.setAlpha(200);
        canvas.drawBitmap(portrait, 0, 0, paint);
        return mutableBitmap;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}
