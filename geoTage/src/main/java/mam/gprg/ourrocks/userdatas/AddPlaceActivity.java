package mam.gprg.ourrocks.userdatas;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mam.gprg.ourrocks.GeoTage;
import mam.gprg.ourrocks.R;
import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.adapter.CategoryAdapter;
import mam.gprg.ourrocks.adapter.TypeAdapter;
import mam.gprg.ourrocks.dialogs.ProgressDialog;
import mam.gprg.ourrocks.model.Category;
import mam.gprg.ourrocks.model.Rock;
import mam.gprg.ourrocks.model.RockPhoto;
import mam.gprg.ourrocks.model.Type;
import mam.gprg.ourrocks.model.db.RockPhotoTable;
import mam.gprg.ourrocks.model.db.RockTable;
import mam.gprg.ourrocks.services.AddRockService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class AddPlaceActivity extends FragmentActivity implements OnMapReadyCallback,
		OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	static int REQUEST_TAKE_CAMERA = 1;
	static int REQUEST_PICK_FROM_GALLERY = 2;
	int mode;
	public static int MODE_ADD = 1;
	public static int MODE_EDIT = 2;

	int fromWhere = 2;
	public static int FROM_DRAFT = 1;
	public static int FROM_SERVER = 2;

	Spinner spinCategory, spinType;
	CategoryAdapter categoryAdapter;
	TypeAdapter typeAdapter;
	EditText etName, etFormation, etAddress, etCity, etLatitude, etLongitude,
			etDescription;
	Button btnSaveToDraft, btnSave, btnCancel;
	Button btnSetLocation;

	GeoTage app;
	Context mContext;

	GoogleApiClient client;
	Location loc;

	int rockId, draftRockId;
	public static String MODE = "mode";
	public static String ROCK_ID = "rock_id";
	public static String FROM_WHERE = "from_where";
	public static String DRAFT_ROCK_ID = "draft_rock_id";

	@Override
	protected void onResume() {
		super.onResume();
		client.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		client.disconnect();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		app = (GeoTage) getApplication();
		mContext = this;
		setContentView(R.layout.add_place_form);
		mode = getIntent().getExtras().getInt(MODE);
		client = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		initView();
		getCategories();
		getTypes();

		if (mode == MODE_EDIT) {
			if (getIntent().getExtras().containsKey(FROM_WHERE))
				fromWhere = getIntent().getExtras().getInt(FROM_WHERE);
			if (fromWhere == FROM_DRAFT) {
				draftRockId = getIntent().getExtras().getInt(DRAFT_ROCK_ID);
				getDraftRockDetail();
			} else {
				rockId = getIntent().getExtras().getInt(ROCK_ID);
				getRockDetail();
			}
		}

	}

	void setContent() {
		int catPos = 0;
		for (int i = 0; i < categoryAdapter.getCount(); i++) {
			if (categoryAdapter.getItem(i).getId() == rock.getCategoryId()) {
				catPos = i;
				break;
			}
		}
		spinCategory.setSelection(catPos);
		int typePos = 0;
		for (int i = 0; i < typeAdapter.getCount(); i++) {
			if (typeAdapter.getItem(i).getId() == rock.getTypeId()) {
				typePos = i;
				break;
			}
		}
		spinType.setSelection(typePos);
		etName.setText(rock.getName());
		etFormation.setText(rock.getFormation());
		etAddress.setText(rock.getAddress());
		etCity.setText(rock.getCity());
		etLatitude.setText(String.valueOf(rock.getLatitude()));
		etLongitude.setText(String.valueOf(rock.getLongitude()));
		etDescription.setText(rock.getDesc());

		if (rock.getPhotos() != null) {
			String imageBaseURL = null;
			if (fromWhere == FROM_SERVER) {
				imageBaseURL = API.IMAGE_BASE_URL;
			} else
				imageBaseURL = "file://";
			try {
				if (rock.getPhotos().get(0) != null) {
					app.getUniversalImageLoader().displayImage(
							imageBaseURL + rock.getPhotos().get(0).getUrl(),
							ib1);
				}
				if (rock.getPhotos().get(1) != null) {
					app.getUniversalImageLoader().displayImage(
							imageBaseURL + rock.getPhotos().get(1).getUrl(),
							ib2);
				}
				if (rock.getPhotos().get(2) != null) {
					app.getUniversalImageLoader().displayImage(
							imageBaseURL + rock.getPhotos().get(2).getUrl(),
							ib3);
				}
				if (rock.getPhotos().get(3) != null) {
					app.getUniversalImageLoader().displayImage(
							imageBaseURL + rock.getPhotos().get(3).getUrl(),
							ib4);
				}
				if (rock.getPhotos().get(4) != null) {
					app.getUniversalImageLoader().displayImage(
							imageBaseURL + rock.getPhotos().get(4).getUrl(),
							ib5);
				}
				if (rock.getPhotos().get(5) != null) {
					app.getUniversalImageLoader().displayImage(
							imageBaseURL + rock.getPhotos().get(5).getUrl(),
							ib6);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	Rock rock;

	void getRockDetail() {

		Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				app.log(this, response.toString());
				ResponseParser parser = new ResponseParser(response);
				if (parser.getStatusCode() == 200) {
					rock = Rock.Parse(parser.getDataObject());
					setContent();
				}
			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				app.log(this, error.toString());
			}
		};
		StringRequest request = new StringRequest(API.ROCK_DETAIL_GET + rockId,
				listener, errorListener);
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	void getDraftRockDetail() {
		if (rockTbl == null) {
			rockTbl = new RockTable(this);
			rockTbl.open();
		}
		rock = rockTbl.read(draftRockId);

		RockPhotoTable photoTbl = new RockPhotoTable(this);
		photoTbl.open();
		ArrayList<RockPhoto> s = photoTbl.readAll(draftRockId);
		if (s.size() > 0) {
			for (RockPhoto x : s) {
				Log.d("THIS IS ", x.getUrl());
				pathsToAdd.add(x.getUrl());
			}
		}
		rock.setPhotos(s);
		setContent();
	}

	void initView() {
		spinCategory = (Spinner) findViewById(R.id.add_place_form_spinCategory);
		categoryAdapter = new CategoryAdapter(this, R.layout.simple_text);
		spinCategory.setAdapter(categoryAdapter);

		spinType = (Spinner) findViewById(R.id.add_place_form_spinType);
		typeAdapter = new TypeAdapter(this, R.layout.simple_text);
		spinType.setAdapter(typeAdapter);

		etName = (EditText) findViewById(R.id.add_place_form_etName);
		etFormation = (EditText) findViewById(R.id.add_place_form_etFormation);
		etAddress = (EditText) findViewById(R.id.add_place_form_etAddress);
		etCity = (EditText) findViewById(R.id.add_place_form_etCity);
		etLatitude = (EditText) findViewById(R.id.add_place_form_etLatitude);
		etLongitude = (EditText) findViewById(R.id.add_place_form_etLongitude);
		etDescription = (EditText) findViewById(R.id.add_place_form_etDescription);

		btnSaveToDraft = (Button) findViewById(R.id.add_place_form_btnSaveToDraft);
		btnSaveToDraft.setOnClickListener(this);
		btnSave = (Button) findViewById(R.id.add_place_form_btnSave);
		btnSave.setOnClickListener(this);

		if (mode == MODE_EDIT)
			btnSave.setText("Save");

		btnSetLocation = (Button) findViewById(R.id.add_place_form_btnSetLocation);
		btnSetLocation.setOnClickListener(this);

		// disini
		ib1 = (ImageView) findViewById(R.id.add_place_form_ibAddPhoto1);
		ib2 = (ImageView) findViewById(R.id.add_place_form_ibAddPhoto2);
		ib3 = (ImageView) findViewById(R.id.add_place_form_ibAddPhoto3);
		ib4 = (ImageView) findViewById(R.id.add_place_form_ibAddPhoto4);
		ib5 = (ImageView) findViewById(R.id.add_place_form_ibAddPhoto5);
		ib6 = (ImageView) findViewById(R.id.add_place_form_ibAddPhoto6);

		ib1.setOnClickListener(this);
		ib2.setOnClickListener(this);
		ib3.setOnClickListener(this);
		ib4.setOnClickListener(this);
		ib5.setOnClickListener(this);
		ib6.setOnClickListener(this);
	}

	public void addPhoto(View v) {
		showPickCameraOrGallery();
	}

	private void showPickCameraOrGallery() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setItems(
				new String[] { "Take a camera", "Choose from gallery" },
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (arg1 == 0) {
							Intent intent = new Intent(
									android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(intent, REQUEST_TAKE_CAMERA);
						} else {
							pickImage();
						}
					}
				});
		builder.create().show();
	}

	void pickImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, REQUEST_PICK_FROM_GALLERY);
	}

	public void goBack(View v) {
		finish();
	}

	void getCategories() {
		Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				app.log(this, response);
				try {
					JSONObject obj = new JSONObject(response);
					ResponseParser parser = new ResponseParser(obj);
					if (parser.getStatusCode() == 200) {
						JSONArray datas = parser.getDataArray();
						for (int i = 0; i < datas.length(); i++) {
							categoryAdapter.add(Category.Parse(datas
									.getJSONObject(i)));
							categoryAdapter.notifyDataSetChanged();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		};
		StringRequest request = new StringRequest(API.CATEGORIES_GET, listener,
				errorListener);
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	void getTypes() {
		Listener<String> listener = new Listener<String>() {

			@Override
			public void onResponse(String response) {
				app.log(this, response);
				try {
					JSONObject obj = new JSONObject(response);
					ResponseParser parser = new ResponseParser(obj);
					if (parser.getStatusCode() == 200) {
						JSONArray datas = parser.getDataArray();
						for (int i = 0; i < datas.length(); i++) {
							typeAdapter.add(Type.Parse(datas.getJSONObject(i)));
							typeAdapter.notifyDataSetChanged();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		};

		StringRequest request = new StringRequest(API.TYPES_GET, listener,
				errorListener);
		app.log(this, request.getUrl());
		app.getQueue().add(request);
	}

	RockTable rockTbl;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (rockTbl != null) {
			rockTbl.close();
		}
	}

	void saveToDraft() {
		if (rockTbl == null) {
			rockTbl = new RockTable(mContext);
			rockTbl.open();
		}
		Rock rock = new Rock();
		rock.setAddress(etAddress.getText().toString());
		if (categoryAdapter.getCount() > 0)
			rock.setCategoryId(categoryAdapter.getItem(
					spinCategory.getSelectedItemPosition()).getId());
		else
			rock.setCategoryId(0);
		rock.setCity(etCity.getText().toString());
		rock.setDesc(etDescription.getText().toString());
		rock.setFormation(etFormation.getText().toString());
		rock.setLatitude(Double.parseDouble(etLatitude.getText().toString()));
		rock.setLongitude(Double.parseDouble(etLongitude.getText().toString()));
		rock.setName(etName.getText().toString());
		if (typeAdapter.getCount() > 0)
			rock.setTypeId(typeAdapter.getItem(
					spinType.getSelectedItemPosition()).getId());
		else
			rock.setTypeId(0);
		rock.setUserId(app.getPref().getInt("user_id", 0));
		if (mode == MODE_ADD) {
			long id = rockTbl.insert(rock);
			if (pathsToAdd.size() > 0) {
				RockPhotoTable photoTbl = new RockPhotoTable(this);
				photoTbl.open();
				for (String s : pathsToAdd) {
					RockPhoto photo = new RockPhoto();
					photo.setCaption("");
					photo.setDate("");
					photo.setRockId((int) id);
					photo.setUrl(s);
					photoTbl.insert(photo);
				}
				photoTbl.close();
			}
		} else {
			rockTbl.updateRock(draftRockId, rock);
		}

		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.add_place_form_btnSaveToDraft:
			saveToDraft();
			break;
		case R.id.add_place_form_btnSetLocation:
			Intent intent = new Intent(this, SetLocationActivity.class);
			if (latitude != 0 && longitude != 0) { 
 			 	intent.putExtra("latitude", latitude);
				intent.putExtra("longitude", longitude);
			}  
			startActivityForResult(intent, 989);
			break;
		case R.id.add_place_form_ibAddPhoto1:
			pickFromGallery(SELECT_FILE1);
			break;
		case R.id.add_place_form_ibAddPhoto2:
			pickFromGallery(SELECT_FILE2);
			break;
		case R.id.add_place_form_ibAddPhoto3:
			pickFromGallery(SELECT_FILE3);
			break;
		case R.id.add_place_form_ibAddPhoto4:
			pickFromGallery(SELECT_FILE4);
			break;
		case R.id.add_place_form_ibAddPhoto5:
			pickFromGallery(SELECT_FILE5);
			break;
		case R.id.add_place_form_ibAddPhoto6:
			pickFromGallery(SELECT_FILE6);
			break;
		case R.id.add_place_form_btnSave:
			if (app.isLoggedIn()) {
				if (mode == MODE_ADD) {
					addRock();
				} else {
					if (fromWhere == FROM_SERVER)
						editRock();
					else
						addRock();

				}
			}
			break;
		case R.id.add_place_form_btnCancel:
			setResult(RESULT_CANCELED);
			this.finish();
			break;
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {

	}

	// upload image satu per satu, pas upload, imagebutton berubah
	class UploadImageTask extends AsyncTask<Void, Void, String> {

		String path;
		int ibId;

		public UploadImageTask(String path, int imageButtonId) {
			this.path = path;
			this.ibId = imageButtonId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (ibId == R.id.add_place_form_ibAddPhoto1)
				findViewById(R.id.add_place_form_progress1).setVisibility(
						View.VISIBLE);
			if (ibId == R.id.add_place_form_ibAddPhoto2)
				findViewById(R.id.add_place_form_progress2).setVisibility(
						View.VISIBLE);
			if (ibId == R.id.add_place_form_ibAddPhoto3)
				findViewById(R.id.add_place_form_progress3).setVisibility(
						View.VISIBLE);
			if (ibId == R.id.add_place_form_ibAddPhoto4)
				findViewById(R.id.add_place_form_progress4).setVisibility(
						View.VISIBLE);
			if (ibId == R.id.add_place_form_ibAddPhoto5)
				findViewById(R.id.add_place_form_progress5).setVisibility(
						View.VISIBLE);
			if (ibId == R.id.add_place_form_ibAddPhoto6)
				findViewById(R.id.add_place_form_progress6).setVisibility(
						View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// disini nih pembedanya
			if (mode == MODE_ADD) {
				return addPhoto(ibId, path);
			} else {
				try {
					if (ibId == R.id.add_place_form_ibAddPhoto1) {
						if (rock.getPhotos().get(0) != null) {
							return editExistingPhoto(ibId, path, rock
									.getPhotos().get(0));
						} else {
							return editNewPhoto(ibId, path);
						}
					}
					if (ibId == R.id.add_place_form_ibAddPhoto2) {
						if (rock.getPhotos().size() > 1
								& rock.getPhotos().get(1) != null) {
							return editExistingPhoto(ibId, path, rock
									.getPhotos().get(1));
						} else {
							return editNewPhoto(ibId, path);
						}
					}
					if (ibId == R.id.add_place_form_ibAddPhoto3) {
						if (rock.getPhotos().get(2) != null) {
							return editExistingPhoto(ibId, path, rock
									.getPhotos().get(2));
						} else {
							return editNewPhoto(ibId, path);
						}
					}
					if (ibId == R.id.add_place_form_ibAddPhoto4) {
						if (rock.getPhotos().get(3) != null) {

							return editExistingPhoto(ibId, path, rock
									.getPhotos().get(3));
						} else {
							return editNewPhoto(ibId, path);
						}
					}
					if (ibId == R.id.add_place_form_ibAddPhoto5) {
						if (rock.getPhotos().get(4) != null) {
							return editExistingPhoto(ibId, path, rock
									.getPhotos().get(4));
						} else {
							return editNewPhoto(ibId, path);
						}
					}
					if (ibId == R.id.add_place_form_ibAddPhoto6) {
						if (rock.getPhotos().get(5) != null) {
							return editExistingPhoto(ibId, path, rock
									.getPhotos().get(5));
						} else {
							return editNewPhoto(ibId, path);
						}
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			app.log(this, result);
			if (ibId == R.id.add_place_form_ibAddPhoto1)
				findViewById(R.id.add_place_form_progress1).setVisibility(
						View.GONE);
			if (ibId == R.id.add_place_form_ibAddPhoto2)
				findViewById(R.id.add_place_form_progress2).setVisibility(
						View.GONE);
			if (ibId == R.id.add_place_form_ibAddPhoto3)
				findViewById(R.id.add_place_form_progress3).setVisibility(
						View.GONE);
			if (ibId == R.id.add_place_form_ibAddPhoto4)
				findViewById(R.id.add_place_form_progress4).setVisibility(
						View.GONE);
			if (ibId == R.id.add_place_form_ibAddPhoto5)
				findViewById(R.id.add_place_form_progress5).setVisibility(
						View.GONE);
			if (ibId == R.id.add_place_form_ibAddPhoto6)
				findViewById(R.id.add_place_form_progress6).setVisibility(
						View.GONE);

			if (result != null) {
				ResponseParser parser = new ResponseParser(result);
				if (parser.getStatusCode() == 200) {
					String url = parser.getDataString();
					if (ibId == R.id.add_place_form_ibAddPhoto1) {
						imageUrl1 = url;
					} else if (ibId == R.id.add_place_form_ibAddPhoto2) {
						imageUrl2 = url;
					} else if (ibId == R.id.add_place_form_ibAddPhoto3) {
						imageUrl3 = url;
					} else if (ibId == R.id.add_place_form_ibAddPhoto4) {
						imageUrl4 = url;
					} else if (ibId == R.id.add_place_form_ibAddPhoto5) {
						imageUrl5 = url;
					} else if (ibId == R.id.add_place_form_ibAddPhoto6) {
						imageUrl1 = url;
					}

					app.log(this, "URL IMAGE " + url);
				}
			}

		}

	}

	String imageUrl1, imageUrl2, imageUrl3, imageUrl4, imageUrl5, imageUrl6;

	void addRock() {
		final int typeId = typeAdapter.getItem(
				spinType.getSelectedItemPosition()).getId();
		final int categoryId = categoryAdapter.getItem(
				spinCategory.getSelectedItemPosition()).getId();
		final String name = etName.getText().toString();
		final String description = etDescription.getText().toString();
		final String address = etAddress.getText().toString();
		final String city = etCity.getText().toString();
		final String formation = etFormation.getText().toString();
		final double longitude = Double.parseDouble(etLongitude.getText()
				.toString());
		final double latitude = Double.parseDouble(etLatitude.getText()
				.toString());

		if (name.length() > 0 && description.length() > 0
				&& address.length() > 0 && city.length() > 0
				&& formation.length() > 0 && pathsToAdd.size() > 0) {
			final ProgressDialog prog = app.addProgress(
					getSupportFragmentManager(), "Adding object");
			Listener<String> listener = new Listener<String>() {
				@Override
				public void onResponse(String response) {
					prog.dismiss();
					app.log(this, response);
					ResponseParser parser = new ResponseParser(response);
					if (parser.getStatusCode() == 200) {
						try {
							int rockId = parser.getJSONObject().getInt(
									"rock_id");
							Intent addService = new Intent(
									AddPlaceActivity.this, AddRockService.class);
							addService.putExtra("rockId", rockId);
							addService.putExtra("paths", pathsToAdd);
							startService(addService);
							setResult(RESULT_OK);
							AddPlaceActivity.this.finish();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else if (parser.getStatusCode() == 400) {
						app.shortToast(mContext, parser.getFirstError());
					}
				}
			};
			ErrorListener errorListener = new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub

					error.printStackTrace();
					if (error instanceof TimeoutError)
						app.shortToast(mContext, "time out");
				}
			};
			StringRequest request = new StringRequest(Method.POST,
					API.ROCK_ADD_POST, listener, errorListener) {
				@Override
				protected Map<String, String> getParams()
						throws AuthFailureError {
					Map<String, String> params = new HashMap<String, String>();
					params.put("type_id", String.valueOf(typeId));
					params.put("category_id", String.valueOf(categoryId));
					params.put("name", name);
					params.put("description", description);
					params.put("address", address);
					params.put("city", city);
					params.put("formation", formation);
					params.put("longitude", String.valueOf(longitude));
					params.put("latitude", String.valueOf(latitude));
					params.put("user_id",
							String.valueOf(app.getPref().getInt("user_id", 0)));

					if (imageUrl1 != null) {
						params.put("image_url1", imageUrl1);
					}
					if (imageUrl2 != null) {
						params.put("image_url2", imageUrl2);
					}
					if (imageUrl3 != null) {
						params.put("image_url3", imageUrl3);
					}
					if (imageUrl4 != null) {
						params.put("image_url4", imageUrl4);
					}
					if (imageUrl5 != null) {
						params.put("image_url5", imageUrl5);
					}
					if (imageUrl6 != null) {
						params.put("image_url6", imageUrl6);
					}

					return params;
				}
			};
			app.log(this, request.getUrl());
			app.getQueue().add(request);
		} else {
			app.shortToast(AddPlaceActivity.this,
					"Please fill every input data");
		}

	}

	void editRock() {
		ProgressDialog prog = new ProgressDialog();
		Bundle args = new Bundle();
		args.putString(ProgressDialog.MESSAGE, "Editing object");
		prog.setArguments(args);
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				app.log(this, response);
				ResponseParser parser = new ResponseParser(response);
				if (parser.getStatusCode() == 200) {
					setResult(RESULT_OK);
					AddPlaceActivity.this.finish();
				} else if (parser.getStatusCode() == 400) {
					app.shortToast(mContext, parser.getFirstError());
				}
			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

				error.printStackTrace();
				if (error instanceof TimeoutError)
					app.shortToast(mContext, "time out");
			}
		};
		StringRequest request = new StringRequest(Method.PUT, API.ROCK_EDIT_PUT
				+ rock.getId(), listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put(
						"type_id",
						String.valueOf(typeAdapter.getItem(
								spinType.getSelectedItemPosition()).getId()));
				params.put("category_id", String.valueOf(categoryAdapter
						.getItem(spinCategory.getSelectedItemPosition())
						.getId()));
				params.put("name", etName.getText().toString());
				params.put("description", etDescription.getText().toString());
				params.put("address", etAddress.getText().toString());
				params.put("city", etCity.getText().toString());
				params.put("formation", etFormation.getText().toString());
				params.put("longitude",
						String.valueOf(etLongitude.getText().toString()));
				params.put("latitude",
						String.valueOf(etLatitude.getText().toString()));
				return params;
			}
		};
		app.log(this, request.getUrl());
		app.getQueue().add(request);

	}

	// dari sini
	private static final int SELECT_FILE1 = 1;
	private static final int SELECT_FILE2 = 2;
	private static final int SELECT_FILE3 = 3;
	private static final int SELECT_FILE4 = 4;
	private static final int SELECT_FILE5 = 5;
	private static final int SELECT_FILE6 = 6;

	HttpEntity resEntity;

	ImageView ib1, ib2, ib3, ib4, ib5, ib6;

	public void pickFromGallery(int req_code) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(Intent.createChooser(intent, "Select File"),
				req_code);
	}

	ArrayList<String> pathsToAdd = new ArrayList<String>();

	public void onActivityResult(int req_code, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (req_code != 989) {
				Uri selectedImageUri = data.getData();
				int ibId = 0;
				HashMap<String, Object> map = app.getThumbnail(
						selectedImageUri, 800);
				if (req_code == SELECT_FILE1) {
					ibId = R.id.add_place_form_ibAddPhoto1;
				} else if (req_code == SELECT_FILE2) {
					ibId = R.id.add_place_form_ibAddPhoto2;
				} else if (req_code == SELECT_FILE3) {
					ibId = R.id.add_place_form_ibAddPhoto3;
				} else if (req_code == SELECT_FILE4) {
					ibId = R.id.add_place_form_ibAddPhoto4;
				} else if (req_code == SELECT_FILE5) {
					ibId = R.id.add_place_form_ibAddPhoto5;
				} else
					ibId = R.id.add_place_form_ibAddPhoto6;

				((ImageView) findViewById(ibId)).setImageBitmap((Bitmap) map
						.get("bitmap"));

				if (mode == MODE_ADD) {
					if (pathsToAdd.contains((String) map.get("path"))) {
						pathsToAdd.remove((String) map.get("path"));
					}
					pathsToAdd.add((String) map.get("path"));
				} else {
					// mode edit
					RockPhoto photo = null;
					try {
						if (ibId == R.id.add_place_form_ibAddPhoto1) {
							photo = rock.getPhotos().get(0);
						} else if (ibId == R.id.add_place_form_ibAddPhoto2) {
							photo = rock.getPhotos().get(1);
						} else if (ibId == R.id.add_place_form_ibAddPhoto3) {
							photo = rock.getPhotos().get(2);
						} else if (ibId == R.id.add_place_form_ibAddPhoto4) {
							photo = rock.getPhotos().get(3);
						} else if (ibId == R.id.add_place_form_ibAddPhoto5) {
							photo = rock.getPhotos().get(4);
						} else if (ibId == R.id.add_place_form_ibAddPhoto6) {
							photo = rock.getPhotos().get(5);
						}
					} catch (NullPointerException e) {
						e.printStackTrace();
					} catch (IndexOutOfBoundsException e) {
						e.printStackTrace();
					}
					if (fromWhere == FROM_SERVER) {
						if (photo != null) {
							app.log(this, "edit existing image");
							new EditExistingImageTask(ibId,
									(String) map.get("path"), photo).execute();
						} else {
							app.log(this, "edit new image");
							new EditNewImageTask(ibId, (String) map.get("path"))
									.execute();
						}
					}
					// from draft
					else {
						pathsToAdd.remove(photo.getUrl());
						pathsToAdd.add((String) map.get("path"));
					}

				}
			} else {
				if (data.hasExtra("latitude")) {
					setManual = true;
					DecimalFormat df = new DecimalFormat("0.0000000");
					latitude = data.getDoubleExtra("latitude", 0);
					longitude = data.getDoubleExtra("longitude", 0);
					etLatitude.setText(df.format(latitude));
					etLongitude.setText(df.format(longitude));
				}
 			}
		}
	}

	double latitude, longitude;
	boolean setManual;

	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @author paulburke
	 */
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	String addPhoto(int imageViewId, String path) {
		File file = new File(path);
		String urlString = API.CLIENTS_UPLOAD_IMAGE;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urlString);
			MultipartEntity reqEntity = new MultipartEntity();

			if (file.exists()) {
				FileBody bin = new FileBody(file);
				reqEntity.addPart("uploadedfile", bin);

			}
			post.setEntity(reqEntity);
			app.log(this, reqEntity.toString());
			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();
			String response_str = EntityUtils.toString(resEntity);
			return response_str;

		} catch (Exception e) {
			return null;
		}

	}

	String editExistingPhoto(int imageViewId, String path, RockPhoto photo) {
		File file = new File(path);
		String urlString = API.CLIENTS_EDIT_IMAGE;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urlString);
			MultipartEntity reqEntity = new MultipartEntity();
			if (file.exists()) {
				FileBody bin = new FileBody(file);
				reqEntity.addPart("uploadedfile", bin);
				reqEntity.addPart("rockimageid",
						new StringBody(String.valueOf(photo.getId())));
				reqEntity.addPart("rockurl", new StringBody(photo.getUrl()));
			}
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();
			String response_str = EntityUtils.toString(resEntity);
			return response_str;
		} catch (Exception e) {
			return null;
		}
	}

	String editNewPhoto(int imageViewId, String path) {
		File file = new File(path);
		String urlString = API.CLIENTS_EDIT_NEW_IMAGE;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(urlString);
			MultipartEntity reqEntity = new MultipartEntity();

			if (file.exists()) {
				FileBody bin = new FileBody(file);
				reqEntity.addPart("uploadedfile", bin);
				reqEntity.addPart("rockid",
						new StringBody(String.valueOf(rockId)));
			}
			post.setEntity(reqEntity);
			app.log(this, reqEntity.toString());
			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();
			String response_str = EntityUtils.toString(resEntity);
			return response_str;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	class EditExistingImageTask extends AsyncTask<Void, Void, String> {

		int imgViewId;
		String path;
		RockPhoto photo;

		// ini sudah path menuju fiele tumbnail
		public EditExistingImageTask(int imgViewId, String path, RockPhoto photo) {
			this.imgViewId = imgViewId;
			this.path = path;
			this.photo = photo;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			return editExistingPhoto(imgViewId, path, photo);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				try {
					JSONObject obj = new JSONObject(result);
					if (obj.getInt("status") == 200) {
						String url = obj.getString("data");
						this.photo.setUrl(url);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class EditNewImageTask extends AsyncTask<Void, Void, String> {

		int imageViewId;
		String path;

		public EditNewImageTask(int imageViewId, String path) {
			this.imageViewId = imageViewId;
			this.path = path;
		}

		@Override
		protected String doInBackground(Void... params) {
			return editNewPhoto(imageViewId, path);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				app.log(this, result);
			} else
				app.log("rsult ", "null");
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (mode == MODE_ADD && (loc = LocationServices.FusedLocationApi.getLastLocation(
				client)) != null && !setManual) {
			latitude = loc.getLatitude();
			longitude = loc.getLongitude();
			etLatitude.setText(String.valueOf(loc.getLatitude()));
			etLongitude.setText(String.valueOf(loc.getLongitude()));
			setManual = true;
		}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (pathsToAdd.size() > 0) {
			outState.putStringArrayList("paths", pathsToAdd);
		}
	}

	int[] imgIds = new int[] { R.id.add_place_form_ibAddPhoto1,
			R.id.add_place_form_ibAddPhoto2, R.id.add_place_form_ibAddPhoto3,
			R.id.add_place_form_ibAddPhoto4, R.id.add_place_form_ibAddPhoto5,
			R.id.add_place_form_ibAddPhoto6 };

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("paths")) {
			pathsToAdd = savedInstanceState.getStringArrayList("paths");
			for (int i = 0; i < pathsToAdd.size(); i++) {
				((ImageButton) findViewById(imgIds[i]))
						.setImageBitmap(BitmapFactory.decodeFile(pathsToAdd
								.get(i)));
			}
		}
	}

}
