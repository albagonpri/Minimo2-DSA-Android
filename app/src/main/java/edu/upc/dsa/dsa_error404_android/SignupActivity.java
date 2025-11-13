package edu.upc.dsa.dsa_error404_android;

import android.content.Intent; // <-- 1. AFEGIR AQUEST IMPORT
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {
    EditText etUsuari, etPassword;
    Button btnSignUp, btnBackToMain; // <-- 2. AFEGIR LA VARIABLE PER AL BOTÓ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsuari = findViewById(R.id.editUsuari);
        etPassword = findViewById(R.id.EditPassword);
        btnSignUp = findViewById(R.id.SignUp);
        btnBackToMain = findViewById(R.id.btnBackToMain); // <-- 3. ENLLAÇAR EL NOU BOTÓ

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        // <-- 4. AFEGIR LA LÒGICA DEL BOTÓ DE TORNAR -->
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent per obrir MainActivity
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Tanca SignupActivity
            }
        });

    }
    private void handleSignUp() {
        String usuari = etUsuari.getText().toString();
        String password = etPassword.getText().toString();

        if (usuari.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Dades incorrectes.", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Registrant usuari: " + usuari, Toast.LENGTH_LONG).show();
        finish();
    }
}