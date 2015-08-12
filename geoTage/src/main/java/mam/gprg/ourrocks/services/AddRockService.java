package mam.gprg.ourrocks.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.detail.RocksDetailActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AddRockService extends Service {

	ArrayList<String> imagePaths;
	int rockId;

	GeoTage app;

	@Override
	public void onCreate() {
		super.onCreate();
		app = (GeoTage) getApplication();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.hasExtra("paths")) {
			imagePaths = intent.getStringArrayListExtra("paths");
			rockId = intent.getIntExtra("rockId", 0);
			new UploadImageTask().execute();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void uploadImage(int rockId, String path) {
		File file = new File(path);
		String urlString = API.CLIENTS_EDIT_NEW_IMAGE;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urlString);
			MultipartEntity reqEntity = new MultipartEntity();
			if (file.exists()) {
				FileBody bin = new FileBody(file) {
					@Override
					public void writeTo(OutputStream arg0) throws IOException {
						super.writeTo(new CoutingOutputStream(arg0));
					}
				};
				reqEntity.addPart("uploadedfile", bin);
				reqEntity.addPart("rockid",
						new StringBody(String.valueOf(rockId)));
			}
			post.setEntity(reqEntity);
 			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			// {"status":200,"message":"succeed","data":"IMG_20141209_0936371.jpg"}
			String response_str = EntityUtils.toString(resEntity);
			JSONObject obj = new JSONObject(response_str);
			if(obj.getInt("status") == 200) {
				file.delete();
			}   
			app.log(this, response_str);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

  
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	static class CoutingOutputStream extends FilterOutputStream {

		CoutingOutputStream(final OutputStream out) {
			super(out);
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
			System.out.println("Written 1 byte");
		}

		@Override
		public void write(byte[] b) throws IOException {
			out.write(b);
			System.out.println("Written " + b.length + " bytes");
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			System.out.println("Written " + len + " bytes");
		}
	}

	class UploadImageTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			for (int i = 0; i < imagePaths.size(); i++) {
				publishProgress("upload " + (i + 1) + " of "
						+ imagePaths.size() + " images");
				uploadImage(rockId, imagePaths.get(i));

				// File file = new File(imagePaths.get(i));
				// String urlString = API.CLIENTS_EDIT_NEW_IMAGE;
				// try {
				// HttpClient client = new DefaultHttpClient();
				// HttpPost post = new HttpPost(urlString);
				// MultipartEntity reqEntity = new MultipartEntity();
				// if (file.exists()) {
				// FileBody bin = new FileBody(resizeImage(imagePaths.get(i))) {
				// @Override
				// public void writeTo(OutputStream arg0) throws IOException {
				// super.writeTo(new CoutingOutputStream(arg0));
				// }
				// };
				// reqEntity.addPart("uploadedfile", bin);
				// reqEntity.addPart("rockid", new
				// StringBody(String.valueOf(rockId)));
				// }
				// post.setEntity(reqEntity);
				//
				// HttpResponse response = client.execute(post);
				// HttpEntity resEntity = response.getEntity();
				// //
				// {"status":200,"message":"succeed","data":"IMG_20141209_0936371.jpg"}
				// String response_str = EntityUtils.toString(resEntity);
				// JSONObject obj = new JSONObject(response_str);
				// String URL = obj.getString("data");
				// app.log(this, response_str);
				//
				// } catch (Exception e) {
				// e.printStackTrace();
				// }

			}
			return null;
		}

		NotificationManager nm;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					AddRockService.this);
			builder.setTicker(values[0]);
			builder.setContentText("Uploading new object");
			builder.setContentTitle("GeoTage");
			builder.setSmallIcon(R.drawable.arrow_88);
			nm.notify(0, builder.build());
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					AddRockService.this);
			builder.setSmallIcon(R.drawable.arrow_88);
			builder.setTicker("Upload Done");
			builder.setContentText("Object succesfully uploaded");
			builder.setContentTitle("GeoTage");
			Intent intent = new Intent(AddRockService.this,
					RocksDetailActivity.class);
			intent.putExtra("rock_id", rockId);
			builder.setContentIntent(PendingIntent
					.getActivity(AddRockService.this, 0, intent,
							PendingIntent.FLAG_ONE_SHOT));
			nm.cancel(0);
			nm.notify(1, builder.build());

		}

	}
}
