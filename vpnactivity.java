package com.creysvpn.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VpnActivity extends Activity {

    private static final int REQUEST_VPN = 1;
    private static final int REQUEST_OVERLAY = 2;

    public static final String PREFS_NAME = "creysvpn_prefs";
    public static final String KEY_VPN_ACTIVE = "vpn_active";

    private Button btnVpn;
    private TextView tvStatus;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpn);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        btnVpn = findViewById(R.id.btnVpn);
        tvStatus = findViewById(R.id.tvStatus);

        checkOverlayPermission();

        btnVpn.setOnClickListener(v -> {
            if (!isVpnActive()) {
                startVPN();
            } else {
                stopVPN();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private boolean isVpnActive() {
        return prefs.getBoolean(KEY_VPN_ACTIVE, false);
    }

    private void updateUI() {
        if (isVpnActive()) {
            btnVpn.setText("■");
            btnVpn.setBackgroundResource(R.drawable.button_vpn_on);
            tvStatus.setText("ПОДКЛЮЧЕНО");
        } else {
            btnVpn.setText("▶");
            btnVpn.setBackgroundResource(R.drawable.button_vpn_off);
            tvStatus.setText("ОТКЛЮЧЕНО");
        }
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this,
                        "Требуется разрешение на отображение поверх приложений",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(intent, REQUEST_OVERLAY);
            }
        }
    }

    private void startVPN() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Сначала дай разрешение на отображение поверх приложений",
                        Toast.LENGTH_LONG).show();
                checkOverlayPermission();
                return;
            }
        }

        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            startActivityForResult(intent, REQUEST_VPN);
        } else {
            onActivityResult(REQUEST_VPN, RESULT_OK, null);
        }
    }

    private void stopVPN() {
        prefs.edit().putBoolean(KEY_VPN_ACTIVE, false).apply();

        Intent vpnIntent = new Intent(this, PcapVpnService.class);
        stopService(vpnIntent);

        Intent floatingIntent = new Intent(this, OverlayService.class);
        stopService(floatingIntent);

        updateUI();
        Toast.makeText(this, "VPN остановлен", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VPN) {
            if (resultCode == RESULT_OK) {
                prefs.edit().putBoolean(KEY_VPN_ACTIVE, true).apply();

                Intent vpnIntent = new Intent(this, PcapVpnService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(vpnIntent);
                } else {
                    startService(vpnIntent);
                }

                Intent floatingIntent = new Intent(this, OverlayService.class);
                startService(floatingIntent);

                updateUI();
                Toast.makeText(this, "VPN запущен. Плавающие окна активны!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Разрешение VPN отклонено", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Разрешение получено! Теперь запусти VPN",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,
                            "Разрешение не получено. Плавающие окна не будут работать!",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
