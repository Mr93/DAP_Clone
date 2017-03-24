package com.example.administrator.dapclone;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 03/22/2017.
 */

public class FileInfo implements Parcelable {
	public String url = "";
	public String name = "";
	public String extension = "";
	public long size = 0;
	public int downloadedSize = 0;
	public String status = "pending";
	public boolean isMultiThread = true;
	public String path = "";


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.url);
		dest.writeString(this.name);
		dest.writeString(this.extension);
		dest.writeLong(this.size);
		dest.writeInt(this.downloadedSize);
		dest.writeString(this.status);
		dest.writeByte(this.isMultiThread ? (byte) 1 : (byte) 0);
		dest.writeString(this.path);
	}

	public FileInfo() {
	}

	protected FileInfo(Parcel in) {
		this.url = in.readString();
		this.name = in.readString();
		this.extension = in.readString();
		this.size = in.readLong();
		this.downloadedSize = in.readInt();
		this.status = in.readString();
		this.isMultiThread = in.readByte() != 0;
		this.path = in.readString();
	}

	public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
		@Override
		public FileInfo createFromParcel(Parcel source) {
			return new FileInfo(source);
		}

		@Override
		public FileInfo[] newArray(int size) {
			return new FileInfo[size];
		}
	};
}
