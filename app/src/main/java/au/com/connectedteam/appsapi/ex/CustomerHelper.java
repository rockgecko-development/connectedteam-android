package au.com.connectedteam.appsapi.ex;

import android.content.res.Resources;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.appsapi.generated.dto;
import au.com.connectedteam.util.StringUtils;
import au.com.connectedteam.util.ValidatorUtil;

/**
 * Created by bramleyt on 20/07/2015.
 */
public class CustomerHelper {
    public static final int MIN_AGE_FOR_SIGNUP = 18;
    public static final int MIN_PASSWORD_LENGTH = 6;

    public static List<String> validate(dto.User c, boolean isPartialSignup){
        Resources r = ConnectedApp.getContextStatic().getResources();
        List<String> results = new ArrayList<>();
        if(StringUtils.isNullOrEmpty(c.getFirstName())) results.add("First name is required");
        if(StringUtils.isNullOrEmpty(c.getSurname())) results.add("Last name is required");
        if(StringUtils.isNullOrEmpty(c.getUserNameDisplay())) results.add(r.getString(R.string.username_error));
        if(!ValidatorUtil.email(c.getEmail())) results.add(r.getString(R.string.email_error));
        if(!isPartialSignup){
            //DOB etc
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -MIN_AGE_FOR_SIGNUP);
            if(c.getDateOfBirth()==null || c.getDateOfBirth().after(cal.getTime())) results.add(r.getString(R.string.validation_min_age_for_signup));
            if(StringUtils.isNullOrEmpty(c.getResidentialAddress1())) results.add("Residential address cannot be empty");
            if(StringUtils.isNullOrEmpty(c.getResidentialCountryCode())) results.add("Country is required");

        }
        return results;
    }

    public static boolean isProfileComplete(dto.User c){
        return c.getResidentialCountryCode()!=null && c.getResidentialAddress1()!=null && c.getDateOfBirth()!=null;
    }


}
