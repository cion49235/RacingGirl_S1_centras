package racingmodel.muscle.centras.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdView.AdAnimation;
import com.admixer.AdViewListener;
import com.admixer.CustomPopup;
import com.admixer.CustomPopupListener;
import com.admixer.InterstitialAd;
import com.admixer.InterstitialAdListener;
import com.admixer.PopupInterstitialAdOption;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import kr.co.inno.autocash.AutoLayoutGoogleActivity;
import kr.co.inno.autocash.service.AutoLoginServiceActivity;
import kr.co.inno.autocash.service.AutoServiceActivity;
import racingmodel.muscle.centras.R;
import racingmodel.muscle.centras.adapter.MainAdapter;
import racingmodel.muscle.centras.data.Favorite_DBopenHelper;
import racingmodel.muscle.centras.data.Main_Data;
import racingmodel.muscle.centras.data.Pause_DBOpenHelper;
import racingmodel.muscle.centras.mediaplayer.ContinueMediaPlayer;
import racingmodel.muscle.centras.util.Crypto;
import racingmodel.muscle.centras.util.HangulUtil;
import racingmodel.muscle.centras.util.PreferenceUtil;
import racingmodel.muscle.centras.util.Utils;
import racingmodel.muscle.centras.youtubeplayer.CustomYoutubePlayer;
public class MainActivity extends Activity implements OnItemClickListener, OnClickListener, OnScrollListener, AdViewListener, CustomPopupListener, InterstitialAdListener{
	public static Context context;
	public ConnectivityManager connectivityManger;
	public NetworkInfo mobile;
	public NetworkInfo wifi;
	public static MainAdapter main_adapter;
	public static GridView listview_main;
	public int SDK_INT = android.os.Build.VERSION.SDK_INT;
	public static LinearLayout layout_nodata;
	public static RelativeLayout ad_layout;
	public static boolean loadingMore = true;
	public static boolean exeFlag;
	public Handler handler = new Handler();
	public static int start_index;
	public static int itemsPerPage = 50;
	public static int current_position = 0;
	public boolean flag;
	public Main_ParseAsync main_parseAsync = null;
	public ProgressDialog progressDialog = null;
	public static Favorite_DBopenHelper favorite_mydb;
	public static Pause_DBOpenHelper pause_mydb;
	public static NotificationManager notificationManager;
	public static Notification notification;
	public static int noti_state = 1;
	public static TextView txt_main_title;
	public static int TotalRow;;
	public static ArrayList<Main_Data> list;
	public static LinearLayout layout_progress;
	public static Button bt_category, bt_intent_favorite, bt_plus_coin;
	public static LinearLayout action_layout;
	public static Button bt_all_select, bt_play_video, bt_play_media;
	public Cursor cursor;
	public static AlertDialog alertDialog;
	public static int category_which = 0;
	public static SharedPreferences settings,pref;
	public Editor edit;
	public boolean retry_alert = false;
	public String num;
	public static EditText edit_searcher;
	public static ImageButton bt_home, bt_search_result; 
	public String searchKeyword;
	private AdView adView;
	public static com.admixer.InterstitialAd interstialAd;
	private NativeExpressAdView admobNative;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main_activity);
	context = this;
	
	txt_main_title = (TextView)findViewById(R.id.txt_main_title);
	txt_main_title.setText(context.getString(R.string.app_name));
	
	AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "hdtqihar");
	AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/3138890161");
	AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/4615623362");
	
//	  
	CustomPopup.setCustomPopupListener(this);
	CustomPopup.startCustomPopup(this, "hdtqihar");
	
	addBannerView();
