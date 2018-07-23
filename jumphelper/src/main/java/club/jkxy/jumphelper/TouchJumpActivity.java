package club.jkxy.jumphelper;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TouchJumpActivity extends Activity {
    private JumpView jumpView;
    private JumpView.JumpListener jumpListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_jump);
        jumpView = findViewById(R.id.jumpView);
    }


    public void setJumpListener(JumpView.JumpListener jumpListener) {
        this.jumpListener = jumpListener;
        if (jumpView != null)
            jumpView.setJumpListener(jumpListener);
    }
}
