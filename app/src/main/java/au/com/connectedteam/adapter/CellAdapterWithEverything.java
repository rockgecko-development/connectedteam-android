package au.com.connectedteam.adapter;

import java.security.InvalidParameterException;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;

public abstract class CellAdapterWithEverything extends BaseAdapter{
	private int numHeaders;
	protected final int numItemViewTypes;
	private int numIntermediateDataSets;
	private int numFooters;

	//private int view_type_invisible;
	//protected View blankView;
	//protected Context mContext;
	private static final int BLANK_VIEW_ID = 0x00ff0010;


	public CellAdapterWithEverything(int numHeaders, int numFooters) {
		this(numHeaders, 1, 1, numFooters);
	}
	/**
	 * This constructor allows more than one itemViewType for dataBetweenHeadersAndFooters. If using this, you must override
	 * getItemViewTypeForDataBetweenHeadersAndFooters and return the view type for that item.
	 * @param numHeaders
	 * @param numItemViewTypes the number of different view types contained in dataBetweenHeadersAndFooters
	 * @param numFooters
	 */
	public CellAdapterWithEverything(int numHeaders, int numIntermediateDataSets, int numItemViewTypes, int numFooters) {
		if(numItemViewTypes<1) throw new InvalidParameterException("numItemViewTypes must be >=1 (default is 1)");
		this.numHeaders=numHeaders;
		this.numIntermediateDataSets=numIntermediateDataSets;
		this.numItemViewTypes=numItemViewTypes;
		this.numFooters=numFooters;
		//this.mContext=c;

		//View types start at 0, so our invisible type is equal to the number of other types
		//view_type_invisible=numHeaders+numItemViewTypes+numFooters;
		//view_type_invisible=numItemViewTypes;

		if(ConnectedApp.DEBUG && numItemViewTypes>1){
			Log.d("cellAdapter", "Constructed with h:"+numHeaders+" v:"+numItemViewTypes +" f:"+numFooters);
			//	Log.d("cellAdapter", "view_type_invisible:"+view_type_invisible+" viewTypeCount: "+getViewTypeCount());

		}


	}

	protected void setNumHeadersAndFooters(int numHeaders, int numFooters, int numIntermediateDataSets){
		this.numHeaders=numHeaders;
		this.numFooters=numFooters;
		this.numIntermediateDataSets=numIntermediateDataSets;
	}

	protected int getNumHeaders(){
		return numHeaders;
	}
	protected int getNumFooters(){
		return numFooters;
	}
	protected int getNumIntermediateDataSets(){
		return numIntermediateDataSets;
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Constructed with h:"+numHeaders+" v:"+numItemViewTypes +" f:"+numFooters);
		builder.append( "viewTypeCount: "+getViewTypeCount());
		/*
		for(int i=0; i<getCount();i++){
			String type;
			if(isHeader(i)){
				type="header";
			}
			else if (isFooter(i)){
				type="footer";
			}
			else if(shouldShowEmptyMsgView() && i==numHeaders){
				type="empty message view";
			}
			else{
				type="elem: "+getItem(i).toString();
			}
			builder.append( i+" "+type+" viewType: "+ getItemViewType(i));
			
		}*/
		return builder.toString();
	}


	@Override
	public long getItemId(int position) {
		return position;
	}



	public Object getItem(int position){
		if (isHeader(position) || isFooter(position) || shouldShowEmptyMsgView()){
			return null;
		}
		int positionIndex = numHeaders;
		for(int i=0;i<numIntermediateDataSets;i++){
			//List<?> data = getIntermediateDataSet(i);
			//if(data!=null){

			if((positionIndex+getIntermediateDataSetSize(i))>position){
				return getIntermediateDataSetItem(i, position-positionIndex);
			}
			positionIndex+=getIntermediateDataSetSize(i);
			//	}
		}
		return null;
	}

	public int getIntermediateDataSetIndex(int position){
		if (isHeader(position) || isFooter(position)){
			return -1;
		}
		int positionIndex = numHeaders;
		for(int i=0;i<numIntermediateDataSets;i++){
			//List<?> data = getIntermediateDataSet(i);
			//if(data!=null){

			if(positionIndex+getIntermediateDataSetSize(i)>position) return i;
			positionIndex+=getIntermediateDataSetSize(i);
			//}
		}
		return -1;
	}

