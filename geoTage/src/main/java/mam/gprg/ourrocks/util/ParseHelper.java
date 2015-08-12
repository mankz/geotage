package mam.gprg.ourrocks.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseHelper {

	JSONObject obj;

	public ParseHelper(JSONObject obj) {
		this.obj = obj;
	}

	public int getInt(String field) {
		if (obj.has(field)) {
			try {
				return obj.getInt(field);
			} catch (JSONException e) {
				return 0;
			}
		}
		return 0;
	}

	public double getDouble(String field) {
		if (obj.has(field)) {
			try {
				return obj.getDouble(field);
			} catch (JSONException e) {
				return 0;
			}
		}
		return 0;
	}

	public double getLong(String field) {
		if (obj.has(field)) {
			try {
				return obj.getLong(field);
			} catch (JSONException e) {
				return 0;
			}
		}
		return 0;
	}

	public String getString(String field) {
		if (obj.has(field)) {
			try {
				return obj.getString(field);
			} catch (JSONException e) {
				return null;
			}
		}
		return null;
	}

	public boolean getBoolean(String field) {
		if (obj.has(field)) {
			try {
				return obj.getBoolean(field);
			} catch (JSONException e) {
				return false;
			}
		}
		return false;
	}

	public JSONObject getJSONObject(String field) {
		if (obj.has(field)) {
			try {
				return obj.getJSONObject(field);
			} catch (JSONException e) {
				return null;
			}
		}
		return null;
	}

	
	public JSONArray getJSONArray(String field){
		if (obj.has(field)) {
			try {
				return obj.getJSONArray(field);
			} catch (JSONException e) {
				return null;
			}
		}
		return null;		
		} 
}
