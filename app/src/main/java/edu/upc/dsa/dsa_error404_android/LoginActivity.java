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

    public static final String BASE_URL = "http://10.0.2.2:8080/dsaApp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLoginSubmit);
        btnBackToMain = findViewById(R.id.btnBackToMain);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

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
            Toast.makeText(this, "Introdueix usuari/email i contrasenya", Toast.LENGTH_SHORT).show();
            return;
        }

        Credentials credentials = new Credentials();
        credentials.setNombre(input);
        credentials.setPassword(password);

        Call<User> call = apiService.loginUser(credentials);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    User user = response.body();
                    Toast.makeText(LoginActivity.this, "Sessió iniciada! Benvingut " + user.getNombre(), Toast.LENGTH_LONG).show();

                    // Guardar dades localment
                    SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("username", user.getNombre());
                    //editor.putString("userId", user.getId());
                    editor.putInt("monedas", user.getMonedas());
                    editor.putInt("vidaInicial", user.getVidaInicial());
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, InicioLoginActivity.class));
                    finish();

                } else {
                    Log.e("LoginActivity", "Error onResponse: " + response.code());
                    Toast.makeText(LoginActivity.this, "Error: Usuari o contrasenya incorrectes", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Fallo de connexió: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "Error onFailure", t);
            }
        });
    }
}
