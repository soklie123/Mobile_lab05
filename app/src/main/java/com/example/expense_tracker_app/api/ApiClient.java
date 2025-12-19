package com.example.expense_tracker_app.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://expense-tracker-db-one.vercel.app/";
    private static Retrofit retrofit = null;

    // This code goes inside ApiClient.java

    public static class ISO8601DateAdapter extends TypeAdapter<Date> {
        // These are the two formats your app needs to understand.
        private final SimpleDateFormat formatWithMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        private final SimpleDateFormat formatWithoutMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

        public ISO8601DateAdapter() {
            formatWithMillis.setTimeZone(TimeZone.getTimeZone("UTC"));
            formatWithoutMillis.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override
        public void write(JsonWriter out, Date date) throws IOException {
            if (date == null) {
                out.nullValue();
            } else {
                // Always write in the standard format without milliseconds.
                out.value(formatWithoutMillis.format(date));
            }
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateString = in.nextString();

            // First, try parsing with milliseconds.
            try {
                return formatWithMillis.parse(dateString);
            } catch (ParseException e) {
                // If that fails, try parsing without milliseconds.
                try {
                    return formatWithoutMillis.parse(dateString);
                } catch (ParseException e2) {
                    // If both fail, the format is truly invalid.
                    throw new IOException("Failed to parse date: " + dateString, e2);
                }
            }
        }
    }


    public static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new ISO8601DateAdapter())
                    .create();

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        // THIS IS THE CORRECT ID FROM YOUR POSTMAN
                        .header("X-DB-NAME", "8b722b75-4a75-468b-8175-759a0e5a119b")
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            httpClient.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
