package au.com.connectedteam.activity.home;

import android.os.Bundle;
import android.view.Window;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseActivity;
import au.com.connectedteam.application.Session;

/**
 * Created by bramleyt on 17/07/2015.
 */
public class HomeActivity extends BaseActivity {

    public static final String ARG_ON_UNAUTHORISED = "onUnauthorised";

    private boolean hasGoneToLobby;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeatureBaseActivity(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        if(getSupportFragmentManager().findFragmentById(R.id.fragment_container_main)==null){
            transactTo(0, new HomeFragment(), HomeFragment.TAG);
        }
        postApplicationInit();
        if(Session.getInstance().isLoggedIn() && !Session.getInstance().isExecutingAutoLogin()) navigateLobbyOrGoBack();
    }
    private void postApplicationInit(){

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    public void onLogoutClicked(){
        Session.getInstance().executeLogout();
    }
    protected void updateUserHeader() {
    }

        @Override
    protected void updateUIOnLoginOut() {
        if(Session.getInstance().isLoggedIn() && !Session.getInstance().isExecutingAutoLogin()) navigateLobbyOrGoBack();
    }
    @Override
    protected void onUserHeader(){
        if(Session.getInstance().isLoggedIn() && !Session.getInstance().isExecutingAutoLogin()) navigateLobbyOrGoBack();
    }
    @Override
    protected void onReAuth(){
        if(Session.getInstance().isLoggedIn() && !Session.getInstance().isExecutingAutoLogin()) navigateLobbyOrGoBack();
    }

    private void navigateLobbyOrGoBack(){
        if(!hasGoneToLobby ) {
            hasGoneToLobby = true;
            if(getIntent().hasExtra(ARG_ON_UNAUTHORISED)){
                //finish();
            }else {
                //TODO
                /*
                Intent intent = new Intent(this, LobbyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                */
            }
        }
    }


    public void navigateLogin(Bundle args){
        LoginFragment fragment = new LoginFragment();
        if(args!=null){
            fragment.setArguments(args);
        }
        transactTo(0, fragment, LoginFragment.TAG);
    }
    public void navigateSignup(){
        SignupFragment fragment = new SignupFragment();
        transactTo(0, fragment, SignupFragment.TAG);
    }
}
