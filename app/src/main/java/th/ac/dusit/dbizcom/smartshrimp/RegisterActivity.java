package th.ac.dusit.dbizcom.smartshrimp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import retrofit2.Call;
import retrofit2.Retrofit;
import th.ac.dusit.dbizcom.smartshrimp.etc.Utils;
import th.ac.dusit.dbizcom.smartshrimp.model.User;
import th.ac.dusit.dbizcom.smartshrimp.net.ApiClient;
import th.ac.dusit.dbizcom.smartshrimp.net.MyRetrofitCallback;
import th.ac.dusit.dbizcom.smartshrimp.net.RegisterResponse;
import th.ac.dusit.dbizcom.smartshrimp.net.WebServices;

import static th.ac.dusit.dbizcom.smartshrimp.LoginActivity.KEY_USERNAME;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmailEditText, mUsernameEditText;
    private EditText mPasswordEditText, mConfirmPasswordEditText;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailEditText = findViewById(R.id.email_edit_text);
        mUsernameEditText = findViewById(R.id.username_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mConfirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        mProgressView = findViewById(R.id.progress_view);

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    Utils.hideKeyboard(RegisterActivity.this);
                    doRegister();
                } else {
                    Utils.showShortToast(RegisterActivity.this, "กรอกข้อมูลให้ครบ");
                }
            }
        });
    }

    private boolean isFormValid() {
        boolean valid = true;

        String confirmPassword = mConfirmPasswordEditText.getText().toString().trim();
        if (confirmPassword.length() == 0) {
            mConfirmPasswordEditText.setText("");
            mConfirmPasswordEditText.setError("กรอกรหัสผ่านอีกครั้งเพื่อยืนยัน");
            valid = false;
        }
        String password = mPasswordEditText.getText().toString().trim();
        if (password.length() == 0) {
            mPasswordEditText.setText("");
            mPasswordEditText.setError("กรอกรหัสผ่าน");
            valid = false;
        }
        if (password.length() > 0 && confirmPassword.length() > 0
                && !password.equals(confirmPassword)) {
            mConfirmPasswordEditText.setError("กรอกยืนยันรหัสผ่านให้ตรงกัน");
            valid = false;
        }
        String username = mUsernameEditText.getText().toString().trim();
        if (username.length() == 0) {
            mUsernameEditText.setText("");
            mUsernameEditText.setError("กรอกชื่อผู้ใช้");
            valid = false;
        }
        String email = mEmailEditText.getText().toString().trim();
        if (email.length() == 0) {
            mEmailEditText.setText("");
            mEmailEditText.setError("กรอกอีเมล");
            valid = false;
        } else if (!Utils.isValidEmail(email)) {
            mEmailEditText.setError("รูปแบบอีเมลไม่ถูกต้อง");
            valid = false;
        }

        return valid;
    }

    private void doRegister() {
        String username = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();

        mProgressView.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);

        Call<RegisterResponse> call = services.register(username, password, email);
        call.enqueue(new MyRetrofitCallback<>(
                RegisterActivity.this,
                null,
                mProgressView,
                new MyRetrofitCallback.MyRetrofitCallbackListener<RegisterResponse>() {
                    @Override
                    public void onSuccess(RegisterResponse responseBody) { // register สำเร็จ
                        User user = responseBody.user;
                        // ส่ง username ที่ register สำเร็จ กลับไปแสดงในหน้า login
                        Intent intent = new Intent();
                        intent.putExtra(KEY_USERNAME, user.username);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) { // register ไม่สำเร็จ หรือเกิดข้อผิดพลาดอื่นๆ (เช่น ไม่มีเน็ต, server ล่ม)
                        Utils.showOkDialog(RegisterActivity.this, "ผิดพลาด", errorMessage);
                    }
                }
        ));
    }
}
