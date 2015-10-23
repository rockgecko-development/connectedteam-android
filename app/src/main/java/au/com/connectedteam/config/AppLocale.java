package au.com.connectedteam.config;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import au.com.connectedteam.application.ConnectedApp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


import au.com.connectedteam.R;

import proguard.annotation.KeepClassMembers;

/**
 * Contains the current country, currency and webservice url to use.
 * Also has {@link #getStringResource(LocaleResource)} for country-specific strings.
 * @author bramleyt
 *
 */


@KeepClassMembers
public class AppLocale {



    public enum LocaleResource{
		support_contact,

	}
    public enum LocaleDrawableResource{
        logo_splash
    }

	private String country;
	private String countryCode;
	private String languageCode;
	private String currency;
	private String currencyCode;

	private String webserviceUrl;

	private String name;
	
	public AppLocale(String country, String countryCode, String languageCode, 
			String currency,
			String currencyCode, String url, String name) {
		this.country = country;
		this.countryCode = countryCode;
		this.languageCode = languageCode;
		this.currency = currency;
		this.currencyCode = currencyCode;
		this.webserviceUrl=url;
		this.name=name;
	}

    @Override
    public boolean equals(Object o) {
        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == o) {
            return true;
        }

        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (!(o instanceof AppLocale)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        AppLocale lhs = (AppLocale) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.
        return
                getCountryCode().equals(lhs.getCountryCode())
                && getName().equals(lhs.getName())
                && getLanguageCode().equals(lhs.getLanguageCode())
                && getCurrencyCode().equals(lhs.getCurrencyCode())
                && getWebserviceUrl().equals(lhs.getWebserviceUrl());
    }
	
	/**
	 * Get a country-specific string
	 * @param resource
	 * @return
	 */
	public String getStringResource(LocaleResource resource){
		Context c = ConnectedApp.getContextStatic();
		switch(resource){		
		case support_contact:
            default:
			int resId= c.getResources().getIdentifier(resource.toString()+"_"+ getCountryCode().toLowerCase(Locale.US), "string",
					c.getPackageName());
			return c.getString(resId);
		}
		//return null;
	}

    /**
     * Get a country-specific drawable
     * @param resource
     * @return
     */
    public Drawable getDrawableResource(LocaleDrawableResource resource){
        Context c = ConnectedApp.getContextStatic();
        switch(resource){
            case logo_splash:
                String name = resource.toString();
                String nameLocale = name+"_"+getCountryCode().toLowerCase(Locale.US);
                int resId = c.getResources().getIdentifier(nameLocale, "drawable",
                        c.getPackageName());
                if(resId==0) resId = c.getResources().getIdentifier(name, "drawable",
                        c.getPackageName());
                return resId!=0?c.getResources().getDrawable(resId):null;
        }
        return null;
    }


	
	/**
	 * Eg en-AU
	 * @return
	 */
	public String getCultureName(){

        return getLanguageCode() +"-"+ getCountryCode();
	}
	
	public String getWebserviceUrl(){
		return webserviceUrl;
	}
	/**
	 * Eg DEBUG (url) or 'Webjet Australia'
	 * @return
	 */
	public String getName(){
		if(name==null) return webserviceUrl;
		return name;
	}

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }


}
