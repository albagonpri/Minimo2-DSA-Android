package edu.upc.dsa.dsa_error404_android;

import android.content.Intent; // <-- 1. AFEGIR AQUEST IMPORT
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnBackToMain; // <-- 2. AFEGIR LA VARIABLE PER AL BOTÓ
    ApiService apiService;

    public static final String BASE_URL = "http://10.0.2.2:8080/dsaApp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLoginSubmit);
        btnBackToMain = findViewById(R.id.btnBackToMain); // <-- 3. ENLLAÇAR EL BOTÓ (amb l'ID del XML)

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // <-- 4. AFEGIR LA LÒGICA DEL BOTÓ DE TORNAR -->
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent per obrir MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Tanca LoginActivity
            }
        });
    }

    private void handleLogin() {
        String name = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introdueix usuari i contrasenya", Toast.LENGTH_SHORT).show();
            return;
        }

        Credentials credentials = new Credentials(name, password);
        Call<User> call = apiService.loginUser(credentials);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    Toast.makeText(LoginActivity.this, "Login OK! Benvingut " + user.getName(), Toast.LENGTH_LONG).show();

                } else {
                    Log.e("LoginActivity", "Error en onResponse: " + response.code());
                    Toast.makeText(LoginActivity.this, "Error: Usuari o contrasenya incorrectes", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Fallo de connexió: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "Error en onFailure", t);
            }
        });
    }
}