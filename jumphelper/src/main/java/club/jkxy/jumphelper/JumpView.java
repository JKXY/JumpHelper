package club.jkxy.jumphelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by JKXY on 2017/12/11.
 */

public class JumpView extends View {
    private float downX = -1, downY = -1, upX = -1, upY = -1;
    private JumpListener jumpListener;
    private Paint paint;

    public void setJumpListener(JumpListener jumpListener) {
        this.jumpListener = jumpListener;
    }

    public JumpView(Context context) {
        super(context, null);
    }

    public JumpView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(40);
        paint.setColor(Color.RED);
    }

    public interface JumpListener {
        void getTouchPoint(float downX, float downY, float upX, float upY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (downX != -1 && downY != -1)
            canvas.drawCircle(downX, downY, 20, paint);
        if (upX != -1 && upY != -1)
            canvas.drawCircle(upX, upY, 20, paint);
        String content = "开始";
        int width = (int) paint.measureText(content);
        canvas.drawText(content, (getWidth() - width) / 2, 100, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();
                invalidate();
                if (jumpListener != null)
                    jumpListener.getTouchPoint(downX, downY, upX, upY);
                break;
        }
        return true;
    }
}
