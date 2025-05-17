package com.example.pontosgeograficos;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Report extends ListActivity {

    private final ArrayList<Integer> idLogs = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ArrayList<String> logs = new ArrayList<>();

        final Cursor c = BancoDadosSingleton.getInstance().buscar(
                "Logs",
                new String[]{"id", "msg", "timestamp"},
                "",
                "timestamp ASC"
        );

        while (c.moveToNext()) {
            @SuppressLint("Range") final int idLog = c.getInt(c.getColumnIndex("id"));
            @SuppressLint("Range") final String msg = c.getString(c.getColumnIndex("msg"));
            @SuppressLint("Range") final String ts = c.getString(c.getColumnIndex("timestamp"));

            logs.add(msg + " - " + ts);
            this.idLogs.add(idLog);
        }

        c.close();
        BancoDadosSingleton.getInstance().fechar();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, logs);
        this.setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final int logId = this.idLogs.get(position);

        final String sql = "SELECT L.latitude, L.longitude FROM Logs G " +
                "INNER JOIN Location L ON G.id_location = L.id " +
                "WHERE G.id = " + logId;

        final Cursor c = BancoDadosSingleton.getInstance().rawQuery(sql);

        if (c.moveToFirst()) {
            @SuppressLint("Range") final double lat = c.getDouble(c.getColumnIndex("latitude"));
            @SuppressLint("Range") final double lng = c.getDouble(c.getColumnIndex("longitude"));

            Toast.makeText(this, "Latitude: " + lat + "\nLongitude: " + lng, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Local não encontrado", Toast.LENGTH_SHORT).show();
        }

        c.close();
        BancoDadosSingleton.getInstance().fechar();
    }
}
