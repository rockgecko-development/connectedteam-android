package au.com.connectedteam.activity.feed;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.developer.tagview.entity.Tag;
import com.ns.developer.tagview.widget.TagCloudLinkView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseFragment;
import au.com.connectedteam.util.AQueryEx;
import au.com.connectedteam.util.IntentUtil;
import au.com.connectedteam.util.Reflect;
import au.com.connectedteam.util.StringUtils;

/**
 * Created by bramleyt on 25/10/2015.
 */
public class EventDetailsFragment extends BaseFragment {

    public static final String TAG = "EventDetailsFragment";
    public static final String ARG_JOIN_EVENT = "EventDetailsFragment.JoinEvent";
    private AQueryEx aq;

    private String eventID;
    private ParseObject mEvent;
    private boolean mHasJoined;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventID=getArguments().getString(IntentUtil.ARG_EVENT_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        aq = new AQueryEx(view);
        aq.id(R.id.btn_submit).clicked(mClickListener);
        return view;
    }
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_submit:
                    joinEvent();
                    break;
            }
        }
    };

    private void joinEvent() {
        showLoadingDialog(getString(R.string.submitting));
        ParseRelation<ParseObject> attendees = mEvent.getRelation("attendees");
        attendees.add(ParseUser.getCurrentUser());
        mEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dismissDialog();
                if(e!=null){
                    new AlertDialog.Builder(getActivity()).setMessage(e.getMessage()).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    }).setCancelable(false).show();
                }
                else{
                    Toast.makeText(getActivity(), "You're going!", Toast.LENGTH_SHORT).show();
                    aq.id(R.id.btn_submit).enabled(false);
                    mHasJoined=true;
                }
            }
        });
    }
private int requestingCount;
    @Override
    public void onResume() {
        super.onResume();
        if(mEvent==null){
            requestingCount=1;
            getBaseActivity().notifyRefreshing(this, isRequesting());
                ParseQuery.getQuery("Event").getInBackground(eventID, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        requestingCount=0;
                        getBaseActivity().notifyRefreshing(EventDetailsFragment.this, isRequesting());
                        if (e != null) {
                            new AlertDialog.Builder(getActivity()).setMessage(e.getMessage()).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getActivity().finish();
                                }
                            }).setCancelable(false).show();
                        }
                        else{
                            mEvent=object;
                            modelToUIIfReady();
                        }
                    }
                });


        }
        modelToUIIfReady();

    }
    private void modelToUIIfReady(){
        if(isResumed() && mEvent!=null){
            modelToUI();
            if(getArguments().getBoolean(ARG_JOIN_EVENT) && !mHasJoined){
                joinEvent();
            }
        }
    }

    private void modelToUI() {
        ParseObject event = mEvent;
        aq.id(R.id.textHospitalName).text(event.getString("hospital"));
        String location = event.getString("location");
        location+=" "+StringUtils.isNullOrEmpty(event.getString("room"), "");
        aq.id(R.id.textLocation).text(location);
        aq.id(R.id.textDate).text(StringUtils.formatDate(event.getDate("startTime"), StringUtils.DATE_AND_TIME_SHORT));
        int duration = event.getInt("duration");
        int clockRes = Reflect.getImageResId("ic_clock_" + duration);
        if (clockRes==0)clockRes=R.drawable.ic_clock_60;
        aq.id(R.id.imgDate).image(clockRes);
        String category = event.getString("category");
        category = StringUtils.isNullOrEmpty(category, "lecture").toLowerCase();
        int categoryRes = Reflect.getImageResId("ic_event_" + category);
        if(categoryRes==0) categoryRes=R.drawable.ic_event_lecture;
        aq.id(R.id.imgCategory).image(categoryRes);

        List<String> tags = event.getList("tags");
        if(tags==null) tags = new ArrayList<>();
        TagCloudLinkView hashTags = (TagCloudLinkView) aq.id(R.id.hashtag_tags).getView();
        while(hashTags.getTags().size()>0){
            hashTags.remove(0);
        }
        for(String tag : tags){
            hashTags.add(new Tag(1, tag));
        }
        hashTags.drawTags();

        ParseObject owner = event.getParseObject("owner");
        if(owner!=null)
            aq.id(R.id.textOwnerName).text(String.format("%s %s", owner.get("firstName"), owner.get("lastName")));
        else
            aq.id(R.id.textOwnerName).text("");
        aq.id(R.id.textBlurb).text(event.getString("blurb"));
        int headCount = event.getInt("headCount");
        int quota = event.getInt("quota");
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        if(headCount>=quota)
            StringUtils.appendSpan(stringBuilder, ""+headCount, new ForegroundColorSpan(getResources().getColor(R.color.body_text_1_negative)));
        else
            stringBuilder.append(""+headCount);
        stringBuilder.append(String.format("/%d places taken", quota));
        aq.id(R.id.textHeadCount).text(stringBuilder);

        ParseUser user = ParseUser.getCurrentUser();
        if(user!=null && owner!=null && user.getObjectId().equals(owner.getObjectId())){
            aq.id(R.id.btn_submit).text("Cancel").enabled(false);
        }
        else{
            aq.id(R.id.btn_submit).text("Reserve").enabled(!mHasJoined);

        }
    }

    @Override
    public boolean isRequesting() {
        return requestingCount>0;
    }

    @Override
    public boolean onRefreshButtonClicked() {
        return false;
    }

    @Override
    public boolean onBackKeyPressed() {
        return true;
    }
    private Dialog mDialog;
    private void showLoadingDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = LayoutInflater.from(builder.getContext()).inflate(R.layout.dialog_loading, null);
        ((TextView) dialogView.findViewById(R.id.text1)).setText(msg);
        builder.setView(dialogView).setCancelable(false);
        mDialog= builder.show();
    }
    private void dismissDialog(){
        if(mDialog!=null)
            mDialog.dismiss();
        mDialog=null;
    }
}
