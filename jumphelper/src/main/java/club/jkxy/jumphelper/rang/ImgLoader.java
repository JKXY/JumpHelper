package club.jkxy.jumphelper.rang;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by chenliang on 2017/12/31.
 */
public class ImgLoader {
    public static Bitmap load(String path) {
        return BitmapFactory.decodeFile(path);
    }
}
