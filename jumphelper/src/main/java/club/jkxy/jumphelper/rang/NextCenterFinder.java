package club.jkxy.jumphelper.rang;

import android.graphics.Bitmap;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by chenliang on 2018/1/1.
 */
public class NextCenterFinder {

    BottleFinder bottleFinder = new BottleFinder();

    public int[] find(Bitmap image, int[] myPos) {
        if (image == null) {
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int pixel = image.getPixel(200, 200);
        int r1 = (pixel & 0xff0000) >> 16;
        int g1 = (pixel & 0xff00) >> 8;
        int b1 = (pixel & 0xff);
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < width; i++) {
            pixel = image.getPixel(i, height - 1);
//            map.put(pixel, map.getOrDefault(pixel, 0) + 1);
            int val = 0;
            if (map.get(pixel) != null)
                val = map.get(pixel);
            else
                val = 0;
            map.put(pixel, val + 1);
        }
        int max = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                pixel = entry.getKey();
                max = entry.getValue();
            }
        }
        int r2 = (pixel & 0xff0000) >> 16;
        int g2 = (pixel & 0xff00) >> 8;
        int b2 = (pixel & 0xff);

        int t = 16;

        int minR = Math.min(r1, r2) - t;
        int maxR = Math.max(r1, r2) + t;
        int minG = Math.min(g1, g2) - t;
        int maxG = Math.max(g1, g2) + t;
        int minB = Math.min(b1, b2) - t;
        int maxB = Math.max(b1, b2) + t;

        System.out.println(minR + ", " + minG + ", " + minB);
        System.out.println(maxR + ", " + maxG + ", " + maxB);

        int[] ret = new int[6];
        int targetR = 0, targetG = 0, targetB = 0;
        boolean found = false;
        for (int j = height / 4; j < myPos[1]; j++) {
            for (int i = 0; i < width; i++) {
                int dx = Math.abs(i - myPos[0]);
                int dy = Math.abs(j - myPos[1]);
                if (dy > dx) {
                    continue;
                }
                pixel = image.getPixel(i, j);
                int r = (pixel & 0xff0000) >> 16;
                int g = (pixel & 0xff00) >> 8;
                int b = (pixel & 0xff);
                if (r < minR || r > maxR || g < minG || g > maxG || b < minB || b > maxB) {
                    j = j + 2;
                    ret[0] = i;
                    ret[1] = j;
                    System.out.println("top, x: " + i + ", y: " + j);
                    for (int k = 0; k < 5; k++) {
                        pixel = image.getPixel(i, j + k);
                        System.out.println(pixel);
                        targetR += (pixel & 0xff0000) >> 16;
                        targetG += (pixel & 0xff00) >> 8;
                        targetB += (pixel & 0xff);
                    }
                    targetR /= 5;
                    targetG /= 5;
                    targetB /= 5;
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }

        if (targetR == BottleFinder.TARGET && targetG == BottleFinder.TARGET && targetB == BottleFinder.TARGET) {
            return bottleFinder.find(image, ret[0], ret[1]);
        }

        boolean[][] matchMap = new boolean[width][height];
        boolean[][] vMap = new boolean[width][height];
        ret[2] = Integer.MAX_VALUE;
        ret[3] = Integer.MAX_VALUE;
        ret[4] = Integer.MIN_VALUE;
        ret[5] = Integer.MAX_VALUE;

        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(ret);
        while (!queue.isEmpty()) {
            int[] item = queue.poll();
            int i = item[0];
            int j = item[1];
//            int dx = Math.abs(i - myPos[0]);
//            int dy = Math.abs(j - myPos[1]);
//            if (dy > dx) {
//                continue;
//            }
            if (j >= myPos[1]) {
                continue;
            }

            if (i < Math.max(ret[0] - 300, 0) || i >= Math.min(ret[0] + 300, width) || j < Math.max(0, ret[1] - 400) || j >= Math.max(height, ret[1] + 400) || vMap[i][j]) {
                continue;
            }
            vMap[i][j] = true;
            pixel = image.getPixel(i, j);
            int r = (pixel & 0xff0000) >> 16;
            int g = (pixel & 0xff00) >> 8;
            int b = (pixel & 0xff);
            matchMap[i][j] = ToleranceHelper.match(r, g, b, targetR, targetG, targetB, 16);
//            if (i == ret[0] && j == ret[1]) {
//                System.out.println(matchMap[i][j]);
//            }
            if (matchMap[i][j]) {
                if (i < ret[2]) {
                    ret[2] = i;
                    ret[3] = j;
                } else if (i == ret[2] && j < ret[3]) {
                    ret[2] = i;
                    ret[3] = j;
                }
                if (i > ret[4]) {
                    ret[4] = i;
                    ret[5] = j;
                } else if (i == ret[4] && j < ret[5]) {
                    ret[4] = i;
                    ret[5] = j;
                }
                if (j < ret[1]) {
                    ret[0] = i;
                    ret[1] = j;
                }
                queue.add(buildArray(i - 1, j));
                queue.add(buildArray(i + 1, j));
                queue.add(buildArray(i, j - 1));
                queue.add(buildArray(i, j + 1));
            }
        }

        System.out.println("left, x: " + ret[2] + ", y: " + ret[3]);
        System.out.println("right, x: " + ret[4] + ", y: " + ret[5]);

        return ret;
    }

    public static int[] buildArray(int i, int j) {
        int[] ret = {i, j};
        return ret;
    }

}
