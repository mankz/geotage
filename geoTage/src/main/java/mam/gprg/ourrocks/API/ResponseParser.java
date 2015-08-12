package mam.gprg.ourrocks.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseParser {

	static String STATUS_CODE = "status";
	static String DATA = "data";

	JSONObject response;

	public ResponseParser(String string) {
		try {
			JSONObject obj = new JSONObject(string);
			this.response = obj;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getJSONObject() {
		return this.response;
	}

	public ResponseParser(JSONObject response) {
		this.response = response;
	}

	public int getStatusCode() {
		try {
			return this.response.getInt(STATUS_CODE);
		} catch (JSONException e) {
			return 404;
		}
	}

	public JSONObject getDataObject() {
		try {
			return this.response.getJSONObject(DATA);
		} catch (JSONException e) {
			return null;
		}
	}

	public String getDataString() {
		try {
			return this.response.getString(DATA);
		} catch (JSONException e) {
			return null;
		}
	}

	public JSONArray getDataArray() {
		try {
			return this.response.getJSONArray(DATA);
		} catch (JSONException e) {
			return null;
		}
	}

	public boolean getDataBoolean() {
		try {
			return this.response.getBoolean(DATA);
		} catch (JSONException e) {
			return false;
		}
	}

	public JSONArray getErrors() {
		try {
			return response.getJSONArray("errors");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getCurrentPage() {
		try {
			return response.getInt("current_page");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int getTotalResult() {
		try {
			return response.getInt("total_result");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public String getFirstError() {

		try {
			return getErrors().getString(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getMessage() {
		try {
			return response.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
