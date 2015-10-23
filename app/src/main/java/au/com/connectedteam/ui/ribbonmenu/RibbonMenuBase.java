
package au.com.connectedteam.ui.ribbonmenu;


import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;



/**

 * RibbonMenu Navigation menu for Android (based on Google+ app).
 * 
 * 
 */
public abstract class RibbonMenuBase extends LinearLayout {
	
		
	
	
	protected IRibbonMenuCallback callback;
	protected IRibbonMenuViewAdapter mViewAdapterCallback;
	
	protected ArrayList<RibbonMenuItem> mMenuItems;
	protected boolean mHideDisabled=false;
	
		
	
	public RibbonMenuBase(Context context) {
		super(context);	
		
	}
	
	public RibbonMenuBase(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	
	
	protected abstract void load();		
	
	@Override
	protected void onFinishInflate(){
		super.onFinishInflate();		
		if(!isInEditMode()) inflateLayout();
		
	}
	protected abstract void inflateLayout();

		/*if(layoutResId!=0){
			LayoutInflater.from(getContext()).inflate(layoutResId, this, true);	
			rbmListView = (ListView) findViewById(R.id.rbm_listview);
		}*/
				
		
	
	public void setMenuClickCallback(IRibbonMenuCallback callback){
		this.callback = callback;
	}
	public void setMenuViewAdapterCallback(IRibbonMenuViewAdapter viewAdapterCallback){
		mViewAdapterCallback=viewAdapterCallback;
	}
	
	public void setMenuItems(int menuRes){
		
		mMenuItems = new RibbonMenuItemParser(getContext()).parseXml(menuRes);
				
							
	}
	
	public void addMenuItem(RibbonMenuItem item){
		mMenuItems.add(item);
		notifyMenuItemChanges();
	}
	
	/**
	 * Call setMenuItemChanges() afterwards if you change the enabled status, or add or remove
	 * any items.
	 * @return
	 */
	public ArrayList<RibbonMenuItem> getMenuItems(){
		return mMenuItems;
	}
	
	/**
	 * Call notifyMenuItemChanges() afterwards if you change the enabled status of this item
	 * @param id
	 * @return
	 */
	public RibbonMenuItem getMenuItem(int id){
		return getMenuItemRecur(mMenuItems, id);
	}
	
	protected RibbonMenuItem getMenuItemRecur(List<RibbonMenuItem> items, int id){
		for (RibbonMenuItem item : items){
			if (item.getId()==id){
				return item;
			}
			if(item.hasSubItems()){
				RibbonMenuItem found = getMenuItemRecur(item.getSubItems(), id);
				if(found!=null) return found;
			}
		}
		return null;
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
	public abstract void notifyMenuItemChanges();
	
	public abstract void setBackgroundResource(int colorRes);
	
	
	
	
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
	
	


}
