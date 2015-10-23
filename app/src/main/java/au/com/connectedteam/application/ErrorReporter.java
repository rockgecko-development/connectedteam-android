package au.com.connectedteam.application;

import android.content.Context;
import android.widget.Toast;

import com.koushikdutta.ion.Response;
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

    public String onApiErrorGetMessage(Response<?> result){
        String msg;
        String userMsg;
        if(result.getException()!=null){
            userMsg = "Request failed: "+getErrorMessageForUser(result.getException());
            msg= "Error: "+ result.getException().getMessage()+"\nProd message: "+userMsg;
        }
        else if (result.getResult() instanceof dto.ResponseBase){
            userMsg = ResponseBaseHelper.formatMessage((dto.ResponseBase) result.getResult());
            msg=userMsg;
        }
        else if (result.getHeaders().code()==401){
            userMsg=mContext.getString(R.string.request_fail_unauthorized);
            msg= "HTTP "+ result.getHeaders().code()+" "+result.getHeaders().message()+"\nProd message: "+userMsg;
        }
        else{
            userMsg=mContext.getString(R.string.request_fail_server_error);
            msg= "HTTP "+ result.getHeaders().code()+" "+result.getHeaders().message()+"\nProd message: "+userMsg;
        }

        if(ConnectedApp.DEBUG){
            return msg;
        }
        else{
            return userMsg;
        }
    }
    public void onApiErrorShowToast(Response<?> result){
        String msg = onApiErrorGetMessage(result);
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

    }

    public String getErrorMessageForUser(Exception exception){
        return mContext.getString(R.string.request_fail);
    }
}
