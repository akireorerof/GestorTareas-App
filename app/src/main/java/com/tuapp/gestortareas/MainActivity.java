package com.tuapp.gestortareas;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tuapp.gestortareas.adapter.TaskAdapter;
import com.tuapp.gestortareas.database.TaskDAO;
import com.tuapp.gestortareas.model.Task;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TaskDAO taskDAO;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerViewTareas);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregarTarea);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskDAO = new TaskDAO(this);

        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTareas();
    }

    private void cargarTareas() {
        taskDAO.open();
        List<Task> tareas = taskDAO.obtenerTodasLasTareas();
        taskDAO.close();

        if (tareas.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(this, tareas, this);
            recyclerView.setAdapter(taskAdapter);
        } else {
            taskAdapter.actualizarLista(tareas);
        }
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
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    taskDAO.open();
                    taskDAO.eliminarTarea(tarea.getId());
                    taskDAO.close();
                    cargarTareas();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onToggleEstado(Task tarea) {
        int nuevoEstado = tarea.isCompletada() ? 0 : 1;
        tarea.setEstado(nuevoEstado);
        taskDAO.open();
        taskDAO.actualizarTarea(tarea);
        taskDAO.close();
        cargarTareas();
    }
}