	//public abstract List<?> getIntermediateDataSet(int dataSetIndex);
	public abstract int getIntermediateDataSetSize(int dataSetIndex);
	public abstract Object getIntermediateDataSetItem(int dataSetIndex, int indexInDataSet);

	@Override
	public boolean areAllItemsEnabled() {
		return numHeaders==0 && numFooters==0 && !shouldShowEmptyMsgView();
	}
	@Override
	public boolean isEnabled(int position) {
		//return position>=numHeaders && (getCount()-position)>numFooters;
		if(shouldShowEmptyMsgView()) return false;
		return !isHeader(position) && !isFooter(position);
	}

	public abstract View getHeaderView(int headerIndex, ViewGroup root);
	//public abstract View getHeaderView(int headerIndex);
	//public abstract boolean isHeaderVisible(int headerIndex);
	public abstract View getFooterView(int footerIndex, ViewGroup root);
	//public abstract boolean isFooterVisible(int footerIndex);

	protected boolean isHeader(int position){
		return position<numHeaders;
	}
	protected boolean isFooter(int position){
		//return (getCount()-position)<=numFooters;
		return (numberOfItemsBetweenHeadersAndFooters()+numHeaders+numFooters-position)<=numFooters;
	}
	protected int getFooterIndex(int position){
		//return numFooters-(getCount()-position);		
		return numFooters-(numberOfItemsBetweenHeadersAndFooters()+numHeaders+numFooters-position);
	}
	public int numberOfItemsBetweenHeadersAndFooters(){
		if(shouldShowEmptyMsgView()){
			return 1;
		}
		int count=0;
		for(int i=0;i<numIntermediateDataSets;i++){
			count+=getIntermediateDataSetSize(i);

		}
		return count;
	}



	@Override
	public int getItemViewType(int position){
		if(shouldShowEmptyMsgView()&&position==numHeaders){
			return 0;
		}
		if(isHeader(position)){
			return AdapterView.ITEM_VIEW_TYPE_IGNORE;
		}
		if(isFooter(position)){
			return AdapterView.ITEM_VIEW_TYPE_IGNORE;
		}

		return getItemViewTypeForDataBetweenHeadersAndFooters(position);
	}

	/**
	 * Override if you have more than one view type in your data.
	 * @param position
	 * @return a number between 0 and numItemViewTypes-1
	 */
	public int getItemViewTypeForDataBetweenHeadersAndFooters(int position){
		if (numItemViewTypes!=1) throw new RuntimeException("You must override " +
				"getItemViewTypeForDataBetweenHeadersAndFooters if you have more than one view type in your data");
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		//number of headers ++number of item view types + number of footers +  type for invisible view
		//type for invisible view is numHeaders+numItemViewTypes+numFooters+1
		//return numHeaders+numFooters+2;

		//view types start at 0, so add 1 for a count
		return numItemViewTypes+1;
	}

	@Override
	public int getCount() {
		return numHeaders+numberOfItemsBetweenHeadersAndFooters()+numFooters;

	}

	public boolean shouldShowEmptyMsgView(){
		int count=0;
		for(int i=0;i<numIntermediateDataSets;i++){
			count+=getIntermediateDataSetSize(i);
		}
		return count==0;
	}

	public View getHeaderOrFooterView(int position, View convertView, ViewGroup parent){
		if(shouldShowEmptyMsgView() && position==numHeaders){
			return createEmptyMessageView(ConnectedApp.getContextStatic().getString(R.string.empty_result), parent);
		}

		if(isHeader(position)){
			return getHeaderView(position, parent);
		}
		if(isFooter(position)){
			int footerIndex = getFooterIndex(position);
			return getFooterView(footerIndex, parent);
		}
		return null;
	}



	@SuppressWarnings("ResourceType")
	public View createBlankView(View convertView, ViewGroup parent){
		if(convertView!=null && convertView.getId()==BLANK_VIEW_ID){
			return convertView;
		}
		View blank=new View(parent.getContext());
		blank.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, 0));
		//blankView.setVisibility(View.GONE);
		blank.setId(BLANK_VIEW_ID);
		return blank;
	}
	public View createEmptyMessageView(String msg, ViewGroup parent){
		View empty = View.inflate(parent.getContext(), R.layout.cell_empty, null);
		((TextView) empty.findViewById(R.id.text1)).setText(msg);
		return empty;
	}





}
