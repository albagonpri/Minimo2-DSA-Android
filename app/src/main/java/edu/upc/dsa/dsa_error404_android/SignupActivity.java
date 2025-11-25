package edu.upc.dsa.dsa_error404_android;

import android.content.Intent;
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

public class SignupActivity extends AppCompatActivity {

    EditText etUsuari, etEmail, etPassword, etRepeatPassword;
    Button btnSignUp, btnBackToMain;
    ApiService apiService;

    public static final String BASE_URL = "http://10.0.2.2:8080/dsaApp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Assignació de components
        etUsuari = findViewById(R.id.editUsuari);
        etEmail = findViewById(R.id.editEmail);
        etPassword = findViewById(R.id.EditPassword);
        etRepeatPassword = findViewById(R.id.editRepeatPassword);

        btnSignUp = findViewById(R.id.SignUp);
        btnBackToMain = findViewById(R.id.btnBackToMain);

        // Configurar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        btnSignUp.setOnClickListener(v -> handleSignUp());
        btnBackToMain.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
        });
    }

    private void handleSignUp() {
        String usuari = etUsuari.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();

        // Validació bàsica
        if (usuari.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(this, "Omple tots els camps.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validació email
        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        if (!email.matches(emailRegex)) {
            Toast.makeText(this, "El format del correu no és vàlid.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validació contrasenyes
        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Les contrasenyes no coincideixen.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objecte credentials
        Credentials credentials = new Credentials();
        credentials.setNombre(usuari);
        credentials.setEmail(email);
        credentials.setPassword(password);

        // Crida al servidor
        Call<User> call = apiService.registerUser(credentials);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Usuari registrat! Ja pots iniciar sessió.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else if (response.code() == 409) {
                    Log.e("SignupActivity", "Usuari ja existeix: " + response.code());
                    Toast.makeText(SignupActivity.this, "Error: L'usuari ja existeix.", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.e("SignupActivity", "Error desconegut: " + response.code());
                    Toast.makeText(SignupActivity.this, "Error desconegut en el registre.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Fallo de connexió: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("SignupActivity", "Error onFailure", t);
            }
        });
    }
}
