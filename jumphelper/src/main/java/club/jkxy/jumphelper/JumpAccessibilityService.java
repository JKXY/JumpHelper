package club.jkxy.jumphelper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import java.util.Random;

import club.jkxy.floatwindow.FloatWindow;
import club.jkxy.floatwindow.Screen;
import club.jkxy.jumphelper.rang.BlackBorderFinder;
import club.jkxy.jumphelper.rang.ImgLoader;
import club.jkxy.jumphelper.rang.MyPosFinder;
import club.jkxy.jumphelper.rang.NextCenterFinder;
import club.jkxy.jumphelper.rang.WhitePointFinder;

/**
 * Created by JKXY on 2017/9/26.
 */

public class JumpAccessibilityService extends AccessibilityService {
    public boolean isContinue = true;//是否继续
    private boolean isBuild;//悬浮窗是否初始化
    public static String path = "";
    public static double JUMP_RATIO = 1.385f;//弹跳系数1.390f
    public static final double JUMP_RATIO_DEF = 1.385f;
    MyPosFinder myPosFinder = new MyPosFinder();
    NextCenterFinder nextCenterFinder = new NextCenterFinder();
    WhitePointFinder whitePointFinder = new WhitePointFinder();

    BlackBorderFinder blackBorderFinder = new BlackBorderFinder();
    double jumpRatio = 0;
    private static Random RANDOM = new Random();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    public void startJump() {
//        path = getExternalFilesDir("jumphelper").getAbsoluteFile() + "/jump.png";
//        int[] black =  blackBorderFinder.find(ImgLoader.load(path));
//        new DealBlasckBorderTask(getApplicationContext(),path,black[0],black[1]).execute();

        Intent intent = new Intent(getApplicationContext(), ScreenShotActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                path = getExternalFilesDir("jumphelper").getAbsoluteFile() + "/newjump.png";
                isContinue = jump(path);
                if (!isContinue) {
                    FloatWindow.get().show();
                } else {
                    long sleeptime = RANDOM.nextInt(1000) + 3000;//  [3000,4000)
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startJump();
                        }
                    }, sleeptime);
                }
            }
        }, 1_000);
    }


    private boolean jump(String path) {
        Bitmap image = ImgLoader.load(path);
        int[] myPos = null;
        if (jumpRatio == 0) {
            jumpRatio = JUMP_RATIO * 1080 / image.getWidth();
        }
        try {
            myPos = myPosFinder.find(image);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (myPos != null) {
            LogUtils.e("JUMP", "find myPos, succ, (" + myPos[0] + ", " + myPos[1] + ")");
            int[] nextCenter = null;
            try {
                nextCenter = nextCenterFinder.find(image, myPos);
                blackBorderFinder.find(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (nextCenter == null || nextCenter[0] == 0) {
                LogUtils.e("JUMP", "find nextCenter, fail");
                return false;
            } else {
                int centerX, centerY;
                int[] whitePoint = null;
                try {
                    whitePoint = whitePointFinder.find(image, nextCenter[0] - 120, nextCenter[1], nextCenter[0] + 120, nextCenter[1] + 180);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (whitePoint != null) {
                    centerX = whitePoint[0];
                    centerY = whitePoint[1];
                    LogUtils.e("JUMP", "find whitePoint, succ, (" + centerX + ", " + centerY + ")");
                } else {
                    if (nextCenter[2] != Integer.MAX_VALUE && nextCenter[4] != Integer.MIN_VALUE) {
                        centerX = (nextCenter[2] + nextCenter[4]) / 2;
                        centerY = (nextCenter[3] + nextCenter[5]) / 2;
                        LogUtils.e("JUMP", "nextCenter========>11: (" + centerX + ", " + centerY + ")");
                    } else {
                        centerX = nextCenter[0];
                        centerY = nextCenter[1] + 48;
                        LogUtils.e("JUMP", "nextCenter========>22: (" + centerX + ", " + centerY + ")");
                    }
                }
                LogUtils.e("JUMP", "find nextCenter, succ, (" + centerX + ", " + centerY + ")");
//                new MarkTask(getApplicationContext(), image, myPos[0], myPos[1], centerX, centerY).execute();
//                new MarkTask(getApplicationContext(), image, myPos[0], myPos[1], nextCenter[0], nextCenter[1]).execute();
//                new MarkTask(getApplicationContext(), image, nextCenter[2], nextCenter[3], nextCenter[4], nextCenter[5]).execute();
                double distance = Math.sqrt((centerX - myPos[0]) * (centerX - myPos[0]) + (centerY - myPos[1]) * (centerY - myPos[1]));//1.385
                LogUtils.e("JUMP", "distance:" + distance);
                long time = (long) (distance * jumpRatio);
                LogUtils.e("JUMP", "time:" + time);
                //触摸区域随机在中下方
                int x = (int) (Math.random() * Screen.width / 3 + Screen.width / 3);
                int y = (int) (Math.random() * Screen.height / 2 + Screen.height / 2);
                return findPointClick(x, y, time);
//                return false;
            }
        } else {
            LogUtils.e("JUMP", "find myPos, fail");
            return false;
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtils.e("JUMP", "onServiceConnected");
        if (!isBuild) {
            isBuild = true;
            FloatWindow.get().getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FloatWindow.get().hide();
                    startJump();
                }
            });
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    /**
     * @param x
     * @param y
     * @return
     */
    @TargetApi(24)
    private boolean findPointClick(float x, float y, long duration) {
        if ((Build.VERSION.SDK_INT < 24) || (x < 0.0F) || (y < 0.0F)) {
            LogUtils.e("JUMP", "版本过低，无法点击：" + Build.VERSION.SDK_INT);
            return false;
        }
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(x, y);
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, duration));
        boolean result = dispatchGesture(builder.build(),
                new GestureResultCallback() {
                    public void onCancelled(GestureDescription paramGestureDescription) {
                        super.onCancelled(paramGestureDescription);
                        LogUtils.e("JUMP", "GestureResultCallback onCompleted");
                    }

                    public void onCompleted(GestureDescription paramGestureDescription) {
                        super.onCompleted(paramGestureDescription);
                        LogUtils.e("JUMP", "GestureResultCallback onCompleted");
                    }
                }
                , null);

        path.close();
        LogUtils.e("JUMP", "the findPointClick(" + x + "," + y + ") is " + result);
        return result;
    }


}