package mam.gprg.ourrocks.model;

import java.util.ArrayList;

import mam.gprg.ourrocks.util.ParcelHelper;
import mam.gprg.ourrocks.util.ParseHelper;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Rock implements Parcelable {

	public static String ID = "id";
	public static String NAME = "name";
	public static String DESCRIPTION = "desciption";
	public static String ADDRESS = "address";
	public static String CITY = "city";
	public static String TYPE_ID = "type_id";
	public static String CATEGORY_ID = "category_id";
	public static String LATITUDE = "latitude";
	public static String LONGITUDE = "longitude";
	public static String FORMATION = "formation";
	public static String USER_ID = "user_id";
	public static String DATE = "date";
	public static String STATUS = "status";

	public static String USER = "user";
	public static String CATEGORY = "category";
	public static String TYPE = "type";
	public static String PHOTOS = "photos";

	int id;
	String name;
	String desc;
	String address;
	String city;
	int typeId;
	int categoryId;
	double latitude;
	double longitude;
	String formation;
	int userId;
	String date;
	int status;

	User user;
	Category category;
	Type type;
	
	ArrayList<RockPhoto> photos;
	
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getFormation() {
		return formation;
	}

	public void setFormation(String formation) {
		this.formation = formation;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	
	public ArrayList<RockPhoto> getPhotos(){
		return photos;
	}
	
	public void setPhotos(ArrayList<RockPhoto> photos){
		this.photos = photos;
	}
	public static Rock Parse(JSONObject object) {
		ParseHelper helper = new ParseHelper(object);
		Rock rock = new Rock();
		try {
			rock.setId(helper.getInt(ID));
			rock.setName(helper.getString(NAME));
			rock.setDesc(helper.getString(DESCRIPTION));
			rock.setAddress(helper.getString(ADDRESS));
			rock.setCity(helper.getString(CITY));
			rock.setTypeId(helper.getInt(TYPE_ID));
			rock.setCategoryId(helper.getInt(CATEGORY_ID));
			rock.setLatitude(helper.getDouble(LATITUDE));
			rock.setLongitude(helper.getDouble(LONGITUDE));
			rock.setFormation(helper.getString(FORMATION));
			rock.setUserId(helper.getInt(USER_ID));
			rock.setDate(helper.getString(DATE));
			rock.setStatus(helper.getInt(STATUS));

			if (object.has(USER)) {
				Log.d("ADA USER", "ADA");
 				rock.setUser(User.Parse(helper.getJSONObject(USER)));
			}  else 
				Log.d("ADA USER", "NGGAK");

			if (object.has(TYPE)) {
				Log.d("ADA TYPE", "ADA");
				rock.setType(Type.Parse(helper.getJSONObject(TYPE)));
			}else 
				Log.d("ADA TYPE", "NGGAK");

			if (object.has(CATEGORY)) {
				Log.d("ADA CATEGORY", "ADA");
				Category cat = Category.Parse(helper.getJSONObject(CATEGORY));
				if(cat != null) {
				rock.setCategory(cat); 
				} else
					Log.d("CAT dari " + rock.getName(), "NULL");
			} else 
				Log.d("ADA CATEGORY", "NGGAK");
			
			if(object.has(PHOTOS)){
				rock.setPhotos(RockPhoto.Parse(helper.getJSONArray("photos")));
			}
		} catch (Exception e) {

		}
		return rock;
	}

	public static Parcelable.Creator<Rock> CREATOR = new Creator<Rock>() {

		@Override
		public Rock[] newArray(int arg0) {
			return new Rock[arg0];
		}

		@Override
		public Rock createFromParcel(Parcel arg0) {
			Rock rock = new Rock();
			rock.setId(arg0.readInt());
			rock.setName(ParcelHelper.read(arg0));
			rock.setDesc(ParcelHelper.read(arg0));
			rock.setAddress(ParcelHelper.read(arg0));
			rock.setCity(ParcelHelper.read(arg0));
			rock.setTypeId(arg0.readInt());
			rock.setCategoryId(arg0.readInt());
			rock.setLatitude(arg0.readDouble());
			rock.setLongitude(arg0.readDouble());
			rock.setFormation(ParcelHelper.read(arg0));
			rock.setUserId(arg0.readInt());
			rock.setDate(ParcelHelper.read(arg0));
			rock.setStatus(arg0.readInt());

			if (arg0.readInt() == 1) {
				rock.setUser((User) arg0.readParcelable(User.class
						.getClassLoader()));
			}

			if (arg0.readInt() == 1) {
				rock.setType((Type) arg0.readParcelable(Type.class
						.getClassLoader()));
			}

			if (arg0.readInt() == 1) {
				rock.setCategory((Category) arg0.readParcelable(Category.class
						.getClassLoader()));
			}

			return rock;
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
		ParcelHelper.write(arg0, desc);
		ParcelHelper.write(arg0, address);
		ParcelHelper.write(arg0, city);
		arg0.writeInt(typeId);
		arg0.writeInt(categoryId);
		arg0.writeDouble(latitude);
		arg0.writeDouble(longitude);
		ParcelHelper.write(arg0, formation);
		arg0.writeInt(userId);
		ParcelHelper.write(arg0, date);
		arg0.writeInt(status);

		if (user != null) {
			arg0.writeInt(1);
			arg0.writeParcelable(user, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
		} else
			arg0.writeInt(0);

		if (type != null) {
			arg0.writeInt(1);
			arg0.writeParcelable(type, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
		} else
			arg0.writeInt(0);

		if (category != null) {
			arg0.writeInt(1);
			arg0.writeParcelable(category,
					Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
		} else
			arg0.writeInt(0);

	}

	 
}
