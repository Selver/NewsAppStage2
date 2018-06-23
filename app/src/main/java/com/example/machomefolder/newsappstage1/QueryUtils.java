package com.example.machomefolder.newsappstage1;
import android.util.Log;
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
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//helper methods
public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //constructor
    private QueryUtils() {
    }

    //Return new URL object from the given string URL
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    //Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            //handle the exception
            Log.e(LOG_TAG, "Problem with connection", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {

                inputStream.close();
            }
        }
        return jsonResponse;
    }

    public static List<Article> fetchArticleData(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);
        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of articles
        List<Article> articles = extractFeatureFromJson(jsonResponse);
        return articles;
    }

    //parsing json and show results
    public static ArrayList<Article> extractFeatureFromJson(String jsonResponse) {
        ArrayList<Article> articles = new ArrayList<>();
        try {
            JSONObject rootObject = new JSONObject(jsonResponse);
            JSONObject response = rootObject.getJSONObject("response");
            JSONArray resultsArray = response.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentObject = resultsArray.getJSONObject(i);
                String title = currentObject.getString("webTitle");
                String section = currentObject.getString("sectionName");
                String date = currentObject.getString("webPublicationDate");
                JSONObject fieldsObject = currentObject.getJSONObject("fields");
                String author = fieldsObject.getString("byline");
                String articleUrl = currentObject.getString("webUrl");
                String image = fieldsObject.getString("thumbnail");
                //format the date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'", Locale.UK);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date formattedDate = null;
                try {
                    formattedDate = sdf.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String dateFinal = formatDate(formattedDate);

                Article article = new Article(title, section, author, dateFinal, articleUrl, image);
                articles.add(article);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the article JSON results", e);
        }
        return articles;
    }

    //Convert the {@link InputStream} into a String which contains the whole JSON response from the server.
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String formatDate(Date date) {
        SimpleDateFormat newDateFormat = new SimpleDateFormat("MMM dd, yyyy  hh:mm", Locale.ENGLISH);
        newDateFormat.setTimeZone(TimeZone.getDefault());
        String formattedDate = newDateFormat.format(date);
        return formattedDate;
    }
}





