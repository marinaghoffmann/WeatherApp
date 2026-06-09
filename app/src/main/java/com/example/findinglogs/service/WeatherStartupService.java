package com.example.findinglogs.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.findinglogs.R;
import com.example.findinglogs.model.repo.Repository;
import com.example.findinglogs.model.repo.remote.api.WeatherCallback;
import com.example.findinglogs.model.model.Weather;
import com.example.findinglogs.model.util.Utils;
import com.example.findinglogs.provider.WeatherProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherStartupService extends Service {

    private static final String TAG = "WeatherStartupService";
    private static final String CHANNEL_ID = "weather_startup_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service criado");
        criarCanalDeNotificacao();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: iniciando busca do clima após boot");

        // Notificação obrigatória para Foreground Service
        Notification notificacao = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("WeatherApp")
                .setContentText("Atualizando dados do clima...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(NOTIFICATION_ID, notificacao);

        buscarClima();

        return START_NOT_STICKY;
    }

    private void buscarClima() {
        Repository repository = new Repository(getApplication());
        HashMap<String, String> localizations = repository.getLocalizations();

        int total = localizations.size();
        if (total == 0) {
            Log.d(TAG, "buscarClima: nenhuma cidade cadastrada, encerrando service");
            stopSelf();
            return;
        }

        Map<String, Weather> resultados = new HashMap<>();
        int[] respostas = {0};

        for (String latlon : localizations.values()) {
            repository.retrieveForecast(latlon, new WeatherCallback() {
                @Override
                public void onSuccess(Weather weather) {
                    resultados.put(weather.getName(), weather);
                    respostas[0]++;
                    if (respostas[0] == total) {
                        finalizarComDados(new ArrayList<>(resultados.values()));
                    }
                }

                @Override
                public void onFailure(String error) {
                    respostas[0]++;
                    Log.e(TAG, "onFailure: erro ao buscar cidade: " + error);
                    if (respostas[0] == total) {
                        finalizarComDados(new ArrayList<>(resultados.values()));
                    }
                }
            });
        }
    }

    private void finalizarComDados(List<Weather> cidades) {
        // Alimenta o ContentProvider com os dados buscados
        List<String[]> dadosProvider = new ArrayList<>();
        for (Weather w : cidades) {
            String cidade = w.getName();
            String temp = Utils.getCelsiusTemperatureFromKevin(w.getMain().getTemp());
            String descricao = (w.getWeather() != null && !w.getWeather().isEmpty())
                    ? w.getWeather().get(0).getDescription()
                    : "sem descrição";
            dadosProvider.add(new String[]{cidade, temp, descricao});
        }

        WeatherProvider.atualizarDados(dadosProvider);
        Log.d(TAG, "finalizarComDados: " + cidades.size() + " cidades atualizadas no provider");

        // Atualiza a notificação com o resultado
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notificacaoFinal = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("WeatherApp")
                .setContentText(cidades.size() + " cidades atualizadas com sucesso")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        manager.notify(NOTIFICATION_ID, notificacaoFinal);

        stopSelf();
    }

    private void criarCanalDeNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Atualização do clima",
                    NotificationManager.IMPORTANCE_LOW
            );
            canal.setDescription("Canal usado para atualizar o clima após o boot");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: service encerrado");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}