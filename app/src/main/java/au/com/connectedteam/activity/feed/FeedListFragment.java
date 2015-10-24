package au.com.connectedteam.activity.feed;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.ListOrExpandableListFragment;
import au.com.connectedteam.util.StringUtils;

/**
 * Created by bramleyt on 24/10/2015.
 */
public class FeedListFragment extends ListOrExpandableListFragment {
    public static final String TAG = "FeedListFragment";
    public static final String ARG_MODE = "FeedListFragment.mode";
    public static final int MODE_FEED = 0;
    public static final int MODE_HISTORY = 1;
    private int mMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMode=getArguments().getInt(ARG_MODE);
    }

    SwipeRefreshLayout mRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_or_expandable_list_layout_swiperefresh, container, false);
        mRefresh= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshButtonClicked();

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getListAdapter()==null) {
            FeedAdapter adapter = new FeedAdapter(getActivity(), new ParseQueryAdapter.QueryFactory() {
                @Override
                public ParseQuery create() {
                    Log.d(TAG, mMode+"Create query");
                    ParseQuery query = new ParseQuery("Event");
                    return query;
                }
            });
            adapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener() {
                @Override
                public void onLoading() {
                    Log.d(TAG, mMode+"Feed loading");
                    requestingCount=1;
                    onRequestingChanged();
                }

                @Override
                public void onLoaded(List objects, Exception e) {
                    requestingCount=0;
                    Log.d(TAG, mMode+"Feed loaded");
                    onRequestingChanged();
                }
            });
            setListAdapter(adapter);
        }
    }

    private int requestingCount;
    @Override
    public boolean isRequesting() {
        return requestingCount>0;
    }

    @Override
    public boolean onRefreshButtonClicked() {
        if(getListAdapter()!=null){
            ((FeedAdapter)getListAdapter()).loadObjects();
        }
        return false;
    }

    @Override
    public boolean onBackKeyPressed() {
        return true;
    }

    @Override
    protected void onRequestingChanged() {
        super.onRequestingChanged();
        if(mRefresh!=null){
            boolean requesting = isRequesting();
            Log.d(TAG, mMode+"SwipeRefresh: "+requesting);
            mRefresh.setRefreshing(requesting);
        }
    }

    public void onFilterChanged() {

    }
    public int getMode(){
        return mMode;
    }

    class FeedAdapter extends ParseQueryAdapter{

        public FeedAdapter(Context context, QueryFactory queryFactory) {
            super(context, queryFactory);
        }
        private AQuery aqCell = new AQuery((View)null);
        @Override
        public View getItemView(ParseObject event, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_simple_2_line, parent, false);
            }
            aqCell.recycle(convertView);
            aqCell.id(R.id.text1).text(StringUtils.stringListToString(event.getList("tags"), ", ", false));
            aqCell.id(R.id.text2).text(event.getString("blurb"));
            return convertView;
        }
    }
}
