package io.money.moneyio.model.utilities;


import android.text.TextUtils;


public class Utilities {
    private static boolean isFirebasePersistence = false;
    private static boolean hasFriend = false;

    public static boolean isHasFriend() {
        return hasFriend;
    }

    public static void setHasFriend(boolean hasFriend) {
        Utilities.hasFriend = hasFriend;
    }

    public static boolean isFirebasePersistence() {
        return isFirebasePersistence;
    }

    public static void setIsFirebasePersistence(boolean isFirebasePersistence) {
        Utilities.isFirebasePersistence = isFirebasePersistence;
    }

    public static boolean checkString(String str){
        if (TextUtils.isEmpty(str) ||  str.matches("[&.\\;'\"]")){
            return false;
        }
        return true;
    }

    public static boolean isMail(String str){
        return !TextUtils.isEmpty(str) && android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public static boolean isNumber(String str){
        if (!TextUtils.isEmpty(str) ||  str.matches("[0-9.]+")){
            return true;
        }
        return false;
    }
}
