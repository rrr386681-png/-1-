package com.example.fixsensa;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity {

    private static final int SHIZUKU_CODE = 101;
    private static final int OVERLAY_PERMISSION_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#141416"));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 60, 40, 60);

        TextView title = new TextView(this);
        title.setText("Fix Sensa\nTurboBoost");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 50);
        layout.addView(title);

        Button btnShizuku = createButton("1. Включить Shizuku");
        btnShizuku.setOnClickListener(v -> checkShizukuPermission());
        layout.addView(btnShizuku);

        Button btnOverlay = createButton("ЗАПУСТИТЬ В ФОНЕ (Оверлей)");
        btnOverlay.setBackgroundColor(Color.parseColor("#ff3b30"));
        btnOverlay.setOnClickListener(v -> checkOverlayPermission());
        layout.addView(btnOverlay);

        TextView sensaTitle = new TextView(this);
        sensaTitle.setText("Уровень фикса сенсы:");
        sensaTitle.setTextColor(Color.LTGRAY);
        sensaTitle.setPadding(0, 30, 0, 10);
        layout.addView(sensaTitle);

        Button btnStandard = createButton("Стандарт (Стабильно)");
        btnStandard.setOnClickListener(v -> applySensaLevel(1));
        layout.addView(btnStandard);

        Button btnStrong = createButton("Сильный (Отклик)");
        btnStrong.setOnClickListener(v -> applySensaLevel(2));
        layout.addView(btnStrong);

        Button btnExpert = createButton("Эксперт (Резкость)");
        btnExpert.setOnClickListener(v -> applySensaLevel(3));
        layout.addView(btnExpert);

        Button btnMax = createButton("Максимум (Резкая как в CS)");
        btnMax.setOnClickListener(v -> applySensaLevel(4));
        layout.addView(btnMax);

        scrollView.addView(layout);
        setContentView(scrollView);
    }

    private Button createButton(String text) {
        Button btn = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 15, 0, 15);
        btn.setLayoutParams(params);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor("#2C2C30"));
        return btn;
    }

    private void checkShizukuPermission() {
        try {
            if (Shizuku.isPreV11() || Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Shizuku подключен", Toast.LENGTH_SHORT).show();
            } else {
                Shizuku.requestPermission(SHIZUKU_CODE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Установи Shizuku", Toast.LENGTH_LONG).show();
        }
    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
        } else {
            startOverlayService();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            if (Settings.canDrawOverlays(this)) {
                startOverlayService();
            } else {
                Toast.makeText(this, "Разрешение отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startOverlayService() {
        Intent intent = new Intent(this, OverlayService.class);
        startService(intent);
        Toast.makeText(this, "Оверлей запущен. Сверни приложение.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void applySensaLevel(int level) {
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Сначала дай права Shizuku!", Toast.LENGTH_SHORT).show();
            return;
        }
        execSensaCmd(level);
        Toast.makeText(this, "Сенса: Уровень " + level, Toast.LENGTH_SHORT).show();
    }

    public static void execSensaCmd(int level) {
        try {
            switch (level) {
                case 1:
                    Shizuku.newProcess(new String[]{"settings", "put", "system", "touch_sensitivity", "1"}, null, null);
                    break;
                case 2:
                    Shizuku.newProcess(new String[]{"settings", "put", "system", "touch_sensitivity", "2"}, null, null);
                    Shizuku.newProcess(new String[]{"settings", "put", "secure", "pointer_speed", "1"}, null, null);
                    break;
                case 3:
                    Shizuku.newProcess(new String[]{"settings", "put", "system", "touch_sensitivity", "3"}, null, null);
                    Shizuku.newProcess(new String[]{"settings", "put", "secure", "pointer_speed", "2"}, null, null);
                    Shizuku.newProcess(new String[]{"settings", "put", "secure", "touch_smoothing", "0"}, null, null);
                    break;
                case 4:
                    Shizuku.newProcess(new String[]{"settings", "put", "system", "touch_sensitivity", "4"}, null, null);
                    Shizuku.newProcess(new String[]{"settings", "put", "secure", "pointer_speed", "3"}, null, null);
                    Shizuku.newProcess(new String[]{"settings", "put", "secure", "touch_smoothing", "0"}, null, null);
                    break;
            }
        } catch (Exception ignored) {}
    }
  }
