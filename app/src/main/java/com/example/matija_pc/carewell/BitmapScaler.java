package com.example.matija_pc.carewell;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by Matija-PC on 9.4.2015..
 */

//class for loading scaled-down bitmaps
public class BitmapScaler {

    public static Bitmap decodeSampledBitmap(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        //read bitmap dimensions
        BitmapFactory.decodeFile(path, options);
        //get scaling factor
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds=false;
        //load scaled-down bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        //if rotation is required, rotate
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            bitmap = rotateBitmap (bitmap, orientation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //calculate if scaling-down is required
    private static int calculateInSampleSize (BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (reqWidth==0)
            reqWidth=1024;
        if (reqHeight==0)
            reqHeight=1024;

        if (width > reqWidth || height > reqHeight) {
            final int halfWidth = (int) (width / 2);
            final int halfHeight = (int) (height / 2);

            while ((halfWidth/inSampleSize) > reqWidth && (halfHeight/inSampleSize) > reqHeight)
            {
                inSampleSize*=2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix m = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_ROTATE_90:
                m.setRotate(90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                m.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                m.setRotate(270);
                break;

            default:
                return bitmap;

        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        return rotatedBitmap;
    }
}
