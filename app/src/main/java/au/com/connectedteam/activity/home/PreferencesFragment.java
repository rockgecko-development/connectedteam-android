package au.com.connectedteam.activity.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.BaseFragment;
import au.com.connectedteam.adapter.HashtagAdapters;
import au.com.connectedteam.util.FuncEx;
import au.com.connectedteam.util.StringUtils;

/**
 * Created by bramleyt on 24/10/2015.
 */
public class PreferencesFragment extends BaseFragment{
    public static final java.lang.String TAG = "PreferencesFragment";

    private AQuery aq;
    private List<ParseObject> mHospitalAvails;
    private ParseObject mSettings;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        aq=new AQuery(view);
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
    private void fetchInitialData(){
        if(mHospitalAvails==null){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("HospitalAvail");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    mHospitalAvails=objects;
                    modelToUIIfReady();
                }
            });
        }

    }

    private void modelToUIIfReady(){
        if(isResumed() && mHospitalAvails!=null) modelToUI();
    }
    private void modelToUI(){
        List<String> hospitals = ParseUser.getCurrentUser().getList("hospitals");
        if(hospitals==null) hospitals = new ArrayList<>();
        aq.id(R.id.tv_hospitals).text(StringUtils.stringListToString(hospitals, ", ", false));

        TagCloudLinkView hospitalHashtags = (TagCloudLinkView) aq.id(R.id.hashtag_hospitals).getView();

        while(hospitalHashtags.getTags().size()>0){
            hospitalHashtags.remove(0);
        }
        for(String tag : hospitals){
            hospitalHashtags.add(new Tag(1, tag));
        }
        hospitalHashtags.drawTags();
           // hospitalHashtags.setData(hospitals, HashtagAdapters.HASH);
         //   hospitalHashtags.setVisibility(View.VISIBLE);

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
        return true;
    }
}
