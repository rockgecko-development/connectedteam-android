package au.com.connectedteam.appsapi.ex;

import android.content.Context;

import au.com.connectedteam.BuildConfig;
import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.appsapi.generated.dto;

/**
 * Created by bramleyt on 20/07/2015.
 */
public class ResponseBaseHelper {

    public static String formatMessage(dto.ResponseBase result){
        Context c = ConnectedApp.getContextStatic();
        if(result.getErrorInfo()!=null) {
            if (ConnectedApp.DEBUG) {
                return String.format("%d: %s", result.getErrorInfo().getErrorNo(), result.getErrorInfo().getMessage());
            }
            return result.getErrorInfo().getMessage();
        }
        if(result.getResponseStatus()!=null){
            if (ConnectedApp.DEBUG) {
                return String.format("%s: %s", result.getResponseStatus().getErrorCode(), result.getResponseStatus().getMessage());
            }
            return result.getResponseStatus().getMessage();
        }
        return null;
    }

    public static boolean isError(dto.ResponseBase result){
        if(result.getErrorInfo()!=null) return result.getErrorInfo().getErrorNo()!=null && result.getErrorInfo().getErrorNo()!=0;
        if(result.getResponseStatus()!=null) return !"200".equals(result.getResponseStatus().ErrorCode);
        return false;
    }

}
