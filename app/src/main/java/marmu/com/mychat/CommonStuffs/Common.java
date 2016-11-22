package marmu.com.mychat.CommonStuffs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sahil on 19/07/16.
 */
public class Common {

    public static void saveUserData(String key,String data,Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, data);
        editor.commit();

    }

    public static String getUserData(String key, Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return sharedPref.getString(key,"");

    }
}
