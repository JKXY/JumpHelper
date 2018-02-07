package club.jkxy.jumphelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by JKXY on 2018/1/10.
 */

public class MarkTask extends AsyncTask<Void, Void, Bitmap> {
    private Bitmap bitmap;
    private Context context;
    private int x1, y1, x2, y2;

    public MarkTask(Context context, Bitmap bitmap, int x1, int y1, int x2, int y2) {
        this.context = context;
        this.bitmap = bitmap;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return saveBitmap(addMarkBitmap(bitmap, x1, y1, x2, y2), context);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        if (this.bitmap != null && !this.bitmap.isRecycled()) {
            this.bitmap.recycle();
        }
    }


    private Bitmap saveBitmap(Bitmap bitmap, Context context) {
        File fileImage = null;
        if (bitmap != null) {
            try {
                String mLocalUrl = context.getExternalFilesDir("jumphelper").getAbsoluteFile() + "/mark_" + System.currentTimeMillis() + ".png";
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


    //添加标记
    public Bitmap addMarkBitmap(Bitmap mBitmap, int x1, int y1, int x2, int y2) {
        Bitmap originalBitmap = BitmapFactory.decodeFile(JumpAccessibilityService.path).copy(Bitmap.Config.ARGB_8888, true);
        Canvas mCanvas = new Canvas(originalBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        mCanvas.drawCircle(x1, y1, 10, paint);
        mCanvas.drawCircle(x2, y2, 10, paint);
        return originalBitmap;
    }
}
