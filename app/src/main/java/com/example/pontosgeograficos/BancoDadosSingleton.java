package com.example.pontosgeograficos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public final class BancoDadosSingleton {
    private static BancoDadosSingleton INSTANCE;
    private final String LOCALE_POINTS = "GeoApp.db";
    private final String[] SCRIPT_CREATE = new String[]{
            "CREATE TABLE IF NOT EXISTS Location (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "descricao TEXT, latitude REAL, longitude REAL);",

            "CREATE TABLE IF NOT EXISTS Logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "msg TEXT, timestamp TEXT, id_location INTEGER);",

            "INSERT INTO Location (descricao, latitude, longitude) VALUES ('Meu apartamento em Viçosa', -20.753170, -42.878656);",
            "INSERT INTO Location (descricao, latitude, longitude) VALUES ('Minha casa em Timóteo', -19.545576, -42.656434);",
            "INSERT INTO Location (descricao, latitude, longitude) VALUES ('DPI - UFV', -20.764978, -42.868461);"
    };
    private SQLiteDatabase db;

    private BancoDadosSingleton() {
        final Context ctx = MyApp.getAppContext();
        this.db = ctx.openOrCreateDatabase(this.LOCALE_POINTS, Context.MODE_PRIVATE, null);

        final Cursor c = this.buscar("sqlite_master", null, "type = 'table'", "");

        if (c.getCount() == 1) {
            for (int i = 0; i < this.SCRIPT_CREATE.length; i++) {
                this.db.execSQL(this.SCRIPT_CREATE[i]);
            }
            Log.i("BANCO_DADOS", "Tabelas criadas e populadas.");
        }

        c.close();
        Log.i("BANCO_DADOS", "Conexão aberta.");
    }

    public static BancoDadosSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BancoDadosSingleton();
        }
        INSTANCE.abrir();
        return INSTANCE;
    }

    private void abrir() {
        final Context ctx = MyApp.getAppContext();
        if (!this.db.isOpen()) {
            this.db = ctx.openOrCreateDatabase(this.LOCALE_POINTS, Context.MODE_PRIVATE, null);
            Log.i("BANCO_DADOS", "Conexão aberta.");
        } else {
            Log.i("BANCO_DADOS", "Conexão com o banco já estava aberta.");
        }
    }

    public void fechar() {
        if (this.db != null && this.db.isOpen()) {
            this.db.close();
            Log.i("BANCO_DADOS", "Conexão encerrada.");
        }
    }

    public long inserir(final String tabela, final ContentValues valores) {
        final long id = this.db.insert(tabela, null, valores);
        Log.i("BANCO_DADOS", "Registro com o id [" + id + "] cadastrado.");
        return id;
    }

    public int atualizar(final String tabela, final ContentValues valores, final String where) {
        final int count = this.db.update(tabela, valores, where, null);
        Log.i("BANCO_DADOS", "[" + count + "] registros atualizados.");
        return count;
    }

    public int deletar(final String tabela, final String where) {
        final int count = this.db.delete(tabela, where, null);
        Log.i("BANCO_DADOS", "[" + count + "] registros deletados.");
        return count;
    }

    public Cursor rawQuery(final String sql) {
        return this.db.rawQuery(sql, null);
    }

    public Cursor buscar(final String tabela, final String[] colunas, final String where, final String orderBy) {
        final Cursor c;
        if (!where.equals("")) {
            c = this.db.query(tabela, colunas, where, null, null, null, orderBy);
        } else {
            c = this.db.query(tabela, colunas, null, null, null, null, orderBy);
        }

        Log.i("BANCO_DADOS", "Busca realizada e [" + c.getCount() + "] registros retornados.");
        return c;
    }
}
