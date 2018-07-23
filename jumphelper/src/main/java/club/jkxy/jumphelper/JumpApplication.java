package club.jkxy.jumphelper;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import club.jkxy.floatwindow.FloatWindow;
import club.jkxy.floatwindow.MoveType;


/**
 * Created by JKXY on 2017/10/11.
 */

public class JumpApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build());

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_float_window_start, null);
        FloatWindow.with(getApplicationContext()).setDesktopShow(true).setView(view).setMoveType(MoveType.slide).setX(400).build();

        View view_stop = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_float_window_stop, null);
        FloatWindow.with(getApplicationContext()).setTag("STOP").setDesktopShow(true).setView(view_stop).setMoveType(MoveType.slide).setX(400).build();
    }

    public static Context getContext() {
        return context;
    }


}
