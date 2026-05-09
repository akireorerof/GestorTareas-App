package com.tuapp.gestortareas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuapp.gestortareas.R;
import com.tuapp.gestortareas.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> listaTareas;
    private Context context;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task tarea);
        void onDeleteClick(Task tarea);
        void onToggleEstado(Task tarea);
    }

    public TaskAdapter(Context context, List<Task> listaTareas, OnTaskClickListener listener) {
        this.context = context;
        this.listaTareas = listaTareas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task tarea = listaTareas.get(position);

        holder.tvTitulo.setText(tarea.getTitulo());
        holder.tvDescripcion.setText(tarea.getDescripcion());

        if (tarea.isCompletada()) {
            holder.imgEstado.setImageResource(android.R.drawable.checkbox_on_background);
            holder.tvEstadoTexto.setText("Completada");
            holder.tvTitulo.setAlpha(0.5f);
        } else {
            holder.imgEstado.setImageResource(android.R.drawable.checkbox_off_background);
            holder.tvEstadoTexto.setText("Pendiente");
            holder.tvTitulo.setAlpha(1.0f);
        }

        holder.itemView.setOnClickListener(v -> listener.onTaskClick(tarea));
        holder.btnEliminar.setOnClickListener(v -> listener.onDeleteClick(tarea));
        holder.imgEstado.setOnClickListener(v -> listener.onToggleEstado(tarea));
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    public void actualizarLista(List<Task> nuevaLista) {
        this.listaTareas = nuevaLista;
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEstado;
        TextView tvTitulo, tvDescripcion, tvEstadoTexto;
        ImageButton btnEliminar;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEstado = itemView.findViewById(R.id.imgEstado);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvEstadoTexto = itemView.findViewById(R.id.tvEstadoTexto);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}