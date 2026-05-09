package com.tuapp.gestortareas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tuapp.gestortareas.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public TaskDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertarTarea(Task tarea) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITULO, tarea.getTitulo());
        values.put(DatabaseHelper.COLUMN_DESCRIPCION, tarea.getDescripcion());
        values.put(DatabaseHelper.COLUMN_ESTADO, tarea.getEstado());
        return database.insert(DatabaseHelper.TABLE_TAREAS, null, values);
    }

    public List<Task> obtenerTodasLasTareas() {
        List<Task> listaTareas = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_TAREAS +
                " ORDER BY " + DatabaseHelper.COLUMN_ID + " DESC";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Task tarea = new Task();
                tarea.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
                tarea.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITULO)));
                tarea.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION)));
                tarea.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO)));
                listaTareas.add(tarea);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaTareas;
    }

    public Task obtenerTareaPorId(int id) {
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_TAREAS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        Task tarea = null;
        if (cursor != null && cursor.moveToFirst()) {
            tarea = new Task();
            tarea.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
            tarea.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITULO)));
            tarea.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION)));
            tarea.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ESTADO)));
            cursor.close();
        }
        return tarea;
    }

    public int actualizarTarea(Task tarea) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITULO, tarea.getTitulo());
        values.put(DatabaseHelper.COLUMN_DESCRIPCION, tarea.getDescripcion());
        values.put(DatabaseHelper.COLUMN_ESTADO, tarea.getEstado());

        return database.update(
                DatabaseHelper.TABLE_TAREAS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(tarea.getId())}
        );
    }

    public void eliminarTarea(int id) {
        database.delete(
                DatabaseHelper.TABLE_TAREAS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
}