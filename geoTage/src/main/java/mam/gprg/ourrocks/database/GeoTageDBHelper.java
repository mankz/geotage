package mam.gprg.ourrocks.database;

import mam.gprg.ourrocks.model.Rock;
import mam.gprg.ourrocks.model.RockPhoto;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class GeoTageDBHelper extends SQLiteOpenHelper {

	static String DB_Name = "geotage.db";
	static int DB_Version = 1;

	public static String TABLE_ROCK = "tb_rock";
	public static String TABLE_PHOTO = "tb_photo";

	static String CREATE_TABLE_ROCK = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_ROCK + "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Rock.NAME + " TEXT, " + Rock.DESCRIPTION + " TEXT, "
			+ Rock.ADDRESS + " TEXT, " + Rock.CITY + " TEXT, " + Rock.TYPE_ID
			+ " INTEGER, " + Rock.CATEGORY_ID + " INTEGER, " + Rock.LATITUDE
			+ " REAL, " + Rock.LONGITUDE + " REAL," + Rock.USER_ID
			+ " INTEGER, " + Rock.FORMATION + " TEXT, " + Rock.DATE + " TEXT, "
			+ Rock.STATUS + " INTEGER )";

	static String CREATE_TABLE_PHOTO = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_PHOTO + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ RockPhoto.URL + " TEXT, " + RockPhoto.ROCK_ID + " INTEGER, "
			+ RockPhoto.ROCK_CAPTION + " TEXT, " + RockPhoto.DATE + " TEXT)";

	public GeoTageDBHelper(Context context) {
		super(context, DB_Name, null, DB_Version);
	}

	public GeoTageDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DB_Name, factory, DB_Version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_PHOTO);
		db.execSQL(CREATE_TABLE_ROCK);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROCK);
		db.execSQL("DROP TABLE IF EXISTS  " + TABLE_PHOTO);

		onCreate(db);
	}

}
