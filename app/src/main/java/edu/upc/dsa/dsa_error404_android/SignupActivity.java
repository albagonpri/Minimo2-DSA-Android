package edu.upc.dsa.dsa_error404_android;

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
    Button btnSignUp;

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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llama a la función que maneja el registro
                handleSignUp();
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