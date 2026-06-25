package com.tuapp.gestortareas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.tuapp.gestortareas.R;
import com.tuapp.gestortareas.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onTaskClick(Task tarea);
        void onDeleteClick(Task tarea);
        void onToggleEstado(Task tarea);
    }

    private Context context;
    private List<Task> listaTareas;
    private OnTaskClickListener listener;

    public TaskAdapter(Context context, List<Task> listaTareas, OnTaskClickListener listener) {
        this.context = context;
        this.listaTareas = listaTareas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task tarea = listaTareas.get(position);

        holder.tvTitulo.setText(tarea.getTitulo());
        holder.tvDescripcion.setText(tarea.getDescripcion());

        holder.switchEstado.setOnCheckedChangeListener(null);
        holder.switchEstado.setChecked(tarea.isCompletada());
        holder.tvEstado.setText(tarea.isCompletada() ? "Completada" : "Pendiente");

        int colorEstado = tarea.isCompletada()
                ? context.getColor(R.color.estado_completada)
                : context.getColor(R.color.estado_pendiente);
        holder.tvEstado.setTextColor(colorEstado);

        holder.switchEstado.setOnCheckedChangeListener((btn, isChecked) -> {
            tarea.setEstado(isChecked ? 1 : 0);
            holder.tvEstado.setText(isChecked ? "Completada" : "Pendiente");
            holder.tvEstado.setTextColor(isChecked
                    ? context.getColor(R.color.estado_completada)
                    : context.getColor(R.color.estado_pendiente));
            listener.onToggleEstado(tarea);
        });

        // Clic en la tarjeta → editar
        holder.itemView.setOnClickListener(v -> listener.onTaskClick(tarea));

        // Botón editar → editar
        holder.btnEditar.setOnClickListener(v -> listener.onTaskClick(tarea));

        // Botón eliminar → eliminar
        holder.btnEliminar.setOnClickListener(v -> listener.onDeleteClick(tarea));
    }

    @Override
    public int getItemCount() { return listaTareas.size(); }

    public void actualizarLista(List<Task> nuevaLista) {
        listaTareas.clear();
        listaTareas.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvEstado;
        SwitchMaterial switchEstado;
        ImageButton btnEliminar, btnEditar;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo      = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvEstado      = itemView.findViewById(R.id.tvEstado);
            switchEstado  = itemView.findViewById(R.id.switchEstado);
            btnEliminar   = itemView.findViewById(R.id.btnEliminar);
            btnEditar     = itemView.findViewById(R.id.btnEditar);
        }
    }
}