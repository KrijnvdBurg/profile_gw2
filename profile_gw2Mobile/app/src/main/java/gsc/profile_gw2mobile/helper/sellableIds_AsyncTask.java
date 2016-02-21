package gsc.profile_gw2mobile.helper;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by Krijn on 8-5-2015.
 */
public class sellableIds_AsyncTask extends AsyncTask<String, Void, String> {

    String bodyHtml;
    List<String> items;
    String[] apple;
    List<Integer> banana;
    Context context;
    private sellableIdsFilter sellableIdsFilter;

    public sellableIds_AsyncTask(Context context) {
        this.context = context;
    }

    public void setSellableIdsFilter(sellableIdsFilter sellableIdsFilter) {
        this.sellableIdsFilter = sellableIdsFilter;
    }


    @Override
    protected String doInBackground(String... params) {
        StringBuilder content = new StringBuilder();
        try{
            URL url = new URL(params[0]);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return content.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        this.sellableIdsFilter.sellableIdsFilter(result);
    }

    public interface sellableIdsFilter {
        public void sellableIdsFilter(String result);
    }


}