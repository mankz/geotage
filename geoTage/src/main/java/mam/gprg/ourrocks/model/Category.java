package mam.gprg.ourrocks.model;

import mam.gprg.ourrocks.util.ParcelHelper;
import mam.gprg.ourrocks.util.ParseHelper;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {

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

	public static Category Parse(JSONObject obj) {
		Category category = new Category();
		ParseHelper helper = new ParseHelper(obj);
		category.setId(helper.getInt(ID));
		category.setName(helper.getString(NAME));
		category.setDescription(DESCRIPTION);
		return category;
	}

	public static Parcelable.Creator<Category> CREATOR = new Creator<Category>() {

		@Override
		public Category[] newArray(int arg0) {
			return new Category[arg0];
		}

		@Override
		public Category createFromParcel(Parcel arg0) {
			Category category = new Category();
			category.setId(arg0.readInt());
			category.setName(ParcelHelper.read(arg0));
			category.setDescription(ParcelHelper.read(arg0));
			return category;
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(id);
		ParcelHelper.write(arg0, name);
		ParcelHelper.write(arg0, description);
	}
}
