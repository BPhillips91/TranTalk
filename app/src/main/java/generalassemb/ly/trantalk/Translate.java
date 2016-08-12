package generalassemb.ly.trantalk;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by brendan on 7/29/16.
 */
public class Translate {

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final String TRANSLATE_API_KEY = "AIzaSyD4jOJ10geXa89nhbXBQZFPYHcGc0sq_dg&q=";
    static String url = "https://www.googleapis.com/language/translate/v2?key=";
    private static String messageKey;
    private static long timestamp;
    private static String user = auth.getCurrentUser().getDisplayName();
    OkHttpClient client = new OkHttpClient();

    public static void translateToSpanish(String Message, String key,long time) {
        messageKey = key;
        timestamp = time;
        String toTranslate = Message.replace(" ", "%20");
        String source = "&source=en";
        String target = "&target=es";
        new SpanishTranslateTask().execute(url + TRANSLATE_API_KEY + toTranslate + source + target);
    }

    public static void translateToEnglish(String message, String key, long time) {
        messageKey = key;
        timestamp = time;
        String toTranslate = message.replace(" ", "%20");
        String source = "&source=es";
        String target = "&target=en";
        new EnglishTranslateTask().execute(url + TRANSLATE_API_KEY + toTranslate + source + target);
    }

    private String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        Log.d("RESPONSE", response.body().toString());
        return response.body().string();
    }

    private static String downloadUrl(String url) throws IOException, JSONException {
        InputStream inputStream = null;
        try {
            URL nativeUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) nativeUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            inputStream = connection.getInputStream();
            return readInput(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

    }

    private static String readInput(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String read;
        while ((read = bufferedReader.readLine()) != null) {
            stringBuilder.append(read);
        }
        return stringBuilder.toString();
    }

    public static class SpanishTranslateTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                String json = downloadUrl(urls[0]);
                return parseToSpanish(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("TAG", "Translate response = " + s);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("user",user);
            map.put("message",s);
            map.put("timestamp", timestamp);

            DatabaseReference spanish = FirebaseDatabase.getInstance().getReference("spanish").child(HomeActivity.chatRoom);
            spanish.child(messageKey).setValue(map);
        }
    }

    public static class EnglishTranslateTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                String json = downloadUrl(urls[0]);
                return parseToEnglish(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("user",user);
            map.put("message",s);
            map.put("timestamp", timestamp);
            DatabaseReference english = FirebaseDatabase.getInstance().getReference("english").child(HomeActivity.chatRoom);
            english.child(messageKey).setValue(map);
            Log.d("TAG", "Translate response = " + s);
        }
    }

    private static String parseToSpanish(String jsonToParse) throws JSONException {
        String message = "";
        JSONObject dataObject = new JSONObject(jsonToParse).getJSONObject("data");
        JSONArray translationsArray = dataObject.getJSONArray("translations");
        for (int i = 0; i < translationsArray.length(); i++) {
            JSONObject translated = translationsArray.getJSONObject(i);
            message = translated.getString("translatedText");
        }
        return message;
    }

    private static String parseToEnglish(String jsonToParse) throws JSONException {
        String message = "";
        JSONObject dataObject = new JSONObject(jsonToParse).getJSONObject("data");
        JSONArray translationsArray = dataObject.getJSONArray("translations");
        for (int i = 0; i < translationsArray.length(); i++) {
            JSONObject translated = translationsArray.getJSONObject(i);
            message = translated.getString("translatedText");
        }
        return message;
    }
}
