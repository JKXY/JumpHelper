package club.jkxy.jumphelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button button,btnClean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit);
        editText.setText(JumpAccessibilityService.JUMP_RATIO + "");
        button = findViewById(R.id.btn_ok);
        btnClean = findViewById(R.id.btn_clean);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double var = Double.valueOf(editText.getText().toString().trim());
                JumpAccessibilityService.JUMP_RATIO = var;
                Toast.makeText(MainActivity.this,"设置成功！",Toast.LENGTH_SHORT).show();
            }
        });
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpAccessibilityService.JUMP_RATIO = JumpAccessibilityService.JUMP_RATIO_DEF;
                editText.setText(JumpAccessibilityService.JUMP_RATIO + "");
                Toast.makeText(MainActivity.this,"设置成功！",Toast.LENGTH_SHORT).show();
            }
        });

    }

}
