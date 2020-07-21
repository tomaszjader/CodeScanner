package pl.tomaszjader.codescanner;

import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendRequestThread implements Callable<String> {

    private String password;

    public SendRequestThread(String password) {
        this.password = password;
    }

    @Override
    public String call() {
        OkHttpClient httpClient = new OkHttpClient();
        HttpUrl httpUrl = HttpUrl.parse("https://apps-up.pl/api/checkCard")
                .newBuilder()
                .addQueryParameter("devId", "123321")
                .addQueryParameter("cardId", password)
                .build();
        System.out.println(httpUrl);
        Request request = new Request.Builder().url(httpUrl).get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            return response.code() == 200 ? response.body().string() : null;
        } catch (IOException e) {
            return null;
        }
    }

}
