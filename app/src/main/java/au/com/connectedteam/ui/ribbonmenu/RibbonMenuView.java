
package au.com.connectedteam.ui.ribbonmenu;


import java.util.ArrayList;
import java.util.List;
import com.androidquery.AQuery;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import au.com.connectedteam.R;



/**

 * RibbonMenu Navigation menu for Android
 * 
 * 
 */
public class RibbonMenuView extends RibbonMenuBase {

	protected ListView rbmListView;
				
	
	private RbmAdapter mAdapter;
	
	protected AQuery aqCell;
	
	public RibbonMenuView(Context context) {
		super(context);	
		load();
		onFinishInflate();
		
	}
	
	public RibbonMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		load();
	}


	
	@Override
	protected void load(){
		if(isInEditMode()) return;
		setOrientation(VERTICAL);		
		
		aqCell=new AQuery(this);
		mViewAdapterCallback = new IRibbonMenuViewAdapter() {
			
			@Override
			public View createRibbonMenuCell(BaseAdapter adapter,
					int position, ViewGroup parent) {				
				return View.inflate(getContext(), R.layout.cell_ribbonmenu_item, null);
			}

			@Override
			public View refreshRibbonMenuCell(BaseAdapter adapter,
					int position, RibbonMenuItem menuItem, boolean selected,
					View convertView) {
				aqCell.recycle(convertView);
				aqCell.id(R.id.menu_left_icon).image(menuItem.getIconDrawable());
				CharSequence text=menuItem.getText();
				if(menuItem.hasSubItems()){
                    SpannableStringBuilder builder = new SpannableStringBuilder(text);
					builder.append(": "+menuItem.getSubItems().toString());
                    text=builder;
				}
				
				TextView text1 = aqCell.id(R.id.text1).text(text).enabled(menuItem.isEnabled()).getTextView();
				/*
				if(menuItem.getCounterBadge()>0){
					//checkBox.setImageDrawable(UIUtil.makeDrawableCounterOverlayMenu(model.getCounterBadge()));
					text1.setCompoundDrawablesWithIntrinsicBounds(null, null, UIUtil.makeDrawableCounterOverlayMenu(menuItem.getCounterBadge()), null);
				}
				else if (menuItem.isCheckable()){
					text1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.switch_ribbonmenu,0);
					text1.setSelected(selected);
					
				}
				else{
					text1.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
				}*/
				convertView.setBackgroundResource((selected && !menuItem.isCheckable())?R.drawable.list_selector_ribbonmenu_selected:0);
				
				return convertView;
				

			}
			
			private void setSelectedBackgroundResource(boolean selected){
				setBackgroundResource(selected?R.drawable.list_selector_ribbonmenu_selected:0);
			}
			

			
		};
		
		
		
		
	}
	
	
	protected void inflateLayout(){

		/*if(layoutResId!=0){
			LayoutInflater.from(getContext()).inflate(layoutResId, this, true);	
			rbmListView = (ListView) findViewById(R.id.rbm_listview);
		}*/
		
		rbmListView = new ListView(getContext());
		addView(rbmListView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		initListView();
		

	}
	
	protected void initListView(){
		rbmListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if(callback != null){		
					callback.onRibbonMenuItemClick(RibbonMenuView.this, (RibbonMenuItem) rbmListView.getAdapter().getItem(position));

				}								

			}

		});
		if(mAdapter!=null) rbmListView.setAdapter(mAdapter);
	}

	
	
	
	public void setMenuItems(int menuRes){
		
		super.setMenuItems(menuRes);
		
		if(mMenuItems != null && mMenuItems.size() > 0)
		{
			mAdapter = new RbmAdapter();					
		}
		if(rbmListView!=null){
			rbmListView.setAdapter(mAdapter);
		}
							
	}
	
	
	/**
	 * Call this if you have changed the enabled status of a menu item,
	 * or added or removed a menu item, via getMenuItem or getMenuItems
	 */
	@Override
	public void notifyMenuItemChanges(){
		if(mAdapter!=null)
			mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void setBackgroundResource(int colorRes){
		rbmListView.setBackgroundResource(colorRes);
		
	}
	
	
	public ListView getListView(){
		return rbmListView;
	}
	
	
	/*public abstract void showMenu();
	public abstract void hideMenu();	
	public abstract boolean isMenuVisible();
	
	public void toggleMenu(){
		if(isMenuVisible()){
			hideMenu();
		} else {
			showMenu();
		}
	}
	
	
	*/
	
	protected static final int BLANK_VIEW_ID=0x00ff0020;
	private class RbmAdapter extends BaseAdapter {
							
		
		private ArrayList<RibbonMenuItem> adapterItems;
		
		public RbmAdapter(){		
			adapterItems = new ArrayList<RibbonMenuItem>(mMenuItems.size());
			loadAdapterItems();
		}
		
		private void loadAdapterItems(){
			adapterItems.clear();
			for(RibbonMenuItem item : mMenuItems){
				if(item.isVisible() || (mHideDisabled && item.isEnabled())) adapterItems.add(item);
			}
			//else adapterItems.addAll(mMenuItems);
		}
		
						
		@Override
		public void notifyDataSetChanged(){
			loadAdapterItems();			
			super.notifyDataSetChanged();
		}
						
		
		@Override
		public boolean isEnabled(int position){
			//return true;
			return getItem(position).isEnabled();
		}

				

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return adapterItems.size();
		}

		@Override
		public RibbonMenuItem getItem(int position) {
			// TODO Auto-generated method stub
			return adapterItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return getItem(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = mViewAdapterCallback.createRibbonMenuCell(this, position, parent);
			}
			RibbonMenuItem menuItem = getItem(position);			
			boolean isSelected = callback.isRibbonMenuItemSelected(RibbonMenuView.this, menuItem);
			return mViewAdapterCallback.refreshRibbonMenuCell(this, position, menuItem, isSelected, convertView);
		}

		
		
	}
	


}
