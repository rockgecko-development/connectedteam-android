package au.com.connectedteam.activity.home;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.koushikdutta.ion.Response;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import net.servicestack.func.Func;
import net.servicestack.func.Function;
import net.servicestack.func.Predicate;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseIonFragment;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.application.Session;
import au.com.connectedteam.appsapi.ex.ResponseBaseHelper;
import au.com.connectedteam.appsapi.ex.CustomerHelper;
import au.com.connectedteam.appsapi.generated.dto;
import au.com.connectedteam.util.FuncEx;
import au.com.connectedteam.util.ParseUtils;
import au.com.connectedteam.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * Created by bramleyt on 20/07/2015.
 */
public class SignupFragment extends BaseIonFragment {

    public static final String TAG = "SignupFragment";
    public static final String ARG_CUSTOMER = "customer";
    public static final String ARG_PASSWORD_1 = "password1";
    public static final String ARG_PASSWORD_2 = "password2";
    public static final String ARG_PROMO_CODE = "promoCode";

    private ParseUser mCustomer;
    private String mPassword1, mPassword2, mEmailPrefix, mEmailSuffix;
    private List<ParseObject> mHospitalAvails;

    AQuery aq;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            String userJson= savedInstanceState.getString(ARG_CUSTOMER);
            mCustomer = ParseUtils.getGson().fromJson(userJson, ParseUser.class);
            mPassword1 = savedInstanceState.getString(ARG_PASSWORD_1);
            mPassword2 = savedInstanceState.getString(ARG_PASSWORD_2);
        }
        if(mCustomer==null)mCustomer=new ParseUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_signup_1, container, false);
        aq = new AQuery(view);
        aq.id(R.id.btn_submit).clicked(new View.OnClickListener() {
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
        fetchInitialData();
        modelToUIIfReady();

    }
    private void fetchInitialData(){
        if(mHospitalAvails==null){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("HospitalAvail");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    mHospitalAvails=objects;
                    modelToUIIfReady();
                }
            });
        }
    }

    private void modelToUIIfReady(){
        if(mHospitalAvails!=null) modelToUI();
    }

    @Override
    protected void onRequestingChanged() {
        super.onRequestingChanged();
        aq.id(R.id.btn_submit).enabled(!isRequesting());
    }

    private void clearFocusAndSubmit() {
        View focussedView = getView().findFocus();
        if (focussedView != null) focussedView.clearFocus();
        List<String> validationErrors = CustomerHelper.validate(mCustomer);
        boolean passwordsOK = !StringUtils.isAnyNullOrEmpty(mPassword1, mPassword2) && mPassword1.equals(mPassword2) && mPassword1.length()>0;
        if(!passwordsOK) validationErrors.add("Passwords do not match");
        else if (mPassword2.length()<CustomerHelper.MIN_PASSWORD_LENGTH)
            validationErrors.add(String.format("A valid password is required of at least %d characters", CustomerHelper.MIN_PASSWORD_LENGTH));
        if(validationErrors.size()>0){
            aq.id(R.id.validation).text(StringUtils.stringListToString(validationErrors, "\n", false));
        }
        else{
            aq.id(R.id.validation).text("");
            mCustomer.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e!=null){
                        new AlertDialog.Builder(getActivity()).setMessage(e.getMessage()).setPositiveButton(R.string.ok, null).show();
                    }
                    else{
                        Session.getInstance().setCustomerHeader(mCustomer, mPassword1);
                    }
                }
            });

        }
    }


    private void modelToUI(){

        aq.id(R.id.edit_email_prefix).text(mEmailPrefix).getView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) mEmailPrefix=((EditText) v).getText().toString().trim();
            }
        });
        //security questions
        List<String> domains = Func.distinct(FuncEx.selectMany(mHospitalAvails, new Function<ParseObject, Collection<String>>() {
            @Override
            public Collection<String> apply(ParseObject parseObject) {
                return parseObject.getList("allowedEmails");
            }
        }));
        ArrayAdapter<String> questionsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, domains);
        questionsAdapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);

        aq.id(R.id.spinner_email_domain).adapter(questionsAdapter).setSelection(domains.indexOf(mEmailSuffix))
                .itemSelected(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mEmailSuffix = (String) parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        /*
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
        outState.putString(ARG_CUSTOMER, ParseUtils.getGson().toJson(mCustomer));
        outState.putString(ARG_PASSWORD_1, mPassword1);
        outState.putString(ARG_PASSWORD_2, mPassword2);
    }
}
