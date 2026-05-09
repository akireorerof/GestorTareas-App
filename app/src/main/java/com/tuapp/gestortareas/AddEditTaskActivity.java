package com.tuapp.gestortareas;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.tuapp.gestortareas.database.TaskDAO;
import com.tuapp.gestortareas.model.Task;

public class AddEditTaskActivity extends AppCompatActivity {

    private TextInputEditText etTitulo, etDescripcion;
    private SwitchMaterial switchEstado;
    private TextView tvEstadoLabel;
    private MaterialButton btnGuardar;
    private TaskDAO taskDAO;

    private int taskId = -1;
    private boolean esModoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        switchEstado = findViewById(R.id.switchEstado);
        tvEstadoLabel = findViewById(R.id.tvEstadoLabel);
        btnGuardar = findViewById(R.id.btnGuardar);

        taskDAO = new TaskDAO(this);

        switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tvEstadoLabel.setText(isChecked ? "Completada" : "Pendiente");
        });

        if (getIntent().hasExtra("TASK_ID")) {
            taskId = getIntent().getIntExtra("TASK_ID", -1);
            esModoEdicion = true;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Editar Tarea");
            }
            cargarDatosTarea();
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Nueva Tarea");
            }
        }

        btnGuardar.setOnClickListener(v -> guardarTarea());
    }

    private void cargarDatosTarea() {
        taskDAO.open();
        Task tarea = taskDAO.obtenerTareaPorId(taskId);
        taskDAO.close();

        if (tarea != null) {
            etTitulo.setText(tarea.getTitulo());
            etDescripcion.setText(tarea.getDescripcion());
            boolean completada = tarea.isCompletada();
            switchEstado.setChecked(completada);
            tvEstadoLabel.setText(completada ? "Completada" : "Pendiente");
        }
    }

    private void guardarTarea() {
        String titulo = etTitulo.getText() != null ? etTitulo.getText().toString().trim() : "";
        String descripcion = etDescripcion.getText() != null ? etDescripcion.getText().toString().trim() : "";
        int estado = switchEstado.isChecked() ? 1 : 0;

        if (TextUtils.isEmpty(titulo)) {
            etTitulo.setError("El título es obligatorio");
            etTitulo.requestFocus();
            return;
        }

        taskDAO.open();
        if (esModoEdicion) {
            Task tarea = new Task(taskId, titulo, descripcion, estado);
            taskDAO.actualizarTarea(tarea);
            Toast.makeText(this, "Tarea actualizada ✅", Toast.LENGTH_SHORT).show();
        } else {
            Task tarea = new Task(titulo, descripcion, estado);
            taskDAO.insertarTarea(tarea);
            Toast.makeText(this, "Tarea creada ✅", Toast.LENGTH_SHORT).show();
        }
        taskDAO.close();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}