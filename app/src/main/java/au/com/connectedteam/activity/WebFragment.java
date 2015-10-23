package au.com.connectedteam.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidquery.AQuery;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import au.com.connectedteam.application.ConnectedApp;
import au.com.connectedteam.util.StringUtils;
import au.com.connectedteam.util.UIUtil;


/**
 * WebView
 * @author bramleyt
 *
 */
public class WebFragment extends BaseFragment{
	
	

	

	private WebView webview;	
	private boolean interceptYoutube;
	private boolean canNavigate;
	private boolean forwardLinksToSystem;
	
	
	
	public static final String ARG_REQUESTED_URL = "WebFragment.RequestedURL";	
	public static final String ARG_HTML_DATA = "WebFragment.HtmlData";	
	public static final String ARG_CAN_NAVIGATE = "WebFragment.CanNavigate";	
	public static final String ARG_FWD_LINKS_TO_SYSTEM = "WebFragment.FwdLinksToSystem";	
	/**
	 * Set this flag in the initial args bundle to allow the webfragment to override youtube urls and open them in the
	 * native youtube app. If not set, the default is to use the youtube app on Android devices, and to stay in the
	 * webview for Blackberry devices.
	 */
	public static final String YOUTUBE_INTERCEPT_ENABLED_TAG = "WebFragment.YoutubeIntercept";	


	/**
	 * If a loaded URL starts with any of these strings, the fragment will launch an Intent when loading it.
	 */
	public static final String[] OVERRIDE_URLS_WITH_INTENT = new String[]{
		//"http://www.youtube.com/embed/",
		"http://www.youtube.com/",		
		"http://youtu.be/"		
	};
	
	public static final String[] YOUTUBE_URLS = new String[]{		
		"http://www.youtube.com/",		
		"http://youtu.be/"		
	};

	private static final String URL_FAKE_HTML_DATA = "webjet://original.html.data";
	
	public static final String YOUTUBE_VIEW_URL = "http://www.youtube.com/watch?v=";
	
	
	public static final String YOUTUBE_EMBED_URL = "http://www.youtube.com/embed/";
	public static final String YOUTUBE_THUMBNAIL_URL_FORMAT = "http://img.youtube.com/vi/%s/default.jpg";
	
	public static final String YOUTUBE_THUMBNAIL_URL_FORMAT_HQ = "http://img.youtube.com/vi/%s/hqdefault.jpg";
	public static final String YOUTUBE_THUMBNAIL_URL_FORMAT_MQ = "http://img.youtube.com/vi/%s/mqdefault.jpg";
	public static final String YOUTUBE_THUMBNAIL_URL_FORMAT_MAX = "http://img.youtube.com/vi/%s/maxresdefault.jpg";
	public static final int YOUTUBE_VIDEO_ID_LENGTH = 11;
	
	public static String formatYoutubePreviewUrlByDensity(String youtubeId){
		if(UIUtil.densityDpi>DisplayMetrics.DENSITY_MEDIUM){
			return String.format(YOUTUBE_THUMBNAIL_URL_FORMAT_HQ, youtubeId);
		}	
		return String.format(YOUTUBE_THUMBNAIL_URL_FORMAT_MQ, youtubeId);
	}
	
	
	public static WebFragment newInstance(String url, String htmlData)  {
		
		WebFragment instance = new WebFragment();
		Bundle bundle = new Bundle();
		if(!StringUtils.isNullOrEmpty(url)) bundle.putString(ARG_REQUESTED_URL, url);		
		if(!StringUtils.isNullOrEmpty(htmlData)) bundle.putString(ARG_HTML_DATA, htmlData);		
		instance.setArguments(bundle);
		return instance;
	}
	
	public static String addHtmlHeadTags(String bodyHtml, boolean addFillViewportTag){
		
		if(addFillViewportTag){
			return "<html><head><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /></head><body>"
				+bodyHtml
				+"</body></html>";
		}
		return "<html><body>"
			+bodyHtml
			+"</body></html>";
	}
	
