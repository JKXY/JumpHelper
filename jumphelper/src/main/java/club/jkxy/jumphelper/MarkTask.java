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
import java.util.ArrayList;

/**
 * Created by JKXY on 2018/1/10.
 */

public class MarkTask extends AsyncTask<Void, Void, Bitmap> {
    private String path;
    private Context context;
    private ArrayList<Integer> point;

    public MarkTask(Context context, String path, ArrayList<Integer> point) {
        this.context = context;
        this.path = path;
        this.point = point;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return saveBitmap(addMarkBitmap(path, point), context);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
//        if (this.bitmap != null && !this.bitmap.isRecycled()) {
//            this.bitmap.recycle();
//        }
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
    public Bitmap addMarkBitmap(String path, ArrayList<Integer> point) {
        Bitmap originalBitmap = BitmapFactory.decodeFile(path).copy(Bitmap.Config.ARGB_8888, true);
        Canvas mCanvas = new Canvas(originalBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        if (point != null && point.size() > 0 && point.size() % 2 == 0) {
            for (int i = 0; i < point.size() / 2; i++) {
                mCanvas.drawCircle(point.get(i * 2), point.get(i * 2 + 1), 10, paint);
            }
        }
        return originalBitmap;
    }
}
