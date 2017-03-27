package com.example.administrator.dapclone;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 03/22/2017.
 */

public class TaskInfo implements Parcelable {
	public String url = "";
	public String name = "";
	public String extension = "";
	public long size = 0;
	public int processedSize = 0;
	public String status = "pending";
	public boolean isMultiThread = true;
	public String path = "";
	public boolean isDownload = true;
	public int taskId = -1;


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
		dest.writeInt(this.processedSize);
		dest.writeString(this.status);
		dest.writeByte(this.isMultiThread ? (byte) 1 : (byte) 0);
		dest.writeString(this.path);
	}

	public TaskInfo() {
	}

	protected TaskInfo(Parcel in) {
		this.url = in.readString();
		this.name = in.readString();
		this.extension = in.readString();
		this.size = in.readLong();
		this.processedSize = in.readInt();
		this.status = in.readString();
		this.isMultiThread = in.readByte() != 0;
		this.path = in.readString();
	}

	public static final Creator<TaskInfo> CREATOR = new Creator<TaskInfo>() {
		@Override
		public TaskInfo createFromParcel(Parcel source) {
			return new TaskInfo(source);
		}

		@Override
		public TaskInfo[] newArray(int size) {
			return new TaskInfo[size];
		}
	};
}
