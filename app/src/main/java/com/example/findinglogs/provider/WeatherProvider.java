package com.example.findinglogs.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WeatherProvider extends ContentProvider {

    private static final String TAG = "WeatherProvider";

    // Endereço único do provider — como uma URL interna do Android
    public static final String AUTHORITY = "com.example.findinglogs.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/weather");

    // Nomes das colunas que o app secundário vai receber
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_TEMP = "temp";
    public static final String COLUMN_DESCRIPTION = "description";

    // Código interno para identificar a rota /weather
    private static final int CODE_WEATHER = 1;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "weather", CODE_WEATHER);
    }

    // Lista em memória que guarda os dados mais recentes
    private static final List<String[]> dadosClima = new ArrayList<>();

    // Chamado pelo MainViewModel para atualizar os dados disponíveis
    public static void atualizarDados(List<String[]> novosDados) {
        dadosClima.clear();
        dadosClima.addAll(novosDados);
        Log.d(TAG, "atualizarDados: " + dadosClima.size() + " cidades disponíveis no provider");
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: provider criado");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        if (uriMatcher.match(uri) != CODE_WEATHER) {
            throw new IllegalArgumentException("URI desconhecida: " + uri);
        }

        Log.d(TAG, "query: app externo consultou o provider, retornando "
                + dadosClima.size() + " cidades");

        MatrixCursor cursor = new MatrixCursor(
                new String[]{COLUMN_CITY, COLUMN_TEMP, COLUMN_DESCRIPTION}
        );

        for (String[] linha : dadosClima) {
            cursor.addRow(linha);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) { return null; }

    @Override
    public Uri insert(Uri uri, ContentValues values) { return null; }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) { return 0; }
}