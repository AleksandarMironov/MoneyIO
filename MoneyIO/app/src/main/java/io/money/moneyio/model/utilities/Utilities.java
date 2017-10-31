package io.money.moneyio.model.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import io.money.moneyio.R;
import io.money.moneyio.activities.HomeActivity;

public class Utilities {
    private static boolean isFirebasePersistence = false;

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
