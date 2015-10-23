package au.com.connectedteam.activity;


import com.androidquery.AQuery;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.settings.UserSettingsActivity;
import au.com.connectedteam.application.Session;
import au.com.connectedteam.ui.ribbonmenu.IRibbonMenuCallback;
import au.com.connectedteam.ui.ribbonmenu.MainDrawerMenu;
import au.com.connectedteam.ui.ribbonmenu.RibbonMenuBase;
import au.com.connectedteam.ui.ribbonmenu.RibbonMenuItem;
import au.com.connectedteam.ui.ribbonmenu.RibbonMenuView;



public abstract class BaseDrawerActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);






	}
	

	protected void updateUIOnLoginOut(){
		if(mNavigationView!=null){
			boolean loggedIn = Session.getInstance().isLoggedIn();
			TextView tvUsername = (TextView) mNavigationView.findViewById(R.id.drawer_header_username);
			tvUsername.setText(loggedIn?Session.getInstance().getCustomerHeader().userName.getUserNameDisplay():"");
			//mDrawerMenu.getRibbonMenuView().setMenuItemVisible(R.id.rbm_item_signup, !loggedIn);					
		}
	}
	
	/**
	 * call this from onCreate, after setting content view, to set up the drawer menu
	 *
	 */
	protected void setupBaseUI(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar!=null)
			setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.ic_menu);
		ab.setDisplayHomeAsUpEnabled(true);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mNavigationView = (NavigationView) findViewById(R.id.nav_view);
		mNavigationView.setNavigationItemSelectedListener(this);

		if(isNavigationDisabled()) mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

	}
	@Override
	public void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);

	}
	@Override
	public void onConfigurationChanged (Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
			mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
		return super.onOptionsItemSelected(item);
	}

    /*
    private int mOriginalActionBarDisplayOptions;
    @Override
    public void configureHomeAsUpButton(){
        ActionBar actionBar = getSupportActionBar();
        mOriginalActionBarDisplayOptions = actionBar.getDisplayOptions();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP
        );
        if(mDrawerToggle!=null){
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }
        if(mDrawerMenu!=null){
            mDrawerMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
        }
    }
    @Override
    public void removeDoneBar(){
        if(mOriginalActionBarDisplayOptions!=0){
            getSupportActionBar().setDisplayOptions(mOriginalActionBarDisplayOptions);
        }
        else{
            getSupportActionBar().setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP,
                    ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP
                            | ActionBar.DISPLAY_SHOW_TITLE);
        }
        if(mDrawerToggle!=null) mDrawerToggle.setDrawerIndicatorEnabled(true);
        if(mDrawerMenu!=null && !isNavigationDisabled()) {
            mDrawerMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
        }
    }
    */
	
	@Override
	public boolean onNavigationItemSelected(MenuItem menuItem){
        if(isNavigationDisabled()) return true;
		Intent intent=null;
		switch(menuItem.getItemId()){
			case R.id.menu_settings:
				intent = new Intent(BaseDrawerActivity.this, UserSettingsActivity.class);
				break;
			case R.id.menu_logout:
				onLogoutClicked();
				return true;
		default:
			break;
		}
		mDrawerLayout.closeDrawers();
		if(intent!=null){
			if(intent.getComponent()!=null && intent.getComponent().getClassName().equals(BaseDrawerActivity.this.getClass().getName())){
				//already here.
				return true;
			}
			startActivity(intent);
			return true;
		}
		return false;
	}


    public void setNavigationDisabled(boolean navigationDisabled){
        super.setNavigationDisabled(navigationDisabled);
        if(navigationDisabled && mDrawerLayout!=null){
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }
	/*
    @Override
    public void onBackPressed() {
        if(mDrawerMenu!=null
                && mDrawerMenu.getDrawerLockMode(mDrawerMenu.getRibbonMenuView())==DrawerLayout.LOCK_MODE_UNLOCKED
                && mDrawerMenu.isDrawerOpen(mDrawerMenu.getRibbonMenuView())){
            mDrawerMenu.closeDrawers();
        }
        else super.onBackPressed();
    }*/
}
