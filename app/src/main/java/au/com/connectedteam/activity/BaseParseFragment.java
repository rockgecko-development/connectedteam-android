package au.com.connectedteam.activity;

import com.parse.ParseException;

import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.application.Session;

/**
 * Created by bramleyt on 20/07/2015.
 */
public abstract class BaseParseFragment extends BaseFragment {
    private static final String TAG = "BaseParseFragment";


    @Override
    public void onResume() {
        super.onResume();
        Session.getInstance().executeGetUserHeader();
    }


    protected void onRequestingChanged(){
        if(getBaseActivity()!=null) getBaseActivity().notifyRefreshing(BaseParseFragment.this, isRequesting());
    }



    protected void onErrorResponse(Exception response){
        if(response==null) return;
        int responseCode=-1;
        if(response instanceof ParseException) responseCode=((ParseException) response).getCode();
        if(responseCode==ParseException.INVALID_SESSION_TOKEN || responseCode==ParseException.SESSION_MISSING){
            Session.getInstance().onUnauthorised();
        }
        else {


                ConnectedApp.getErrorReporter().onApiErrorShowToast(response);


        }
    }



}
