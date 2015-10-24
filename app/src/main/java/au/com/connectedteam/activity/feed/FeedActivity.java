package au.com.connectedteam.activity.feed;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseActivity;
import au.com.connectedteam.activity.home.PreferencesActivity;
import au.com.connectedteam.activity.settings.UserSettingsActivity;
import au.com.connectedteam.adapter.CustomFragmentPagerAdapter;
import au.com.connectedteam.util.IntentUtil;

/**
 * Created by bramleyt on 24/10/2015.
 */
public class FeedActivity extends BaseActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs_viewpager_overlay);
        if(savedInstanceState!=null){
            mFilter= (FeedFilter) savedInstanceState.getSerializable("mFilter");
        }
        if(mFilter==null)
            mFilter=new FeedFilter();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager();


        String nextActivity = getIntent().getStringExtra(IntentUtil.ARG_NEXT_ACTIVITY_CLASS);
        if(nextActivity!=null){
            try {
                Class<?> nextClass = Class.forName(nextActivity);
                Intent intent = new Intent(this, nextClass);
                startActivity(intent);
            }

            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private void showFilter(boolean show){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_overlay);
        if(show && fragment==null) {
            fragment = new FeedFilterFragment();
            transactTo(R.id.fragment_container_overlay, fragment, FeedFilterFragment.TAG, false, R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.in_from_top, R.anim.out_to_top);

        }
        else if (!show && fragment instanceof  FeedFilterFragment && ((FeedFilterFragment)fragment).onBackKeyPressed()){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            fragment=null;
        }
       // if(mTabLayout!=null)
         //   mTabLayout.setVisibility(fragment!=null? View.GONE:View.VISIBLE);
    }
    protected Fragment getMainFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container_overlay);
    }
    private FeedListFragment getCurrentListFragment(){
        if(getMainFragment()!=null || mAdapter==null || mViewPager==null) return null;
        Fragment fragment =mAdapter.getExistingFragment(mViewPager.getCurrentItem());
        if(fragment instanceof FeedListFragment) return (FeedListFragment) fragment;
        return null;
    }
    public void notifyFilterChanged(){
        if(mAdapter!=null) for (int i=0;i<mAdapter.getCount();i++){
            Fragment fragment=mAdapter.getExistingFragment(i);
            if(fragment instanceof FeedListFragment)
                ((FeedListFragment) fragment).onFilterChanged();
        }
    }
    @Override
    public void onBackPressed() {
        Fragment fragment =getMainFragment();
        if(fragment instanceof  FeedFilterFragment && ((FeedFilterFragment)fragment).onBackKeyPressed()){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            if(mTabLayout!=null)
                mTabLayout.setVisibility(View.VISIBLE);
        }
        else super.onBackPressed();
    }

    private Adapter mAdapter;
    public void setupViewPager() {
        mAdapter = new Adapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
    static final String[] TITLES = new String[]{"My Feed", "My History"};

    private FeedFilter mFilter;
    public FeedFilter getFilter() {
        return mFilter;
    }

    class Adapter extends CustomFragmentPagerAdapter{

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment makeFragment(int position) {
            Bundle args = new Bundle();
            args.putInt(FeedListFragment.ARG_MODE, position);
            FeedListFragment fragment = new FeedListFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_feed, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add).setVisible(getMainFragment() == null);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=null;
        switch(item.getItemId()){
            case R.id.menu_preferences:
                intent = new Intent(this, PreferencesActivity.class);
                break;
            case R.id.menu_app_settings:
                intent = new Intent(this, UserSettingsActivity.class);
                break;
            case R.id.menu_filter:
                showFilter(getMainFragment()==null);
                return true;
            case R.id.menu_logout:
                onLogoutClicked();
                return true;
        }
        if(intent!=null){
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
