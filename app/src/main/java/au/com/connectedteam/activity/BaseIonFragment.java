package au.com.connectedteam.activity;

import android.app.AlertDialog;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;
import com.koushikdutta.ion.builder.LoadBuilder;
import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.application.Session;
import au.com.connectedteam.appsapi.ex.ResponseBaseHelper;
import au.com.connectedteam.appsapi.generated.dto;
import au.com.connectedteam.network.IonHelper;
import au.com.connectedteam.util.StringUtils;

/**
 * Created by bramleyt on 20/07/2015.
 */
public abstract class BaseIonFragment extends BaseFragment implements IonHelper.IonHelperCallbacks{
    private static final String TAG = "BaseIonFragment";
    private IonHelper mIonHelper;
    protected IonHelper getIonHelper(){
        if(mIonHelper==null) mIonHelper=new IonHelper(this);
        return mIonHelper;
    }
    protected LoadBuilder<Builders.Any.B> getIonLoadBuilder(){
        return getIonHelper().build(BaseIonFragment.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Session.getInstance().executeGetUserHeader();
    }

    @Override
    public boolean isRequesting() {
        return Ion.getDefault(ConnectedApp.getContextStatic()).getPendingRequestCount(StringUtils.isNullOrEmpty(getTag(), this.getClass().getName()))>0;
    }
    protected void onRequestingChanged(){
        if(getBaseActivity()!=null) getBaseActivity().notifyRefreshing(BaseIonFragment.this, isRequesting());
    }

    @Override
    public void onIonRequestPreExecute(IonHelper.HelperRequest<?> request) {
        Log.d(TAG, "onIonRequestPreExecute. IsRequesting: " + isRequesting());
        request.getBuilder().group(StringUtils.isNullOrEmpty(getTag(), this.getClass().getName()));
    }
    @Override
    public void onIonRequestStarting(IonHelper.HelperRequest<?> request) {
        Log.d(TAG, "onIonRequestStarting. IsRequesting: " + isRequesting());
        onRequestingChanged();
    }

    @Override
    public void onIonRequestFinished(IonHelper.HelperRequest<?> request, Response<?> response) {
        Log.d(TAG, "onIonRequestFinished. IsRequesting: " + isRequesting());
        onRequestingChanged();
    }

    protected void onErrorResponse(Response<?> response){
        if(response.getHeaders() != null && response.getHeaders().code() == 401){
            Session.getInstance().onUnauthorised();
        }
        else {
            if(response.getResult() instanceof dto.ResponseBase){
                new AlertDialog.Builder(getActivity())
                        .setMessage(ResponseBaseHelper.formatMessage((dto.ResponseBase) response.getResult()))
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }
            else {
                ConnectedApp.getErrorReporter().onApiErrorShowToast(response);

            }
        }
    }


    public abstract class BaseFragmentIonCallback<T> implements FutureCallback<Response<T>> {
        private Response<T> response;

        public void onCompleted(Exception e, Response<T> response) {
            //if(response==null)response=new Response<>(null, null, null, e, null);
            this.response = response;
            //onRequestingChanged();
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
            onErrorResponse(response);
        }
    }
}
