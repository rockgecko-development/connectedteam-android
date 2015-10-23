package au.com.connectedteam.ui.ribbonmenu;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuItemCompat;

import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.util.StringUtils;


public class RibbonMenuItem{
	
	private final int id;
	private CharSequence text;
	private int icon;
    private Drawable iconDrawable;
	private int counterBadge;
	private int showAsAction;
	private boolean enabled=true;
	private boolean checkable=false;
	private boolean visible=true;
	private List<RibbonMenuItem> subItems;
	private RibbonMenuItem parent;
	private Intent intent;
	
	public RibbonMenuItem(int id, CharSequence text, int iconRes, boolean checkable){
		this.id=id;
		this.text=text;
		this.checkable=checkable;
		icon=iconRes;

	}

	public boolean hasSubItems(){
		return !StringUtils.isNullOrEmpty(subItems);
	}
	public List<RibbonMenuItem> getSubItems(){
		return subItems;
	}	
	
	public void setSubItems(List<RibbonMenuItem> subItems){
		this.subItems=subItems;
	}
	public RibbonMenuItem getParent(){
		return parent;		
	}
	public void setParent(RibbonMenuItem parent){
		this.parent=parent;
	}

	public CharSequence getText() {
		return text;
	}

	public void setText(CharSequence text) {
		this.text = text;
	}
	
	public void setIntent(Intent intent){
		this.intent = intent;
	}
	public Intent getIntent(){
		return intent;
	}

	public int getIcon() {
		return icon;
	}
    public Drawable getIconDrawable() {
        if(iconDrawable==null && icon!=0){
            iconDrawable = ConnectedApp.getContextStatic().getResources().getDrawable(icon);
        }
        return iconDrawable;
    }
	
	public int getId() {
		return id;
	}
	
	public int getCounterBadge(){
		return counterBadge;
	}
	public void setCounterBadge(int count){
		this.counterBadge=count;
	}

	public void setIcon(int icon) {
		this.icon = icon;
        this.iconDrawable=null;
	}

    public void setIcon(Drawable icon) {
        this.icon=0;
        this.iconDrawable = icon;
    }

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}


	public boolean isCheckable() {
		return checkable;
	}

	@Override
	public String toString(){
		return getText().toString();
	}

	public int getShowAsAction() {
		return showAsAction;
	}

	public void setShowAsAction(int showAsAction) {
		this.showAsAction = showAsAction;
	}
	public boolean showsTextAsAction() {
        return (showAsAction & MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT) == MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT;
    }
	
}