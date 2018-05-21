package com.eliot.ltq.ltquest;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static MapApi mapApi;

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://maps.googleapis.com/") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        mapApi = retrofit.create(MapApi.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public static MapApi getApi() {
        if(mapApi == null){
           Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://maps.googleapis.com/") //Базовая часть адреса
                    .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                    .build();
            mapApi = retrofit.create(MapApi.class); //Создаем объект, при помощи которого будем выполнять запросы

        }
        return mapApi;
    }
}
