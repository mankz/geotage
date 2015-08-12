package mam.gprg.ourrocks.model;

import mam.gprg.ourrocks.util.ParseHelper;

import org.json.JSONObject;

public class Article {

	static String ID = "id";
	static String USER_ID = "user_id";
	static String TITLE = "title";
	static String CONTENT = "content";
	static String DATE = "date";
	static String STATUS = "status";
	static String USER = "user";
	
	int id;
	int userId;
	String title;
	String content;
	String date;
	int status;
	User user;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
	
	public static Article Parse(JSONObject obj){
		ParseHelper helper  = new ParseHelper(obj);
		Article article = new Article();
		article.setId(helper.getInt(ID));
		article.setUserId(helper.getInt(USER_ID));
		article.setTitle(helper.getString(TITLE));
		article.setContent(helper.getString(CONTENT));
		article.setDate(helper.getString(DATE));
		article.setStatus(helper.getInt(STATUS));
		if(helper.getJSONObject(USER) != null){
			article.setUser(User.Parse(helper.getJSONObject(USER)));
		}
		return article;
	}
}
