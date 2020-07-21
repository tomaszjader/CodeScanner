package pl.tomaszjader.codescanner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CodeChecker {

    private String password;

    public CodeChecker(String password) {
        this.password = password;
    }

    public boolean checkCodeValid() {
        return this.password.length() >= 3;
    }

    public String send() {

        ExecutorService ex = Executors.newSingleThreadExecutor();
        Future<String> futureResult = ex.submit(new SendRequestThread(password));
        String result = null;
        try {
            result = futureResult.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;

    }
}
