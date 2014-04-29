package edu.yourbucks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.opengl.Visibility;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteStockSymbol);
		textView.setThreshold(1);
		textView.setAdapter(new CustomAutoCompleteAdaptor(this, textView.getText().toString()));
		textView.setOnItemClickListener(new OnItemClickListener() {
			@Override
		    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
		        String selection = (String)parent.getItemAtPosition(position);
		        Log.i("selected : ", selection);
		        AutoCompleteTextView textView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteStockSymbol);
		        InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(textView2.getWindowToken(), 0);
//					textView2.setText(selection.subSequence(0, selection.indexOf(",")));
					String editTextStr = selection.substring(0, selection.indexOf(",")); 
//					textView2.getText().toString(); 
					JSONObject results = getData(editTextStr);
					renderStockInfo(results);
		    }
		});
		
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Error");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Log.i("Button", "Search clicked");
				AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteStockSymbol);
				
				InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
					
				String editTextStr = textView.getText().toString(); 
				if(editTextStr.trim().length() < 1) {
					alertDialog.setMessage("Please enter a stock symbol");
					hideShowElements(View.INVISIBLE);
					alertDialog.show();
				} else {
					JSONObject results = getData(editTextStr);
					renderStockInfo(results);
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void renderStockInfo(JSONObject res) {
		
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Error");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		if(res.has("stock") && res.optJSONObject("stock").has("Error")) {
			alertDialog.setMessage("Please enter a valid stock symbol");
			hideShowElements(View.INVISIBLE);
			alertDialog.show();
			return;
		}
		try {
			// company symbol and name
			TextView textViewElement = (TextView)findViewById(R.id.StockHeader);
			String str = res.getJSONObject("result").getString("Name") + " ("
					+ res.getJSONObject("result").getString("Symbol") + ")";
			textViewElement.setText(str);
			Log.i("Setting value : ", "StockHeader");
			
			// stock price
			textViewElement = (TextView)findViewById(R.id.StockValue);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("LastTradePriceOnly");
			textViewElement.setText(str);
			Log.i("Setting value : ", "StockValue");
			
			// stock rise
			textViewElement = (TextView)findViewById(R.id.StockIndicator);
			String type = res.getJSONObject("result").getJSONObject("Quote").getString("ChangeType").trim();
			str = res.getJSONObject("result").getJSONObject("Quote").getString("ChangeValue").trim();
//			ImageView img = (ImageView)findViewById(R.id.imageView1);
//			img.setScaleType(ScaleType.FIT_XY);
			Log.i("Setting value : ", "StockImage");
			
			Log.i("ChangeType", type);
			Log.i("ChangeValue", str);
			
			try {
				
				ImageView mImgView1 = (ImageView) findViewById(R.id.stockChartImage);
		        String url = res.getJSONObject("result").getString("StockChartImageURL");
		        BitmapFactory.Options bmOptions;
		        bmOptions = new BitmapFactory.Options();
		        bmOptions.inSampleSize = 1;
		        Bitmap bm = loadBitmap(url, bmOptions);
		        mImgView1.setImageBitmap(bm);
		        mImgView1.setScaleType(ScaleType.FIT_XY);
				
			} catch (Exception e3) {
				Log.e(e3.getClass().toString(), e3.getMessage());
			}
			
			
			try {
				if(type.equalsIgnoreCase("+")) {
					
					if(!str.equalsIgnoreCase("0.00")) {
//						String uri = "@drawable/up_g";
//						int imageResource = getResources().getIdentifier(uri, null, getPackageName());
//						Drawable imgRes = getResources().getDrawable(imageResource);
//						img.setImageDrawable(imgRes);
						
//						img.setImageResource(R.drawable.up_g);
//						img.setVisibility(View.VISIBLE);
//						img.setScaleType(ScaleType.FIT_XY);
						textViewElement.setTextColor(Color.GREEN);
					} else {
//						img.setVisibility(View.INVISIBLE);
					}
				} else if(type.equalsIgnoreCase("-")) {
//					String uri = "@drawable/down_r";
//					int imageResource = getResources().getIdentifier(uri, null, getPackageName());
//					Drawable imgRes = getResources().getDrawable(imageResource);
//					img.setImageDrawable(imgRes);
					
//					img.setImageResource(R.drawable.down_r);
//					img.setVisibility(View.VISIBLE);
					textViewElement.setTextColor(Color.RED);
				} else {
					textViewElement.setTextColor(Color.GREEN);
//					img.setVisibility(View.INVISIBLE);
				}
				textViewElement.setText(str + " (" + 
						res.getJSONObject("result").getJSONObject("Quote").getString("ChangePercent").trim() + ")");
				Log.i("ChangeValue ", str);
			} catch (Exception e1) {
				Log.i(e1.getClass().toString(), e1.getMessage());
			}
			
			
			//previous closing
			textViewElement = (TextView)findViewById(R.id.TextView01);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("PreviousClose");
			textViewElement.setText(str);
			Log.i("PreviousClose ", str);
			
			//days range
			textViewElement = (TextView)findViewById(R.id.TextView11);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("DaysLow") + " - "
					+ res.getJSONObject("result").getJSONObject("Quote").getString("DaysHigh");
			textViewElement.setText(str);
			Log.i("day range ", str);
			
			//open price
			textViewElement = (TextView)findViewById(R.id.TextView03);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("Open");
			textViewElement.setText(str);
			Log.i("Open ", str);
			
			//52wk Range
			textViewElement = (TextView)findViewById(R.id.TextView13);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("YearLow") + " - "
					+ res.getJSONObject("result").getJSONObject("Quote").getString("YearHigh");
			textViewElement.setText(str);
			Log.i("year range ", str);
			
			//Bid
			textViewElement = (TextView)findViewById(R.id.TextView05);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("Bid");
			textViewElement.setText(str);
			Log.i("Bid ", str);
			
			// Volume
			textViewElement = (TextView)findViewById(R.id.Volume);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("Volume");
			textViewElement.setText(str);
			Log.i("Ask ", str);
			
			// Ask
			textViewElement = (TextView)findViewById(R.id.TextView07);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("Ask");
			textViewElement.setText(str);
			Log.i("Ask ", str);
			
			// Avg Vol (3m)
			textViewElement = (TextView)findViewById(R.id.AvgVol);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("AverageDailyVolume");
			textViewElement.setText(str);
			Log.i("OneyrTargetPrice ", str);
			
			// 1 yr TargetPrice
			textViewElement = (TextView)findViewById(R.id.TextView09);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("OneyrTargetPrice");
			textViewElement.setText(str);
			Log.i("OneyrTargetPrice ", str);
			
			// MarketCapitalization
			textViewElement = (TextView)findViewById(R.id.MarketCapVal);
			str = res.getJSONObject("result").getJSONObject("Quote").getString("MarketCapitalization");
			textViewElement.setText(str);
			Log.i("MarketCapitalization ", str);
			
			hideShowElements(View.VISIBLE);
			
		} catch (Exception e2) {
			Log.e(e2.getClass().toString(), e2.getMessage());
		}
		
	}
	
	private JSONObject getData(String term){

		// clean the term
    	if(term.indexOf(')') > 0 && term.indexOf('(') > 0) {
    		int st = term.indexOf('(') + 1;
    		int ed = term.indexOf(')');
    		term = term.substring(st, ed);
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
	             
	             JSONObject retVal =  new JSONObject(stringBuilder.toString());
	             if(retVal.has("result")) {
		             Iterator<String> itr = retVal.getJSONObject("result").getJSONObject("Quote").keys();
		             String key, val;
		             while(itr.hasNext()) {
		            	 key = itr.next();
		            	 val = retVal.getJSONObject("result").getJSONObject("Quote").getString(key);
		            	 if("true".equalsIgnoreCase(val)){
		            		 retVal.getJSONObject("result").getJSONObject("Quote").put(key, "");
		            	 }
		             }
		             if(retVal.getJSONObject("result").getString("Symbol").equalsIgnoreCase("true")){
		            	 retVal.getJSONObject("result").put("Symbol",""); 
		             }
		             if(retVal.getJSONObject("result").getString("Name").equalsIgnoreCase("true")){
		            	 retVal.getJSONObject("result").put("Name",""); 
		             }
	             }
	             return retVal;
	         }
	     }catch (Exception e){
	    	 e.printStackTrace();
	     }
	     return new JSONObject();
	}
	
	
	private  Bitmap loadBitmap(String URL, BitmapFactory.Options options) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in, null, options);
            in.close();
        } catch (IOException e1) {
        }
        return bitmap;
    }

    private  InputStream OpenHttpConnection(String strURL)
            throws IOException {
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        } catch (Exception ex) {
        }
        return inputStream;
    }
    
    private void hideShowElements(int val) {
    	((TextView)findViewById(R.id.TextView01)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView02)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView03)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView04)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView05)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView06)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView07)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView08)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView09)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView10)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView11)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView12)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView13)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView14)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView16)).setVisibility(val);
    	((TextView)findViewById(R.id.TextView18)).setVisibility(val);
    	((TextView)findViewById(R.id.textView1)).setVisibility(val);
    	((TextView)findViewById(R.id.textView2)).setVisibility(val);
    	((TextView)findViewById(R.id.StockHeader)).setVisibility(val);
    	((TextView)findViewById(R.id.StockValue)).setVisibility(val);
    	((TextView)findViewById(R.id.AvgVol)).setVisibility(val);
    	((TextView)findViewById(R.id.MarketCapVal)).setVisibility(val);
    	((TextView)findViewById(R.id.Volume)).setVisibility(val);
    	((ImageView)findViewById(R.id.stockChartImage)).setVisibility(val);
    	((TextView)findViewById(R.id.StockIndicator)).setVisibility(val);
    }

}
