package au.com.connectedteam.activity.feed;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.ns.developer.tagview.entity.Tag;
import com.ns.developer.tagview.widget.TagCloudLinkView;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.ListOrExpandableListFragment;
import au.com.connectedteam.util.ListUtils;
import au.com.connectedteam.util.Reflect;
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

                    Log.d(TAG, mMode + "Create query");

                    if(mMode==MODE_HISTORY){
                        ParseUser parseUser = ParseUser.getCurrentUser();
                        ParseQuery<ParseObject> ownerQuery = new ParseQuery("Event");
                        ownerQuery.whereEqualTo("owner", parseUser);
                        ParseQuery<ParseObject> attendingQuery = new ParseQuery("Event");
                        attendingQuery.whereContainedIn("attendees", ListUtils.asArrayList(parseUser));
                        ParseQuery query = ParseQuery.or(ListUtils.asArrayList(ownerQuery, attendingQuery));
                        query.orderByDescending("startTime");
                        query.include("owner");
                        return query;

                    }
                    else if (mMode==MODE_FEED) {
                        ParseQuery query = new ParseQuery("Event");
                        query.orderByDescending("startTime");
                        query.include("owner");//.include("owner.lastName");
                        ParseUser parseUser = ParseUser.getCurrentUser();
                        if (parseUser != null) {
                            List<Object> hospitals = parseUser.getList("hospitals");
                            if(hospitals==null) hospitals=new ArrayList<>();
                            query.whereContainedIn("hospital", hospitals);
                        }
                        FeedFilter filter = getFilter();
                        if (filter != null) {
                            if (filter.tags.size() > 0) {
                                query.whereContainedIn("tags", filter.tags);
                            }
                        }
                        return query;
                    }
                    else return null;
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
                    Log.d(TAG, mMode + "Feed loaded");
                    if(e!=null) Log.e(TAG, "load error "+e.getMessage(),e);
                    onRequestingChanged();
                }
            });
            setListAdapter(adapter);
        }
    }

    private FeedFilter getFilter(){
        if(getActivity()==null) return null;
        return ((FeedActivity)getActivity()).getFilter();
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
        onRefreshButtonClicked();
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
                convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_event, parent, false);
            }
            aqCell.recycle(convertView);
            aqCell.id(R.id.text1).text(event.getString("hospital"));
            aqCell.id(R.id.textDate).text(StringUtils.formatDate(event.getDate("startDate"), StringUtils.DATE_AND_TIME_SHORT));
            int duration = event.getInt("duration");
            int clockRes = Reflect.getImageResId("ic_clock_" + duration);
            if (clockRes==0)clockRes=R.drawable.ic_clock_60;
            aqCell.id(R.id.imgDate).image(clockRes);
            String category = event.getString("category");
            category = StringUtils.isNullOrEmpty(category, "lecture").toLowerCase();
            int categoryRes = Reflect.getImageResId("ic_event_" + category);
            if(categoryRes==0) categoryRes=R.drawable.ic_event_lecture;
            aqCell.id(R.id.imgCategory).image(categoryRes);

            List<String> tags = event.getList("tags");
            if(tags==null) tags = new ArrayList<>();
            TagCloudLinkView hashTags = (TagCloudLinkView) aqCell.id(R.id.hashtag_tags).getView();
            while(hashTags.getTags().size()>0){
                hashTags.remove(0);
            }
            for(String tag : tags){
                hashTags.add(new Tag(1, tag));
            }
            hashTags.drawTags();
            ParseObject owner = event.getParseObject("owner");
            if(owner!=null)
                aqCell.id(R.id.textOwnerName).text(String.format("%s %s", owner.get("firstName"), owner.get("lastName")));
            else
                aqCell.id(R.id.textOwnerName).text("");
            int headCount = event.getInt("headCount");
            int quota = event.getInt("quota");
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            if(headCount>=quota)
                StringUtils.appendSpan(stringBuilder, ""+headCount, new ForegroundColorSpan(getResources().getColor(R.color.body_text_1_negative)));
            else
            stringBuilder.append(""+headCount);
            stringBuilder.append(String.format("/%d places taken", quota));
            aqCell.id(R.id.textHeadCount).text(stringBuilder);

            return convertView;
        }
    }
}
