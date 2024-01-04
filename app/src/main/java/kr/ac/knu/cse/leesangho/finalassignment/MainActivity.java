package kr.ac.knu.cse.leesangho.finalassignment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        textView2 = findViewById(R.id.textView2);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryName = editText.getText().toString();

                new CountryCodeTask().execute(countryName);
            }
        });
    }

    private class CountryCodeTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            String apiUrlTemplate = "https://apis.data.go.kr/1262000/CountryCodeService2/getCountryCodeList2?ServiceKey=PC55ku1KN9RjkN0XCslVwQVAOwMv7gSR7Ksp7UuESCFZ75iZENJftdwK555JFQoNE7RyIwdLDK%2BUDBYGxv6C4g%3D%3D&type=json&pageNo=%s";

            List<String> results = new ArrayList<>();

            for (int pageNo = 1; pageNo <= 24; pageNo++) {
                try {
                    String apiUrl = apiUrlTemplate.replace("%s", String.valueOf(pageNo));
                    URL url = new URL(apiUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    StringBuilder pageResult = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        pageResult.append(line);
                    }

                    results.add(pageResult.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return results;
        }

        @Override
        protected void onPostExecute(List<String> results) {
            super.onPostExecute(results);

            for (String result : results) {
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray dataArray = jsonObject.getJSONArray("data");

                        String userInputCountryName = editText.getText().toString().toUpperCase();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject countryObject = dataArray.getJSONObject(i);

                            String countryName = countryObject.getString("country_eng_nm").toUpperCase(Locale.ROOT);
                            String countryName2 = countryObject.getString("country_nm");

                            if (userInputCountryName.equals(countryName) || userInputCountryName.equals(countryName2)) {
                                String countryCodeId = countryObject.optString("iso_num");

                                if (!countryCodeId.isEmpty()) {
                                    textView2.setText("국가코드 ID: " + countryCodeId);
                                    return;
                                }
                            }
                        }
                        textView2.setText("해당 국가 코드를 찾을 수 없습니다.");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



}
