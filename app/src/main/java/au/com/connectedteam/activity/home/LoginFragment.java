package au.com.connectedteam.activity.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseFragment;
import au.com.connectedteam.activity.BaseIonFragment;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.application.Session;
import au.com.connectedteam.appsapi.generated.dto;
import au.com.connectedteam.config.AppConfig;
import au.com.connectedteam.network.IonHelper;
import au.com.connectedteam.network.BaseIonCallback;
import au.com.connectedteam.util.StringUtils;

/**
 * Created by bramleyt on 17/07/2015.
 */
public class LoginFragment extends BaseIonFragment {
    public static final String TAG = "LoginFragment";

    private static final int MIN_USERNAME_PASSWORD_LENGTH=1;

    private static final String EMAIL_TAG = "login.email";
    private static final String PASSWORD_TAG = "login.password";

    public interface LoginFragmentCallback{
        public void onLoginFinished(LoginFragment fragment);
    }

    private AQuery aq;
    private String lastValidation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        aq = new AQuery(view);
        String email, password;
        if(savedInstanceState!=null){
            email=savedInstanceState.getString(EMAIL_TAG);
            password=savedInstanceState.getString(PASSWORD_TAG);
        }
        else{
            SharedPreferences settings = getActivity().getSharedPreferences(AppConfig.PREFS_FILE_USERPREFS, Context.MODE_PRIVATE);
            email = settings.getString(AppConfig.LOGIN_REMEMBERME_EMAIL, null);
            password = settings.getString(AppConfig.LOGIN_REMEMBERME_PASSWORD, null);
        }
        aq.id(R.id.login_button).clicked(this, "onLoginClicked");
        aq.id(R.id.forgot_password).clicked(this, "onForgotPasswordClicked");
        aq.id(R.id.login_email).text(email);
        aq.id(R.id.login_password).text(password).getTextView().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onLoginClicked(v);
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public boolean onRefreshButtonClicked() {
        return false;
    }

    @Override
    public boolean onBackKeyPressed() {
        return true;
    }

    public void onForgotPasswordClicked(View v){
       // Fragment fragment = new ForgotPasswordFragment();
       // getBaseActivity().transactTo(0, fragment, ForgotPasswordFragment.TAG);
    }

    protected void onRequestingChanged(){
        super.onRequestingChanged();
        aq.id(R.id.login_button).enabled(!isRequesting());
    }

    private void setLastValidation(String msg){
        lastValidation=msg;
        aq.id(R.id.validation).text(lastValidation);

    }


    private void doLogin(final String email, final String password){
        if(email.length()<MIN_USERNAME_PASSWORD_LENGTH || password.length()<MIN_USERNAME_PASSWORD_LENGTH){
            setLastValidation("Please enter your email address and password");
            return;
        }
        setLastValidation("");
        final IonHelper helper = getIonHelper();
        helper.doPost(getIonLoadBuilder(), new dto.Authenticate().setUserName(email).setPassword(password))
                .go().then(new FutureCallback<Response<dto.AuthenticateResponse>>() {
            @Override
            public void onCompleted(Exception e, Response<dto.AuthenticateResponse> result) {
                if (result.getResult() != null && !StringUtils.isNullOrEmpty(result.getResult().getUserId())) {
                    /*
                    helper.doGet(LoginFragment.this, new dto.GetCustomerHeader()).go().then(new BaseIonCallback<dto.CustomerHeader>() {
                        @Override
                        public void onSuccess(dto.CustomerHeader result) {
                            //          getBaseActivity().notifyRefreshing(LoginFragment.this, isRequesting());
                            Session.getInstance().setCustomerHeader(result, password);

                        }
                    });*/
                } else {

                    if (result.getResult() != null && result.getResult().getResponseStatus() != null) {
                        setLastValidation(result.getResult().getResponseStatus().getMessage());
                    } else
                        setLastValidation(ConnectedApp.getErrorReporter().onApiErrorGetMessage(result));
                }
            }

        });
        //getBaseActivity().notifyRefreshing(this, isRequesting());
    }

    String cookie;
    public void onLoginClicked(View v){
        String email = aq.id(R.id.login_email).getText().toString();
        final String password = aq.id(R.id.login_password).getText().toString();
        doLogin(email, password);

    }
}
