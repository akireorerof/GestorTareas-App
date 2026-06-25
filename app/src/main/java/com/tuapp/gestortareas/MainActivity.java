package com.tuapp.gestortareas;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.tuapp.gestortareas.adapter.TaskAdapter;
import com.tuapp.gestortareas.model.Task;

import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.GridLayoutManager;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TextView tvEmpty;

    private FirebaseFirestore db;
    private ListenerRegistration listenerReg;
    private List<Task> listaTareas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerViewTareas);
        tvEmpty      = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregarTarea);


        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        taskAdapter = new TaskAdapter(this, listaTareas, this);
        recyclerView.setAdapter(taskAdapter);

        db = FirebaseFirestore.getInstance();

        db.collection("tareas")
                .get()
                .addOnSuccessListener(snapshot -> {
                    android.widget.Toast.makeText(
                            MainActivity.this,
                            "Documentos Firebase: " + snapshot.size(),
                            android.widget.Toast.LENGTH_LONG
                    ).show();
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(
                            MainActivity.this,
                            "Error Firebase: " + e.getMessage(),
                            android.widget.Toast.LENGTH_LONG
                    ).show();
                });

        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        conectarFirestore();
    }

    private void conectarFirestore() {

        listenerReg = db.collection("tareas")
                .addSnapshotListener((snapshots, error) -> {

                    if (error != null) {
                        error.printStackTrace();

                        android.widget.Toast.makeText(
                                MainActivity.this,
                                "Error: " + error.getMessage(),
                                android.widget.Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    listaTareas.clear();

                    if (snapshots != null) {

                        for (DocumentSnapshot doc : snapshots.getDocuments()) {

                            Task t = doc.toObject(Task.class);

                            if (t != null) {
                                t.setId(doc.getId());
                                listaTareas.add(t);
                            }
                        }
                    }

                    taskAdapter.actualizarLista(listaTareas);

                    android.widget.Toast.makeText(
                            MainActivity.this,
                            "Tareas encontradas: " + listaTareas.size(),
                            android.widget.Toast.LENGTH_SHORT
                    ).show();

                    if (listaTareas.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onTaskClick(Task tarea) {
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra("TASK_ID", tarea.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Task tarea) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar tarea")
                .setMessage("¿Eliminar \"" + tarea.getTitulo() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) ->
                        db.collection("tareas").document(tarea.getId()).delete()
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onToggleEstado(Task tarea) {
        db.collection("tareas").document(tarea.getId())
                .update("estado", tarea.getEstado());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerReg != null) {
            listenerReg.remove();
        }
    }
}