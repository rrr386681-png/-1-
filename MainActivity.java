package com.example.fixsens;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Button btn = new Button(this);
        btn.setText("ВКЛЮЧИТЬ ФИКС СЕНСЫ");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Нет прав Shizuku!", Toast.LENGTH_SHORT).show();
                    return;
                }
                exec("settings put secure touch_smoothing 0");
                exec("settings put system pointer_acceleration 0");
                exec("settings put secure game_mode_input_latency low");
                exec("settings put secure multi_press_timeout 0");
                Toast.makeText(MainActivity.this, "Фикс применен!", Toast.LENGTH_LONG).show();
            }
        });
        setContentView(btn);
    }

    private void exec(String cmd) {
        try {
            Process process = Shizuku.newProcess(new String[]{"sh", "-c", cmd}, null, null);
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}