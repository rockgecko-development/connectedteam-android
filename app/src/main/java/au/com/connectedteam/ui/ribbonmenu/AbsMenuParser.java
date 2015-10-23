package au.com.connectedteam.ui.ribbonmenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

public abstract class AbsMenuParser<T extends RibbonMenuItem> {

public static final String NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";
public static final String NAMESPACE_PROJECT = "http://schemas.android.com/apk/res-auto";
	
	protected Context mContext;
	public AbsMenuParser(Context c){
		mContext = c;
		
	}

	public ArrayList<T> parseXml(int menu){
		//RibbonMenuItem rootItem = new RibbonMenuItem(0, null, 0, false);
		ArrayList<T> mMenuItems = new ArrayList<T>();
		ArrayList<T> pointer = mMenuItems;
		Stack<ArrayList<T>> parents = new Stack<ArrayList<T>>();
		
		try{
			XmlResourceParser xpp = mContext.getResources().getXml(menu);
			
			xpp.next();
			int eventType = xpp.getEventType();
			
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				
				if(eventType == XmlPullParser.START_TAG){
					
					String elemName = xpp.getName();
						
					
					
					if(elemName.equals("item")){
																																			
						T item = parseItem(xpp);
						if(parents.size()>0){
							
							item.setParent(parents.peek().get(parents.peek().size()-1));
						}
						pointer.add(item);
						
					}
					else if (elemName.equals("intent")){
						pointer.get(pointer.size()-1).setIntent(parseIntent(xpp));
					}
					else if (elemName.equals("menu") && pointer.size()>0){
						//Log.e("AbsMenuParser", "Sub menus not supported");
						if(pointer.get(pointer.size()-1).getSubItems()==null){
							pointer.get(pointer.size()-1).setSubItems(new ArrayList());
						}
						parents.push(pointer);
						pointer = (ArrayList) pointer.get(pointer.size()-1).getSubItems();
					}
					
					
					
				}
				else if (eventType==XmlPullParser.END_TAG){
					String elemName = xpp.getName();
					if(elemName.equals("menu") && parents.size()>0){
						pointer=parents.pop();
					}
				}
				
				eventType = xpp.next();
				
				
			}
			
		xpp.close();	
		} catch(Exception e){
			e.printStackTrace();
		}
		
		
		return mMenuItems;
		
	}
	
	
	protected abstract T parseItem(XmlResourceParser xpp);
	
	protected Intent parseIntent(XmlResourceParser xpp){
		try {
			return Intent.parseIntent(mContext.getResources(), xpp, Xml.asAttributeSet(xpp));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		/*
		String targetPackage = xpp.getAttributeValue(NAMESPACE_ANDROID, "targetPackage");
		String targetClass = xpp.getAttributeValue(NAMESPACE_ANDROID, "targetClass");
		
		
		Intent intent = new Intent();
		if(mContext.getPackageName().equals(targetPackage)){
			intent.setClassName(mContext, targetClass);
		}
		else{
			intent.setClassName(targetPackage, targetClass);
		}
		
		return intent;
		*/
	}
	
	
	protected String resourceIdToString(String text){
		
		if(!text.startsWith("@")){
			return text;
		} else {
									
			String id = text.replace("@", "");
									
			return mContext.getString(Integer.parseInt(id));
			
		}
		
	}
}
