package com.example.fixsensa;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

public class MainActivity extends Activity {
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

        Button btnOverlay = createButton("ЗАПУСТИТЬ В ФОНЕ (Оверлей)");
        btnOverlay.setBackgroundColor(Color.parseColor("#ff3b30"));
        btnOverlay.setOnClickListener(v -> checkOverlayPermission());
        layout.addView(btnOverlay);

        TextView sensaTitle = new TextView(this);
        sensaTitle.setText("Уровень фикса сенсы:");
        sensaTitle.setTextColor(Color.LTGRAY);
        sensaTitle.setPadding(0, 30, 0, 10);
        layout.addView(sensaTitle);

        layout.addView(createButton("Стандарт (Стабильно)"));
        layout.addView(createButton("Сильный (Отклик)"));
        layout.addView(createButton("Эксперт (Резкость)"));
        layout.addView(createButton("Максимум (Резкая)"));

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
        btn.setOnClickListener(v -> Toast.makeText(this, text + " (симуляция)", Toast.LENGTH_SHORT).show());
        return btn;
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
            } else {
                startService(new Intent(this, OverlayService.class));
                Toast.makeText(this, "Оверлей запущен.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_CODE && Settings.canDrawOverlays(this)) {
            startService(new Intent(this, OverlayService.class));
            finish();
        }
    }
}
