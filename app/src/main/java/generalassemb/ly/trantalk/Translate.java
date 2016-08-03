package generalassemb.ly.trantalk;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by brendan on 7/29/16.
 */
public class Translate {

    //TODO: Turn class into a service to do in background


    private static final String TRANSLATE_API_KEY="AIzaSyD4jOJ10geXa89nhbXBQZFPYHcGc0sq_dg";
    String url = "https://www.googleapis.com/language/translate/v2?key=";
    String sourceLan="&source=en";
    String targetLan="&target=es";
    String message= "&q=";
    String query= url+TRANSLATE_API_KEY+message+sourceLan+targetLan;


    OkHttpClient client = new OkHttpClient();


    private String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        Log.d("RESPONSE",response.body().toString());
        return response.body().string();



    }
    private String downloadUrl(String url) throws IOException, JSONException {

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

    private String readInput(InputStream inputStream) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String read;

        while ((read = bufferedReader.readLine()) != null) {
            stringBuilder.append(read);
        }
        return stringBuilder.toString();
    }




    private class DownloadUrlTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            try {
                String json = downloadUrl(urls[0]);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d("TAG", "Translate response = "+s);


        }
    }


}