	public static String addHtmlWrapperFromAssets(Context c, String assetFilename, String replaceToken, String bodyHtml){
		String htmlWrapper;
		try {
			htmlWrapper = StringUtils.inputStreamToString(c.getAssets().open(assetFilename));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return htmlWrapper.replace(replaceToken, bodyHtml);
	}
	
	
	public static boolean isYoutubeUrl(String url){
		for(String testUrl : OVERRIDE_URLS_WITH_INTENT) if (url.startsWith(testUrl)){
			return true;
		}
		return false;
	}
	
	public static Intent getYoutubeIntentFromVideoID(String id){
		return getYoutubeIntent(YOUTUBE_VIEW_URL+id);
	}
	public static Intent getYoutubeIntent(String url){				
		Uri youtubeLink = Uri.parse(url.replace("://youtu.be/", "://www.youtube.com/watch?v="));								
		Intent directYoutubeIntent = new Intent(Intent.ACTION_VIEW);
		directYoutubeIntent.setData(youtubeLink);
		directYoutubeIntent.setPackage("com.google.android.youtube");
		if( ConnectedApp.getContextStatic().getPackageManager().resolveActivity(directYoutubeIntent, PackageManager.MATCH_DEFAULT_ONLY)==null){
			directYoutubeIntent.setPackage(null);
			//return Intent.createChooser(new Intent(Intent.ACTION_VIEW, youtubeLink), "View video using:");
		}
		return directYoutubeIntent;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRetainInstance(true);
		
		interceptYoutube = getArguments().getBoolean(YOUTUBE_INTERCEPT_ENABLED_TAG, true);
		canNavigate = getArguments().getBoolean(ARG_CAN_NAVIGATE, true);
		forwardLinksToSystem = getArguments().getBoolean(ARG_FWD_LINKS_TO_SYSTEM, false);
	}
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){		
		if(container==null) return null;
		if (webview != null) {
			if(savedInstanceState==null){
				savedInstanceState = new Bundle();
				
			}
			//Save state of any previous webview, and destroy it
			webview.saveState(savedInstanceState);
            webview.destroy();
            
        }
		
		
			webview = new WebView(this.getActivity());
			WebSettings settings = webview.getSettings();
			settings.setJavaScriptEnabled(true);
			//settings.setPluginState(PluginState.OFF);
			//settings.setPluginsEnabled(false);
			settings.setAllowFileAccess(true);
			settings.setBuiltInZoomControls(true);
			if(Build.VERSION.SDK_INT>=11){
				settings.setDisplayZoomControls(false);
			}
			webview.setWebChromeClient(new WebChromeClient() {
				public void onProgressChanged(WebView view, int progress) {
					if(getActivity()==null) return;
					
					getBaseActivity().notifyRefreshing(WebFragment.this, progress<100);
					
				}
				
			});
			webview.setWebViewClient(new WebViewClient() {
				private AQuery aqWebClient = new AQuery(webview.getContext());
				public void onLoadResource (final WebView view, String url){					
							             
					if(Build.VERSION.SDK_INT<11 && url.startsWith(URL_FAKE_HTML_DATA)){
						//very hacky way to go to original html data in API<11 - before ShouldInterceptRequest was introduced
						view.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								view.clearHistory();
								view.loadDataWithBaseURL(getArguments().getString(ARG_REQUESTED_URL), getArguments().getString(ARG_HTML_DATA), "text/html", "UTF-8", URL_FAKE_HTML_DATA);
								view.clearHistory();
							}
						}, 100);
					}
				}
				//API>=11
				public WebResourceResponse shouldInterceptRequest(WebView view,
			            String url) {					
					//user is navigating back to original HTML data, which the WebView can't provide from history itself.
					//so provide it via an input stream
					if(url.startsWith(URL_FAKE_HTML_DATA)){
						String htmlData = getArguments().getString(ARG_HTML_DATA);
						try {
							InputStream is = new ByteArrayInputStream(htmlData.getBytes("UTF-8"));
							return new WebResourceResponse("text/html", "UTF-8", is);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						
					}
					else if (url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".jpeg")){
						File file = aqWebClient.getCachedFile(url);
						if(file!=null) try{
							InputStream is = new FileInputStream(file);
							return new WebResourceResponse("image/jpeg", null, is);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						
						}
					}
			        return null;
			    }
				
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					Log.d("WebView", url);
					if (getActivity()==null) return false;
					return WebFragment.this.shouldOverrideUrlLoading(view, url);
		         }
				
				public void onPageFinished (WebView view, String url){
					if(getActivity()!=null) WebFragment.this.onPageFinished(view, url);
				}
			});
			
			if(savedInstanceState!=null){
				webview.restoreState(savedInstanceState);
				if(!StringUtils.isNullOrEmpty(getArguments().getString(ARG_HTML_DATA) )){
					WebBackForwardList backForwardList = webview.copyBackForwardList();
					int size = backForwardList.getSize();
					//current page was the ARG_HTML_DATA page, which is not saved. Restore it.
				/*	if(size>0 && backForwardList.getCurrentItem().getOriginalUrl()!=null 
							&& backForwardList.getCurrentItem().getOriginalUrl().equals(getArguments().getString(ARG_REQUESTED_URL))){
						//webview.clearHistory();
						//webview.loadDataWithBaseURL(getArguments().getString(ARG_REQUESTED_URL), getArguments().getString(ARG_HTML_DATA), "text/html", "UTF-8", URL_FAKE_HTML_DATA);
					} */
				}
			}
			else if(!StringUtils.isNullOrEmpty(getArguments().getString(ARG_HTML_DATA) )){
				webview.loadDataWithBaseURL(getArguments().getString(ARG_REQUESTED_URL), getArguments().getString(ARG_HTML_DATA), "text/html", "UTF-8", URL_FAKE_HTML_DATA);
			}
			
			else{				
				webview.loadUrl(getArguments().getString(ARG_REQUESTED_URL));				
			}
		return webview;

	}

	int i=0;

	
	
	/**
	 * Go somewhere else
	 * @param newUrl
	 */
	public void navigate(String newUrl){
		if(webview!=null) webview.loadUrl(newUrl);
	}
	public void navigateInHistoryIfPossible(String newUrl){
		if(webview!=null){
			WebBackForwardList history = webview.copyBackForwardList();
			for(int i=0;i<history.getSize();i++){
				if(newUrl.equals(history.getItemAtIndex(i).getOriginalUrl()) || newUrl.equals(history.getItemAtIndex(i).getUrl())){
					Log.d("WebView", "Found "+newUrl + " at "+i+", steps: "+(i-history.getCurrentIndex()));
					webview.goBackOrForward(i-history.getCurrentIndex());
					return;
				}
				
			}
			webview.loadUrl(newUrl);
		}
	}
	
	public String getUrl(){
		if(webview!=null) return webview.getOriginalUrl();
		//if(webview!=null) return webview.getUrl();
		return null;
	}
	
	protected void onPageFinished (WebView view, String url){
		
	}
	
	
	protected boolean shouldOverrideUrlLoading(final WebView view, String url){
		
		if (url.startsWith("mailto:") || url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url)); 
            startActivity(intent); 
            return true;
         }
		if(forwardLinksToSystem && (url.startsWith("http:") || url.startsWith("https:"))){
			 Intent intent = new Intent(Intent.ACTION_VIEW,
	                    Uri.parse(url)); 
	            startActivity(intent); 
	            return true;
		}
		
		if(!canNavigate && !url.equalsIgnoreCase(getUrl()) && !url.equalsIgnoreCase(getArguments().getString(ARG_REQUESTED_URL))){
			//intercept, do nothing
			return true;
		}

         return false;
	}
	
	//@Override
	public boolean onRefreshButtonClicked() {
		webview.reload();
		return true;
	}
	

	@Override
	public boolean onBackKeyPressed() {	
		if(webview!=null && webview.canGoBack()){
			webview.goBack();
			return false;
		}
		return true;
	}
	
	
	
	@Override
    public void onDestroy() {
        if (webview != null) {
            //webview.destroy();
            webview = null;
        }
        super.onDestroy();
    }
	   /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @SuppressLint("NewApi")
	@Override
    public void onPause() {
        super.onPause();
        if(Build.VERSION.SDK_INT>=11 && webview!=null){
        	webview.onPause();
        }
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @SuppressLint("NewApi")
	@Override
    public void onResume() {
    	if(Build.VERSION.SDK_INT>=11 && webview!=null){
    		webview.onResume();
    	}
        super.onResume();
    }
    
    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    
    @Override
	public void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
    	if(webview!=null)
    		webview.saveState(outState);
    }

	@Override
	public boolean isRequesting() {
		if(webview==null) return false;
		return webview.getProgress()<100;
	}
	
	public WebView getWebView(){
		return webview;
	}

 
}
