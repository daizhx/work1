package com.jiuzhansoft.ehealthtec.sphygmomanometer.data;

import android.os.Parcel;
import android.os.Parcelable;


public class Error extends IBean {

	public static final int ERROR_CONNECTION_FAILED = 0;

	public static final int ERROR_CONNECTION_LOST = 1;

	public static final int ERROR_EEPROM = 0x0E;

	public static final int ERROR_HEART = 0x01;

	public static final int ERROR_DISTURB = 0x02;

	public static final int ERROR_GASING = 0x03;

	public static final int ERROR_TEST = 0x05;

	public static final int ERROR_REVISE = 0x0C;

	public static final int ERROR_POWER = 0x0B;


	private int error_code;

	private int error;

	public Error() {
		super();
	}

	public Error(int errorCode) {
		super();
		error_code = errorCode;
	}

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int errorCode) {
		error_code = errorCode;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(error_code);
		dest.writeInt(error);
	}

	public static final Parcelable.Creator<Error> CREATOR = new Parcelable.Creator<Error>() {
		public Error createFromParcel(Parcel in) {
			return new Error(in);
		}

		public Error[] newArray(int size) {
			return new Error[size];
		}
	};

	private Error(Parcel in) {
		error_code = in.readInt();
		error = in.readInt();
	}

	public void analysis(int[] f) {
		error = f[3];
	}
}
