package club.jkxy.jumphelper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import club.jkxy.floatwindow.FloatWindow;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button button, btnClean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit);
        editText.setText(String.valueOf(JumpAccessibilityService.JUMP_RATIO));
        button = findViewById(R.id.btn_ok);
        btnClean = findViewById(R.id.btn_clean);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double var = Double.valueOf(editText.getText().toString().trim());
                JumpAccessibilityService.JUMP_RATIO = var;
                Toast.makeText(MainActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
            }
        });
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpAccessibilityService.JUMP_RATIO = JumpAccessibilityService.JUMP_RATIO_DEF;
                editText.setText(JumpAccessibilityService.JUMP_RATIO + "");
                Toast.makeText(MainActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
            }
        });

        RadioGroup typeRg = findViewById(R.id.typeRg);
        RadioButton radioButton1 = findViewById(R.id.type1);
        radioButton1.setChecked(true);
        typeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.type1) {
                    JumpAccessibilityService.currType = JumpAccessibilityService.TYPR_1;
                } else if (checkedId == R.id.type2) {
                    JumpAccessibilityService.currType = JumpAccessibilityService.TYPR_2;
                } else if (checkedId == R.id.type3) {
                    JumpAccessibilityService.currType = JumpAccessibilityService.TYPR_3;
                } else if (checkedId == R.id.type4) {
                    JumpAccessibilityService.currType = JumpAccessibilityService.TYPR_4;
                }
            }
        });

        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindow.get().show();
            }
        });

    }

}
