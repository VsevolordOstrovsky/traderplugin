package lordostrov.traderplugin.coins;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class ManageCoin {

    private static final String API_BASE_URL = "https://api.bybit.com"; // Используйте api.bybit.com для mainnet
    private static final String ENDPOINT = "/v5/market/kline";

    private final String INTERVAL = "15";
    private final int LIMIT = 1;

    private String symbol;
    private String category = "spot";

    public ManageCoin(String symbol, String category) {
        this.symbol = symbol;
        this.category = category;
    }

    ManageCoin(String symbol) {
        this.symbol = symbol;
    }

    public String getMarkPriceKline() throws IOException {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(API_BASE_URL + ENDPOINT)).newBuilder()
                .addQueryParameter("category", category)
                .addQueryParameter("symbol", symbol)
                .addQueryParameter("interval", INTERVAL)
                .addQueryParameter("limit", String.valueOf(LIMIT));

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return Objects.requireNonNull(response.body()).string();
            } else {
                System.err.println("API call failed: " + response.code() + " - " + response.message());
                return null;
            }
        }
    }
}
