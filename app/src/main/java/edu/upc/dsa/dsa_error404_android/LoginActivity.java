package edu.upc.dsa.dsa_error404_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnBackToMain;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLoginSubmit);
        btnBackToMain = findViewById(R.id.btnBackToMain);

        apiService = RetrofitClient.getInstance().getMyApi();

        btnLogin.setOnClickListener(v -> handleLogin());
        btnBackToMain.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
    }

    private void handleLogin() {
        String input = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (input.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introduce usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        String inputMinuscula = input.toLowerCase();

        Credentials credentials = new Credentials();
        credentials.setNombre(inputMinuscula);
        credentials.setPassword(password);

        Call<User> call = apiService.loginUser(credentials);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    User user = response.body();
                    Toast.makeText(LoginActivity.this, "Sesión iniciada. ¡Bienvenido " + user.getNombre(), Toast.LENGTH_LONG).show();

                    // Guardar dades localment
                    SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("username", user.getNombre().toLowerCase());
                    //editor.putString("userId", user.getId());
                    editor.putInt("monedas", user.getMonedas());
                    editor.putInt("vidaInicial", user.getVidaInicial());
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, InicioLoginActivity.class));
                    finish();

                } else {
                    Log.e("LoginActivity", "Error onResponse: " + response.code());
                    Toast.makeText(LoginActivity.this, "Error: Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "Error onFailure", t);
            }
        });
    }
}
