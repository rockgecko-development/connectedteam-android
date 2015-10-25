package au.com.connectedteam.activity.feed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseFragment;
import au.com.connectedteam.util.UIUtil;

/**
 * Created by bramleyt on 24/10/2015.
 */
public class FeedFilterFragment extends BaseFragment {
    public static final java.lang.String TAG = "FeedFilterFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_filter, container, false);
        SwitchCompat sw = (SwitchCompat) view.findViewById(R.id.switch_tags);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FeedFilter filter = getFilter();
                if (filter != null) {
                    if (b) filter.reset();
                    else filter.tags.clear();
                }
                modelToUI();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        modelToUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_filter_reset:
               // if(mRecyclerView!=null && mRecyclerView.getAdapter()!=null) {
                    getFilter().reset();
                    modelToUI();
                //}

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void modelToUI(){
        SwitchCompat sw = (SwitchCompat) getView().findViewById(R.id.switch_tags);
        FeedFilter filter = getFilter();
        if(filter!=null){
            boolean checked = filter.tags.size()>0;
            //if(sw.isChecked()!=checked)
                sw.setChecked(checked);
            ((TextView)getView().findViewById(R.id.tvFilterDescription)).setText(checked?R.string.feed_filter_tags_on:R.string.feed_filter_tags_off);
        }

    }
    private FeedFilter getFilter(){
        if(getActivity()==null) return null;
        return ((FeedActivity)getActivity()).getFilter();
    }
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
        View focussedView = getView().findFocus();
        if(focussedView!=null) focussedView.clearFocus();
        ((FeedActivity)getActivity()).notifyFilterChanged();
        return true;
    }
}
