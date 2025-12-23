package com.example.scanidcard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {

    /**
     * 从 Uri 解码 Bitmap（防止 OOM）
     */
    public static Bitmap decodeBitmapFromUri(Context context, Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        if (inputStream != null) {
            inputStream.close();
        }
        return bitmap;
    }

    /**
     * Bitmap → Base64（JPEG，无前缀）
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        // 控制身份证图片大小
        Bitmap scaledBitmap = scaleBitmap(bitmap, 1200);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bytes = baos.toByteArray();

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * 等比例缩放
     */
    private static Bitmap scaleBitmap(Bitmap bitmap, int maxWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxWidth) return bitmap;

        float ratio = maxWidth * 1f / width;
        int newHeight = (int) (height * ratio);

        return Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true);
    }
}
