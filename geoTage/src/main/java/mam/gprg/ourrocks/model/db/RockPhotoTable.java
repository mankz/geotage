package mam.gprg.ourrocks.model.db;

import java.util.ArrayList;

import mam.gprg.ourrocks.database.GeoTageDBHelper;
import mam.gprg.ourrocks.model.RockPhoto;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RockPhotoTable {

	GeoTageDBHelper helper;
	SQLiteDatabase db;

	public RockPhotoTable(Context ctx) {
		helper = new GeoTageDBHelper(ctx);
	}

	public void open() {
		db = helper.getWritableDatabase();
	}

	public void close() {
		db.close();
	}

	public long insert(RockPhoto photo) {
		return db.insert(GeoTageDBHelper.TABLE_PHOTO, null,
				photoToValues(photo));
	}

	public ArrayList<RockPhoto> readAll(int rockId) {
		ArrayList<RockPhoto> photos = new ArrayList<RockPhoto>();
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ GeoTageDBHelper.TABLE_PHOTO + " WHERE " + RockPhoto.ROCK_ID
				+ " = ?", new String[] { String.valueOf(rockId) });
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			RockPhoto photo = cursorToPhoto(cursor);
			photos.add(photo);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return photos;
	}

	public RockPhoto read(int rockId) {
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ GeoTageDBHelper.TABLE_PHOTO + " WHERE rock_id = ?",
				new String[] { String.valueOf(rockId) });
		if (cursor.moveToFirst()) {
			return cursorToPhoto(cursor);
		} else
			return null;
	}

	public RockPhoto cursorToPhoto(Cursor cursor) {
		RockPhoto photo = new RockPhoto();
		photo.setCaption(cursor.getString(cursor
				.getColumnIndex(RockPhoto.ROCK_CAPTION)));
		photo.setDate(cursor.getString(cursor.getColumnIndex(RockPhoto.DATE)));
		photo.setId(cursor.getInt(cursor.getColumnIndex("id")));
		photo.setRockId(cursor.getInt(cursor.getColumnIndex(RockPhoto.ROCK_ID)));
		photo.setUrl(cursor.getString(cursor.getColumnIndex(RockPhoto.URL)));
		return photo;
	}

	public ContentValues photoToValues(RockPhoto photo) {
		ContentValues values = new ContentValues();
		values.put(RockPhoto.DATE, photo.getDate());
		values.put(RockPhoto.ROCK_CAPTION, photo.getCaption());
		values.put(RockPhoto.ROCK_ID, photo.getRockId());
		values.put(RockPhoto.URL, photo.getUrl());
		return values;
	}

	public int delete(int id) {
		return db.delete(GeoTageDBHelper.TABLE_PHOTO, "id = ?",
				new String[] { String.valueOf(id) });
	}

	public int updateRock(int id, RockPhoto photo) {
		return db.update(GeoTageDBHelper.TABLE_PHOTO, photoToValues(photo),
				"id=?", new String[] { String.valueOf(id) });
	}
}
