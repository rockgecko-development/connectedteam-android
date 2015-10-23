package au.com.connectedteam.ui.ribbonmenu;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public interface IRibbonMenuViewAdapter {


	public View createRibbonMenuCell(BaseAdapter adapter, int position, ViewGroup parent);
	public View refreshRibbonMenuCell(BaseAdapter adapter, int position, RibbonMenuItem item, boolean selected, View convertView);
	
	
}
