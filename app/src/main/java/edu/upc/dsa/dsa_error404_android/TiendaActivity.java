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
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TiendaActivity extends AppCompatActivity {
    Button btnBackToInicioLogin;
    TextView tvMonedas;
    RecyclerView recyclerViewTienda;
    TiendaAdapter adapter;
    SharedPreferences sharedPreferences;

    ApiService apiService;

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


        sharedPreferences = getSharedPreferences("user_credentials", Context.MODE_PRIVATE);

        btnBackToInicioLogin = findViewById(R.id.btnBackToInicioLogIn);
        tvMonedas = findViewById(R.id.textViewMonedas);
        recyclerViewTienda = findViewById(R.id.recyclerViewTienda);
        recyclerViewTienda.setLayoutManager(new LinearLayoutManager(this));

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        apiService = RetrofitClient.getInstance().getMyApi();

        actualizarMonedasUI();
        cargarTienda();

        btnBackToInicioLogin.setOnClickListener(v -> {
            Intent intent = new Intent(TiendaActivity.this, InicioLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        String username = sharedPreferences.getString("username", null);
        if (username == null) {
            Toast.makeText(this, "Error: Sesión no iniciada", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<User> call = apiService.getUser(username);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User usuarioActualizado = response.body();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("monedas", usuarioActualizado.getMonedas());
                    editor.apply();

                    actualizarMonedasUI();
                    Log.d("TiendaActivity", "Datos del usuario actualizados desde la API.");

                } else {
                    Log.e("TiendaActivity", "Error al cargar datos del usuario: " + response.code());
                    actualizarMonedasUI();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("TiendaActivity", "Fallo de red al cargar datos del usuario.", t);
                Toast.makeText(TiendaActivity.this, "Fallo de conexión. Mostrando datos locales.", Toast.LENGTH_SHORT).show();
                actualizarMonedasUI();
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

                    // Configura el adaptador con la lista de objetos
                    adapter = new TiendaAdapter(TiendaActivity.this, objetos, gameObject -> {
                        // Lógica de compra al hacer clic en el botón
                        handleCompra(gameObject);
                    });
                    recyclerViewTienda.setAdapter(adapter);

                } else {
                    Toast.makeText(TiendaActivity.this, "Error cargando la tienda", Toast.LENGTH_SHORT).show();
                    Log.e("TiendaActivity", "Error cargando tienda: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<GameObject>> call, Throwable t) {
                Toast.makeText(TiendaActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("TiendaActivity", "Error en onFailure al cargar tienda", t);
            }
        });
    }

    private void handleCompra(GameObject item) {
        SharedPreferences prefs = getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);
        if (username == null) {
            Toast.makeText(this, "Error: Sesión no iniciada", Toast.LENGTH_LONG).show();
            return;
        }

        String objectId = item.getId();
        if (objectId == null) {
            Toast.makeText(this, "Error: Objeto no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        CompraRequest request = new CompraRequest(username, objectId);

        Call<Void> call = apiService.comprarItem(request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TiendaActivity.this, item.getNombre() + " comprado con éxito!", Toast.LENGTH_SHORT).show();

                    int monedasActuales = prefs.getInt("monedas", 0);
                    int nuevasMonedas = monedasActuales - item.getPrecio();

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("monedas", nuevasMonedas);
                    editor.apply();

                    actualizarMonedasUI();
                } else {
                    String errorMessage = "Error " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("TiendaActivity", "Error al parsear el errorBody", e);
                    }
                    Toast.makeText(TiendaActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("TiendaActivity", "Error en la compra: " + response.code() + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TiendaActivity.this, "Fallo de conexión:: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("TiendaActivity", "Error en onFailure al comprar", t);
            }
        });
    }


    private void actualizarMonedasUI() {
        int monedas = sharedPreferences.getInt("monedas", 0);
        tvMonedas.setText("Monedas: " + monedas);
    }
}
