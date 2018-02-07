package club.jkxy.jumphelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 去除图片四周黑/白边
 * Created by JKXY on 2018/1/10.
 */

public class DealBlasckBorderTask extends AsyncTask<Void, Void, Bitmap> {
    private String path;
    private Context context;
    private int x1, y1;

    public DealBlasckBorderTask(Context context, String path, int x1, int y1) {
        this.context = context;
        this.path = path;
        this.x1 = x1;
        this.y1 = y1;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return saveBitmap(dealBlackBorderBitmap(path, x1, y1), context);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }


    private Bitmap saveBitmap(Bitmap bitmap, Context context) {
        File fileImage = null;
        if (bitmap != null) {
            try {
                String mLocalUrl = context.getExternalFilesDir("jumphelper").getAbsoluteFile() + "/newjump.png";
                fileImage = new File(mLocalUrl);
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                fileImage = null;
            } catch (IOException e) {
                e.printStackTrace();
                fileImage = null;
            }
        }
        if (fileImage != null) {
            return bitmap;
        }
        LogUtils.e("JUMP-save", "Bitmap null");
        return null;
    }


    public Bitmap dealBlackBorderBitmap(String path, int x1, int y1) {
//        Bitmap bitmap = BitmapFactory.decodeFile(path).copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (x1 == Integer.MIN_VALUE && y1 == Integer.MIN_VALUE)
            return bitmap;
        if (x1 == Integer.MIN_VALUE)
            x1 = 0;
        if (y1 == Integer.MIN_VALUE)
            y1 = 0;
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        bitmap = Bitmap.createBitmap(bitmap, x1, y1, w - 2 * x1, h - 2 * y1, null, false);//裁剪黑边

        float scaleWidth = ((float) 1080) / bitmap.getWidth();
        float scaleHeight = ((float) 2220) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);//拉伸成原图大小
        return bitmap;
    }
}
