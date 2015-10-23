package au.com.connectedteam.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

/**
 * Created by Bramley on 4/08/2015.
 */
public class CellViewHolder extends RecyclerView.ViewHolder{
    private AQuery aq;
    public CellViewHolder(View itemView) {
        super(itemView);
    }
    public CellViewHolder(ViewGroup parent, int layoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }
    public AQuery aq(){
        if(aq==null) aq=new AQuery(itemView);
        return aq;
    }
}
