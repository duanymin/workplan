package com.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by Duan on 3月25日.
 */
public class ImageUtils {
    // 1图库 2拍照 3完成后的返回结果
    public static final int CHOICEPHOTO = 1, TAKEPHOTO = 2, PHOTORESOULT = 3;
    public static final String[] photos = new String[]{"图库", "拍照"};
    public static final String IMAGE_UNSPECIFIED = "image/*";

    /**
     * 选择图片来源
     */
    public static void setPic( final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(ImageUtils.photos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        choicephoto(activity);
                        break;
                    case 1:
                        takephoto(activity);
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * 选择图片来源
     */
    public static void setPic( final Fragment fragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
        builder.setItems(ImageUtils.photos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        choicephoto(fragment);
                        break;
                    case 1:
                        takephoto(fragment);
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * 图库选择
     */
    public static void choicephoto( Activity activity) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent();
            intent.setType(IMAGE_UNSPECIFIED);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, CHOICEPHOTO);
        } else {
            Toast.makeText(activity, "请先插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 拍照
     */
    public static void takephoto( Activity activity) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activity.startActivityForResult(intent, TAKEPHOTO);
        } else {
            Toast.makeText(activity, "请先插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 图库选择
     */
    public static void choicephoto( Fragment fragment) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent();
            intent.setType(IMAGE_UNSPECIFIED);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            fragment.startActivityForResult(intent, CHOICEPHOTO);
        } else {
            Toast.makeText(fragment.getActivity(), "请先插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 拍照
     */
    public static void takephoto( Fragment fragment) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fragment.startActivityForResult(intent, TAKEPHOTO);
        } else {
            Toast.makeText(fragment.getActivity(), "请先插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 裁剪图片页面
     */
    public static void startPhotoZoom(Uri uri,  Activity activity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, PHOTORESOULT);
    }

    /**
     * 压缩图片
     *
     * @return
     */
    
    public static Bitmap compressImage( Bitmap bitmap) {
        //压缩尺寸(PX)
        float hh = 1280f;
        float ww = 720f;
        //图片宽高
        float w = bitmap.getWidth();
        float h = bitmap.getHeight();
        float be = 1.0f;
        if (w > h) {
            be = ((w / hh + h / ww) / 2.0f);
        } else {
            be = ((w / ww + h / hh) / 2.0f);
        }
        //压缩图片
        if (be > 4.0f) {
            be = 4.0f;
        } else if (be > 2.0f) {
            be = 2.0f;
        } else if (be > 1.0f) {
            be = 1.5f;
        } else if (be < 1.0f) {
            be = 1.0f;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (w / be), (int) (h / be), true);
        return bitmap;
    }

    
    public static Bitmap compressImage(Context context,  Uri uri) {
        //压缩尺寸(PX)
        float hh = 1280f;
        float ww = 720f;
        //图片宽高
        BitmapFactory.Options options = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        //此时返回bm为空
        String path = FileUtils.getImageAbsolutePath(context, uri);
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        float w = options.outWidth;
        float h = options.outHeight;
        float be = 1.0f;
        if (w > h) {
            be = ((w / hh + h / ww) / 2.0f);
        } else {
            be = ((w / ww + h / hh) / 2.0f);
        }
        if (be <= 1.5f) {
            be = 1;
        }
        options.inSampleSize = (int) be;//设置缩放比例
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }
}
