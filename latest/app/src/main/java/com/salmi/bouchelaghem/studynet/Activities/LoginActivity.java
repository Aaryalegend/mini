package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.Serializers;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityLoginBinding;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("ConstantConditions")
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private final CurrentUser currentUser = CurrentUser.getInstance();

    private SharedPreferences sharedPreferences;
    private CustomLoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Set the light theme is the default theme.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        loadingDialog = new CustomLoadingDialog(LoginActivity.this);

        loadingDialog = new CustomLoadingDialog(LoginActivity.this);

        binding.btnGoToSignUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        binding.btnGoToResetPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class)));

        binding.btnLogin.setOnClickListener(v -> {
            if (validateEmail() & validatePassword()) {
                loadingDialog.show();
                String email = binding.txtLoginEmail.getEditText().getText().toString().trim();
                String password = binding.txtLoginPassword.getEditText().getText().toString();

                // Authenticate user using Firebase Authentication
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // Proceed with your app flow
//                                    Toast.makeText(SignUpActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                                    startActivity(intent);
                                    finish();
                                    
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                }
                            }
                        });
            }
        });
    }

    public boolean validateEmail() {
        String email = binding.txtLoginEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            binding.txtLoginEmail.setError(getString(R.string.email_msg1));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtLoginEmail.setError(getString(R.string.email_msg2));
            return false;
        } else {
            binding.txtLoginEmail.setError(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String password = binding.txtLoginPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            binding.txtLoginPassword.setError(getString(R.string.empty_password_msg));
            return false;
        } else if (password.length() < 6) {
            binding.txtLoginPassword.setError(getString(R.string.password_msg2));
            return false;
        } else {
            binding.txtLoginPassword.setError(null);
            return true;
        }
    }
}