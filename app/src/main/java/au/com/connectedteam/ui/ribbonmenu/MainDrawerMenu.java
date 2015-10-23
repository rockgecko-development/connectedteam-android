package au.com.connectedteam.ui.ribbonmenu;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import au.com.connectedteam.R;



/**
 * Extension of support library's DrawerLayout, containing
 * a {@link RibbonMenuView}
 * @author bramleyt
 *
 */
public class MainDrawerMenu extends DrawerLayout{

	

	protected RibbonMenuViewImpl mRibbonMenu;
	
	
	public MainDrawerMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRibbonMenu = new RibbonMenuViewImpl(context);		
		//mRibbonMenu.setBackgroundDrawable(context.getResources().getDrawable(R.color.ribbonmenu_background));
		mRibbonMenu.setBackgroundColor(context.getResources().getColor(R.color.ribbonmenu_background));
		//mRibbonMenu.setBackgroundColor(0xff302922);
	}
	
	@Override
	public void onFinishInflate(){
		super.onFinishInflate();
		addViewInLayout(mRibbonMenu, -1, new DrawerLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.rmb_menu_width), LayoutParams.MATCH_PARENT, Gravity.LEFT));
	}
	
	public RibbonMenuView getRibbonMenuView(){
		return mRibbonMenu;
	}
	/*
	public void setUsername(String username){
		if(menuHeader!=null){
			if(StringUtils.isNullOrEmpty(username)){
				((TextView) menuHeader.findViewById(R.id.drawer_menu_msg)).setText(R.string.menu_logged_out);
				menuHeader.findViewById(R.id.drawer_menu_username).setVisibility(GONE);
				menuHeader.findViewById(R.id.drawer_menu_login_btn).setVisibility(VISIBLE);
			}
			else{
				((TextView) menuHeader.findViewById(R.id.drawer_menu_msg)).setText(R.string.menu_logged_in_as);
				((TextView) menuHeader.findViewById(R.id.drawer_menu_username)).setText(username);
				menuHeader.findViewById(R.id.drawer_menu_username).setVisibility(VISIBLE);
				menuHeader.findViewById(R.id.drawer_menu_login_btn).setVisibility(GONE);
			}
		}
		mUsername=username;
	}*/
	
	
	
	public class RibbonMenuViewImpl extends RibbonMenuView{
	
	public RibbonMenuViewImpl(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void inflateLayout(){
		/*menuHeader = View.inflate(getContext(), R.layout.drawer_menu_hdr_logged_in, null);
		menuHeader.findViewById(R.id.drawer_menu_login_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(callback != null){		
					boolean hide = callback.onRibbonMenuItemClick(RibbonMenuViewImpl.this, new RibbonMenuItem(R.id.rbm_item_logout, "Login", 0,false));
					if(hide) closeDrawer(Gravity.LEFT);
				}
				else closeDrawer(Gravity.LEFT);	
				
			}
		});
		if(mUsername!=null) setUsername(mUsername);*/
		rbmListView = new ListView(getContext());
		rbmListView.setCacheColorHint(getResources().getColor(R.color.ribbonmenu_background));
		//rbmListView.setBackgroundResource(R.color.ribbonmenu_background);		
		rbmListView.setSelector(R.drawable.list_selector_ribbonmenu);
		rbmListView.setDivider(null);
		//RibbonMenuViewImpl.this.addView(menuHeader, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		RibbonMenuViewImpl.this.addView(rbmListView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		initListView();

	}
	
	@Override
	protected void initListView(){
		rbmListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if(callback != null){		
					RibbonMenuItem menuItem = (RibbonMenuItem) rbmListView.getAdapter().getItem(position);
					boolean handled = callback.onRibbonMenuItemClick(RibbonMenuViewImpl.this, menuItem);
					if(!handled && menuItem.getIntent()!=null){
						getContext().startActivity(menuItem.getIntent());
						handled=true;
					}
					if(handled) closeDrawer(Gravity.LEFT);
				}
				else closeDrawer(Gravity.LEFT);	

			}

		});

	}
	/*
	@Override
	protected void onAttachedToWindow (){
		super.onAttachedToWindow();
		if(drawerLayout==null) initOutsideView();
	}*/

	
	}
	
	
}
