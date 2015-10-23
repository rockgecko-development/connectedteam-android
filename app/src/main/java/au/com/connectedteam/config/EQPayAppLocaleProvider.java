package au.com.connectedteam.config;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;

import proguard.annotation.KeepClassMembers;

/**
 * Created by bramleyt on 23/01/2015.
 */
@KeepClassMembers
public class EQPayAppLocaleProvider implements IAppLocaleProvider{

    public static final String[] AVAILABLE_COUNTRY_CODES = new String[]{"AU"};

    private Context mContext;

    public EQPayAppLocaleProvider(Context c){
        mContext=c;
    }

    @Override
    public IAppLocaleBuilder getAppLocaleBuilder(String countryCode) {
        return new EQPayAppLocaleBuilder(mContext, countryCode);
    }

    @Override
    public List<AppLocale> getAvailableProductionLocales(){
        List<AppLocale> result = new ArrayList<>();
        for(String countryCode : AVAILABLE_COUNTRY_CODES){
            result.add(new EQPayAppLocaleBuilder(mContext, countryCode).build());
        }
        return result;
    }



    @Override
    public List<AppLocale> getAvailableDevLocales(){
        List<AppLocale> result = new ArrayList<>();
        if(ConnectedApp.DEBUG){
            result.add(new EQPayAppLocaleBuilder(mContext, "AU").withDevWebserviceUrl("http://192.168.5.53/EQPay.AppsAPI.SS/").withName("bramley-pc").build());
            result.add(new EQPayAppLocaleBuilder(mContext, "AU").withDevWebserviceUrl("http://connectedteamandroidapiau-dev.azurewebsites.net/").withName("Android Dev").build());
            result.add(new EQPayAppLocaleBuilder(mContext, "AU").withDevWebserviceUrl("http://connectedteamandroidapiau-stag.azurewebsites.net/").withName("Android Stag").build());
            result.add(new EQPayAppLocaleBuilder(mContext, "AU").withDevWebserviceUrl("http://connectedteamiosapiau-dev.azurewebsites.net/").withName("iOS Dev").build());
            result.add(new EQPayAppLocaleBuilder(mContext, "AU").withDevWebserviceUrl("http://connectedteamiosapiau-stag.azurewebsites.net/").withName("iOS Stag").build());
            //

        }
        return result;
    }

}
