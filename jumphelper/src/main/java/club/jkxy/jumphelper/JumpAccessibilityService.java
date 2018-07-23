package club.jkxy.jumphelper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import java.io.File;
import java.util.ArrayList;
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
    double jumpRatio = 0;
    private static Random RANDOM = new Random();

    private boolean isJump = false;//触摸jump方式，是否继续

    public static final int TYPR_1 = 0;//全自动
    public static final int TYPR_2 = TYPR_1 + 1;//全自动一次
    public static final int TYPR_3 = TYPR_2 + 1;//半自动
    public static final int TYPR_4 = TYPR_3 + 1;//半自动一次
    public static int currType = TYPR_1;

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
//                path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "jump.png";
                isContinue = jump(path);
                if (!isContinue || currType == TYPR_2) {
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


    public void startTouchJump() {
        if (jumpRatio == 0) {
            int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
            jumpRatio = JUMP_RATIO * 1080 / widthPixels;
        }
        if (isJump) {
            Intent intent = new Intent(getApplicationContext(), TouchJumpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Activity latestActivity = ActivityLifecycleHelper.getLatestActivity();
                    if (latestActivity instanceof TouchJumpActivity)
                        ((TouchJumpActivity) (latestActivity)).setJumpListener(jumpListener);
                }
            }, 100);
        } else {
            FloatWindow.get("STOP").hide();
            FloatWindow.get().show();
        }
    }


    public JumpView.JumpListener jumpListener = new JumpView.JumpListener() {
        @Override
        public void getTouchPoint(float downX, float downY, float upX, float upY) {
            double distance = Math.sqrt((upX - downX) * (upX - downX) + (upY - downY) * (upY - downY));//1.385
            LogUtils.e("JUMP", "distance:" + distance);
            final long time = (long) (distance * jumpRatio);
            LogUtils.e("JUMP", "time:" + time);
            //触摸区域随机在中下方
            final int x = (int) (Math.random() * Screen.width / 3 + Screen.width / 3);
            final int y = (int) (Math.random() * Screen.height / 2 + Screen.height / 2);
            ActivityLifecycleHelper.getLatestActivity().finish();
//            isJump = findPointClick(x, y, time);
//            findPointClick(x, y, time);
            if (isJump) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findPointClick(x, y, time);
                        try {
//                            long sleeptime = RANDOM.nextInt(1000) + 3000;
                            Thread.sleep(time * 2 + 600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (currType == TYPR_4)
                            isJump = false;
                        startTouchJump();
                    }
                }, 100);
            }
        }
    };


    //标记点
    private ArrayList<Integer> makePoint;

    private boolean jump(String path) {
        Bitmap image = ImgLoader.load(path);
        makePoint = new ArrayList<Integer>();
        if (jumpRatio == 0) {
            jumpRatio = JUMP_RATIO * 1080 / image.getWidth();
        }
        int[] myPos = myPosFinder.find(image);
        if (myPos != null) {
            LogUtils.e("JUMP", "find myPos, succ, (" + myPos[0] + ", " + myPos[1] + ")");
            int[] nextCenter = nextCenterFinder.find(image, myPos);
            if (nextCenter == null || nextCenter[0] == 0) {
                LogUtils.e("JUMP", "find nextCenter, fail");
                return false;
            } else {
                makePoint.add(myPos[0]);
                makePoint.add(myPos[1]);
                int centerX, centerY;
                int[] whitePoint = whitePointFinder.find(image, nextCenter[0] - 120, nextCenter[1], nextCenter[0] + 120, nextCenter[1] + 180);
                if (whitePoint != null) {
                    centerX = whitePoint[0];
                    centerY = whitePoint[1];
                    makePoint.add(whitePoint[0]);
                    makePoint.add(whitePoint[1]);
                    LogUtils.e("JUMP", "find whitePoint, succ, (" + centerX + ", " + centerY + ")");
                } else {
                    if (nextCenter[2] != Integer.MAX_VALUE && nextCenter[4] != Integer.MIN_VALUE) {
                        centerX = (nextCenter[2] + nextCenter[4]) / 2;
                        centerY = (nextCenter[3] + nextCenter[5]) / 2;

                        makePoint.add(nextCenter[0]);
                        makePoint.add(nextCenter[1]);
                        makePoint.add(nextCenter[2]);
                        makePoint.add(nextCenter[3]);
                        makePoint.add(nextCenter[4]);
                        makePoint.add(nextCenter[5]);
                        LogUtils.e("JUMP", "nextCenter========>11: (" + centerX + ", " + centerY + ")");
                    } else {
                        centerX = nextCenter[0];
                        centerY = nextCenter[1] + 48;
                        makePoint.add(nextCenter[0]);
                        makePoint.add(nextCenter[1]);
                        LogUtils.e("JUMP", "nextCenter========>22: (" + centerX + ", " + centerY + ")");
                    }
                }
                makePoint.add(centerX);
                makePoint.add(centerY);
                LogUtils.e("JUMP", "find nextCenter, succ, (" + centerX + ", " + centerY + ")");
//                new MarkTask(getApplicationContext(), path, makePoint).execute();
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
                    if (currType == TYPR_1) {
                        startJump();
                    } else {
                        isJump = true;
                        startTouchJump();
                        FloatWindow.get("STOP").show();
                    }
                }
            });


            FloatWindow.get("STOP").getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isJump = false;
                    FloatWindow.get("STOP").hide();
                    FloatWindow.get().show();
                    Activity latestActivity = ActivityLifecycleHelper.getLatestActivity();
                    if (latestActivity instanceof TouchJumpActivity)
                        latestActivity.finish();
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

