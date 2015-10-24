package au.com.connectedteam.appsapi.ex;

import android.content.res.Resources;


import com.parse.ParseUser;

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

    public static List<String> validate(ParseUser c){
        Resources r = ConnectedApp.getContextStatic().getResources();
        List<String> results = new ArrayList<>();
        if(StringUtils.isNullOrEmpty(c.getUsername()) || StringUtils.isNullOrEmpty(c.getEmail())) results.add(r.getString(R.string.email_error));

        if(!ValidatorUtil.email(c.getEmail())) results.add(r.getString(R.string.email_error));

        return results;
    }




}