//	init_admob_naive();

	num = "1507";
	start_index = 1;
	layout_nodata = (LinearLayout)findViewById(R.id.layout_nodata);
	layout_progress = (LinearLayout)findViewById(R.id.layout_progress);
	action_layout = (LinearLayout)findViewById(R.id.action_layout);
	listview_main = (GridView)findViewById(R.id.listview_main);
	bt_category = (Button)findViewById(R.id.bt_category);
	bt_category.setText(context.getString(R.string.txt_main_activity21));
	bt_category.setTextColor(Color.BLACK);
	bt_all_select = (Button)findViewById(R.id.bt_all_select);
	bt_play_video = (Button)findViewById(R.id.bt_play_video);
	bt_play_media = (Button)findViewById(R.id.bt_play_media);
	edit_searcher = (EditText)findViewById(R.id.edit_searcher);
	bt_home = (ImageButton)findViewById(R.id.bt_home);
	bt_search_result = (ImageButton)findViewById(R.id.bt_search_result);
	bt_intent_favorite = (Button)findViewById(R.id.bt_intent_favorite);
	bt_plus_coin = (Button)findViewById(R.id.bt_plus_coin);
	bt_home.setOnClickListener(this);
	bt_search_result.setOnClickListener(this);
	bt_all_select.setOnClickListener(this);
	bt_play_video.setOnClickListener(this);
	bt_play_media.setOnClickListener(this);
	bt_category.setOnClickListener(this);
	bt_intent_favorite.setOnClickListener(this);
	bt_plus_coin.setOnClickListener(this);
	pause_mydb = new Pause_DBOpenHelper(this);
	favorite_mydb = new Favorite_DBopenHelper(this);
	list = new ArrayList<Main_Data>();
	list.clear();
	retry_alert = true;
	seacher_start();
	displaylist();	
	exit_handler();
	auto_service();
