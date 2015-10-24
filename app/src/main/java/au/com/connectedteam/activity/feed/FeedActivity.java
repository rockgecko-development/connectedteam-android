package au.com.connectedteam.activity.feed;

import android.os.Bundle;
import android.widget.TextView;

import au.com.connectedteam.activity.BaseActivity;

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
    }
}
