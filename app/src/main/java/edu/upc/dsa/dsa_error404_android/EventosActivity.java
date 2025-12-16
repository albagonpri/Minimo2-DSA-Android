package edu.upc.dsa.dsa_error404_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEvents;
    private EventoAdapter adapter;
    private ArrayList<Evento> eventos;
    private ApiService api;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eventos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sp = getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        userId = sp.getString("username", "");

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));

        eventos = new ArrayList<>();
        adapter = new EventoAdapter(this, eventos, this::registerToEvent);
        recyclerViewEvents.setAdapter(adapter);

        api = RetrofitClient.getInstance().getMyApi();

        loadEventos();
    }

    private void loadEventos() {
        api.getEventos().enqueue(new Callback<List<Evento>>() {
            @Override
            public void onResponse(Call<List<Evento>> call, Response<List<Evento>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(EventosActivity.this, "Error cargando eventos", Toast.LENGTH_SHORT).show();
                    return;
                }
                eventos.clear();
                eventos.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Evento>> call, Throwable t) {
                Toast.makeText(EventosActivity.this, "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerToEvent(Evento evento) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return;
        }

        api.registerEvento(evento.getId(), new RegistroEventoRequest(userId)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EventosActivity.this, "Inscrito", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 409) {
                    Toast.makeText(EventosActivity.this, "Ya estabas inscrito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EventosActivity.this, "Error al inscribirse", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EventosActivity.this, "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
