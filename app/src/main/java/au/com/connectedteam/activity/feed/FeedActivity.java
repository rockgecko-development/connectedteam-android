package au.com.connectedteam.activity.feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseActivity;
import au.com.connectedteam.activity.home.PreferencesActivity;
import au.com.connectedteam.activity.settings.UserSettingsActivity;
import au.com.connectedteam.util.IntentUtil;

/**
 * Created by bramleyt on 24/10/2015.
 */
public class FeedActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("feed activity");
        setContentView(tv);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_feed, menu);
        return super.onCreateOptionsMenu(menu);
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
        }
        if(intent!=null){
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
