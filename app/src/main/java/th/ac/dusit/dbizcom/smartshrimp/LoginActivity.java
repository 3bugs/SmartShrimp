package th.ac.dusit.dbizcom.smartshrimp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.etc.MyPrefs;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.User;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.LoginResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_REGISTER = 10001;
    static final String KEY_USERNAME = "username";

    private EditText mUsernameEditText, mPasswordEditText;
    private Button mLoginButton;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameEditText = findViewById(R.id.username_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mProgressView = findViewById(R.id.progress_view);

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_REGISTER);
            }
        });

        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    Utils.hideKeyboard(LoginActivity.this);
                    doLogin();
                } else {
                    Utils.showShortToast(LoginActivity.this, "กรอกข้อมูลให้ครบ");
                }
            }
        });
    }

    private boolean isFormValid() {
        boolean valid = true;

        if (mPasswordEditText.getText().toString().trim().length() == 0) {
            mPasswordEditText.setText("");
            mPasswordEditText.setError("กรอกรหัสผ่าน");
            valid = false;
        }
        if (mUsernameEditText.getText().toString().trim().length() == 0) {
            mUsernameEditText.setText("");
            mUsernameEditText.setError("กรอกชื่อผู้ใช้");
            valid = false;
        }

        return valid;
    }

    private void doLogin() {
        String username = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        mProgressView.setVisibility(View.VISIBLE);
        mLoginButton.setEnabled(false);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<LoginResponse> call = services.login(username, password);
        call.enqueue(new MyRetrofitCallback<>(
                LoginActivity.this,
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse responseBody) {
                        if (responseBody.loginSuccess) { // login สำเร็จ
                            User user = responseBody.user;
                            // จำว่า user login แล้ว
                            MyPrefs.setUserPref(LoginActivity.this, user);

                            // แสดง toast
                            String msg = String.format(
                                    Locale.getDefault(),
                                    "ยินดีต้อนรับ %s %s",
                                    user.firstName,
                                    user.lastName
                            );
                            Utils.showShortToast(LoginActivity.this, msg);

                            // ไปหน้าหลัก
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);

                            // ปิดหน้า login
                            finish();
                        } else { // login ไม่สำเร็จ
                            Utils.showOkDialog(LoginActivity.this, "เข้าสู่ระบบไม่สำเร็จ", "ชื่อผู้ใช้ หรือรหัสผ่าน ไม่ถูกต้อง", null);
                            mLoginButton.setEnabled(true);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) { // เกิดข้อผิดพลาด (เช่น ไม่มีเน็ต, server ล่ม)
                        Utils.showOkDialog(LoginActivity.this, "ผิดพลาด", errorMessage, null);
                        mLoginButton.setEnabled(true);
                    }
                }
        ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_REGISTER) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String username = data.getStringExtra(KEY_USERNAME);
                    mUsernameEditText.setText(username);
                    mPasswordEditText.requestFocus();
                }
            }
        }
    }
}
