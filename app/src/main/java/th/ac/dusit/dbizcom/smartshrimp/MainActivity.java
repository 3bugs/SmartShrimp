package th.ac.dusit.dbizcom.smartshrimp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import th.ac.dusit.dbizcom.smartshrimp.etc.MyPrefs;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup logoutLayout = findViewById(R.id.logout_layout);
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("ออกจากระบบ")
                        .setMessage("ยืนยันออกจากระบบ?")
                        .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // ลบ user ที่จำไว้
                                MyPrefs.setUserPref(MainActivity.this, null);
                                // ไปหน้า login
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                // ปิดหน้าปัจจุบัน
                                finish();
                            }
                        })
                        .setNegativeButton("ยกเลิก", null)
                        .show();
            }
        });
    }
}
