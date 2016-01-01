 package com.android.pfe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends ActionBarActivity {

	public static final String PACKAGE_NAME = "com.app.topic";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/TOPic/";
	private static final String TAG = "TOPic.java";
	protected Button _gallery;
	protected Button _camera;
	protected ImageView img;
	protected String _path;
	protected String _path1;
	public String s;
	protected boolean _taken;
	protected static final String PHOTO_TAKEN = "photo_taken";
	private static final int REQUEST_TAKE_PHOTO = 1;
	private static final int REQUEST_PICK_PHOTO = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	//UI objects
		_camera = (Button) findViewById(R.id.camera);
		_camera.setOnClickListener(new ButtonClickHandler());
		_gallery = (Button) findViewById(R.id.gallery);
		_gallery.setOnClickListener(new ButtonClickHandler());
		//Date to generate image's name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		String imageFileName1 = "CROP_" + timeStamp + "_";
		File dir = new File(DATA_PATH);
		//Handle files
		if (!dir.exists())
			dir.mkdir();
		File image = new File(DATA_PATH + "/" + imageFileName + ".jpg");
		_path = image.getAbsolutePath();	
		File image1 = new File(DATA_PATH + "/" + imageFileName1 + ".jpg");
		_path1 = image1.getAbsolutePath();
	}

	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.gallery:
				startGalleryActivity();
				break;
			case R.id.camera:
				startCameraActivity();
				break;
			}
		}
	}
	
	protected void startCameraActivity() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, REQUEST_TAKE_PHOTO);
	}
	
	protected void startGalleryActivity() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_PICK_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "resultCode: " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		//if request code is same we pass as argument in startActivityForResult
		if(requestCode==REQUEST_PICK_PHOTO){
			Uri uri = data.getData();
			_path = ImageFilePath.getPath(getApplicationContext(),uri);
			Log.v(TAG,"PATH PICK IMAGE1: "+_path);
			
				File f = new File(_path);
				s = f.getAbsolutePath();
				Log.v(TAG,"path: "+s);
				Intent intent = new Intent(this,LangActivity.class);
				intent.putExtra("path", s);
				startActivity(intent);
			
		}
		if(requestCode==REQUEST_TAKE_PHOTO){
			File f = new File(_path);
			s = f.getAbsolutePath();
			Intent intent = new Intent(this,LangActivity.class);
			intent.putExtra("path", s);
			startActivity(intent);
			
		}
		if (resultCode == -1) {
			
		} else {
			Log.v(TAG, "User cancelled");
			
		}
	}

	public void cropCapturedImage(Uri picUri){
		_path = ImageFilePath.getPath(getApplicationContext(),picUri);
		Log.v(TAG,"PATH PICK IMAGE2: "+_path);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);
		
		int targetWidth  = 300;
        int targetHeight = 300;
        int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	    float widthScale = (float) targetWidth / (float) width;
	    float heightScale = (float) targetHeight / (float) height;
	    float scaledWidth;
	    float scaledHeight;
		int startY = 0;
		int startX = 0;
		
		    if (widthScale > heightScale) {
		        scaledWidth = targetWidth;
		        scaledHeight = height * widthScale;
		        startY = (int) ((scaledHeight - targetHeight) / 2);
		    } else {
		        scaledHeight = targetHeight;
		        scaledWidth = width * heightScale;
		        startX = (int) ((scaledWidth - targetWidth) / 2);
		    }
		    
		//call the standard crop action intent 
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		//indicate image type and Uri of image
		cropIntent.setDataAndType(picUri, "image/*");
		//set crop properties
		cropIntent.putExtra("crop", "true");
		//indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 0);
		cropIntent.putExtra("aspectY", 0);
		//indicate output X and Y
		cropIntent.putExtra("outputX", startX);
		cropIntent.putExtra("outputY", startY);
		cropIntent.putExtra("scale", true);
		cropIntent.putExtra("return-data", true);
		//start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, 2);
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(MainActivity.PHOTO_TAKEN, _taken);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(MainActivity.PHOTO_TAKEN)) {
			
		}
	}
}