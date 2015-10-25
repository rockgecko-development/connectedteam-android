package au.com.connectedteam.activity.home;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.ns.developer.tagview.entity.Tag;
import com.ns.developer.tagview.widget.TagCloudLinkView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import net.servicestack.func.Func;
import net.servicestack.func.Function;
import net.servicestack.func.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseFragment;
import au.com.connectedteam.models.ConnectedConstants;
import au.com.connectedteam.util.AQueryEx;
import au.com.connectedteam.util.FuncEx;
import au.com.connectedteam.util.ListUtils;
import au.com.connectedteam.util.StringUtils;

/**
 * Created by bramleyt on 24/10/2015.
 */
public class CreateEventFragment extends BaseFragment{
    public static final String TAG = "CreateEventFragment";

    private AQueryEx aq;
    private List<ParseObject> mHospitalAvails;
    private List<ParseObject> mTagAvails;
    private ParseObject mSettings;
    private CreateEventViewModel mModel;
    private class CreateEventViewModel implements Serializable{
        public String blurb, category, hospital, location, room;
        public List<String> tags;
        public int year, month, day, hour, minute;
        public boolean dateSet, timeSet;
        public int quota, duration;
        CreateEventViewModel(){
            tags=new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            year=cal.get(Calendar.YEAR);
            month=cal.get(Calendar.MONTH);
            day=cal.get(Calendar.DAY_OF_MONTH);
            hour=cal.get(Calendar.HOUR);
            minute=cal.get(Calendar.MINUTE);
        }
        public ParseObject toParseObject(){
            ParseObject e = new ParseObject("Event");
            e.put("owner", ParseUser.getCurrentUser());
            e.put("startTime", toDate());
            e.put("duration", duration);
            e.put("blurb", blurb);
            e.put("category", category);
            e.put("hospital", hospital);
            e.put("location", location);
            e.put("room", room);
            e.put("tags", tags);
            e.put("quota", quota);
            e.put("tags", tags);
            return e;
        }
        public Date toDate(){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR, hour);
            cal.set(Calendar.MINUTE, minute);
            return cal.getTime();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            mModel= (CreateEventViewModel) savedInstanceState.getSerializable("mModel");
        }
        if(mModel==null) mModel=new CreateEventViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        aq=new AQueryEx(view);
        aq.id(R.id.btnDate).clicked(mClickListener);
        aq.id(R.id.btnTime).clicked(mClickListener);
        aq.id(R.id.btn_submit).clicked(mClickListener);
        return view;
    }
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnDate:
                    showDateDialog();
                    break;
                case R.id.btnTime:
                    showTimeDialog();
                    break;
                case R.id.btn_submit:
                    onSubmitClicked();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        fetchInitialData();
        modelToUIIfReady();
    }
    protected void onRequestingChanged(){
        if(getBaseActivity()!=null) getBaseActivity().notifyRefreshing(CreateEventFragment.this, isRequesting());
    }
    private void fetchInitialData(){
        if(mHospitalAvails==null){
            requestingCount++;
            onRequestingChanged();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("HospitalAvail");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    mHospitalAvails=objects;
                    requestingCount--;
                    onRequestingChanged();
                    modelToUIIfReady();
                }
            });
        }
        if(mTagAvails==null){
            requestingCount++;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("TagAvail");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    mTagAvails = objects;
                    requestingCount--;
                    onRequestingChanged();
                    modelToUIIfReady();
                }
            });
        }

    }



    private void modelToUIIfReady(){
        if(isResumed() && mHospitalAvails!=null && mTagAvails!=null) modelToUI();
    }
    private void modelToUI(){
        if(mModel.dateSet)
            aq.id(R.id.btnDate).text(StringUtils.formatDateStandard(mModel.toDate()));
        else aq.id(R.id.btnDate).text("");
        if(mModel.timeSet)
            aq.id(R.id.btnTime).text(String.format("%d:%02d", mModel.hour, mModel.minute));
        else aq.id(R.id.btnTime).text("");
        List<String> allDurations = ListUtils.asArrayList(ConnectedConstants.EVENT_DURATIONS);
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, allDurations);
        durationAdapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);

        aq.id(R.id.spinner_duration).adapter(durationAdapter).setSelection(allDurations.indexOf("" + mModel.duration))
                .itemSelected(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mModel.duration = Integer.parseInt(parent.getItemAtPosition(position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        List<String> allCategories = ListUtils.asArrayList(ConnectedConstants.EVENT_CATEGORIES);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, allCategories);
        categoryAdapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);

        aq.id(R.id.spinner_category).adapter(categoryAdapter).setSelection(allCategories.indexOf(mModel.category))
                .itemSelected(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mModel.category = (String) parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        List<String> hospitals = ParseUser.getCurrentUser().getList("hospitals");
        if(hospitals==null) hospitals = new ArrayList<>();
        ArrayAdapter<String> hospitalsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, hospitals);
        hospitalsAdapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);

        aq.id(R.id.spinner_hospital).adapter(hospitalsAdapter).setSelection(hospitals.indexOf(mModel.hospital))
                .itemSelected(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mModel.hospital = (String) parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        List<String> tags = mModel.tags;
        TagCloudLinkView hashTags = (TagCloudLinkView) aq.id(R.id.hashtag_tags).getView();
        while(hashTags.getTags().size()>0){
            hashTags.remove(0);
        }
        for(String tag : tags){
            hashTags.add(new Tag(1, tag));
        }
        hashTags.drawTags();
        hashTags.setOnTagDeleteListener(new TagCloudLinkView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(Tag tag, int position) {
                mModel.tags.remove(tag.getText());
                modelToUIIfReady();
            }
        });

        AutoCompleteTextView tagAutocomplete = (AutoCompleteTextView) aq.id(R.id.autocomplete_tags).getView();
        final List<String> allTags = Func.map(mTagAvails, new Function<ParseObject, String>() {
            @Override
            public String apply(ParseObject parseObject) {
                return parseObject.getString("title");
            }
        });
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, allTags);
        tagAutocomplete.setAdapter(tagAdapter);
        tagAutocomplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String tag = textView.getText().toString().trim();
                    if (allTags.contains(tag)) {
                        if (!mModel.tags.contains(tag))
                            mModel.tags.add(tag);
                        textView.setText("");
                        modelToUIIfReady();
                    }
                    return true;
                }
                return false;
            }
        });
        tagAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String tag = ((String) adapterView.getItemAtPosition(i));
                if (!mModel.tags.contains(tag))
                    mModel.tags.add(tag);
                aq.id(R.id.autocomplete_tags).text("");
                modelToUIIfReady();
            }
        });

        aq.id(R.id.edit_location_ward).bindString(mModel, "location");
        aq.id(R.id.edit_location_room).bindString(mModel, "room");
        aq.id(R.id.edit_blurb).bindString(mModel, "blurb");
        aq.id(R.id.edit_heads).text(""+mModel.quota).getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    try{
                        mModel.quota = Integer.parseInt(((TextView)v).getText().toString().trim());
                        modelToUIIfReady();
                    }catch (NumberFormatException e){
                        mModel.quota=0;
                    }
                }
            }
        });

    }

    private void showDateDialog(){
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker,  int year, int monthOfYear, int dayOfMonth) {
                mModel.year=year;
                mModel.month=monthOfYear;
                mModel.day=dayOfMonth;
                mModel.dateSet=true;
                modelToUIIfReady();
            }
        }, mModel.year, mModel.month, mModel.day);
        dialog.show();
    }
    private void showTimeDialog(){
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mModel.hour=hour;
                mModel.minute=minute;
                mModel.timeSet=true;
                modelToUIIfReady();
            }
        }, mModel.hour, mModel.minute, true);
        dialog.show();
    }
    private List<String> getHospitalsForDomain(final String domain){
        return Func.map(Func.filter(mHospitalAvails, new Predicate<ParseObject>() {
            @Override
            public boolean apply(ParseObject parseObject) {
                //return true;
                return parseObject.getList("allowedEmails").contains(domain);
            }
        }), new Function<ParseObject, String>() {
            @Override
            public String apply(ParseObject parseObject) {
                return parseObject.getString("name");
            }
        });
    }

    private void onSubmitClicked(){
        View focussed = getView().findFocus();
        if(focussed!=null) focussed.clearFocus();
        List<String> errors = new ArrayList<>();
        if(!mModel.dateSet) errors.add("Please set a date");
        if(!mModel.timeSet) errors.add("Please set a time");
        if(StringUtils.isNullOrEmpty(mModel.hospital)) errors.add("Please set a hospital");
        if(StringUtils.isNullOrEmpty(mModel.tags)) errors.add("Please set some tags");
        if(StringUtils.isNullOrEmpty(mModel.location)) errors.add("Please set a room/location");
        if(mModel.quota<=0) errors.add("Please enter number of places available");
        if(errors.size()>0){
            aq.id(R.id.validation).text(StringUtils.stringListToString(errors, "\n", false));
        }
        else{
            aq.id(R.id.validation).text("");
            showLoadingDialog(getString(R.string.submitting));
            ParseObject object = mModel.toParseObject();
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    dismissDialog();
                    if(e!=null){
                        aq.id(R.id.validation).text("Error: "+e.getMessage());
                    }
                    else{
                        Toast.makeText(getActivity(), "Event created", Toast.LENGTH_LONG).show();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                }
            });

        }

    }
    private int requestingCount;
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
