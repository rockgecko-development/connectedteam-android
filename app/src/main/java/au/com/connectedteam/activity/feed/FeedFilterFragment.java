package au.com.connectedteam.activity.feed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
        View view = inflater.inflate(R.layout.cell_simple_2_line, container, false);
        UIUtil.doHeadingText(view, "Feed filter", "todo");
        return view;
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
