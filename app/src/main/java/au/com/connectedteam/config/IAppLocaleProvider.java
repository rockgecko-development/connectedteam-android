package au.com.connectedteam.config;

import java.util.List;

/**
 * Created by bramleyt on 23/01/2015.
 */
public interface IAppLocaleProvider {
    public IAppLocaleBuilder getAppLocaleBuilder(String countryCode);
    public List<AppLocale> getAvailableProductionLocales();
    public List<AppLocale> getAvailableDevLocales();
}
