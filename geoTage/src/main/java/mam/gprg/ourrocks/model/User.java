package mam.gprg.ourrocks.model;

import mam.gprg.ourrocks.util.ParcelHelper;
import mam.gprg.ourrocks.util.ParseHelper;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

	static String ID = "id";
	static String NAME = "name";
	static String JOB = "job";
	static String COMPANY = "company";
	static String BIRTH_PLACE = "birth_place";
	static String BIRTH_DATE = "birth_date";
	static String EMAIL = "email";
	static String PASSWORD = "password";
	static String SEX = "sex";
	static String PHONE = "phone";
	static String ADDRESS = "address";
	static String CITY = "city";
	static String ABOUT = "about";
	static String TWITTER = "twitter";
	static String FACEBOOK = "facebook";
	static String STATUS = "status";
	static String JOIN_DATE = "join_date";
	static String SHOW_PRIVACY = "show_privacy";
	static String AVATAR = "avatar";

	int id;
	String name;
	String job;
	String company;
	String birthPlace;
	String birthDate;
	String email;
	String password;
	int sex;
	String phone;
	String address;
	String city;
	String about;
	String twitter;
	String facebook;
	int status;
	String joinDate;
	boolean showPrivacy;
	String avatar;
	
	public void setAvatar(String avatar){
		this.avatar = avatar;
	}

	public String getAvatar(){
		return avatar;
	}
	
	public int getId() {
		
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}

	public boolean isShowPrivacy() {
		return showPrivacy;
	}

	public void setShowPrivacy(boolean showPrivacy) {
		this.showPrivacy = showPrivacy;
	}

	public static User Parse(JSONObject obj) {
		ParseHelper helper = new ParseHelper(obj);
		User user = new User();
		user.setId(helper.getInt(ID));
		user.setName(helper.getString(NAME));
		user.setJob(helper.getString(JOB));
		user.setCompany(helper.getString(COMPANY));
		user.setBirthPlace(helper.getString(BIRTH_PLACE));
		user.setBirthDate(helper.getString(BIRTH_DATE));
		user.setEmail(helper.getString(EMAIL));
		user.setSex(helper.getInt(SEX));
		user.setPhone(helper.getString(PHONE));
		user.setAddress(helper.getString(ADDRESS));
		user.setCity(helper.getString(CITY));
		user.setAbout(helper.getString(ABOUT));
		user.setTwitter(helper.getString(TWITTER));
		user.setFacebook(helper.getString(FACEBOOK));
		user.setStatus(helper.getInt(STATUS));
		user.setJoinDate(helper.getString(JOIN_DATE));
		user.setShowPrivacy(helper.getInt(SHOW_PRIVACY) == 1);
		user.setAvatar(helper.getString(AVATAR));
		return user;
	}

	public static Parcelable.Creator<User> CREATOR = new Creator<User>() {
		@Override
		public User[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new User[arg0];
		}

		@Override
		public User createFromParcel(Parcel arg0) {
			User user = new User();
			user.setId(arg0.readInt());
			user.setName(ParcelHelper.read(arg0));
			user.setJob(ParcelHelper.read(arg0));
			user.setCompany(ParcelHelper.read(arg0));
			user.setBirthPlace(ParcelHelper.read(arg0));
			user.setBirthDate(ParcelHelper.read(arg0));
			user.setEmail(ParcelHelper.read(arg0));
			user.setSex(arg0.readInt());
			user.setPhone(ParcelHelper.read(arg0));
			user.setAddress(ParcelHelper.read(arg0));
			user.setCity(ParcelHelper.read(arg0));
			user.setAbout(ParcelHelper.read(arg0));
			user.setTwitter(ParcelHelper.read(arg0));
			user.setFacebook(ParcelHelper.read(arg0));
			user.setStatus(arg0.readInt());
			user.setJoinDate(ParcelHelper.read(arg0));
			user.setShowPrivacy(arg0.readInt() == 1);
			user.setAvatar(ParcelHelper.read(arg0));
			return user;
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(id);
		ParcelHelper.write(arg0, name);
		ParcelHelper.write(arg0, job);
		ParcelHelper.write(arg0, company);
		ParcelHelper.write(arg0, birthPlace);
		ParcelHelper.write(arg0, birthDate);
		ParcelHelper.write(arg0, email);
		arg0.writeInt(sex);
		ParcelHelper.write(arg0, phone);
		ParcelHelper.write(arg0, address);
		ParcelHelper.write(arg0, city);
		ParcelHelper.write(arg0, about);
		ParcelHelper.write(arg0, twitter);
		ParcelHelper.write(arg0, facebook);
		arg0.writeInt(status);
		ParcelHelper.write(arg0, joinDate);
		arg0.writeInt(showPrivacy ? 1 : 0);
		ParcelHelper.write(arg0, avatar);
	}
}
