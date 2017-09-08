package com.peppa.app.utility;


import android.content.Context;
import android.content.SharedPreferences;


public class GlobalVar {


	// **********************************************************//
		// Shared Preferences Setting Like Plist for android //
		// **********************************************************//

		public static SharedPreferences getPrefs(Context context) {
			return context.getSharedPreferences("Peppa_app", Context.MODE_PRIVATE);
		}

		public static String getMyStringPref(Context context, String strname) {
			return getPrefs(context).getString(strname, "");
		}

		public static void setMyStringPref(Context context, String strname,
				String value) {
			getPrefs(context).edit().putString(strname, value).commit();
		}

		public static boolean getMyBooleanPref(Context context, String strname) {
			return getPrefs(context).getBoolean(strname, false);
		}

		public static void setMyBooleanPref(Context context, String strname,
											boolean value) {
			getPrefs(context).edit().putBoolean(strname, value).commit();
		}

		public static int getMyIntPref(Context context, String id) {
			return getPrefs(context).getInt(id, 0);
		}

		public static void setMyIntPref(Context context, String id, int value) {
			getPrefs(context).edit().putInt(id, value).commit();
		}


	public static void clearMyStringPref(Context context) {
		  getPrefs(context).edit().clear();
	    }


}
