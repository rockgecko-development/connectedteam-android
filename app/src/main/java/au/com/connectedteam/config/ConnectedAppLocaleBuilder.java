package au.com.connectedteam.config;

import android.content.Context;

import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;
import proguard.annotation.KeepClassMembers;

/**
 * Created by bramleyt on 23/01/2015.
 */
@KeepClassMembers
public class ConnectedAppLocaleBuilder implements IAppLocaleBuilder{



    private String country;
    private String mCountryCode;
    private String languageCode;
    private String currency;
    private String currencyCode;

    private String webserviceUrl;


    private String name;

    private Context mContext;

    public ConnectedAppLocaleBuilder(Context c, String countryCode){
        this.mContext=c;
        //only AU for now
        mCountryCode ="AU";
        country="Australia";
        languageCode="en";
        currency= "AU Dollars";
        currencyCode = "AUD";
        name = "ConnectED";
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
    public ConnectedAppLocaleBuilder withDevWebserviceUrl(String url) {
        if(ConnectedApp.DEBUG) {
            this.webserviceUrl = url;
        }
        return this;
    }
    @Override
    public ConnectedAppLocaleBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public AppLocale build() {
        return new AppLocale(country, mCountryCode, languageCode, currency, currencyCode, webserviceUrl, name);
    }

}
