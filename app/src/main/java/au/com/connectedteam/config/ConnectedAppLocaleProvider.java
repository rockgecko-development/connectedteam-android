package au.com.connectedteam.config;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import au.com.connectedteam.application.ConnectedApp;

import proguard.annotation.KeepClassMembers;

/**
 * Created by bramleyt on 23/01/2015.
 */
@KeepClassMembers
public class ConnectedAppLocaleProvider implements IAppLocaleProvider{

    public static final String[] AVAILABLE_COUNTRY_CODES = new String[]{"AU"};

    private Context mContext;

    public ConnectedAppLocaleProvider(Context c){
        mContext=c;
    }

    @Override
    public IAppLocaleBuilder getAppLocaleBuilder(String countryCode) {
        return new ConnectedAppLocaleBuilder(mContext, countryCode);
    }

    @Override
    public List<AppLocale> getAvailableProductionLocales(){
        List<AppLocale> result = new ArrayList<>();
        for(String countryCode : AVAILABLE_COUNTRY_CODES){
            result.add(new ConnectedAppLocaleBuilder(mContext, countryCode).build());
        }
        return result;
    }



    @Override
    public List<AppLocale> getAvailableDevLocales(){
        List<AppLocale> result = new ArrayList<>();
        if(ConnectedApp.DEBUG){
            result.add(new ConnectedAppLocaleBuilder(mContext, "AU").withDevWebserviceUrl("http://parse.com/todo/").withName("Parse.com").build());
            //

        }
        return result;
    }

}
