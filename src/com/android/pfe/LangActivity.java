package com.android.pfe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Threshold;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class LangActivity extends ActionBarActivity {
	public String lang1,lang2;
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/TOPic/";
	private static final String TAG = "Language.java";

	public String _path;
	public Bitmap  bitmap;
	public Spinner s1,s2;
	public EditText _ocr,_translated;
	public Button _bOcr;
	public Button _bReturn;
	public Button _bShare;
	public ImageView img;
	public FastBitmap fb;
	public static final String lang = "eng";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lang);
		
		//Handle the network operation 
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy); 
				//UI componenents
		img = (ImageView) findViewById(R.id.img21);
		_ocr = (EditText) findViewById(R.id.ocr1);
		_translated = (EditText) findViewById(R.id.translated1);
		addListenerOnButton();
		s1 = (Spinner) findViewById(R.id.lang1);
		s2 = (Spinner) findViewById(R.id.lang22);
		//Set Spinner (S1,S2) Data
		List<String> list1 = new ArrayList<String>();
		list1.add("Arabic");
		list1.add("French");
		list1.add("English");
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
		        R.array.lang_arrays, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s1.setAdapter(adapter1);
		
		List<String> list2 = new ArrayList<String>();
		list2.add("Arabic");
		list2.add("French");
		list2.add("English");
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
		        R.array.lang_arrays, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s2.setAdapter(adapter2);
		
		//Store the path of the Image from the first activity
		_path = getIntent().getStringExtra("path");
		Log.v(TAG, "path: " + _path);
		
		// Manage the  directory
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		
		
		
		
	}
	//Store the items
	public void addItemsOnSpinner() {
		
		
		String langue1 = String.valueOf(s1.getSelectedItem());
		switch (langue1){
			case "Arabic" :
				lang1 = "ara";
				break;
			case "French" :
				lang1 = "fra";
				break;
			case "English" :
				lang1 = "eng";
				break;
		}
		
		
		String langue2 = String.valueOf(s2.getSelectedItem());
		switch (langue2){
			case "Arabic" :
				lang2 = "ara";
				break;
			case "French" :
				lang2 = "fra";
				break;
			case "English" :
				lang2 = "eng";
				break;
		}
		
	  }
	//Action on buttons
	public void addListenerOnButton() {
		_bOcr = (Button) findViewById(R.id.translate1);
		_bOcr.setOnClickListener(new ButtonClickHandler());
		_bReturn = (Button) findViewById(R.id.ret1);
		_bReturn.setOnClickListener(new ButtonClickHandler());
		_bShare = (Button) findViewById(R.id.share1);
		_bShare.setOnClickListener(new ButtonClickHandler());
	  }
	
	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.translate1:
				addItemsOnSpinner();
				Log.v(TAG, "lang1: " + lang1);
				Log.v(TAG, "lang2: " + lang2);
				langueFolder();
				onPhotoTaken();
				break;
			case R.id.ret1:
				retour();
				break;
			case R.id.share1:
				share();
				break;
			}
			
		}
	}
	
	public void retour() {
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
	}
	//Share image via Social Network
	protected void share(){
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		Uri screenshotUri = Uri.parse(_path);
		shareIntent.setType("images/*");
		shareIntent.putExtra(shareIntent.EXTRA_STREAM, screenshotUri);
		startActivity(Intent.createChooser(shareIntent, "Share using"));
	}
	protected void onPhotoTaken(){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		
		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);
		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			Log.v(TAG, "Rotation: " + rotate);

			if (rotate != 0) {

				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				// Setting rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}
		
			fb = new FastBitmap(bitmap);
	        
            //Set image to Grayscale (OCR work with Grayscaleimages )
			fb.toGrayscale();
			
			//threshold Filters
			Threshold t = new Threshold(100);
			t.applyInPlace(fb);
			
			
			
		Log.v(TAG, "Before baseApi");
        //Fire up the OCR API
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang1);
		baseApi.setImage(fb.toBitmap());
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();
		Log.v(TAG, "Ocr result: " + recognizedText);
		img.setImageBitmap(fb.toBitmap());
		recognizedText = recognizedText.trim();

		if ( recognizedText.length() != 0 ) {
			_ocr.setText(_ocr.getText().toString().length() == 0 ? recognizedText : _ocr.getText() + " " + recognizedText);
			_ocr.setSelection(_ocr.getText().toString().length());
		}
		String text = _ocr.getText().toString();
		//FireUp the Traduction api 
		String translatedText = null;
		Translate.setClientId("Mahd");
		Translate.setClientSecret("j+9JfKxh0vBqRHPuzSKKtUNhAb+OTGWJJknKFBiKJ1Y=");
		
					try {
					if (lang1=="ara" && lang2=="fra"){
						translatedText = Translate.execute(text, Language.ARABIC, Language.FRENCH);
						_translated.setText(translatedText);
					} else if (lang1=="ara" && lang2=="eng"){
						translatedText = Translate.execute(text, Language.ARABIC, Language.ENGLISH);
						_translated.setText(translatedText);
					}else if (lang1=="fra" && lang2=="eng"){
						translatedText = Translate.execute(text, Language.FRENCH, Language.ENGLISH);
						_translated.setText(translatedText);
					}else if (lang1=="fra" && lang2=="ara"){
						translatedText = Translate.execute(text, Language.FRENCH, Language.ARABIC);
						_translated.setText(translatedText);
					}else if (lang1=="eng" && lang2=="ara"){
						translatedText = Translate.execute(text, Language.ENGLISH, Language.ARABIC);
						_translated.setText(translatedText);
					}else if (lang1=="eng" && lang2=="fra"){
						translatedText = Translate.execute(text, Language.ENGLISH, Language.FRENCH);
						_translated.setText(translatedText);
					}
					else{
						_translated.setText("invalid");
					}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				Log.e(TAG, "Translated Text " + translatedText);
				
		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}
	}
	 
	 //Handle the  trainData
	public void langueFolder(){
		if (!(new File(DATA_PATH + "tessdata/" + lang1 + ".traineddata")).exists()){
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang1 + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + lang1 + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				
				Log.v(TAG, "Copied " + lang1 + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang1 + " traineddata " + e.toString());
			}
		}
		if (!(new File(DATA_PATH + "tessdata/" + lang2 + ".traineddata")).exists()){
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang2 + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + lang2 + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				Log.v(TAG, "Copied " + lang2 + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang2 + " traineddata " + e.toString());
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lang, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}