package mam.gprg.ourrocks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import mam.gprg.ourrocks.API.API;
import mam.gprg.ourrocks.API.ResponseParser;
import mam.gprg.ourrocks.detail.RocksDetailActivity;
import mam.gprg.ourrocks.gcm.RegistrationIntentService;
import mam.gprg.ourrocks.locations.DirectoryFragment;
import mam.gprg.ourrocks.locations.DirectoryFragment.DirectoryFragmentListener;
import mam.gprg.ourrocks.map.RocksFragment;
import mam.gprg.ourrocks.model.Rock;
import mam.gprg.ourrocks.settings.MainSettingFragment;
import mam.gprg.ourrocks.settings.ProfileFragment;
import mam.gprg.ourrocks.settings.SettingFragment;
import mam.gprg.ourrocks.settings.SettingFragment.ToggleSettingListener;
import mam.gprg.ourrocks.userdatas.AddPlaceFragment;
import mam.gprg.ourrocks.util.ViewPagerAdapter;

public class MainActivity extends FragmentActivity implements
        OnPageChangeListener, ToggleSettingListener, DirectoryFragmentListener {

    GeoTage app;
    Activity mContext;
    View layout;
    ViewPager mPager;

    ViewPagerAdapter adapter;

    int[] menuIds = new int[]{R.id.main_ibMap, R.id.main_ibDirectory,
            R.id.main_ibNearestPlace, R.id.main_ibAddPlace};

    RocksFragment mRockFragment;
    DirectoryFragment mDirFragment;
    AddPlaceFragment mAddPlaceFragment;
    MainSettingFragment mMainSettingFragment;

    NewCommentReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (GeoTage) getApplication();
        mContext = this;
        layout = LayoutInflater.from(mContext).inflate(R.layout.main, null);
        setContentView(layout);
        mPager = (ViewPager) findViewById(R.id.main_viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(this);
        mPager.setOffscreenPageLimit(5);

        mRockFragment = new RocksFragment();
        mDirFragment = new DirectoryFragment();
        mAddPlaceFragment = new AddPlaceFragment();
        mMainSettingFragment = new MainSettingFragment();
        adapter.addFragment(mRockFragment);
        adapter.addFragment(mDirFragment);
        adapter.addFragment(mAddPlaceFragment);
        adapter.addFragment(mMainSettingFragment);
        app.setActiveMenu(layout, menuIds, 0);

        setContentView(layout);


        //app.registerGCM(this);
        if (app.isLoggedIn() && checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        receiver = new NewCommentReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //	registerReceiver(receiver, new IntentFilter("add_comment"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //	unregisterReceiver(receiver);
    }

    public void onMenuClick(View v) {
        if (!v.isSelected()) {
            mPager.setCurrentItem(Arrays.binarySearch(menuIds, v.getId()));
            // setActiveMenu(Arrays.binarySearch(menuIds, v.getId()));
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        app.setActiveMenu(layout, menuIds, arg0);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (arg0 == AddPlaceFragment.ADD_PLACE) {
            if (arg1 == RESULT_OK) {
                mDirFragment.reload(false);
                mAddPlaceFragment.reload(false);
            }
        } else if (arg0 == GeoTage.EDIT_ROCK) {
            if (arg1 == RESULT_OK) {
                mDirFragment.reload(false);
                mAddPlaceFragment.reload(false);
            } else {
            }
        } else if (arg0 == ProfileFragment.REQUEST_PICK_FROM_GALLERY
                && arg1 == RESULT_OK) {
            app.log(this, "update lo harusnya");
            if (arg2 != null) {
                Uri imageUri = arg2.getData();
                try {
                    HashMap<String, Object> data = app.getThumbnail(imageUri,
                            250);
                    Bitmap selectedAva = (Bitmap) data.get("bitmap");
                    String path = (String) data.get("path");
                    mMainSettingFragment.getProfileFragment(
                            ProfileFragment.MODE_EDIT).changeAvatar(
                            selectedAva, path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getPath(Uri uri) {
        if (uri == null) {

            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure want to exit?");
        builder.setPositiveButton("Yes", new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("No", new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.show();

    }

    @Override
    public void onToggleNotif(boolean on) {
        // TODO Auto-generated method stub
        if (on) {
            app.registerGCM(this);
        } else {
            app.unregisterGCM();
        }
    }

    @Override
    public void onToggleCompass(boolean on) {
        Editor editor = app.getPref().edit();
        editor.putBoolean(SettingFragment.SHOW_COMPASS, on);
        editor.commit();
        mRockFragment.showCompass(on);
    }

    @Override
    public void onAddRock(Rock rock) {
        mRockFragment.add(rock);
    }

    @Override
    public void onClear() {
        mRockFragment.clear();
    }

    public void getRockDetailAndNotify(Bundle extras) {
        final int rockId = Integer.parseInt(extras.getString("object_id"));
        final int senderId = Integer.parseInt(extras.getString("sender_id"));
        final String message = extras.getString("message");
        final String senderName = extras.getString("sender_name");
        final String senderData = extras.getString("sender_data");
        Listener<JSONObject> listener = new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                app.log(this, response.toString());
                ResponseParser parser = new ResponseParser(response);
                if (parser.getStatusCode() == 200) {
                    Rock rock = Rock.Parse(parser.getDataObject());
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(
                            mContext);
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    builder.setOnlyAlertOnce(true);
                    builder.setContentTitle("New Comment");
                    builder.setContentText(senderData);
                    builder.setTicker(message);
                    builder.setAutoCancel(true);
                    builder.setSmallIcon(R.drawable.geotage_logo);
                    builder.setLargeIcon(BitmapFactory.decodeResource(
                            getResources(), R.drawable.geotage_logo));
                    Intent intent = new Intent(mContext,
                            RocksDetailActivity.class);
                    intent.putExtra("rock_id", rockId);
                    intent.putExtra("rock", rock);
                    PendingIntent pIntent = PendingIntent.getActivity(mContext,
                            0, intent, PendingIntent.FLAG_ONE_SHOT);
                    builder.setContentIntent(pIntent);
                    nm.notify(1, builder.build());
                }
            }
        };
        ErrorListener errorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        };
        JsonObjectRequest request = new JsonObjectRequest(API.ROCK_DETAIL_GET
                + rockId, null, listener, errorListener);
        app.log(this, request.getUrl());
        app.getQueue().add(request);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        9000).show();
            } else {
                Log.i("MainActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    class NewCommentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            if (action.equals("add_comment")) {
                Bundle extras = arg1.getExtras();
                getRockDetailAndNotify(extras);
            } else {
            }

        }

    }
}
