package au.com.connectedteam.models;

import au.com.connectedteam.application.ConnectedApp;

import au.com.connectedteam.util.StringUtils;
import proguard.annotation.KeepClassMembers;

/**
 * Created by bramleyt on 22/12/2014.
 */
@KeepClassMembers
public class AppVersion {

    /**
     * Version of settings file. Not used yet.
     */
    private int settingVersion;

    private String clientVersion;
    private String minClientVersion;
    private String whatsNew;
    private String versionUpdateDate;

    public AppVersion(int settingVersion, String clientVersion, String minClientVersion, String whatsNew, String versionUpdateDate) {
        this.settingVersion = settingVersion;
        this.clientVersion = clientVersion;
        this.minClientVersion = minClientVersion;
        this.whatsNew = whatsNew;
        this.versionUpdateDate = versionUpdateDate;
    }
    public AppVersion(){

    }


    public boolean updateAvailable(){
        double latestClientVersionDouble=-1, localAppVersionDouble=-1;
        try{
            String localAppVersionName = ConnectedApp.getContextStatic().getPackageManager().getPackageInfo(
                    ConnectedApp.getContextStatic().getPackageName(), 0).versionName;

            latestClientVersionDouble = Double.parseDouble(clientVersion);

            localAppVersionDouble = Double.parseDouble(localAppVersionName);
        }
        catch(Exception e){}
        return latestClientVersionDouble>localAppVersionDouble;

    }

    public boolean forceUpdate() {
        double minAPIVersionDouble=-1, localAppVersionDouble=-1;
        try{
            String localAppVersionName = ConnectedApp.getContextStatic().getPackageManager().getPackageInfo(
                    ConnectedApp.getContextStatic().getPackageName(), 0).versionName;
            minAPIVersionDouble = Double.parseDouble(minClientVersion);
            localAppVersionDouble = Double.parseDouble(localAppVersionName);
        }
        catch(Exception e){}
        return minAPIVersionDouble>localAppVersionDouble;
    }
    public String getAppDownloadURL(){
            return "market://details?id="+ConnectedApp.getContextStatic().getPackageName();
    }

    public String getVersionUpdateDate() {
        return versionUpdateDate;
    }

    public String getWhatsNew() {
        return StringUtils.isNullOrEmpty(whatsNew, "").replace("\\n", "\n");
    }
    public String getLatestClientVersion() {
        return clientVersion;
    }
    public String getMinClientVersion() {
        return minClientVersion;
    }
}
