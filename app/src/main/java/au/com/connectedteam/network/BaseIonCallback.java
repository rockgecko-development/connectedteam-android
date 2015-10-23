package au.com.connectedteam.network;

import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.appsapi.ex.ResponseBaseHelper;
import au.com.connectedteam.appsapi.generated.dto;

/**
 * Created by bramleyt on 20/07/2015.
 */
public abstract class BaseIonCallback<T> implements FutureCallback<Response<T>> {
    private Response<T> response;

    public void onCompleted(Exception e, Response<T> response) {
        if(response==null) response=new Response<>(null, null, null, e, null);
        this.response = response;
        if (e == null
                && response.getHeaders()!=null
                && response.getHeaders().code() < 400
                && response.getResult() != null
                && !(response.getResult() instanceof dto.ResponseBase && ResponseBaseHelper.isError((dto.ResponseBase) response.getResult()))
                )
            onSuccess(response.getResult());
        else onError(response);

    }

    public Response<T> getResponse() {
        return response;
    }

    public abstract void onSuccess(T result);

    public void onError(Response<T> response) {
        String msg=ConnectedApp.getErrorReporter().onApiErrorGetMessage(response);
        Toast.makeText(ConnectedApp.getContextStatic(), msg, Toast.LENGTH_SHORT).show();
    }
}
