package mam.gprg.ourrocks.util;

import android.os.Parcel;

public class ParcelHelper {

	public static void write(Parcel dest, String string) {
		String s;
		if (string != null && string.length() > 0 && !string.equalsIgnoreCase("-"))
			s = string;
		else
			s = ""; 
		dest.writeString(s);
	}

	public static String read(Parcel source) {
		String string = source.readString();
		return string;
 	}
}
