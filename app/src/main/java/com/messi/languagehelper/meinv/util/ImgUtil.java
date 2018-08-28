package com.messi.languagehelper.meinv.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import com.messi.languagehelper.meinv.R;

public class ImgUtil {

    public static void toBitmap(Activity mContext, String path){
        try {
            Bitmap srcBitmap= BitmapFactory.decodeFile(path);
            int resourseId = R.drawable.qrcode_mh;
            if(mContext.getPackageName().equals(Setings.application_id_meixiu)){
                resourseId = R.drawable.qrcode;
            }else if (mContext.getPackageName().equals(Setings.application_id_meinv)) {
                resourseId = R.drawable.qrcode;
            } else if (mContext.getPackageName().equals(Setings.application_id_caricature)) {
                resourseId = R.drawable.qrcode_mh;
            } else if (mContext.getPackageName().equals(Setings.application_id_browser)) {
            }
            Bitmap waterBitmap = BitmapFactory.decodeResource(mContext.getResources(),resourseId);
//            Bitmap newBitmap = createWaterMaskRightBottom(mContext,srcBitmap,waterBitmap,15,15);
            Bitmap newBitmap = WaterMask(srcBitmap,waterBitmap);
            String imgPath = SDCardUtil.saveBitmap(mContext, newBitmap, "share_img.png");
            Setings.shareImg(mContext, imgPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark,
                                                int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        //创建一个bitmap
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newb);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // 保存
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        canvas.restore();
        return newb;
    }

    public static Bitmap createWaterMaskRightBottom(
            Context context, Bitmap src, Bitmap watermark,
            int paddingRight, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark,
                src.getWidth() - watermark.getWidth() - ScreenUtil.dip2px(context, paddingRight),
                src.getHeight() - watermark.getHeight() - ScreenUtil.dip2px(context, paddingBottom));
    }

    public static Bitmap WaterMask(Bitmap src, Bitmap watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Log.i("WaterMask", "原图宽: "+w);
        Log.i("WaterMask", "原图高: "+h);
        // 设置原图想要的大小
        float newWidth = SystemUtil.SCREEN_WIDTH;
        float newHeight = h*(newWidth/w);
        // 计算缩放比例
        float scaleWidth = ( newWidth) / w;
        float scaleHeight = (newHeight) / h;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        src = Bitmap.createBitmap(src, 0, 0, w, h, matrix, true);

        //根据bitmap缩放水印图片
        float w1 = w / 5;
        float h1 = (float) (w1 / 5);
        //获取原始水印图片的宽、高
        int w2 = watermark.getWidth();
        int h2 = watermark.getHeight();

        //计算缩放的比例
        float scalewidth = ((float) w1) / w2;
        float scaleheight = ((float) h1) / h2;

        Matrix matrix1 = new Matrix();
        matrix1.postScale((float) 0.4, (float) 0.4);

        watermark = Bitmap.createBitmap(watermark, 0, 0, w2, h2, matrix1, true);
        //获取新的水印图片的宽、高
        w2 = watermark.getWidth();
        h2 = watermark.getHeight();

        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(result);
        //在canvas上绘制原图和新的水印图
        cv.drawBitmap(src, 0, 0, null);
        //水印图绘制在画布的右下角，距离右边和底部都为20
        cv.drawBitmap(watermark, src.getWidth() - w2-20, src.getHeight() - h2-20, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return result;
    }

}
