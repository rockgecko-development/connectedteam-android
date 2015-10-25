package au.com.connectedteam.application;

import android.content.Context;
import android.widget.Toast;

import com.parse.ParseException;

import au.com.connectedteam.R;
import au.com.connectedteam.appsapi.ex.ResponseBaseHelper;
import au.com.connectedteam.appsapi.generated.dto;

/**
 * Created by bramleyt on 20/07/2015.
 */
public class ErrorReporter {

    private final Context mContext;

    ErrorReporter(Context applicationContext){
        mContext=applicationContext;
    }

    public String onApiErrorGetMessage(Exception result){
        String msg;
        String userMsg;
        int resultCode=-1;
        if(result instanceof ParseException){
            resultCode=((ParseException) result).getCode();
        }
        if (resultCode==ParseException.INVALID_SESSION_TOKEN || resultCode==ParseException.SESSION_MISSING){
            userMsg=mContext.getString(R.string.request_fail_unauthorized);
            msg= result.getMessage()+"\nProd message: "+userMsg;
        }
        else{
            userMsg=mContext.getString(R.string.request_fail_server_error);
            msg= result.getMessage()+"\nProd message: "+userMsg;
        }

        if(ConnectedApp.DEBUG){
            return msg;
        }
        else{
            return userMsg;
        }
    }
    public void onApiErrorShowToast(Exception result){
        String msg = onApiErrorGetMessage(result);
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

    }

    public String getErrorMessageForUser(Exception exception){
        if(exception instanceof ParseException){

        }
        return mContext.getString(R.string.request_fail);
    }
}
