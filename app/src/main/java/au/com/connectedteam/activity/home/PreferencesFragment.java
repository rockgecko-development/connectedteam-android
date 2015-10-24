package au.com.connectedteam.activity.home;

import au.com.connectedteam.activity.BaseFragment;

/**
 * Created by bramleyt on 24/10/2015.
 */
public class PreferencesFragment extends BaseFragment{
    public static final java.lang.String TAG = "PreferencesFragment";

    @Override
    public boolean isRequesting() {
        return false;
    }

    @Override
    public boolean onRefreshButtonClicked() {
        return false;
    }

    @Override
    public boolean onBackKeyPressed() {
        return true;
    }
}
