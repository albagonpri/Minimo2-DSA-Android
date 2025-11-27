package edu.upc.dsa.dsa_error404_android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;
import android.content.Context;

public class InicioLoginActivity extends AppCompatActivity {

    Button buttonTienda;
    Button buttonLogOut;
    Button buttonInventario;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonTienda = findViewById(R.id.buttonTienda);

        buttonTienda.setOnClickListener(v -> {
            Intent intent = new Intent(InicioLoginActivity.this, TiendaActivity.class);
            startActivity(intent);
        });

        buttonLogOut = findViewById(R.id.buttonLogOut);

        buttonLogOut.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(InicioLoginActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
        });

        buttonInventario = findViewById(R.id.buttonInventario);

        buttonInventario.setOnClickListener(v -> {
            Intent intent = new Intent(InicioLoginActivity.this, InventarioActivity.class);
            startActivity(intent);
        });
    }
}
