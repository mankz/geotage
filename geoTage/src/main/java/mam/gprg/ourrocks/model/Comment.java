package mam.gprg.ourrocks.model;

import mam.gprg.ourrocks.util.ParseHelper;

import org.json.JSONObject;

import android.util.Log;

public class Comment {

	static String ID = "id";
	static String ROCK_ID = "rock_id";
	static String USER_ID = "user_id";
	static String COMMENT = "comment";
	static String DATE = "date";
	static String STATUS = "status";
	static String USER = "user";

	int id;
	int rockId;
	int userId;
	String comment;
	String date;
	int status;
	User user;

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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return this.user;
	}

	public static Comment Parse(JSONObject obj) {
		Log.d("PARSE", obj.toString());
		ParseHelper helper = new ParseHelper(obj);
		Comment comment = new Comment();
		comment.setId(helper.getInt(ID));
		comment.setRockId(helper.getInt(ROCK_ID));
		comment.setUserId(helper.getInt(USER_ID));
		comment.setComment(helper.getString(COMMENT));
		comment.setDate(helper.getString(DATE));
		comment.setStatus(helper.getInt(STATUS));
		if (obj.has(USER)) {
			Log.d("USER", "ADA");
			comment.setUser(User.Parse(helper.getJSONObject(USER)));
		} else {

			Log.d("USER", "tidak ada");
		}
		return comment;
	}

}
