package club.jkxy.jumphelper.rang;

import android.graphics.Bitmap;

/**
 * Created by chenliang on 2017/12/31.
 */
public class BlackBorderFinder {
    public static final int TARGET_1 = 0;
    public static final int TARGET_2 = 255;
    public static final int MAX_BLACKBORDER = 50;

    public int[] find(Bitmap image) {
        if (image == null) {
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();

        int[] ret = {0, 0};
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (int i = 0; i < MAX_BLACKBORDER; i++) {
            int pixelX = image.getPixel(i, height / 2);
            int pixelY = image.getPixel(width / 2, i);
            int rx = (pixelX & 0xff0000) >> 16;
            int gx = (pixelX & 0xff00) >> 8;
            int bx = (pixelX & 0xff);
            if ((rx == TARGET_1 && gx == TARGET_1 && bx == TARGET_1) || (rx == TARGET_2 && gx == TARGET_2 && bx == TARGET_2)) {
                maxX = Math.max(maxX, i);
            }

            int ry = (pixelY & 0xff0000) >> 16;
            int gy = (pixelY & 0xff00) >> 8;
            int by = (pixelY & 0xff);
            if ((ry == TARGET_1 && gy == TARGET_1 && by == TARGET_1) || (ry == TARGET_2 && gy == TARGET_2 && by == TARGET_2)) {
                maxY = Math.max(maxX, i);
            }

        }
        ret[0] = maxX;
        ret[1] = maxY;
        System.out.println("BlackBorder, x: " + ret[0] + ", y: " + ret[1]);
        return ret;
    }

}
