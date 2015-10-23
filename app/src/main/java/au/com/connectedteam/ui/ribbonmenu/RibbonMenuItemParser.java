package au.com.connectedteam.ui.ribbonmenu;

import android.content.Context;
import android.content.res.XmlResourceParser;
import au.com.connectedteam.util.StringUtils;

public class RibbonMenuItemParser extends AbsMenuParser<RibbonMenuItem>{


	
	
	public RibbonMenuItemParser(Context c){
		super(c);
		
	}

			
	protected RibbonMenuItem parseItem(XmlResourceParser xpp){
		String textId = xpp.getAttributeValue(NAMESPACE_ANDROID, "title");
		String iconId = xpp.getAttributeValue(NAMESPACE_ANDROID, "icon");
		String resId = xpp.getAttributeValue(NAMESPACE_ANDROID, "id");
		String checkableId = xpp.getAttributeValue(NAMESPACE_ANDROID, "checkable");
		String visibleId = xpp.getAttributeValue(NAMESPACE_ANDROID, "visible");
		//String showAsActionCompatId = xpp.getAttributeValue(NAMESPACE_PROJECT, "showAsAction");
		String showAsActionId = xpp.getAttributeValue(NAMESPACE_ANDROID, "showAsAction");
		
		
		int id = Integer.parseInt(resId.replace("@", ""));
		String text = resourceIdToString(textId);
		int icon;
		if(iconId!=null){
			icon= Integer.parseInt(iconId.replace("@", ""));
		}
		else icon=0;
		boolean checkable = Boolean.parseBoolean(checkableId);
		boolean visible = StringUtils.isNullOrEmpty(visibleId)?true: Boolean.parseBoolean(visibleId);
		int showAsAction = StringUtils.isNullOrEmpty(showAsActionId) ? 0 : Integer.decode(showAsActionId);
		RibbonMenuItem item = new RibbonMenuItem(id, text, icon, checkable);
		item.setShowAsAction(showAsAction);
		item.setVisible(visible);
		return item;
	}
	
	
	
	
	
}
