package au.com.connectedteam.activity;

import android.widget.ArrayAdapter;

/**
 * Created by bramleyt on 18/06/2015.
 */
public class DemoListFragment extends ListOrExpandableListFragment {

    private int id;
    public DemoListFragment(){};
    public DemoListFragment setID(int id){
        this.id=id;
        return this;
    }
    @Override
    public void onResume(){
        super.onResume();
        if(getListAdapter()==null){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseActivity(),android.R.layout.simple_list_item_1);
            for(int i=0;i<100;i++){
                adapter.add(id+" Match "+i);
            }
            setListAdapter(adapter);
        }
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
