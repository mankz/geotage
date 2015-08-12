package mam.gprg.ourrocks.model;

import mam.gprg.ourrocks.util.ParcelHelper;
import mam.gprg.ourrocks.util.ParseHelper;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Type implements Parcelable {

	public static String ID = "id";
	public static String NAME = "name";
	public static String DESCRIPTION = "description";

	int id;
	String name;
	String description;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static Type Parse(JSONObject object) {
		Type type = new Type();
		ParseHelper helper = new ParseHelper(object);
		type.setId(helper.getInt(ID));
		type.setName(helper.getString(NAME));
		type.setDescription(helper.getString(DESCRIPTION));
		return type;
	}

	public static Parcelable.Creator<Type> CREATOR = new Creator<Type>() {

		@Override
		public Type[] newArray(int arg0) {
			return new Type[arg0];
		}

		@Override
		public Type createFromParcel(Parcel arg0) {
			Type type = new Type();
			type.setId(arg0.readInt());
			type.setName(ParcelHelper.read(arg0));
			type.setDescription(ParcelHelper.read(arg0));
			return type;
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(id);
		ParcelHelper.write(arg0, name);
		ParcelHelper.write(arg0, description);
	}

}
