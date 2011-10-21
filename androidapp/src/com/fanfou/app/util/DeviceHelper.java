/**
 * 
 */
package com.fanfou.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fanfou.app.App;

/**
 * @author mcxiaoke
 * 
 */
public final class DeviceHelper {

	public synchronized static String uuid(Context context) {
		String uuid = OptionHelper.readString(context, "uuid", null);
		if (uuid == null) {
			uuid = generateUUID(context);
			OptionHelper.saveString(context, "uuid", uuid);
		}
		return uuid;
	}

	private static String generateUUID(Context context) {
		String uuid = Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.ANDROID_ID);
		if (App.DEBUG)
			Log.e("DeviceHelper", "generateUUID uuid=" + uuid);
		if (uuid == null || uuid.equals("9774d56d682e549c")) {
			uuid = UUID.randomUUID().toString();
			if (App.DEBUG)
				Log.e("DeviceHelper", "generateUUID randomid=" + uuid);
		}
		return uuid;
	}

	public static int getAppVersionCode(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			pi = new PackageInfo();
			pi.versionName = "1.0";
			pi.versionCode = 1;
		}
		return pi.versionCode;
	}

	public static String getAppVersionName(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			pi = new PackageInfo();
			pi.versionName = "1.0";
			pi.versionCode = 1;
		}
		return pi.versionName;
	}

	public static HashMap<String, String> getDeviceInfo(Context context) {
		HashMap<String, String> map = new HashMap<String, String>();
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			pi = new PackageInfo();
			pi.versionName = "1.0";
			pi.versionCode = 1;
		}

		String appName = pi.packageName;
		String appVersion = pi.versionName;
		String appVersionCode = String.valueOf(pi.versionCode);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss_zzz");
		String date = sdf.format(new Date());
		String api = String.valueOf(Build.VERSION.SDK_INT);
		String release = Build.VERSION.RELEASE;
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		String product = Build.PRODUCT;
		String device = Build.FINGERPRINT;
		String locale = ""
				+ context.getResources().getConfiguration().locale
						.getDisplayName();
		String display = ""
				+ context.getResources().getDisplayMetrics().toString();
		String deviceId = ""
				+ Settings.Secure.getString(context.getContentResolver(),
						Settings.Secure.ANDROID_ID);

		map.put("appName", appName);
		map.put("appVersion", appVersion);
		map.put("apVersionCode", appVersionCode);
		map.put("date", date);
		map.put("api", api);
		map.put("release", release);
		map.put("manufacturer", manufacturer);
		map.put("model", model);
		map.put("product", product);
		map.put("locale", locale);
		map.put("device", device);
		map.put("display", display);
		map.put("device", device);
		map.put("deviceId", deviceId);
		return map;
	}

	public static String getDeviceId(Context context) {

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// String imsi = ""+tm.getSubscriberId();
		String imei = "" + tm.getDeviceId();
		String sim = "" + tm.getSimSerialNumber();
		String androidId = ""
				+ Settings.Secure.getString(context.getContentResolver(),
						Settings.Secure.ANDROID_ID);
		UUID uuid = new UUID(androidId.hashCode(),
				((long) imei.hashCode() << 32) | sim.hashCode());

		return uuid.toString();
	}

}