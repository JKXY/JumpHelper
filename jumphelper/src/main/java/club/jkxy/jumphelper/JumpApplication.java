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

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_float_window, null);
        FloatWindow.with(getApplicationContext()).setDesktopShow(true).setView(view).setMoveType(MoveType.slide).setX(400).build();
        FloatWindow.get().show();
    }

    public static Context getContext() {
        return context;
    }


}
