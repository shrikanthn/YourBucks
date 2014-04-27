package edu.yourbucks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class CustomAutoCompleteAdaptor extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList;

    public CustomAutoCompleteAdaptor(Context context, String nameFilter) {
    	super(context, android.R.layout.simple_dropdown_item_1line);
    	resultList = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = getData(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }
    
    
    private ArrayList<String> getData(String term){

		String url;
		if(term.length() < 1) {
			url = "http://autoc.finance.yahoo.com/autoc?query=goog&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
		} else {
			url = "http://autoc.finance.yahoo.com/autoc?query="+term+"&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
		}
		Log.i("url", url);
	    
	     HttpClient httpClient = new DefaultHttpClient();

	     try{
	         HttpGet httpGet = new HttpGet(url);
	         HttpResponse httpResponse = httpClient.execute(httpGet);
	         HttpEntity httpEntity = httpResponse.getEntity();

	         if(httpEntity != null){
	        	 Log.i("response", "got it");
	             InputStream inputStream = httpEntity.getContent();
	             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	             StringBuilder stringBuilder = new StringBuilder();
	             String line = bufferedReader.readLine();
	             while(line != null){
	                 stringBuilder.append(line);
	                 line = bufferedReader.readLine();
	             }
	             bufferedReader.close();
	             
	             JSONObject jsonObject = new JSONObject(stringBuilder.toString().substring(stringBuilder.indexOf("(") + 1, stringBuilder.lastIndexOf(")")));
	             JSONArray quote = jsonObject.getJSONObject("ResultSet").getJSONArray("Result");
	             ArrayList<String> list = new ArrayList<String>();
	             for(int i=0 ; i<quote.length(); i++) {
	            	 JSONObject obj = quote.getJSONObject(i);
	            	 list.add(obj.getString("name") + "(" + obj.getString("symbol") + ")");
	             }
	             Log.i("total keys", " - " + list.size());
	             return list;
	         }
	     }catch (Exception e){
	    	 e.printStackTrace();
	     }
	     return new ArrayList<String>();
	}
}