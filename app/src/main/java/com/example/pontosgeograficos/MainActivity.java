package com.example.pontosgeograficos;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String[] menu = new String[]{"Minha casa na cidade natal", "Minha casa em Viçosa", "Meu departamento", "Relatório", "Fechar aplicação"};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu);
        this.setListAdapter(arrayAdapter);
    }

    @SuppressLint("Range")
    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final Intent it = new Intent(this, GeographicPoint.class);
        final String option = l.getItemAtPosition(position).toString();

        final Bundle params = new Bundle();
        params.putString("destination", option);

        it.putExtras(params);

        if (position == 0 || position == 1 || position == 2) {
            Toast.makeText(this, option, Toast.LENGTH_SHORT).show();

            String msg = "";
            String description = "";

            switch (position) {
                case 0:
                    msg = "Timóteo";
                    description = "Minha casa em Timóteo";
                    break;
                case 1:
                    msg = "Viçosa";
                    description = "Meu apartamento em Viçosa";
                    break;
                case 2:
                    msg = "DPI";
                    description = "DPI - UFV";
                    break;
            }

            int id_location = -1;

            final BancoDadosSingleton db = BancoDadosSingleton.getInstance();
            final Cursor c = db.buscar("Location", new String[]{"id"}, "descricao = '" + description + "'", "");

            if (c.moveToFirst()) {
                id_location = c.getInt(c.getColumnIndex("id"));
            }

            c.close();
            db.fechar();

            if (id_location != -1) {
                final ContentValues values = new ContentValues();
                values.put("msg", msg);

                final String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
                values.put("timestamp", timestamp);

                values.put("id_location", id_location);

                BancoDadosSingleton.getInstance().inserir("Logs", values);
                BancoDadosSingleton.getInstance().fechar();
            }

            this.startActivity(it);

        } else if (position == 3) {

            final Intent newIt = new Intent(this, Report.class);
            this.startActivity(newIt);

        } else {
            this.finish();
        }
    }
}