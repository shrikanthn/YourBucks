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

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NewsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		Intent intent = getIntent();
		ArrayList<String> links = new ArrayList<String>();
		ArrayList<String> title = new ArrayList<String>();

		JSONObject res = getData(intent.getStringExtra("term"));
		try {
			JSONObject newsList = res.getJSONObject("result").getJSONObject("News");
			if(newsList.has("Error")) {
				
				final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
				builder2.setMessage("No news fetched")
				.setCancelable(false)
				.setPositiveButton("Info", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				builder2.show();
				
			} else {
				JSONArray news = newsList.getJSONArray("Item");
				for(int i=0; i< news.length(); i++) {
					JSONObject row = news.getJSONObject(i);
					links.add(row.getString("Link"));
					title.add(row.getString("Title"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, title);
			ListView listView = (ListView)findViewById(R.id.listView1);
			listView.setAdapter(adaptor);
			
			final Intent intentSend = new Intent(this, WebViewActivity.class);

			final ArrayList<String> urls = links;
			OnItemClickListener listener = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {

					Log.i("url",urls.get(position));
					final String currentUrl = urls.get(position);
					
					final AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
					builder.setMessage("View News")
					.setCancelable(false)
					.setPositiveButton("View", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							intentSend.putExtra("url", currentUrl);
							startActivity(intentSend);
							
//							Uri uri = Uri.parse(currentUrl);
//							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//							startActivity(intent);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					builder.show();

				}
			};
			listView.setOnItemClickListener(listener);

		} catch (Exception e) {
			e.printStackTrace();
		}    







		//		if(res.has("stock") && res.optJSONObject("stock").has("Error")) {
		//			builder.setMessage("Could not fetch News");
		//			builder.show();
		//			return;
		//		}
		//		




		//		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_fruit,FRUITS));
		//		 
		//		ListView listView = getListView();
		//		listView.setTextFilterEnabled(true);
		// 
		//		listView.setOnItemClickListener(new OnItemClickListener() {
		//			public void onItemClick(AdapterView<?> parent, View view,
		//					int position, long id) {
		//			    // When clicked, show a toast with the TextView text
		//			    Toast.makeText(getApplicationContext(),
		//				((TextView) view).getText(), Toast.LENGTH_SHORT).show();
		//			}
		//		});

		//		TableLayout tl=(TableLayout)findViewById(R.id.tableLayout);
		//		try {
		//			JSONArray news = res.getJSONObject("result").getJSONObject("News").getJSONArray("Item");
		//			for(int i=0; i< news.length(); i++) {
		//				JSONObject row = news.getJSONObject(i);
		//				
		//				TableRow tr1 = new TableRow(this);
		//				tr1.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
		//				TextView textview = new TextView(this);
		//				textview.setText(Html.fromHtml("<a href='"+row.getString("Link")+"'>"+row.getString("Title")+"</a>"));
		//				textview.setMovementMethod(LinkMovementMethod.getInstance());
		//				tr1.addView(textview);
		//				tl.addView(tr1);
		//			}
		//
		//
		//			
		//		} catch (Exception e) {
		//			Log.e(e.getClass().toString(), e.getMessage());
		//		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news, menu);
		return true;
	}

	private JSONObject getData(String term){

		// clean the term
		//		if(term.indexOf(')') > 0 && term.indexOf('(') > 0) {
		//			int st = term.indexOf('(') + 1;
		//			int ed = term.indexOf(')');
		//			term = term.substring(st, ed);
		//			Log.i("search term", term);
		//		}
		if(term.indexOf(',') > 0) {
			int ed = term.indexOf(',');
			term = term.substring(0, ed);
			Log.i("search term", term);
		}

		String url = "http://cs-server.usc.edu:14797/examples/servlet/MyServlet?term=" + term;
		Log.i("url", url);

		HttpClient httpClient = new DefaultHttpClient();
		try{
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();

			if(httpEntity != null){
				Log.i("response", "got News");
				InputStream inputStream = httpEntity.getContent();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder stringBuilder = new StringBuilder();
				String line = bufferedReader.readLine();
				while(line != null){
					stringBuilder.append(line);
					line = bufferedReader.readLine();
				}
				bufferedReader.close();
				JSONObject retVal =  new JSONObject(stringBuilder.toString());
				return retVal;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return new JSONObject();
	}

}
