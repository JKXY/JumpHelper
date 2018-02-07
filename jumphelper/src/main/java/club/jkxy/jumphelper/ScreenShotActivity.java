package club.jkxy.jumphelper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

/**
 * Created by wei on 16-9-18.
 * <p>
 * 完全透明 只是用于弹出权限申请的窗而已
 */
public class ScreenShotActivity extends Activity {
    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如下代码 只是想 启动一个透明的Activity 而上一个activity又不被pause
        requestScreenShot();
    }


    public void requestScreenShot() {
        if (Build.VERSION.SDK_INT >= 21) {
            startActivityForResult(((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION
            );
        } else {
            toast("版本过低,无法截屏");
        }
    }

    private void toast(String str) {
        Toast.makeText(ScreenShotActivity.this, str, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
//        overridePendingTransition(0, 0);
        super.onPause();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: {
                if (resultCode == -1 && data != null) {
                    Shotter shotter = new Shotter(ScreenShotActivity.this, data);
                    shotter.startScreenShot(new Shotter.OnShotListener() {
                        @Override
                        public void onFinish(String url) {
                            JumpAccessibilityService.path = url;
                            LogUtils.e("JUMP", "URL-------->" + url);
//                            toast("shot finish!");
                            finish(); // don't forget finish activity
                        }
                    });
                }
            }
        }
    }


}