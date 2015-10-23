package au.com.connectedteam.config;

import android.content.Context;

import java.util.List;

/**
 * Created by bramleyt on 23/01/2015.
 */
public interface IAppLocaleBuilder {

    public IAppLocaleBuilder withDevWebserviceUrl(String api);
    public IAppLocaleBuilder withName(String name);

    public AppLocale build();



}
