package mam.gprg.ourrocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.detail.RocksDetailActivity;
import mam.gprg.ourrocks.dialogs.ErrorDialog;
import mam.gprg.ourrocks.dialogs.ProgressDialog;
import mam.gprg.ourrocks.model.Rock;
import mam.gprg.ourrocks.settings.SettingFragment;
import mam.gprg.ourrocks.userdatas.AddPlaceActivity;
import mam.gprg.ourrocks.userdatas.UserActivity;
import mam.gprg.ourrocks.util.AnimateFirstDisplayListener;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

@ReportsCrashes(formKey = "", mailTo = "aji.mukhlis@gmail.com", mode = ReportingInteractionMode.TOAST, resDialogOkToast = R.string.crash_toast, resToastText = R.string.crash_toast)
public class GeoTage extends Application {

	ImageLoader uiLoader;
	DisplayImageOptions circleOptions;
	ImageLoadingListener animateFirstListener;

	SharedPreferences pref;

	// Volley
	RequestQueue queue;
	ConnectivityManager connManager;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initImageLoader(getApplicationContext());
		String s;
		ACRA.init(this);

	}

	ConnectivityManager getConnManager() {
		if (connManager == null) {
			connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		}

		return connManager;
	}

	public boolean isConnected() {
		if (getConnManager().getActiveNetworkInfo() == null)
			return false;
		return getConnManager().getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED;
	}

	public RequestQueue getQueue() {
		if (queue == null) {
			queue = Volley.newRequestQueue(getApplicationContext());
		}
		return queue;
	}

	public SharedPreferences getPref() {
		if (pref == null)
			pref = getSharedPreferences("ourrocks", MODE_PRIVATE);
		return pref;
	}

	public ImageLoader getUniversalImageLoader() {
		if (uiLoader == null)
			uiLoader = ImageLoader.getInstance();
		return uiLoader;
	}

	DisplayImageOptions simpleOptions;

	public DisplayImageOptions getSimpleDisplayOption() {
		if (simpleOptions == null)
			simpleOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisk(true)
					.considerExifParams(true)
					.displayer(new SimpleBitmapDisplayer()).build();
		return simpleOptions;
	}

	public DisplayImageOptions getCircleDisplayOption() {
		if (circleOptions == null)
			circleOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisk(true)
					.considerExifParams(true)
					.showImageForEmptyUri(R.color.silver)
					.displayer(new CircleBitmapDisplayer()).build();
		return circleOptions;
	}

	public ImageLoadingListener getFirstAnimationDisplay() {
		if (animateFirstListener == null)
			animateFirstListener = new AnimateFirstDisplayListener();
		return animateFirstListener;
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	void resetButton(View layout, int menuIds[]) {
		for (int i = 0; i < menuIds.length; i++) {
			layout.findViewById(menuIds[i]).setSelected(false);
		}
	}

	public void setActiveMenu(View layout, int menuIds[], int index) {
		resetButton(layout, menuIds);
		layout.findViewById(menuIds[index]).setSelected(true);
	}

	public boolean isLoggedIn() {
		return getPref().contains("user_id")
				&& getPref().getInt("user_id", 0) > 0;
	}

	public void showUserPage(Context ctx, int userId) {
		Intent intent = new Intent(ctx, UserActivity.class);
		intent.putExtra("user_id", userId);
		ctx.startActivity(intent);
	}

	public void showRockPage(Context ctx, Rock rock) {
		Intent intent = new Intent(ctx, RocksDetailActivity.class);
		intent.putExtra("rock_id", rock.getId());
		intent.putExtra("rock", rock);
		ctx.startActivity(intent);
	}

	public void log(Object object, String log) {
		if (log != null) {
			Log.d(object.getClass().getCanonicalName(), log);
		}
	}

	public void shortToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public ProgressDialog addProgress(FragmentManager fm, String msg) {
		final ProgressDialog prog = new ProgressDialog();
		Bundle args = new Bundle();
		args.putString(ProgressDialog.MESSAGE, msg);
		prog.setArguments(args);
		prog.show(fm, "add-comment");
		return prog;
	}

	public static int EDIT_ROCK = 123;

	public void editRock(Context ctx, int rockId) {
		Intent intent = new Intent(ctx, AddPlaceActivity.class);
		intent.putExtra(AddPlaceActivity.MODE, AddPlaceActivity.MODE_EDIT);
		intent.putExtra(AddPlaceActivity.ROCK_ID, rockId);
		((Activity) ctx).startActivityForResult(intent, EDIT_ROCK);
	}

	public void showErrorDialog(FragmentManager manager, String message) {
		ErrorDialog error = new ErrorDialog();
		Bundle args = new Bundle();
		args.putString(ErrorDialog.ERROR_MESSAGE, message);
		error.setArguments(args);
		error.show(manager, "error-dialog");
	}

	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}

	GoogleCloudMessaging gcm;

	public GoogleCloudMessaging getGCM() {
		if (gcm == null)
			gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
		return gcm;
	}

	public void registerGCM(final Activity activity) {
		if (isConnected())
			new Thread() {

				@Override
				public void run() {
					String senderId = getResources().getString(
							R.string.gcm_sender_id);
					try {
						final String registrationId = getGCM().register(
								senderId);
						Listener<String> okListener = new Listener<String>() {

							@Override
							public void onResponse(String response) {
								log(this, response);
								Editor edit = getPref().edit();
								edit.putString(SettingFragment.NOTIF_ME,
										registrationId);
								edit.commit();
							}
						};
						ErrorListener errorListener = new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub

							}
						};
						StringRequest request = new StringRequest(Method.POST,
								API.CLIENTS_CHANGE_GCM_ID, okListener,
								errorListener) {
							@Override
							protected Map<String, String> getParams()
									throws AuthFailureError {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("gcm_id", registrationId);
								params.put("user_id",
										String.valueOf(getUserId()));
								return params;
							}
						};
						getQueue().add(request);
						log(this, request.getUrl());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				};

			}.start();

	}

	public void unregisterGCM() {
		if (isConnected())
			new Thread() {
				public void run() {
					try {
						getGCM().unregister();
						Listener<String> okListener = new Listener<String>() {

							@Override
							public void onResponse(String response) {
								log(this, response);
								Editor editor = getPref().edit();
								editor.putString(SettingFragment.NOTIF_ME, "");
								editor.commit();
							}
						};
						ErrorListener errorListener = new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub

							}
						};
						StringRequest request = new StringRequest(Method.POST,
								API.CLIENTS_DELETE_GCM_ID, okListener,
								errorListener) {
							@Override
							protected Map<String, String> getParams()
									throws AuthFailureError {
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("user_id",
										String.valueOf(getUserId()));
								return params;
							}
						};
						getQueue().add(request);
						log(this, request.getUrl());

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			}.start();

	}

	public HashMap<String, Object> getThumbnail(Uri uri, int thumbnailSize) {

		try {

			HashMap<String, Object> data = new HashMap<String, Object>();

			InputStream input = getContentResolver().openInputStream(uri);
			BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
			onlyBoundsOptions.inJustDecodeBounds = true;
			onlyBoundsOptions.inDither = true;// optional
			onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
			BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
			input.close();
			if ((onlyBoundsOptions.outWidth == -1)
					|| (onlyBoundsOptions.outHeight == -1))
				return null;

			int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
					: onlyBoundsOptions.outWidth;

			double ratio = (originalSize > thumbnailSize) ? (originalSize / thumbnailSize)
					: 1.0;

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
			bitmapOptions.inDither = true;// optional
			bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
			bitmapOptions.inPurgeable = true; // Tell to gc that whether it
												// needs free memory, the Bitmap
												// can be cleared
			bitmapOptions.inInputShareable = true; // Which kind of reference
													// will be used to recover
													// the Bitmap data after
													// being clear, when it will
													// be used in the future
			bitmapOptions.inTempStorage = new byte[32 * 1024];
			input = this.getContentResolver().openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(input, null,
					bitmapOptions);
			input.close();

			String path = getPath(bitmap);
			data.put("bitmap", bitmap);
			data.put("path", path);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	String getPath(Bitmap bitmap) {
		File fileBitmap = new File(getImageFolder(), getPref().getInt(
				"user_id", 0)
				+ "_" + System.currentTimeMillis() + ".png");
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(fileBitmap);
			bitmap.compress(CompressFormat.PNG, 100, stream);
			stream.flush();
			stream.close();
			return fileBitmap.getAbsolutePath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	File imageFolder = new File(Environment.getExternalStorageDirectory(),
			"geotage_images");
	File noMedia = new File(imageFolder, ".nomedia");

	public File getImageFolder() {
		try {
			if (!imageFolder.exists()) {
				imageFolder.mkdirs();
			}
			if (!noMedia.exists())
				noMedia.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageFolder;
	}

	public int getUserId() {
		return getPref().getInt("user_id", 0);
	}

	Bitmap correctOrientation(Bitmap bm, String path) {
		try {

			Bitmap bitmap = bm;
			ExifInterface exif = new ExifInterface(path);
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 1);
			Matrix m = new Matrix();

			if ((orientation == 3)) {
				m.postRotate(180);
				m.postScale((float) bm.getWidth(), (float) bm.getHeight());
				// if(m.preRotate(90)){
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			} else if (orientation == 6) {
				m.postRotate(90);
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			} else if (orientation == 8) {
				m.postRotate(270);
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			}
			return bitmap;
		} catch (Exception e) {
			return bm;
		}
	}

}
