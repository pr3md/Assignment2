package pr3md.weatherapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.content.Intent;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetWeather extends AppCompatActivity {
    String sourceText;
    TextView outputTextView;
    Context mContext;

    WebView weatherInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getBaseContext();
        setContentView(R.layout.activity_getweather);

        outputTextView = (TextView) findViewById(R.id.txt_Response);
    }

    public void logout(View v) {
        Intent redirect = new Intent(GetWeather.this, LoginActivity.class);
        startActivity(redirect);
    }

    private void hideKeyboard(View editableView) {
        InputMethodManager imm = (InputMethodManager)mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editableView.getWindowToken(), 0);
    }

    public void translateText(View v) {
        TextView sourceTextView = (TextView) findViewById(R.id.zipCode);

        sourceText = sourceTextView.getText().toString();

        String getURL = "http://api.openweathermap.org/data/2.5/weather?zip="+sourceText+",us&APPID=097d1729477350f34a9e56facb2b19f3";

        OkHttpClient client = new OkHttpClient();
        try {

            Request request = new Request.Builder()
                    .url(getURL)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final JSONObject jsonResult;
                    final String result = response.body().string();

                    try {
                        jsonResult = new JSONObject(result);
                        final JSONArray weatherArray = jsonResult.getJSONArray("weather");
                        String cloudStatus = "";
                        String wicon = "";
                        for(int i=0;i<weatherArray.length();i++) {
                            final JSONObject childWeatherObject = weatherArray.getJSONObject(i);
                            cloudStatus = childWeatherObject.getString("description");
                            wicon = childWeatherObject.getString("icon");
                        }

                        final JSONObject convertedTextArray = jsonResult.getJSONObject("main");

                        final String areaName = jsonResult.getString("name");

                        final String CloudsType = cloudStatus;
                        final String WeatherIcon = "http://openweathermap.org/img/w/"+wicon+".png";

                        final String convertedText = convertedTextArray.getString("temp");

                        final double temperature = Double.valueOf(convertedText);

                        final double finalTemperature = (temperature - 273.15) * 1.8 + 32;
                        final double roundedFinalTemperature = (double) Math.round(finalTemperature * 100)/100;
                        final String finalResult = "Weather in "+areaName+" is "+Double.toString(roundedFinalTemperature)+"°F with "+CloudsType+".";


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideKeyboard(outputTextView);
                                outputTextView.setText(finalResult);

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


        } catch (Exception ex) {
            outputTextView.setText(ex.getMessage());

        }

    }

}


