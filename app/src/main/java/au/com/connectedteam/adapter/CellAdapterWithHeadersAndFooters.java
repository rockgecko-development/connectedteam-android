package au.com.connectedteam.adapter;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import au.com.connectedteam.R;
import au.com.connectedteam.application.ConnectedApp;

import java.security.InvalidParameterException;
import java.util.List;




public abstract class CellAdapterWithHeadersAndFooters<T> extends BaseAdapter{
	
	public static final int BLANK_VIEW_ID = 0x00ff0010;

	private int numHeaders;
	protected final int numItemViewTypes;
	private int numFooters;
	
	
	//protected View blankView;
	
	
	
	
	public CellAdapterWithHeadersAndFooters(int numHeaders, int numFooters) {
		this(numHeaders, 1, numFooters);
	}
	/**
	 * This constructor allows more than one itemViewType for dataBetweenHeadersAndFooters. If using this, you must override
	 * getItemViewTypeForDataBetweenHeadersAndFooters and return the view type for that item.
	 * @param dataBetweenHeadersAndFooters
	 * @param numHeaders
	 * @param numItemViewTypes the number of different view types contained in dataBetweenHeadersAndFooters
	 * @param numFooters
	 * @param c
	 */
	public CellAdapterWithHeadersAndFooters(int numHeaders, int numItemViewTypes, int numFooters) {
		if(numItemViewTypes<1) throw new InvalidParameterException("numItemViewTypes must be >=1 (default is 1)");
		this.numHeaders=numHeaders;
		this.numItemViewTypes=numItemViewTypes;
		this.numFooters=numFooters;		

		
		//View types start at 0, so our invisible type is equal to the number of other types
		//view_type_invisible=numHeaders+numItemViewTypes+numFooters;
		//view_type_invisible=numItemViewTypes;
		
		if(ConnectedApp.DEBUG && numItemViewTypes>1){
			Log.d("cellAdapter", "Constructed with h:"+numHeaders+" v:"+numItemViewTypes +" f:"+numFooters);
			Log.d("cellAdapter", " viewTypeCount: "+getViewTypeCount());
			
		}
		
		
	}
	
	/**Remember to call notifyDataSetChanged() immediately after this call.
	 *
	 * @param numHeaders
	 * @param numFooters
	 */
	protected void setNumHeadersAndFooters(int numHeaders, int numFooters){
		this.numHeaders=numHeaders;
		this.numFooters=numFooters;
	}
	
	protected int getNumHeaders(){
		return numHeaders;
	}
	protected int getNumFooters(){
		return numFooters;
	}
	
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Constructed with h:"+numHeaders+" v:"+numItemViewTypes +" f:"+numFooters);
		builder.append( " viewTypeCount: "+getViewTypeCount());
		
		return builder.toString();
	}
	

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public T getItem(int position) {
		if (isHeader(position) || isFooter(position) || shouldShowEmptyMsgView()){
			return null;
		}
		else{
			return getItems().get(position-numHeaders);
		}
	}
	
	public abstract List<T> getItems();
	
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

	public abstract View getCellView(int position, View convertView, ViewGroup parent);
	
	public abstract View getHeaderView(int headerIndex, ViewGroup parent);
	
	public abstract View getFooterView(int footerIndex, ViewGroup parent);
	
	protected boolean isHeader(int position){
		return position<numHeaders;
	}
	public boolean isFooter(int position){
		//return (getCount()-position)<=numFooters;
		return (numberOfItemsBetweenHeadersAndFooters()+numHeaders+numFooters-position)<=numFooters;
	}
	public int getFooterIndex(int position){
		//return numFooters-(getCount()-position);		
		return numFooters-(numberOfItemsBetweenHeadersAndFooters()+numHeaders+numFooters-position);
	}
	public int numberOfItemsBetweenHeadersAndFooters(){
		if(shouldShowEmptyMsgView()){
			return 1;
		}
		return getItems().size();
	}
	
	@Override
	public int getItemViewType(int position){
		if(shouldShowEmptyMsgView()&&position==numHeaders){
			 return AdapterView.ITEM_VIEW_TYPE_IGNORE;
		 }
		 if(isHeader(position) || isFooter(position)){
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
		   return getItems().size()==0;
	   }

	   /**
		 * Called to recycle the view for the specified position if possible.
		 * The default implementation
		 * checks convertView. If it's an {@code instanceof AbstractCellView} it recycles it
		 * and sets the model to the one at {@link getItem(position)}. Otherwise, it calls
		 * getCellView where you should create your Cell 
		 * @param position
		 * @param convertView
		 * @param parent
		 * @return
		 */
		public View getView(int position, View convertView, ViewGroup parent){
			
			if(shouldShowEmptyMsgView() && position==numHeaders){
				return createEmptyMessageView(parent);
			}
			
			if(isHeader(position)){								
				return getHeaderView(position, parent);
				
			}
			if(isFooter(position)){
				int footerIndex = getFooterIndex(position);
				return getFooterView(footerIndex, parent);
				
			}
			
			convertView = getCellView(position, convertView, parent);
			if(convertView==null){
				if(ConnectedApp.DEBUG)
					throw new RuntimeException("getCellView returned null view for item at "+position+": "+this.toString());
				//else, empty view to avoid crash (defensive)
				else return createBlankView(convertView, parent);
			}

			return convertView;


		}
		
		public View createBlankView(View convertView, ViewGroup parent){
			if(convertView!=null && convertView.getId()==BLANK_VIEW_ID){
				return convertView;
			}
			View blank=new View(parent.getContext());
			blank.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 0));
			//blankView.setVisibility(View.GONE);
			blank.setId(BLANK_VIEW_ID);
			return blank;
		}
		public View createEmptyMessageView(ViewGroup parent){
			return createMessageView(parent.getContext().getString(R.string.empty_result), parent);
		}
		public static View createMessageView(String msg, ViewGroup parent){
			View empty = View.inflate(parent.getContext(), R.layout.cell_empty, null);
			((TextView) empty.findViewById(R.id.text1)).setText(msg);
			return empty;
		}
		
	
	
		

	
}
