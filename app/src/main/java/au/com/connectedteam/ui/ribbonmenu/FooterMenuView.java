
package au.com.connectedteam.ui.ribbonmenu;


import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import au.com.connectedteam.R;



/**

 * Footer menu view
 * 
 * 
 */
public class FooterMenuView extends RibbonMenuBase {

	
	
	
	public FooterMenuView(Context context) {
		super(context);	
		load();
		onFinishInflate();
		
	}
	
	public FooterMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		load();
	}


	
	
	protected void load(){
		if(isInEditMode()) return;
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
		mViewAdapterCallback = new IRibbonMenuViewAdapter() {
			
			@Override
			public View createRibbonMenuCell(BaseAdapter adapter,
					int position, ViewGroup parent) {
                return LayoutInflater.from(getContext()).inflate(R.layout.footer_menu_item_view, parent, false);
			}

			@Override
			public View refreshRibbonMenuCell(BaseAdapter adapter,
					int position, RibbonMenuItem menuItem, boolean selected,
					View convertView) {
				((FooterMenuItemView)convertView).initialize(menuItem);
				//setSelectedBackgroundResource(selected && !menuItem.isCheckable());
				
				return convertView;
				

			}
			
			
		};
		
	}
	
	@Override
	protected void onFinishInflate(){
		super.onFinishInflate();		
		if(!isInEditMode()) inflateLayout();
		
	}
	protected void inflateLayout(){
		
	}
			
	
	public void setMenuItems(int menuRes){
		
		super.setMenuItems(menuRes);
		
		if(mMenuItems != null && mMenuItems.size() > 0)
		{
			populateItems();
		}
	}
	
	
	/**
	 * Change the visible state of a menu item
	 * @param id
	 * @param visible
	 * @return true if the state was changed, false if it wasn't found
	 */
	public boolean setMenuItemVisible(int id, boolean visible){
		RibbonMenuItem item = getMenuItem(id);
		if (item!=null){
			item.setVisible(visible);
			notifyMenuItemChanges();
			return true;
		}
		return false;
	}
	
	public void setHideDisabledItems(boolean hideDisabledItems){
		
		mHideDisabled=hideDisabledItems;
		notifyMenuItemChanges();
	}
	
	/**
	 * Call this if you have changed the enabled status of a menu item,
	 * or added or removed a menu item, via getMenuItem or getMenuItems
	 */
	public void notifyMenuItemChanges(){
		populateItems();
	}
	
	public void setBackgroundResource(int colorRes){
		//rbmListView.setBackgroundResource(colorRes);
		
	}
	
	
	private void populateItems(){
		removeAllViews();
		if(mMenuItems!=null){
			for(int i=0;i<mMenuItems.size(); i++){
				RibbonMenuItem itemData = mMenuItems.get(i);
				//FooterMenuItemView menuView = (FooterMenuItemView) LayoutInflater.from(getContext()).inflate(R.layout.footer_menu_item_view, this, false);
				//menuView.initialize(itemData);
				View menuView = mViewAdapterCallback.createRibbonMenuCell(null, i, FooterMenuView.this);				
				menuView = mViewAdapterCallback.refreshRibbonMenuCell(null, i, itemData, false, menuView);
				menuView.setOnClickListener(mItemClickListener);
				//menuView.setText(itemData.getText());
				//if(itemData.getIcon()>0)menuView.setCompoundDrawablesWithIntrinsicBounds(itemData.getIcon(), 0,0,0);*/
				LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
				addView(menuView, lp);
			}
		}
	}
	
	private View.OnClickListener mItemClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final RibbonMenuItem menuItem = getMenuItem(v.getId());
			boolean handled=false;
			if(callback!=null) handled = callback.onRibbonMenuItemClick(FooterMenuView.this, menuItem);			
			if(!handled && menuItem.hasSubItems()){
				PopupMenu popup = new PopupMenu(getContext(), v);
				Menu popupMenu = popup.getMenu();
				for(RibbonMenuItem subItem : menuItem.getSubItems()){
					popupMenu.add(0, subItem.getId(), 0, subItem.getText());
				}
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem subItem) {
						if(callback!=null) return callback.onRibbonMenuItemClick(FooterMenuView.this, getMenuItemRecur(menuItem.getSubItems(), subItem.getItemId()));
						return false;
					}
				});
				popup.show();
			}
			
		}
	};
	
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
	
}
