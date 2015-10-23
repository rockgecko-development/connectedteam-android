package au.com.connectedteam.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import au.com.connectedteam.R;
import au.com.connectedteam.util.StringUtils;


public class GenericDetailActivity extends BaseActivity {

	public static final String TAG = "GenericDetailActivity";
	public static final String ARG_FRAGMENT_CLASSNAME = "GenericDetailActivity.FragmentClass";
	public static final String ARG_FRAGMENT_TAG = "GenericDetailActivity.FragmentTag";
	public static final String ARG_TITLE = "GenericDetailActivity.Title";
	public static final String ARG_SUBTITLE = "GenericDetailActivity.Subtitle";
	
	public static Intent makeIntent(Context c, Class<? extends Fragment> fragment, String fragmentTag, String activityTitle){
		Intent intent = new Intent(c, GenericDetailActivity.class);
		intent.putExtra(ARG_FRAGMENT_CLASSNAME, fragment.getName());
		intent.putExtra(ARG_FRAGMENT_TAG, fragmentTag);
		intent.putExtra(ARG_TITLE, activityTitle);
		return intent;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		if(savedInstanceState==null){
			if(!StringUtils.isNullOrEmpty(getIntent().getStringExtra(ARG_TITLE))){
				getSupportActionBar().setTitle(getIntent().getStringExtra(ARG_TITLE));
				if(!StringUtils.isNullOrEmpty(getIntent().getStringExtra(ARG_SUBTITLE))){
					getSupportActionBar().setSubtitle(getIntent().getStringExtra(ARG_SUBTITLE));
				}
			}
			Fragment fragment = Fragment.instantiate(this, getIntent().getStringExtra(ARG_FRAGMENT_CLASSNAME), intentToFragmentArguments(getIntent()));
			transactTo(getMainFragmentContainerId(), fragment, StringUtils.isNullOrEmpty(getIntent().getStringExtra(ARG_FRAGMENT_TAG), fragment.getClass().getSimpleName()));
		}
		else{
			getSupportActionBar().setTitle(savedInstanceState.getString(ARG_TITLE));
			if(!StringUtils.isNullOrEmpty(savedInstanceState.getString(ARG_SUBTITLE))){
				getSupportActionBar().setSubtitle(savedInstanceState.getString(ARG_SUBTITLE));
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putString(ARG_TITLE, getSupportActionBar().getTitle().toString());
		outState.putString(ARG_SUBTITLE, StringUtils.isNullOrEmpty(getSupportActionBar().getSubtitle(), "").toString());
	}
	
	
}
