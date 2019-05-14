package th.ac.dusit.dbizcom.smartshrimp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import th.ac.dusit.dbizcom.smartshrimp.etc.MyPrefs;
import th.ac.dusit.dbizcom.smartshrimp.model.User;

import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.KEY_FRAGMENT;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_BREED_SOURCE;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_FARM_INFO;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_FEEDING_RECORD;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_FORMULA_MAIN;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_REPORT;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_SUMMARY;
import static th.ac.dusit.dbizcom.smartshrimp.MainActivity.TAG_FRAGMENT_WATER_QUALITY;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setupToolbarAndDrawer();

        findViewById(R.id.farm_info_image_view).setOnClickListener(this);
        findViewById(R.id.feeding_record_image_view).setOnClickListener(this);
        findViewById(R.id.water_quality_image_view).setOnClickListener(this);
        findViewById(R.id.breed_source_image_view).setOnClickListener(this);
        findViewById(R.id.formula_image_view).setOnClickListener(this);
        findViewById(R.id.summary_image_view).setOnClickListener(this);
        findViewById(R.id.report_image_view).setOnClickListener(this);

        /*ViewGroup logoutLayout = findViewById(R.id.logout_layout);
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MenuActivity.this)
                        .setTitle("ออกจากระบบ")
                        .setMessage("ยืนยันออกจากระบบ?")
                        .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // ลบ user ที่จำไว้
                                MyPrefs.setUserPref(MenuActivity.this, null);
                                // ไปหน้า login
                                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                                startActivity(intent);
                                // ปิดหน้าปัจจุบัน
                                finish();
                            }
                        })
                        .setNegativeButton("ยกเลิก", null)
                        .show();
            }
        });

        User user = MyPrefs.getUserPref(this);
        if (user != null) {
            String msg = String.format(
                    Locale.getDefault(),
                    "ออกจากระบบ\n(%s)",
                    (user.firstName + " " + user.lastName).trim()
            );
            TextView logoutTextView = logoutLayout.findViewById(R.id.logout_text_view);
            logoutTextView.setText(msg);
        }*/
    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        User user = MyPrefs.getUserPref(this);
        if (user != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView displayNameTextView = headerView.findViewById(R.id.display_name_text_view);
            TextView emailTextView = headerView.findViewById(R.id.email_text_view);

            String displayName = user.firstName + " " + user.lastName;
            displayNameTextView.setText(displayName);
            emailTextView.setText(user.email);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        String fragmentTag = null;
        switch (view.getId()) {
            case R.id.farm_info_image_view:
                fragmentTag = TAG_FRAGMENT_FARM_INFO;
                break;
            case R.id.feeding_record_image_view:
                fragmentTag = TAG_FRAGMENT_FEEDING_RECORD;
                break;
            case R.id.water_quality_image_view:
                fragmentTag = TAG_FRAGMENT_WATER_QUALITY;
                break;
            case R.id.breed_source_image_view:
                fragmentTag = TAG_FRAGMENT_BREED_SOURCE;
                break;
            case R.id.formula_image_view:
                fragmentTag = TAG_FRAGMENT_FORMULA_MAIN;
                break;
            case R.id.summary_image_view:
                fragmentTag = TAG_FRAGMENT_SUMMARY;
                break;
            case R.id.report_image_view:
                fragmentTag = TAG_FRAGMENT_REPORT;
                break;
        }
        intent.putExtra(KEY_FRAGMENT, fragmentTag);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_developer) {
            // Handle the camera action
        } else if (id == R.id.nav_home) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_logout) {
            doLogout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void doLogout() {
        new AlertDialog.Builder(MenuActivity.this)
                .setTitle("ออกจากระบบ")
                .setMessage("ยืนยันออกจากระบบ?")
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // ลบ user ที่จำไว้
                        MyPrefs.setUserPref(MenuActivity.this, null);
                        // ไปหน้า login
                        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                        startActivity(intent);
                        // ปิดหน้าปัจจุบัน
                        finish();
                    }
                })
                .setNegativeButton("ยกเลิก", null)
                .show();
    }
}
