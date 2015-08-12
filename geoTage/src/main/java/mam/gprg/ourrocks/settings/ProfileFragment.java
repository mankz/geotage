package mam.gprg.ourrocks.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.dialogs.ProgressDialog;
import mam.gprg.ourrocks.model.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class ProfileFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener {

	GeoTage app;

	public static int MODE_REGISTER = 0;
	public static int MODE_EDIT = 1;

	String[] titles = new String[] { "Register", "Edit Profile" };
	String[] actions = new String[] { "Register", "Save" };
	public static String ARGS_MODE = "mode";

	ImageView ivAvatar;

	EditText etName, etJob, etCompany, etBirthDay, etEmail, etPhone, etAddress,
			etCity, etAbout, etPassword, etConfirmPassword;
	Button btnSetDate;
	CheckBox checkPrivacy;
	TextView tvGender;
	Spinner spinGender;

	int mode;

	RegisterFragmentListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (GeoTage) getActivity().getApplication();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (app == null) {
			app = (GeoTage) getActivity().getApplication();
		}
	}

	public interface RegisterFragmentListener {
		public void onCancelRegister();

		public void onCancelSave();

		public void onRegistered();

		public void onSave();
	}

	public void setListener(RegisterFragmentListener listener) {
		this.listener = listener;
	}

	Button btnAction;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mode = getArguments().getInt(ARGS_MODE, MODE_REGISTER);
		View layout = inflater.inflate(R.layout.profile_fragment, null);
		ivAvatar = (ImageView) layout
				.findViewById(R.id.profile_fragment_ivAvatar);
		ivAvatar.setOnClickListener(this);
		btnAction = ((Button) layout
				.findViewById(R.id.profile_fragment_btnAction));
		btnAction.setText(actions[mode]);
		((TextView) layout.findViewById(R.id.profile_fragment_tvAction))
				.setText(titles[mode]);
		((Button) layout.findViewById(R.id.profile_fragment_btnCancel))
				.setOnClickListener(this);
		((Button) layout.findViewById(R.id.profile_fragment_btnAction))
				.setOnClickListener(this);

		etName = (EditText) layout.findViewById(R.id.profile_fragment_etName);
		etJob = (EditText) layout.findViewById(R.id.profile_fragment_etJob);
		etCompany = (EditText) layout
				.findViewById(R.id.profile_fragment_etUniversity);
		etBirthDay = (EditText) layout
				.findViewById(R.id.profile_fragment_etBirthday);
		etEmail = (EditText) layout.findViewById(R.id.profile_fragment_etEmail);
		tvGender = (TextView) layout
				.findViewById(R.id.profile_fragment_tvGender);

		btnSetDate = (Button) layout
				.findViewById(R.id.profile_fragment_btnSetDate);
		btnSetDate.setOnClickListener(this);
		spinGender = (Spinner) layout
				.findViewById(R.id.profile_fragment_spinnerGender);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1);
		adapter.add("Male");
		adapter.add("Female");
		spinGender.setAdapter(adapter);
		etPhone = (EditText) layout.findViewById(R.id.profile_fragment_etPhone);
		etAddress = (EditText) layout
				.findViewById(R.id.profile_fragment_etAddress);
		etCity = (EditText) layout.findViewById(R.id.profile_fragment_etCity);
		etAbout = (EditText) layout.findViewById(R.id.profile_fragment_etAbout);

		checkPrivacy = (CheckBox) layout
				.findViewById(R.id.profile_fragment_checkPrivacy);
		checkPrivacy.setOnCheckedChangeListener(this);
		etPassword = (EditText) layout
				.findViewById(R.id.profile_fragment_etPassword);
		etConfirmPassword = (EditText) layout
				.findViewById(R.id.profile_fragment_etConfirmPassword);
		if (mode == MODE_EDIT) {
			tvGender.setVisibility(View.GONE);
			spinGender.setVisibility(View.GONE);
			etEmail.setVisibility(View.GONE);
			etPassword.setVisibility(View.GONE);
			etConfirmPassword.setVisibility(View.GONE);
		} else {
			ivAvatar.setVisibility(View.GONE);
		}
		return layout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (mode == MODE_EDIT) {
			getUserDetail();
		}
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode,
	// android.content.Intent data) {
	// app.log(this, "ON ACTIVITY RESULT");
	// if (resultCode == getActivity().RESULT_OK) {
	// Uri selectedImageUri = data.getData();
	// if (requestCode == changeAvatar) {
	// String path = getPath(selectedImageUri);
	// new ChangeAvatarTask().execute(path);
	// }
	// }
	// };

	File resizeImage(String sdPath) {
		Bitmap bitmapOrg = BitmapFactory.decodeFile(sdPath);
		// ByteArrayOutputStream bao = new ByteArrayOutputStream();

		// Resize the image
		double width = bitmapOrg.getWidth();
		double height = bitmapOrg.getHeight();
		double ratio = 400 / width;
		int newheight = (int) (ratio * height);

		bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, 400, newheight, true);
		Log.d("BITMAP " + sdPath,
				bitmapOrg.getHeight() + "/" + bitmapOrg.getWidth());
		String resizedPath = sdPath.replace(".",
				app.getPref().getInt("user_id", 0) + ".");
		FileOutputStream fos;
		File file = new File(resizedPath);
		try {
			fos = new FileOutputStream(file);
			bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 95, fos);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;

		// Here you can define .PNG as well
		// byte[] ba = bao.toByteArray();
		// String ba1 = Base64.encodeBytes(ba);

	}

	public String getPath(Uri uri) {
		if (uri == null) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(uri, projection, null, null,
				null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		return uri.getPath();
	}

	// ProgressDialog prog = null;

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.profile_fragment_btnSetDate:
			DatePickerDialog picker = new DatePickerDialog(
					(FragmentActivity) getActivity(), new OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							etBirthDay.setText(year + "/" + (monthOfYear + 1)
									+ "/" + dayOfMonth);

						}

					}, 1980, 0, 1);
			picker.show();
			break;
		case R.id.profile_fragment_ivAvatar:
			pickImage();
			break;
		case R.id.profile_fragment_btnCancel:
			if (mode == MODE_REGISTER)
				listener.onCancelRegister();
			else
				listener.onCancelSave();
			break;
		case R.id.profile_fragment_btnAction:
			final String name = etName.getText().toString();
			final String job = etJob.getText().toString();
			final String company = etCompany.getText().toString();
			final String birthDate = etBirthDay.getText().toString();
			final String email = etEmail.getText().toString();
			final String password = etPassword.getText().toString();
			final int sex = spinGender.getSelectedItemPosition();
			final String address = etAddress.getText().toString();
			final String city = etCity.getText().toString();
			final String description = etAbout.getText().toString();
			final String phone = etPhone.getText().toString();
			final int showPrivacy = checkPrivacy.isChecked() ? 1 : 0;

			String msg;
			if (mode == MODE_REGISTER) {
				msg = "Registering...";
			} else
				msg = "Saving...";

			btnAction.setText(msg);
			Listener<String> sListener = new Listener<String>() {

				@Override
				public void onResponse(String response) {
					btnAction.setText(actions[mode]);
					try {
						app.log(this, response);
						JSONObject obj = new JSONObject(response);
						ResponseParser parser = new ResponseParser(obj);
						if (parser.getStatusCode() == 200) {
							if (mode == MODE_REGISTER) {
								listener.onRegistered();
								clear();
							} else
								listener.onSave();
						} else {
							app.shortToast(getActivity(), parser.getErrors()
									.getString(0));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			};
			ErrorListener sErrorListener = new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					btnAction.setText(actions[mode]);
				}
			};
			int method = mode == MODE_REGISTER ? Method.POST : Method.PUT;
			String url = mode == MODE_REGISTER ? API.USER_ADD_POST
					: API.USER_UPDATE_PUT + app.getPref().getInt("user_id", 0);
			StringRequest request = new StringRequest(method, url, sListener,
					sErrorListener) {
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> params = new HashMap<String, String>();
					params.put("name", name);
					params.put("job", job);
					params.put("birth_place", city);
					params.put("birth_date", birthDate);
					if (mode == MODE_REGISTER) {
						params.put("email", email);
						params.put("password", password);
						params.put("sex", String.valueOf(sex));
					}
					params.put("phone", phone);
					params.put("address", address);
					params.put("city", city);
					params.put("company", company);
					params.put("about", description);
					params.put("show_privacy", String.valueOf(showPrivacy));
					return params;
				}
			};
			app.log(this, request.getUrl());
			app.getQueue().add(request);
			break;
		}
	}

	void getUserDetail() {
		Listener<String> slistener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				ResponseParser parser = new ResponseParser(response);
				if (parser.getStatusCode() == 200) {
					User user = User.Parse(parser.getDataObject());
					setValues(user);

				}

			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		};
		StringRequest request = new StringRequest(API.USER_DETAIL_GET
				+ app.getPref().getInt("user_id", 0), slistener, errorListener);
		app.getQueue().add(request);
	}

	void setValues(User user) {
		String avatar = user.getAvatar();
		if (avatar != null
				&& (avatar.endsWith(".jpg") || avatar.endsWith(".png") || avatar
						.endsWith(".gif")))
			app.getUniversalImageLoader().displayImage(
					API.IMAGE_BASE_URL + avatar, ivAvatar,
					app.getCircleDisplayOption());
		etName.setText(user.getName());
		etJob.setText(user.getJob());
		etCompany.setText(user.getCompany());
		etBirthDay.setText(user.getBirthDate());
		etEmail.setText(user.getEmail());
		spinGender.setSelection(user.getSex());
		etPhone.setText(user.getPhone());
		etAddress.setText(user.getAddress());
		etCity.setText(user.getCity());
		etAbout.setText(user.getAbout());
		checkPrivacy.setChecked(user.isShowPrivacy());
	}

	public static int changeAvatar = 2043;

	public void changeAvatar(Bitmap bitmap, String path) {
		new ChangeAvatarTask(bitmap, path).execute();
	}

	class ChangeAvatarTask extends AsyncTask<Void, Void, String> {

		File file;
		Bitmap bitmap;
		String path;

		public ChangeAvatarTask(Bitmap bitmap, String path) {
			this.bitmap = bitmap;
			this.path = path;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			app.log(this, "update ava " + path);
			app.getUniversalImageLoader().displayImage("file://" + path,
					ivAvatar, app.getCircleDisplayOption());

		}

		@Override
		protected String doInBackground(Void... arg0) {
			file = new File(path);
			String urlString = API.CLIENTS_UPDATE_AVATAR;
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlString);
				MultipartEntity reqEntity = new MultipartEntity();

				if (file.exists()) {
					// FileBody bin1 = new FileBody(resizeImage(arg0[0]));
					FileBody bin1 = new FileBody(file);
					reqEntity.addPart("avatar", bin1);
				} else {
					return null;
				}
				reqEntity.addPart(
						"userId",
						new StringBody(String.valueOf(app.getPref().getInt(
								"user_id", 0))));
				post.setEntity(reqEntity);
				HttpResponse response = client.execute(post);
				HttpEntity resEntity = response.getEntity();
				String response_str = EntityUtils.toString(resEntity);
				app.log(this, response_str);
				return response_str;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// app.log(this, "ni " + result);
			try {

				JSONObject obj = new JSONObject(result);
				final ResponseParser parser = new ResponseParser(obj);
				if (parser.getStatusCode() == 200) {
					app.getUniversalImageLoader().displayImage(
							"file://" + path, ivAvatar,
							app.getCircleDisplayOption());

				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static int REQUEST_PICK_FROM_GALLERY = 134;

	void pickImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		getActivity().startActivityForResult(intent, REQUEST_PICK_FROM_GALLERY);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, final boolean privacy) {
		Listener<String> completeListener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				ResponseParser parser = new ResponseParser(response);
				app.log(this, response);
				if (parser.getStatusCode() != 200)
					checkPrivacy.setChecked(!privacy);

			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				checkPrivacy.setChecked(!privacy);

			}
		};
		StringRequest request = new StringRequest(Method.POST,
				API.CLIENTS_CHANGE_PRIVACY, completeListener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("privacy", String.valueOf(privacy ? 1 : 0));
				params.put("user_id", String.valueOf(app.getUserId()));
				return params;
			}
		};
		app.log(this, request.getUrl());
		app.getQueue().add(request);

	}

	void clear() {
		etAbout.setText("");
		etAddress.setText("");
		etBirthDay.setText("");
		etCity.setText("");
		etCompany.setText("");
		etConfirmPassword.setText("");
		etEmail.setText("");
		etJob.setText("");
		etName.setText("");
		etPassword.setText("");
		etPhone.setText("");
		spinGender.setSelection(0);
		checkPrivacy.setChecked(false);
	}
}
