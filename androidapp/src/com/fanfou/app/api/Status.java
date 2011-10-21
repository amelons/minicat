package com.fanfou.app.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.http.Response;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.StatusHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.04.30
 * @version 1.1 2011.05.01
 * @version 1.2 2011.05.02
 * @version 1.3 2011.06.03
 * @version 1.4 2011.07.21
 * @version 1.5 2011.10.19
 * @version 1.6 2011.10.21
 * 
 */
public class Status implements Storable<Status> {

	private static final long serialVersionUID = -7878720905855956354L;

	public static final String tag = Status.class.getSimpleName();

	private static void log(String message) {
		Log.d(tag, message);
	}

	public static final int TYPE_HOME = Commons.STATUS_TYPE_HOME;
	public static final int TYPE_MENTION = Commons.STATUS_TYPE_MENTION;
	public static final int TYPE_PUBLIC = Commons.STATUS_TYPE_PUBLIC;
	public static final int TYPE_USER = Commons.STATUS_TYPE_USER;
	public static final int TYPE_FAVORITES = Commons.STATUS_TYPE_FAVORITES;
	public static final int TYPE_SEARCH = Commons.STATUS_TYPE_SEARCH;

	public Date createdAt;

	public String id;
	public String ownerId;
	public String text;
	public String source;
	public String inReplyToStatusId;
	public String inReplyToUserId;
	public String inReplyToScreenName;

	public String photoImageUrl;
	public String photoThumbUrl;
	public String photoLargeUrl;

	public String userId;
	public String userScreenName;
	public String userProfileImageUrl;

	public int type;

	public boolean truncated;
	public boolean favorited;

	public boolean isRead;

	public boolean isThread;
	public boolean hasPhoto;
	public String simpleText;
	
	public long realId;
	
	public boolean special;

	public Status() {
	}

	public Status(Parcel p) {

	}

	@Override
	public int compareTo(Status another) {
		return createdAt.compareTo(another.createdAt);
	}

	public boolean isNull() {
		return StringHelper.isEmpty(id);
	}

	public static List<Status> parseStatuses(Response r, int type)
			throws ApiException {
		if (App.DEBUG) {
			log("parseStatuses response");
		}
		return Status.parseStatuses(r.getContent(), type);
	}

