package au.com.connectedteam.activity.home;

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
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.ns.developer.tagview.entity.Tag;
import com.ns.developer.tagview.widget.TagCloudLinkView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.servicestack.func.Func;
import net.servicestack.func.Function;
import net.servicestack.func.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseFragment;
import au.com.connectedteam.models.ConnectedConstants;
import au.com.connectedteam.util.AQueryEx;
import au.com.connectedteam.util.FuncEx;

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
        public int quota;
        CreateEventViewModel(){
            tags=new ArrayList<>();
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
        aq.id(R.id.btn_edit_hospitals).clicked(mClickListener);
        aq.id(R.id.btn_submit).clicked(mClickListener);
        return view;
    }
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_edit_hospitals:
                    showHospitalListDialog();
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

    private void showHospitalListDialog(){
        List<String> hospitals = ParseUser.getCurrentUser().getList("hospitals");
        if(hospitals==null) hospitals = new ArrayList<>();
        final List<String> selectedHospitals = hospitals;
        String email = ParseUser.getCurrentUser().getEmail();
        String domain = email.substring(email.indexOf('@')+1);
        List<String> allHospitals = getHospitalsForDomain(domain);
        final String[] allHospitalsArr = Func.toArray(allHospitals,String.class);
        final boolean[] checked = new boolean[allHospitalsArr.length];
        for(int i=0;i<checked.length;i++){
            checked[i]=selectedHospitals.contains(allHospitalsArr[i]);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMultiChoiceItems(allHospitalsArr, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if(b){
                    selectedHospitals.add(allHospitalsArr[i]);
                }
                else{
                    selectedHospitals.remove(allHospitalsArr[i]);
                }
            }
        });
        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ParseUser user = ParseUser.getCurrentUser();
                user.put("hospitals", selectedHospitals);
                user.saveInBackground();
                modelToUIIfReady();
            }
        });
        builder.show();
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
        getActivity().finish();
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
}
