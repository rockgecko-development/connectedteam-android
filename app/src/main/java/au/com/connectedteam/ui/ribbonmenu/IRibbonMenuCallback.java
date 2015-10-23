
package au.com.connectedteam.ui.ribbonmenu;

public interface IRibbonMenuCallback {

	/**
	 * 
	 * @param view
	 * @param menuItem
	 * @return true to dismiss the menu, false to keep it open
	 */
	public boolean onRibbonMenuItemClick(RibbonMenuBase view, RibbonMenuItem menuItem);
	public boolean isRibbonMenuItemSelected(RibbonMenuBase view, RibbonMenuItem menuItem);
	
	
}
