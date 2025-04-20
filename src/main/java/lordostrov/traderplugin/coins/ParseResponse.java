package lordostrov.traderplugin.coins;

import org.json.JSONArray;
import org.json.JSONObject;

public class ParseResponse {

    private String jsonResponse;
    private JSONObject jsonObject;
    private int retCode;
    private String retMsg;
    private JSONObject result;
    private JSONArray list;
    private JSONArray kline;
    public ParseResponse(String jsonResponse) {
        init(jsonResponse);
    }

    public void init(String jsonResponse){
        this.jsonResponse = jsonResponse;
        try{
            jsonObject = new JSONObject(jsonResponse);
            retCode = jsonObject.getInt("retCode");
            retMsg = jsonObject.getString("retMsg");

            if (retCode == 0) {
                result = jsonObject.getJSONObject("result");
                list = result.getJSONArray("list");

                if (list.length() > 0) {
                    kline = list.getJSONArray(0);
                } else {
                    System.out.println("No kline data found for the specified parameters.");
                }

            }else {
                System.err.println("API Error: " + retCode + " - " + retMsg);
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getTimesTamp(){
        return kline.getString(0);
    }

    public String getOpen(){
        return kline.getString(1);
    }

    public String getHigh(){
        return kline.getString(2);
    }

    public String getLow(){
        return kline.getString(3);
    }

    public String getClose(){
        return kline.getString(4);
    }





    public static void parseAndPrintResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            int retCode = jsonObject.getInt("retCode"); // Получаем retCode как int
            String retMsg = jsonObject.getString("retMsg");

            if (retCode == 0) { // Сравниваем retCode с числом 0 (а не со строкой "0")
                JSONObject result = jsonObject.getJSONObject("result");
                JSONArray list = result.getJSONArray("list");

                if (list.length() > 0) {
                    JSONArray kline = list.getJSONArray(0);

                    String timestamp = kline.getString(0);
                    String open = kline.getString(1);
                    String high = kline.getString(2);
                    String low = kline.getString(3);
                    String close = kline.getString(4);


                    System.out.println("Kline Data:");
                    System.out.println("Timestamp (ms): " + timestamp);
                    System.out.println("Open: " + open);
                    System.out.println("High: " + high);
                    System.out.println("Low: " + low);
                    System.out.println("Close: " + close);


                } else {
                    System.out.println("No kline data found for the specified parameters.");
                }

            } else {
                System.err.println("API Error: " + retCode + " - " + retMsg);
            }

        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
