package mam.gprg.ourrocks.model.db;

import java.util.ArrayList;

import mam.gprg.ourrocks.database.GeoTageDBHelper;
import mam.gprg.ourrocks.model.Rock;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RockTable {

	GeoTageDBHelper helper;
	SQLiteDatabase db;

	public RockTable(Context ctx) {
		helper = new GeoTageDBHelper(ctx); 
	}

	public void open() {
		db = helper.getWritableDatabase();
	}

	public void close() {
		db.close();
	}

	public long insert(Rock rock) {
		return db.insert(GeoTageDBHelper.TABLE_ROCK, null, rockToValues(rock));
	}

	public ArrayList<Rock> readAll(int userId) {
		ArrayList<Rock> rocks = new ArrayList<Rock>();
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ GeoTageDBHelper.TABLE_ROCK + " WHERE " + Rock.USER_ID
				+ " = ?", new String[] { String.valueOf(userId) });
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Rock rock = cursorToRock(cursor);
			rocks.add(rock);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return rocks;
	}

	public Rock read(int id) {
		
 		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ GeoTageDBHelper.TABLE_ROCK + " WHERE id = ?", new String[] { String.valueOf(id) });
		if(cursor.moveToFirst()){
			return cursorToRock(cursor);
		} else
			return null;
	}

	public Rock cursorToRock(Cursor cursor) {
		Rock rock = new Rock();
		rock.setAddress(cursor.getString(cursor.getColumnIndex(Rock.ADDRESS)));
		rock.setCategoryId(cursor.getInt(cursor
				.getColumnIndex(Rock.CATEGORY_ID)));
		rock.setCity(cursor.getString(cursor.getColumnIndex(Rock.CITY)));
		rock.setDesc(cursor.getString(cursor.getColumnIndex(Rock.DESCRIPTION)));
		rock.setFormation(cursor.getString(cursor
				.getColumnIndex(Rock.FORMATION)));
		rock.setId(cursor.getInt(cursor.getColumnIndex("id")));
		rock.setLatitude(cursor.getDouble(cursor.getColumnIndex(Rock.LATITUDE)));
		rock.setLongitude(cursor.getDouble(cursor
				.getColumnIndex(Rock.LONGITUDE)));
		rock.setName(cursor.getString(cursor.getColumnIndex(Rock.NAME)));
		rock.setTypeId(cursor.getInt(cursor.getColumnIndex(Rock.TYPE_ID)));
		rock.setUserId(cursor.getInt(cursor.getColumnIndex(Rock.USER_ID)));
		return rock;
	}

	public ContentValues rockToValues(Rock rock) {
		ContentValues values = new ContentValues();
		values.put(Rock.ADDRESS, rock.getAddress());
		values.put(Rock.CATEGORY_ID, rock.getCategoryId());
		values.put(Rock.CITY, rock.getCity());
		values.put(Rock.DESCRIPTION, rock.getDesc());
		values.put(Rock.FORMATION, rock.getFormation());
		values.put(Rock.LATITUDE, rock.getLatitude());
		values.put(Rock.LONGITUDE, rock.getLongitude());
		values.put(Rock.NAME, rock.getName());
		values.put(Rock.TYPE_ID, rock.getTypeId());
		values.put(Rock.USER_ID, rock.getUserId());
		return values;
	}

	public int delete(int id) {
		return db.delete(GeoTageDBHelper.TABLE_ROCK, "id = ?",
				new String[] { String.valueOf(id) });
	}

	public int updateRock(int id, Rock rock) {
		return db.update(GeoTageDBHelper.TABLE_ROCK, rockToValues(rock),
				"id=?", new String[] { String.valueOf(id) });
	}
}
