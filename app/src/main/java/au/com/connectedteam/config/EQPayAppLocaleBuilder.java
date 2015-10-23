package au.com.connectedteam.config;

import android.content.Context;

import java.util.Locale;

import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.util.StringUtils;
import proguard.annotation.KeepClassMembers;

/**
 * Created by bramleyt on 23/01/2015.
 */
@KeepClassMembers
public class EQPayAppLocaleBuilder implements IAppLocaleBuilder{



    private String country;
    private String mCountryCode;
    private String languageCode;
    private String currency;
    private String currencyCode;

    private String webserviceUrl;


    private String name;

    private Context mContext;

    public EQPayAppLocaleBuilder(Context c, String countryCode){
        this.mContext=c;
        //only USA for now
        mCountryCode ="US";
        country="United States";
        languageCode="en";
        currency= "US Dollars";
        currencyCode = "USD";
        name = "EQPay US";
        webserviceUrl=mContext.getString(R.string.server_prod);
        /*
        String lCountryCode = countryCode;
        if(StringUtils.isNullOrEmpty(lCountryCode)){
            lCountryCode = Locale.getDefault().getCountry();
        }

        switch(lCountryCode){
            case "NZ":
                mCountryCode ="NZ";
                country="New Zealand";
                languageCode="en";
                currency= "New Zealand Dollars";
                currencyCode = "NZD";
                name = "Webjet New Zealand";
                webserviceUrl=mContext.getString(R.string.server_live_nz);
                break;
            case "AU":
            default:
                mCountryCode ="AU";
                country="Australia";
                languageCode="en";
                currency= "Australian Dollars";
                currencyCode = "AUD";
                name = "Webjet Australia";
                webserviceUrl=mContext.getString(R.string.server_live_au);
                break;
        }*/
    }

    @Override
    public EQPayAppLocaleBuilder withDevWebserviceUrl(String url) {
        if(ConnectedApp.DEBUG) {
            this.webserviceUrl = url;
        }
        return this;
    }
    @Override
    public EQPayAppLocaleBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public AppLocale build() {
        return new AppLocale(country, mCountryCode, languageCode, currency, currencyCode, webserviceUrl, name);
    }

}