//	check_popup();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, false);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		admobNative.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		admobNative.resume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		admobNative.destroy();
		retry_alert = false;
		// Custom Popup 占쏙옙占쏙옙
		CustomPopup.stopCustomPopup();
    	
		settings = getSharedPreferences(context.getString(R.string.txt_main_activity36), MODE_PRIVATE);
		edit = settings.edit();
		edit.putInt("category_which", 0);
		edit.commit();
		
		current_position = 0;
    	start_index = 1;
		loadingMore = true;
		exeFlag = false;
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		layout_progress.setVisibility(View.GONE);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
//		check_popup();
		if(main_adapter != null){
			main_adapter.notifyDataSetChanged();	
		}
	}
	
	private void check_popup() {
    	SharedPreferences prefs = getSharedPreferences("kr.co.byapps", MODE_PRIVATE);
        final  String loginID = prefs.getString("loginID", "");
        String googlePasswd = prefs.getString("googlePasswd", "");
        if(TextUtils.isEmpty(loginID) && TextUtils.isEmpty(googlePasswd)){
        	check_google_account();        	
        }else{
        	 auto_service();
             auto_login_service();
        }
    }
	
	public void check_google_account(){
        new android.app.AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(context.getString(R.string.txt_main_activity41))
                .setMessage(context.getString(R.string.txt_main_activity42))
                .setPositiveButton(context.getString(R.string.txt_main_activity43), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
//                    	if (SDK_INT >= Build.VERSION_CODES.M){ 
//                        	checkPermission();	
//                        } else {
                        	go_googlelogin();
//                        }
                    }
                })
                .setNegativeButton(context.getString(R.string.txt_main_activity44), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .create().show();
    }
	
	private void auto_service() {
        Intent intent = new Intent(context, AutoServiceActivity.class);
        context.stopService(intent);
        context.startService(intent);
    }

    private void auto_login_service() {
        Intent intent = new Intent(context, AutoLoginServiceActivity.class);
        context.stopService(intent);
        context.startService(intent);
    }
	
	private int MY_PERMISSION_REQUEST_STORAGE = 0;
    private void checkPermission() {
	    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
	            || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
	            || checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
	            || checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED
	    		){
	        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
	        	Toast.makeText(context, context.getString(R.string.permission_ment), Toast.LENGTH_LONG).show();
	        }
	        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE},
	                MY_PERMISSION_REQUEST_STORAGE);
	    } else {
	    	go_googlelogin();
	    }
	}
    
    private void go_googlelogin() {
    	Intent intent = new Intent(context, AutoLayoutGoogleActivity.class);
        intent.putExtra("googleType", "2");
        startActivity(intent);
    }
	
	public void exit_handler(){
    	handler = new Handler(){
    		@Override
    		public void handleMessage(Message msg) {
    			if(msg.what == 0){
    				flag = false;
    			}
    		}
    	};
    }
	
	private void init_admob_naive(){
		RelativeLayout nativeContainer = (RelativeLayout) findViewById(R.id.admob_native);
		AdRequest adRequest = new AdRequest.Builder().build();	    
		admobNative = new NativeExpressAdView(this);
		admobNative.setAdSize(new AdSize(360, 100));
		admobNative.setAdUnitId("ca-app-pub-4637651494513698/6092356566");
		nativeContainer.addView(admobNative);
		admobNative.loadAd(adRequest);
	}
	
	public void addBannerView() {
        AdInfo adInfo = new AdInfo("hdtqihar");
    	adInfo.setTestMode(false);
        com.admixer.AdView adView = new com.admixer.AdView(this);
        adView.setAdInfo(adInfo, this);
        adView.setAdViewListener(this);
        adView.setAdAnimation(AdAnimation.TopSlide);
        ad_layout = (RelativeLayout)findViewById(R.id.ad_layout);
        if(ad_layout != null){
        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ad_layout.addView(adView, params);	
        }
    }
	
	public void addInterstitialView() {
		AdInfo adInfo = new AdInfo("hdtqihar");
		adInfo.setInterstitialTimeout(0); 
		adInfo.setUseRTBGPSInfo(false);
		adInfo.setMaxRetryCountInSlot(-1);
		adInfo.setBackgroundAlpha(true); 

		PopupInterstitialAdOption adConfig = new PopupInterstitialAdOption();
		adConfig.setDisableBackKey(true);
		adConfig.setButtonLeft(context.getString(R.string.txt_finish_no), "#234234");
		adConfig.setButtonRight(context.getString(R.string.txt_finish_yes), "#234234");
		adConfig.setButtonFrameColor(null);
		adInfo.setInterstitialAdType(AdInfo.InterstitialAdType.Popup, adConfig);
		
		interstialAd = new InterstitialAd(this);
		interstialAd.setAdInfo(adInfo, this);
		interstialAd.setInterstitialAdListener(this);
		interstialAd.startInterstitial();
    }
	
	
	public void seacher_start(){
		edit_searcher.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable arg0) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					searchKeyword = s.toString().toLowerCase();
					Log.e("dsu", "占싯삼옙占쏙옙 : " + searchKeyword);
				} catch (Exception e) {
				}
			}
		});
	}
	
	public void displaylist(){
		main_parseAsync = new Main_ParseAsync();
		main_parseAsync.execute();
		if (SDK_INT >= Build.VERSION_CODES.HONEYCOMB){ //占쏙옙占쏙옙占� 占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 API 占쏙옙占�}
			listview_main.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		listview_main.setOnItemClickListener(this);
		listview_main.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listview_main.setOnScrollListener(this);
	}
	
	public class Main_ParseAsync extends AsyncTask<String, Integer, String>{
		String Response;
		Main_Data main_data;
		ArrayList<Main_Data> menuItems = new ArrayList<Main_Data>();
		String i;
		int _id;
		String id;
		String title;
		String portal;
		String thumbnail_hq;
		String sprit_title[];
		public Main_ParseAsync(){
		}
			@Override
			protected String doInBackground(String... params) {
				String sTag;
				try{
				   String data = Crypto.decrypt(Utils.data, context.getString(R.string.txt_str8));
		           String str = data+i+".php?view="+num;
		           HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(str).openConnection();
		           HttpURLConnection.setFollowRedirects(false);
		           localHttpURLConnection.setConnectTimeout(15000);
		           localHttpURLConnection.setReadTimeout(15000); 
		           localHttpURLConnection.setRequestMethod("GET");
		           localHttpURLConnection.connect();
		           InputStream inputStream = new URL(str).openStream(); //open Stream占쏙옙 占쏙옙占쏙옙臼占� InputStream占쏙옙 占쏙옙占쏙옙占쌌니댐옙.
		           XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
		           XmlPullParser xpp = factory.newPullParser();
		           xpp.setInput(inputStream, "EUC-KR"); //euc-kr占쏙옙 占쏙옙低� 占쏙옙占쏙옙占쌌니댐옙. utf-8占쏙옙 占싹니깍옙 占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙
		           int eventType = xpp.getEventType();
		           while (eventType != XmlPullParser.END_DOCUMENT) {
			        	if (eventType == XmlPullParser.START_DOCUMENT) {
			        	}else if (eventType == XmlPullParser.END_DOCUMENT) {
			        	}else if (eventType == XmlPullParser.START_TAG){
			        		sTag = xpp.getName();
			        		if(sTag.equals("Content")){
			        			main_data = new Main_Data();
			        			_id = Integer.parseInt(xpp.getAttributeValue(null, "id") + "");
			            	}else if(sTag.equals("videoid")){
			        			Response = xpp.nextText()+"";
			            	}else if(sTag.equals("subject")){
			            		title = xpp.nextText()+"";
			            		sprit_title = title.split("-");
			            	}else if(sTag.equals("portal")){
			            		portal = xpp.nextText()+"";
			            	}else if(sTag.equals("thumb")){
			            		thumbnail_hq = xpp.nextText()+"";
			            	}
			        	} else if (eventType == XmlPullParser.END_TAG){
			            	sTag = xpp.getName();
			            	if(sTag.equals("Content")){
			            		main_data._id = _id;
			            		main_data.id = Response;
			            		main_data.title = title;
			            		main_data.portal = portal;
			            		main_data.category = context.getString(R.string.app_name);
			            		main_data.thumbnail_hq = thumbnail_hq;
			            		boolean isAdd = false;
			            		if(searchKeyword != null && "".equals(searchKeyword.trim()) == false){
			            			String iniName = HangulUtil.getHangulInitialSound(main_data.title,searchKeyword);
			            			if(iniName.indexOf(searchKeyword) >= 0){
			            				isAdd = true;
			            			}
			            		}else{
			            			isAdd = true;
			            		}
			            		if(isAdd){
			            			list.add(main_data);
			            		}
			            	}
			            } else if (eventType == XmlPullParser.TEXT) {
			            }
			            eventType = xpp.next();
			        }
		         }
				 catch (SocketTimeoutException localSocketTimeoutException)
		         {
		         }
		         catch (ClientProtocolException localClientProtocolException)
		         {
		         }
		         catch (IOException localIOException)
		         {
		         }
		         catch (Resources.NotFoundException localNotFoundException)
		         {
		         }
		         catch (NullPointerException NullPointerException)
		         {
		         }
				 catch (JSONException e) 
				 {
				 } 
				 catch (Exception e) 
				 {
				 }
				 return Response;
			}
			
			@Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            i="6";
	            layout_progress.setVisibility(View.VISIBLE);
	        }
			@Override
			protected void onPostExecute(String Response) {
				super.onPostExecute(Response);
				layout_progress.setVisibility(View.GONE);
				try{
					if(Response != null){
						for(int i=0;; i++){
							if(i >= list.size()){
//							while (i > list.size()-1){
								main_adapter = new MainAdapter(context, menuItems, listview_main);
								listview_main.setAdapter(main_adapter);
								listview_main.setFocusable(true);
								listview_main.setSelected(true);
								listview_main.setSelection(current_position);
								if(listview_main.getCount() == 0){
									layout_nodata.setVisibility(View.VISIBLE);
								}else{
									layout_nodata.setVisibility(View.GONE);
								}
								action_layout.setVisibility(View.GONE);
								return;
							}
							menuItems.add(list.get(i));
						}
					}else{
						layout_nodata.setVisibility(View.VISIBLE);
						Retry_AlertShow(context.getString(R.string.sub6_txt8));
					}
				}catch(NullPointerException e){
				}
			}
			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
			}
		}
	
	@Override
	public void onClick(View view) {
		if(view == bt_category){
			final String channel_title[] = {
					context.getString(R.string.txt_channel_title0),
					context.getString(R.string.txt_channel_title1),
					context.getString(R.string.txt_channel_title2),
					context.getString(R.string.txt_channel_title3),
					context.getString(R.string.txt_channel_title4),
					context.getString(R.string.txt_channel_title5),
					context.getString(R.string.txt_channel_title6),
					context.getString(R.string.txt_channel_title7),
					context.getString(R.string.txt_channel_title8),
					context.getString(R.string.txt_channel_title9),
					context.getString(R.string.txt_channel_title10)
			};
			settings = getSharedPreferences(context.getString(R.string.txt_main_activity36), MODE_PRIVATE);
			edit = settings.edit();
			pref = getSharedPreferences(context.getString(R.string.txt_main_activity36), Activity.MODE_PRIVATE);
			category_which = pref.getInt("category_which", 0);
			
			alertDialog = new AlertDialog.Builder(context)
			.setTitle(context.getString(R.string.txt_love_ment))
			.setSingleChoiceItems(channel_title, category_which, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "901";
						displaylist();
						bt_category.setText(channel_title[0]);
						edit.putInt("category_which", 0);
					}else if(which == 1){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "902";
						displaylist();
						bt_category.setText(channel_title[1]);
						edit.putInt("category_which", 1);
					}else if(which == 2){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "903";
						displaylist();
						bt_category.setText(channel_title[2]);
						edit.putInt("category_which", 2);
					}else if(which == 3){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "904";
						displaylist();
						bt_category.setText(channel_title[3]);
						edit.putInt("category_which", 3);
					}else if(which == 4){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "905";
						displaylist();
						bt_category.setText(channel_title[4]);
						edit.putInt("category_which", 4);
					}else if(which == 5){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "906";
						displaylist();
						bt_category.setText(channel_title[5]);
						edit.putInt("category_which", 5);
					}else if(which == 6){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "907";
						displaylist();
						bt_category.setText(channel_title[6]);
						edit.putInt("category_which", 6);
					}else if(which == 7){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "908";
						displaylist();
						bt_category.setText(channel_title[7]);
						edit.putInt("category_which", 7);
					}else if(which == 8){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "909";
						displaylist();
						bt_category.setText(channel_title[8]);
						edit.putInt("category_which", 8);
					}else if(which == 9){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "910";
						displaylist();
						bt_category.setText(channel_title[9]);
						edit.putInt("category_which", 9);
					}else if(which == 10){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "911";
						displaylist();
						bt_category.setText(channel_title[10]);
						edit.putInt("category_which", 10);
					}
					edit.commit();
					dialog.dismiss();
				}
			}).show();
			
		}else if(view == bt_all_select){
			if(bt_all_select.isSelected()){
				bt_all_select.setSelected(false);
				bt_all_select.setText(context.getString(R.string.txt_continue_activity4));
				for(int i=0; i < listview_main.getCount(); i++){
					listview_main.setItemChecked(i, false);
				}
				action_layout.setVisibility(View.GONE);
			}else{
				bt_all_select.setSelected(true);
				bt_all_select.setText(context.getString(R.string.txt_continue_activity7));
				for(int i=0; i < listview_main.getCount(); i++){
					listview_main.setItemChecked(i, true);
				}
			}
		}else if(view == bt_play_video){
				SparseBooleanArray sba = listview_main.getCheckedItemPositions();
				ArrayList<String> array_videoid = new ArrayList<String>();
				ArrayList<String> array_subject = new ArrayList<String>();
				ArrayList<String> array_portal = new ArrayList<String>();
				if(sba.size() != 0){
						for(int i = 0; i < listview_main.getCount(); i++){
							if(sba.get(i)){
								Main_Data main_data = (Main_Data)main_adapter.getItem(i);
								String videoid = main_data.id;
								String subject = main_data.title;
								String portal = main_data.portal;
								array_videoid.add(videoid);
								array_subject.add(subject);
								array_portal.add(portal);
								sba = listview_main.getCheckedItemPositions();
							}
						}
						if(array_videoid.size() != 0){
							Intent intent = new Intent(context, CustomYoutubePlayer.class);
							intent.putExtra("array_videoid", array_videoid);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent);
						}
				}
		}else if(view == bt_play_media){
				SparseBooleanArray sba = listview_main.getCheckedItemPositions();
				ArrayList<String> array_videoid = new ArrayList<String>();
				ArrayList<String> array_subject = new ArrayList<String>();
				ArrayList<String> array_thumb = new ArrayList<String>();
				ArrayList<String> array_portal = new ArrayList<String>();
				ArrayList<String> array_artist = new ArrayList<String>();
				ArrayList<String> array_playtime = new ArrayList<String>();
				if(sba.size() != 0){
				for(int i = listview_main.getCount() -1; i>=0; i--){
					if(sba.get(i)){
						Main_Data main_data = (Main_Data)main_adapter.getItem(i);
						String videoid = main_data.id;
						String subject = main_data.title;
						String thumb = main_data.thumbnail_hq;
						String portal = main_data.portal;
						String artist = context.getString(R.string.app_name);
//						String sprit_playtime[] = subject.split("-");
						String playtime = "";
						array_videoid.add(videoid);
						array_subject.add(subject);
						array_thumb.add(thumb);
						array_portal.add(portal);
						array_artist.add(artist);
						array_playtime.add(playtime);
						sba = listview_main.getCheckedItemPositions();
					}
				}
				if(array_videoid.size() != 0){
					Intent intent = new Intent(context, ContinueMediaPlayer.class);
					intent.putExtra("array_videoid", array_videoid);
					intent.putExtra("array_subject", array_subject);
					intent.putExtra("array_thumb", array_thumb);
					intent.putExtra("array_artist", array_artist);
					intent.putExtra("array_playtime", array_playtime);
					intent.putExtra("array_portal", array_portal);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			}
		}else if(view == bt_search_result){
			pref = getSharedPreferences(context.getString(R.string.txt_main_activity36), Activity.MODE_PRIVATE);
			category_which = pref.getInt("category_which", category_which);
			if(category_which != 0){
				alertDialog = new AlertDialog.Builder(this)
			    .setTitle(context.getString(R.string.txt_alert_search_ment1))
				.setIcon(R.drawable.bt_search_on)
				.setMessage(context.getString(R.string.txt_alert_search_ment2))
				.setPositiveButton(context.getString(R.string.txt_alert_search_button_yes), new DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
//						edit_searcher.setText("");
						num = "901";
						displaylist();
						bt_category.setText(context.getString(R.string.txt_channel_title0));
						
						settings = getSharedPreferences(context.getString(R.string.txt_main_activity36), MODE_PRIVATE);
						edit = settings.edit();
						pref = getSharedPreferences(context.getString(R.string.txt_main_activity36), Activity.MODE_PRIVATE);
						category_which = pref.getInt("category_which", 0);
						edit.putInt("category_which", 0);
						edit.commit();
						
						InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
			    		inputMethodManager.hideSoftInputFromWindow(edit_searcher.getWindowToken(), 0);
					}
				}).show();
			}
			else{
				String search_text = edit_searcher.getText().toString();
				if ((search_text != null) && (search_text.length() > 0)){
					InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		    		inputMethodManager.hideSoftInputFromWindow(edit_searcher.getWindowToken(), 0);
					
		    		list = new ArrayList<Main_Data>();
					list.clear();
					displaylist();
				}else{
					Toast.makeText(context, context.getString(R.string.txt_search_empty), Toast.LENGTH_SHORT).show();
				}
			}
		}else if(view == bt_home){
			current_position = 0;
			start_index = 1;
			loadingMore = true;
			exeFlag = false;
			
			list = new ArrayList<Main_Data>();
			list.clear();
			edit_searcher.setText("");
			num = "1507";
			displaylist();
			bt_category.setText(context.getString(R.string.app_name));
			
			settings = getSharedPreferences(context.getString(R.string.txt_main_activity36), MODE_PRIVATE);
			edit = settings.edit();
			pref = getSharedPreferences(context.getString(R.string.txt_main_activity36), Activity.MODE_PRIVATE);
			category_which = pref.getInt("category_which", 0);
			edit.putInt("category_which", 0);
			edit.commit();
		}else if(view == bt_intent_favorite){
			Intent intent = new Intent(this, FavoriteActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(view == bt_plus_coin){
			alertDialog = new AlertDialog.Builder(this)
		    .setTitle(context.getString(R.string.txt_plus_coin_ment1))
			.setIcon(R.drawable.bt_plus_coin_normal)
			.setMessage(context.getString(R.string.txt_plus_coin_ment2))
			.setPositiveButton(context.getString(R.string.txt_alert_button_yes), new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(context, context.getString(R.string.txt_coin_ment), Toast.LENGTH_LONG).show();
					addInterstitialView();
				}
			})
			.setNegativeButton(context.getString(R.string.txt_alert_button_no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		ArrayList<String> array_videoid = new ArrayList<String>();
		ArrayList<String> array_subject = new ArrayList<String>();
		ArrayList<String> array_portal = new ArrayList<String>();
		Main_Data main_data = (Main_Data)main_adapter.getItem(position);
		String videoid = main_data.id;
		String subject = main_data.title;
		String portal = main_data.portal;
		array_videoid.add(videoid);
		array_subject.add(subject);
		array_portal.add(portal);
		if(array_videoid.size() != 0){
			Intent intent = new Intent(context, CustomYoutubePlayer.class);
			intent.putExtra("array_videoid", array_videoid);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if(view == listview_main){
			if(totalItemCount != 0 && firstVisibleItem  > 1 ){
				listview_main.setFastScrollEnabled(true);
			}else{
				listview_main.setFastScrollEnabled(false);
			}
		}
	}
	
	public void Retry_AlertShow(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setCancelable(false);
		builder.setMessage(msg);
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton(context.getString(R.string.txt_main_activity14), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				 current_position = 0;
            	 loadingMore = true;
            	 exeFlag = false;
            	 main_parseAsync = new Main_ParseAsync();
            	 main_parseAsync.execute();
            	 dialog.dismiss();
			}
		});
		builder.setNegativeButton(context.getString(R.string.txt_main_activity13), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
             	 dialog.dismiss();
			}
		});
		AlertDialog myAlertDialog = builder.create();
		if(retry_alert) myAlertDialog.show();
	}
	
	public static void setNotification_continue(Context context, ArrayList<String> array_music, ArrayList<String> array_videoid, ArrayList<String> array_playtime, ArrayList<String> array_imageurl, ArrayList<String> array_artist, int video_num) {
//    	notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//    	notification = new Notification(android.R.drawable.stat_notify_error, array_music.get(video_num) ,System.currentTimeMillis());
//    	Intent intent = new Intent(context, ContinueMediaPlayer.class);
//    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("array_music", array_music);
//		intent.putExtra("array_videoid", array_videoid);
//		intent.putExtra("array_playtime", array_playtime);
//		intent.putExtra("array_imageurl", array_imageurl);
//		intent.putExtra("array_artist", array_artist);
//		intent.putExtra("video_num", video_num);
//    	PendingIntent content = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    	notification.defaults |= Notification.DEFAULT_LIGHTS;
//    	notification.defaults |= Notification.DEFAULT_SOUND;
//    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
//    	notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//    	notification.flags |= Notification.FLAG_ONGOING_EVENT;
//    	notification.icon = R.drawable.icon128;
//    	notification.setLatestEventInfo(context, context.getString(R.string.app_name) , array_music.get(video_num) + " - " + array_artist.get(video_num), content);
//    	notificationManager.notify(noti_state,notification);
    }
	
	public static void setNotification_Cancel(){
    	if(notificationManager != null) notificationManager.cancel(noti_state);
    }
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
//			Toast.makeText(context, context.getString(R.string.txt_main_activity6) , Toast.LENGTH_SHORT).show();
			addInterstitialView();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//** BannerAd 占싱븝옙트占쏙옙 *************
	@Override
	public void onClickedAd(String arg0, com.admixer.AdView arg1) {
	}

	@Override
	public void onFailedToReceiveAd(int arg0, String arg1,
			com.admixer.AdView arg2) {
	}

	@Override
	public void onReceivedAd(String arg0, com.admixer.AdView arg1) {
	}
	//** CustomPopup 占싱븝옙트占쏙옙 *************
	@Override
	public void onCloseCustomPopup(String arg0) {
	}

	@Override
	public void onHasNoCustomPopup() {
	}

	@Override
	public void onShowCustomPopup(String arg0) {
	}

	@Override
	public void onStartedCustomPopup() {
	}

	@Override
	public void onWillCloseCustomPopup(String arg0) {
	}

	@Override
	public void onWillShowCustomPopup(String arg0) {
	}

	@Override
	public void onInterstitialAdClosed(InterstitialAd arg0) {
		interstialAd = null;
		PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
		finish();
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1,InterstitialAd arg2) {
		interstialAd = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(context.getString(R.string.app_name));
		builder.setMessage(context.getString(R.string.txt_finish_ment));
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton(context.getString(R.string.txt_finish_yes), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
				finish();
			}
		});
		builder.setNegativeButton(context.getString(R.string.txt_finish_no), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
             	 dialog.dismiss();
			}
		});
		AlertDialog myAlertDialog = builder.create();
		if(retry_alert) myAlertDialog.show();
	}

	@Override
	public void onInterstitialAdReceived(String arg0, InterstitialAd arg1) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdShown(String arg0, InterstitialAd arg1) {
	}

	@Override
	public void onLeftClicked(String arg0, InterstitialAd arg1) {
	}

	@Override
	public void onRightClicked(String arg0, InterstitialAd arg1) {
		finish();
	}
}
