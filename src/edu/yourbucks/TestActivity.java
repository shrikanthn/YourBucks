package edu.yourbucks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class TestActivity extends Activity {

	private static final String[] COUNTRIES = new String[] {
		"Belgium", "France", "Italy", "Germany", "Spain"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Reset...");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteStockSymbol);
		textView.setAdapter(new CustomAutoCompleteAdaptor(this, textView.getText().toString()));

		Button button = (Button) findViewById(R.id.btnHit);
		button.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Log.i("test", "it came here");
				EditText txtObj = (EditText)findViewById(R.id.editText1);
				String editTextStr = txtObj.getText().toString(); 
				if(editTextStr.trim().length() < 1) {
					alertDialog.setMessage("You have not extered anything");
				} else {
					alertDialog.setMessage("Whats up " + editTextStr);
				}
				alertDialog.show();
			}
		});
		
		
		Button button2 = (Button) findViewById(R.id.btnFetch);
		button2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				getConnection();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}
	
	public void getConnection(){

		EditText txtObj = (EditText)findViewById(R.id.editText1);
		String editTextStr = txtObj.getText().toString();
		String url;
		if(editTextStr.length() < 1) {
			url = "http://autoc.finance.yahoo.com/autoc?query=goog&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
		} else {
			url = "http://autoc.finance.yahoo.com/autoc?query="+editTextStr+"&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
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
	             
	             ListView myListView = (ListView)findViewById(R.id.listView1);
	             ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
	             myListView.setAdapter(adapter);

	         }// <-- end IF          
	     }catch (Exception e){
	    	 e.printStackTrace();
	     }
	}

}
