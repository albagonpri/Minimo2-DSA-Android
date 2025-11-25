package edu.upc.dsa.dsa_error404_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TiendaActivity extends AppCompatActivity {
    Button btnBackToInicioLogin, btnComprarEspada, btnComprarEscudo, btnComprarPocion;
    TextView tvMonedas;
    Map<String, String> tiendaMap = new HashMap<>();

    ApiService apiService;
    public static final String BASE_URL = "http://10.0.2.2:8080/dsaApp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tienda);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBackToInicioLogin = findViewById(R.id.btnBackToInicioLogIn);
        btnComprarEspada = findViewById(R.id.btnComprarEspada);
        btnComprarEscudo = findViewById(R.id.btnComprarEscudo);
        btnComprarPocion = findViewById(R.id.btnComprarPocion);
        tvMonedas = findViewById(R.id.textViewMonedas);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        cargarTienda();

        btnBackToInicioLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TiendaActivity.this, InicioLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnComprarEspada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTienda("Espada");
            }
        });

        btnComprarEscudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTienda("Escudo");
            }
        });

        btnComprarPocion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTienda("Pocion");
            }
        });
    }

    private void cargarTienda() {
        Call<List<GameObject>> call = apiService.getALLGameObjects();
        call.enqueue(new Callback<List<GameObject>>() {
            @Override
            public void onResponse(Call<List<GameObject>> call, Response<List<GameObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GameObject> objetos = response.body();

                    for (GameObject obj : objetos) {
                        tiendaMap.put(obj.getNombre(), obj.getId());
                    }

                    for (GameObject obj : objetos) {
                        if (obj.getNombre().equals("Espada")) {
                            btnComprarEspada.setText("Comprar Espada (" + obj.getPrecio() + " monedas)");
                        } else if (obj.getNombre().equals("Escudo")) {
                            btnComprarEscudo.setText("Comprar Escudo (" + obj.getPrecio() + " monedas)");
                        } else if (obj.getNombre().equals("Pocion")) {
                            btnComprarPocion.setText("Comprar Pocion (" + obj.getPrecio() + " monedas)");
                        }
                    }

                    actualizarMonedasUI();
                } else {
                    Toast.makeText(TiendaActivity.this, "Error cargando tienda", Toast.LENGTH_SHORT).show();
                    Log.e("TiendaActivity", "Error cargando tienda: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GameObject>> call, Throwable t) {
                Toast.makeText(TiendaActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("TiendaActivity", "Error en onFailure", t);
            }
        });
    }
    private void actualizarMonedasUI() {
        SharedPreferences prefs = getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        int monedas = prefs.getInt("monedas", 0);
        tvMonedas.setText("Monedas: " + monedas);
    }

    private void handleTienda(String item){
        SharedPreferences prefs = getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);
        if (username == null) {
            Toast.makeText(this, "Error: usuario no logueado", Toast.LENGTH_LONG).show();
            return;
        }

        String objectId = tiendaMap.get(item);
        if (objectId == null) {
            Toast.makeText(this, "Error: objeto no encontrado en la tienda", Toast.LENGTH_SHORT).show();
            return;
        }

        CompraRequest request = new CompraRequest(username , objectId);

        Call<User> call = apiService.comprarItem(request);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    // Codi 200
                    Toast.makeText(TiendaActivity.this,
                            "Compra realizada.",
                            Toast.LENGTH_SHORT).show();
                    finish();

                } else if (response.code() == 402) {
                    // Codi 402
                    Log.e("TiendaActivity", "Error en onResponse: " + response.code());
                    Toast.makeText(TiendaActivity.this,
                            "Error: Monedas insificientes",
                            Toast.LENGTH_SHORT).show();

                } else {
                    // Codi 404
                    Log.e("TiendaActivity", "Error en onResponse: " + response.code());
                    Toast.makeText(TiendaActivity.this,
                            "Error: Objeto o usuario no encontrado",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(TiendaActivity.this, "Fallo de connexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("TiendaActivity", "Error en onFailure", t);
            }
        });
    }
}
