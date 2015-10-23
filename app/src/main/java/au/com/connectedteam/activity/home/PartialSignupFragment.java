package au.com.connectedteam.activity.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.koushikdutta.ion.Response;
import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseIonFragment;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.appsapi.ex.ResponseBaseHelper;
import au.com.connectedteam.appsapi.ex.CustomerHelper;
import au.com.connectedteam.appsapi.generated.dto;
import au.com.connectedteam.util.StringUtils;

import java.util.List;

/**
 * Created by bramleyt on 20/07/2015.
 */
public class PartialSignupFragment extends BaseIonFragment {

    public static final String TAG = "PartialSignupFragment";
    public static final String ARG_REFERRAL_CODE = "referralCode";
    public static final String ARG_CUSTOMER = "customer";
    public static final String ARG_PASSWORD_1 = "password1";
    public static final String ARG_PASSWORD_2 = "password2";
    public static final String ARG_PROMO_CODE = "promoCode";

    private dto.User mCustomer;
    private String mPassword1, mPassword2, mPromoCode;


    AQuery aq;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            mCustomer = (dto.User) savedInstanceState.getSerializable(ARG_CUSTOMER);
            mPassword1 = savedInstanceState.getString(ARG_PASSWORD_1);
            mPassword2 = savedInstanceState.getString(ARG_PASSWORD_2);
            mPromoCode = savedInstanceState.getString(ARG_PROMO_CODE);
        }
        if(mCustomer==null)mCustomer=new dto.User();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;// inflater.inflate(R.layout.fragment_partialsignup, container, false);
        aq = new AQuery(view);
        aq.id(R.id.signup_button).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFocusAndSubmit();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        modelToUI();
    }

    @Override
    protected void onRequestingChanged() {
        super.onRequestingChanged();
        aq.id(R.id.signup_button).enabled(!isRequesting());
    }

    private void clearFocusAndSubmit() {
        View focussedView = getView().findFocus();
        if (focussedView != null) focussedView.clearFocus();
        List<String> validationErrors = CustomerHelper.validate(mCustomer, true);
        boolean passwordsOK = !StringUtils.isAnyNullOrEmpty(mPassword1, mPassword2) && mPassword1.equals(mPassword2) && mPassword1.length()>0;
        if(!passwordsOK) validationErrors.add("Passwords do not match");
        else if (mPassword2.length()<CustomerHelper.MIN_PASSWORD_LENGTH)
            validationErrors.add(String.format("A valid password is required of at least %d characters", CustomerHelper.MIN_PASSWORD_LENGTH));
        if(validationErrors.size()>0){
            aq.id(R.id.validation).text(StringUtils.stringListToString(validationErrors, "\n", false));
        }
        else{
            aq.id(R.id.validation).text("");
            dto.Signup req = new dto.Signup().setUser(mCustomer);

            executePartialSignup(req);

        }
    }
    private void executePartialSignup(final dto.Signup req){
        getIonHelper().doPost(getIonLoadBuilder(), req).go().setCallback(new BaseFragmentIonCallback<dto.ResponseBase>() {
            @Override
            public void onSuccess(dto.ResponseBase result) {
                if(result.getErrorInfo().getErrorNo()>0){
                    aq.id(R.id.validation).text(ResponseBaseHelper.formatMessage(result));
                }
                else{
                 /*   getIonHelper().doGet(getIonLoadBuilder(), new dto.GetCustomerHeader()).go().setCallback(new BaseFragmentIonCallback<dto.CustomerHeader>() {
                        @Override
                        public void onSuccess(dto.CustomerHeader result) {
                            Session.getInstance().setCustomerHeader(result, req.getPassword());
                            Toast.makeText(getActivity(), "Balance: $" + result.PrimaryBalance + " VIP: " + result.IsVIP, Toast.LENGTH_LONG).show();
                        }
                    });*/
                }
            }
            public void onError(Response<dto.ResponseBase> response) {
                new AlertDialog.Builder(getActivity()).setMessage(ConnectedApp.getErrorReporter().onApiErrorGetMessage(response)).setPositiveButton(R.string.ok, null).show();

            }
        });
        onRequestingChanged();
    }

    private void modelToUI(){
        /*
        aq.id(R.id.customer_firstname).text(mCustomer.FirstName).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) mCustomer.FirstName=((EditText) v).getText().toString().trim();
            }
        });
        aq.id(R.id.customer_surname).text(mCustomer.Surname).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) mCustomer.Surname=((EditText) v).getText().toString().trim();
            }
        });
        aq.id(R.id.customer_username).text(mCustomer.Username).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) mCustomer.Username=((EditText) v).getText().toString().trim();
            }
        });
        aq.id(R.id.customer_email).text(mCustomer.Email).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) mCustomer.Email=((EditText) v).getText().toString().trim();
            }
        });
        aq.id(R.id.customer_password).text(mPassword1).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) mPassword1=((EditText) v).getText().toString();
            }
        });
        aq.id(R.id.customer_password_confirm).text(mPassword2).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) mPassword2 = ((EditText) v).getText().toString();
            }
        });
        aq.id(R.id.promo_code).text(mPromoCode).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) mPromoCode=((EditText) v).getText().toString().trim();
            }
        });
        */
    }

    @Override
    public boolean onRefreshButtonClicked() {
        return false;
    }

    @Override
    public boolean onBackKeyPressed() {
        return !isRequesting();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_CUSTOMER, mCustomer);
        outState.putString(ARG_PASSWORD_1, mPassword1);
        outState.putString(ARG_PASSWORD_2, mPassword2);
        outState.putString(ARG_PROMO_CODE, mPromoCode);
    }
}