	public static List<Status> parseStatuses(String content, int type)
			throws ApiException {
		if (App.DEBUG) {
			log("parseStatuses content");
		}
		JSONArray a;
		try {
			a = new JSONArray(content);
		} catch (JSONException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e);
		}
		return parseStatuses(a, type);
	}

	public static List<Status> parseStatuses(JSONArray a, int type)
			throws ApiException {
		if (a == null) {
			return null;
		}
		if (App.DEBUG) {
			log("parseStatuses jsonarray.size=" + a.length());
		}
		try {
			List<Status> statuses = new ArrayList<Status>();
			for (int i = 0; i < a.length(); i++) {
				JSONObject o = a.getJSONObject(i);
				Status s = Status.parse(o, type);
				statuses.add(s);
			}
			return statuses;
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e);
		}
	}

	public static Status parse(Cursor c) {
		if (c == null) {
			return null;
		}
		Status s = new Status();
		s.createdAt = Parser.parseDate(c, StatusInfo.CREATED_AT);
		s.id = Parser.parseString(c, StatusInfo.ID);
		s.realId=Parser.parseLong(c, StatusInfo.REAL_ID);
		s.ownerId = Parser.parseString(c, StatusInfo.OWNER_ID);
		s.text = Parser.parseString(c, StatusInfo.TEXT);
		s.simpleText=Parser.parseString(c, StatusInfo.SIMPLE_TEXT);
		s.special=Parser.parseBoolean(c, StatusInfo.SPECIAL);
		s.source = Parser.parseString(c, StatusInfo.SOURCE);
		s.inReplyToStatusId = Parser.parseString(c,
				StatusInfo.IN_REPLY_TO_STATUS_ID);
		s.inReplyToUserId = Parser.parseString(c,
				StatusInfo.IN_REPLY_TO_USER_ID);
		s.inReplyToScreenName = Parser.parseString(c,
				StatusInfo.IN_REPLY_TO_SCREEN_NAME);

		s.photoImageUrl = Parser.parseString(c, StatusInfo.PHOTO_IMAGE_URL);
		s.photoLargeUrl = Parser.parseString(c, StatusInfo.PHOTO_LARGE_URL);
		s.photoThumbUrl = Parser.parseString(c, StatusInfo.PHOTO_THUMB_URL);

		s.userId = Parser.parseString(c, StatusInfo.USER_ID);
		s.userScreenName = Parser.parseString(c, StatusInfo.USER_SCREEN_NAME);
		s.userProfileImageUrl = Parser.parseString(c,
				StatusInfo.USER_PROFILE_IMAGE_URL);

		s.type = Parser.parseInt(c, StatusInfo.TYPE);

		s.truncated = Parser.parseBoolean(c, StatusInfo.TRUNCATED);
		s.favorited = Parser.parseBoolean(c, StatusInfo.FAVORITED);

		s.isRead = Parser.parseBoolean(c, StatusInfo.IS_READ);
		
		s.isThread = Parser.parseBoolean(c, StatusInfo.IS_THREAD);
		s.hasPhoto = Parser.parseBoolean(c, StatusInfo.HAS_PHOTO);
		return s;

	}

	public static Status parse(Response response, int type) throws ApiException {
		return parse(response.getContent(), type);
	}

	public static Status parse(Response response) throws ApiException {
		return parse(response.getContent());
	}

	public static Status parse(String content) throws ApiException {
		return parse(content, Commons.TYPE_NONE);
	}

	public static Status parse(String content, int type) throws ApiException {
		JSONObject o = null;
		try {
			o = new JSONObject(content);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e);
		}
		return parse(o, type);
	}

	public static Status parse(JSONObject o) throws ApiException {
		return parse(o, Commons.TYPE_NONE);
	}

	public static Status parse(JSONObject o, int type) throws ApiException {
		// Log.e(TAG, "Parser.status()");
		if (o == null) {
			return null;
		}
		try {
			Status s = new Status();
			s.createdAt = Parser.date(o.getString(StatusInfo.CREATED_AT));
			s.id = o.getString(StatusInfo.ID);
			s.text = o.getString(StatusInfo.TEXT);

			s.source = Parser.parseSource(o.getString(StatusInfo.SOURCE));;
			s.truncated = o.getBoolean(StatusInfo.TRUNCATED);
			s.inReplyToStatusId = o.getString(StatusInfo.IN_REPLY_TO_STATUS_ID);
			s.inReplyToUserId = o.getString(StatusInfo.IN_REPLY_TO_USER_ID);
			s.inReplyToScreenName = o
					.getString(StatusInfo.IN_REPLY_TO_SCREEN_NAME);
			s.favorited = o.getBoolean(StatusInfo.FAVORITED);

			s.type = type;

			s.ownerId = App.me.userId;
			
			s.realId=Parser.decodeStatusRealId(s.id);

			
			s.simpleText = StatusHelper.getSimpifiedText(s.text);
			
			s.special=false;
			
			if (!TextUtils.isEmpty(s.inReplyToStatusId)) {
				s.isThread = true;
			}

			if (o.has("photo")) {
				JSONObject po = o.getJSONObject("photo");
				s.photoImageUrl = po.getString(StatusInfo.PHOTO_IMAGE_URL);
				s.photoLargeUrl = po.getString(StatusInfo.PHOTO_LARGE_URL);
				s.photoThumbUrl = po.getString(StatusInfo.PHOTO_THUMB_URL);
				s.hasPhoto = true;
			}

			if (o.has("user")) {
				JSONObject uo = o.getJSONObject("user");
				s.userId = uo.getString(StatusInfo.ID);
				s.userScreenName = uo.getString(UserInfo.SCREEN_NAME);
				s.userProfileImageUrl = uo
						.getString(UserInfo.PROFILE_IMAGE_URL);
				// s.user = User.parse(uo);
				// s.userId = s.user.id;
				// s.userScreenName = s.user.screenName;
				// s.userProfileImageUrl = s.user.profileImageUrl;

				// if(s.userId.equalsIgnoreCase(App.userId)){
				// s.isSelf=true;
				// }

			}
			return s;
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e);
		}
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(StatusInfo.CREATED_AT, this.createdAt.getTime());
		cv.put(StatusInfo.ID, this.id);
		
		cv.put(StatusInfo.REAL_ID, this.realId);
		
		cv.put(StatusInfo.OWNER_ID, this.ownerId);
		cv.put(StatusInfo.TEXT, this.text);
		cv.put(StatusInfo.SOURCE, this.source);
		cv.put(StatusInfo.IN_REPLY_TO_STATUS_ID, this.inReplyToStatusId);
		cv.put(StatusInfo.IN_REPLY_TO_USER_ID, this.inReplyToUserId);
		cv.put(StatusInfo.IN_REPLY_TO_SCREEN_NAME, this.inReplyToScreenName);

		cv.put(StatusInfo.PHOTO_IMAGE_URL, this.photoImageUrl);
		cv.put(StatusInfo.PHOTO_THUMB_URL, this.photoThumbUrl);
		cv.put(StatusInfo.PHOTO_LARGE_URL, this.photoLargeUrl);

		cv.put(StatusInfo.USER_ID, this.userId);
		cv.put(StatusInfo.USER_SCREEN_NAME, this.userScreenName);
		cv.put(StatusInfo.USER_PROFILE_IMAGE_URL, this.userProfileImageUrl);

		cv.put(StatusInfo.TYPE, this.type);

		cv.put(StatusInfo.TRUNCATED, this.truncated);
		cv.put(StatusInfo.FAVORITED, this.favorited);

		cv.put(StatusInfo.IS_READ, this.isRead);
		
		cv.put(StatusInfo.IS_THREAD, this.isThread);
		cv.put(StatusInfo.HAS_PHOTO, this.hasPhoto);
		cv.put(StatusInfo.SIMPLE_TEXT, this.simpleText);
		cv.put(StatusInfo.SPECIAL, this.special);
		
		cv.put(StatusInfo.TIMESTAMP, new Date().getTime());
		return cv;
	}

	@Override
	public String toString() {
		// return toContentValues().toString();
		return "[Status] " + StatusInfo.ID + "=" + this.id + " "
				+ StatusInfo.TEXT + "=" + this.text + " "
				+ StatusInfo.CREATED_AT + "+" + this.createdAt + " "
				// +StatusInfo.SOURCE+"="+this.source+" "
				// +StatusInfo.TRUNCATED+"="+this.truncated+" "
				// +StatusInfo.IN_REPLY_TO_STATUS_ID+"="+this.inReplyToStatusId+" "
				// +StatusInfo.IN_REPLY_TO_USER_ID+"="+this.inReplyToUserId+" "
				// +StatusInfo.FAVORITED+"="+this.favorited+" "
				// +StatusInfo.IN_REPLY_TO_SCREEN_NAME+"="+this.inReplyToScreenName+" "
				// +StatusInfo.PHOTO_IMAGE_URL+"="+this.photoImageUrl+" "
				// +StatusInfo.PHOTO_LARGE_URL+"="+this.photoLargeUrl+" "
				// +StatusInfo.PHOTO_THUMB_URL+"="+this.photoThumbUrl+" "
				+ StatusInfo.USER_ID + "=" + this.userId + " ";
		// +StatusInfo.USER_SCREEN_NAME+"="+this.userScreenName+" "
		// +StatusInfo.READ+"="+this.read+" "
		// +StatusInfo.TYPE+"="+this.type+" ";
	}

	// @Override
	// public int describeContents() {
	// return 0;
	// }
	//
	// @Override
	// public void writeToParcel(Parcel dest, int flags) {
	//
	// }
	//
	// public static final Parcelable.Creator<Status> CREATOR=new
	// Parcelable.Creator<Status>() {
	//
	// @Override
	// public Status createFromParcel(Parcel source) {
	// return null;
	// }
	//
	// @Override
	// public Status[] newArray(int size) {
	// return new Status[size];
	// }
	// };

}