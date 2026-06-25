package com.tuapp.gestortareas;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tuapp.gestortareas.model.Task;

import java.util.HashMap;
import java.util.Map;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText      etTitulo, etDescripcion;
    private TextView      tilTitulo, tilDescripcion, tvEstadoLabel;
    private SwitchMaterial switchEstado;
    private Button        btnGuardar;

    private FirebaseFirestore db;
    private String  taskId        = null;
    private boolean esModoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        // Toolbar con botón atrás
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Enlazar vistas
        etTitulo       = findViewById(R.id.etTitulo);
        etDescripcion  = findViewById(R.id.etDescripcion);
        tilTitulo      = findViewById(R.id.tilTitulo);
        tilDescripcion = findViewById(R.id.tilDescripcion);
        switchEstado   = findViewById(R.id.switchEstado);
        tvEstadoLabel  = findViewById(R.id.tvEstadoLabel);
        btnGuardar     = findViewById(R.id.btnGuardar);

        db = FirebaseFirestore.getInstance();

        // Modo edición o creación
        if (getIntent().hasExtra("TASK_ID")) {
            taskId        = getIntent().getStringExtra("TASK_ID");
            esModoEdicion = true;
            getSupportActionBar().setTitle("Editar tarea");
            btnGuardar.setText("Actualizar");
            cargarDatosTarea();
        } else {
            getSupportActionBar().setTitle("Nueva tarea");
        }

        // Switch estado
        switchEstado.setOnCheckedChangeListener((btn, isChecked) ->
                tvEstadoLabel.setText(isChecked ? "Completada" : "Pendiente")
        );

        // TextWatcher título
        etTitulo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString().trim();
                if (texto.isEmpty()) {
                    tilTitulo.setText("El título es obligatorio");
                    tilTitulo.setVisibility(View.VISIBLE);
                } else if (texto.length() < 3) {
                    tilTitulo.setText("Mínimo 3 caracteres");
                    tilTitulo.setVisibility(View.VISIBLE);
                } else {
                    tilTitulo.setVisibility(View.GONE);
                }
            }
        });

        // TextWatcher descripción
        etDescripcion.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    tilDescripcion.setText("La descripción es obligatoria");
                    tilDescripcion.setVisibility(View.VISIBLE);
                } else {
                    tilDescripcion.setVisibility(View.GONE);
                }
            }
        });

        btnGuardar.setOnClickListener(v -> {
            if (validarCampos()) {
                guardarEnFirestore();
            }
        });
    }

    private void cargarDatosTarea() {
        db.collection("tareas").document(taskId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Task tarea = doc.toObject(Task.class);
                        if (tarea != null) {
                            etTitulo.setText(tarea.getTitulo());
                            etDescripcion.setText(tarea.getDescripcion());
                            boolean completada = tarea.getEstado() == 1;
                            switchEstado.setChecked(completada);
                            tvEstadoLabel.setText(completada ? "Completada" : "Pendiente");
                        }
                    }
                });
    }

    private boolean validarCampos() {
        String titulo      = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty()) {
            tilTitulo.setText("El título es obligatorio");
            tilTitulo.setVisibility(View.VISIBLE);
            etTitulo.requestFocus();
            return false;
        }
        if (titulo.length() < 3) {
            tilTitulo.setText("Mínimo 3 caracteres");
            tilTitulo.setVisibility(View.VISIBLE);
            etTitulo.requestFocus();
            return false;
        }
        if (descripcion.isEmpty()) {
            tilDescripcion.setText("La descripción es obligatoria");
            tilDescripcion.setVisibility(View.VISIBLE);
            etDescripcion.requestFocus();
            return false;
        }
        return true;
    }

    private void guardarEnFirestore() {
        String titulo      = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        int    estado      = switchEstado.isChecked() ? 1 : 0;

        // Prevenir doble envío
        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando…");

        if (!esModoEdicion) {
            // CREATE
            db.collection("tareas")
                    .add(new Task(titulo, descripcion, estado))
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "✅ Tarea creada", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        restaurarBoton();
                    });
        } else {
            // UPDATE
            Map<String, Object> datos = new HashMap<>();
            datos.put("titulo",      titulo);
            datos.put("descripcion", descripcion);
            datos.put("estado",      estado);

            db.collection("tareas").document(taskId)
                    .update(datos)
                    .addOnSuccessListener(v -> {
                        Toast.makeText(this, "✅ Tarea actualizada", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        restaurarBoton();
                    });
        }
    }

    private void restaurarBoton() {
        btnGuardar.setEnabled(true);
        btnGuardar.setText(esModoEdicion ? "Actualizar" : "Guardar");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}