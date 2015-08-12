package mam.gprg.ourrocks.model;

import java.util.ArrayList;

import mam.gprg.ourrocks.util.ParcelHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class RockPhoto implements Parcelable {

	public static String ID = "id";
	public static String URL = "url";
	public static String ROCK_ID = "rock_id";
	public static String ROCK_CAPTION = "caption";
	public static String DATE = "date";

	int id;
	int rockId;
	String url;
	String caption;
	String date;
	boolean displayed;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRockId() {
		return rockId;
	}

	public void setRockId(int rockId) {
		this.rockId = rockId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	
	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}


	public static Parcelable.Creator<RockPhoto> CREATOR = new Creator<RockPhoto>() {

		@Override
		public RockPhoto[] newArray(int size) {
			return new RockPhoto[size];
		}

		@Override
		public RockPhoto createFromParcel(Parcel source) {
			RockPhoto photo = new RockPhoto();
			photo.setId(source.readInt());
			photo.setRockId(source.readInt());
			photo.setUrl(ParcelHelper.read(source));
			photo.setCaption(ParcelHelper.read(source));
			photo.setDate(ParcelHelper.read(source));
			return photo;
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(rockId);
		ParcelHelper.write(dest, url);
		ParcelHelper.write(dest, caption);
		ParcelHelper.write(dest, date);
	}

	public static RockPhoto Parse(JSONObject obj) {
		RockPhoto photo = new RockPhoto();
		try {
			photo.setId(obj.getInt("id"));
			photo.setRockId(obj.getInt("rock_id"));
			photo.setUrl(obj.getString("url"));
			photo.setCaption(obj.getString("caption"));
			photo.setDate(obj.getString("date"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return photo;
	}

	public static ArrayList<RockPhoto> Parse(JSONArray arr) {
		ArrayList<RockPhoto> photos = new ArrayList<RockPhoto>();
		if (arr.length() > 0) {
			for (int i = 0; i < arr.length(); i++) {
				try {
					photos.add(Parse(arr.getJSONObject(i)));
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		return photos;
	}

}
