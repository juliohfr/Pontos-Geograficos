package com.example.pontosgeograficos;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String menu [] = new String[] {"Minha casa na cidade natal", "Minha casa em Viçosa", "Meu departamento", "Fechar aplicação"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu);
        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent it = new Intent(this, GeographicPoint.class);
        String option = l.getItemAtPosition(position).toString();

        if(position == 0 || position == 1 || position == 2){
            Toast.makeText(this, option, Toast.LENGTH_SHORT).show();
            startActivity(it);
        }else{
            finish();
        }
    }
}