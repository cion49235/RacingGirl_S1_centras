package racingmodel.muscle.centras.activity;

import java.util.ArrayList;
import java.util.List;

import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdViewListener;
import com.admixer.InterstitialAd;
import com.admixer.InterstitialAdListener;
import com.admixer.AdView.AdAnimation;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import racingmodel.muscle.centras.R;
import racingmodel.muscle.centras.data.Favorite_DBopenHelper;
import racingmodel.muscle.centras.data.Favorite_Data;
import racingmodel.muscle.centras.mediaplayer.ContinueMediaPlayer;
import racingmodel.muscle.centras.util.ImageLoader;
import racingmodel.muscle.centras.util.RoundedTransform;
import racingmodel.muscle.centras.youtubeplayer.CustomYoutubePlayer;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FavoriteActivity extends Activity implements OnItemClickListener, OnClickListener, AdViewListener, InterstitialAdListener{
	public Favorite_DBopenHelper favorite_mydb;
	public SQLiteDatabase mdb;
	public Cursor cursor;
	public Context context;
	public ConnectivityManager connectivityManger;
	public NetworkInfo mobile;
	public NetworkInfo wifi;
	public static GridView listview_favorite;
	public FavoriteAdapter<Favorite_Data> adapter;
	public static LinearLayout layout_listview_favorite, layout_nodata;
	public int SDK_INT = android.os.Build.VERSION.SDK_INT;
	public static RelativeLayout ad_layout;
	public static TextView txt_favorite_title;
	public static LinearLayout action_layout;
	public static Button bt_all_select, bt_play_video, bt_favorite_delete, bt_home, bt_play_media, bt_plus_coin;
	private AdView adView;
	public SharedPreferences settings,pref;
	public Editor edit;
	public static AlertDialog alertDialog;
	public static com.admixer.InterstitialAd interstialAd;
	private NativeExpressAdView admobNative;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_activity);
		context = this;
		
//		adView = new AdView(this);
//	    adView.setAdUnitId(context.getString(R.string.banner_ad_unit_id));
//	    adView.setAdSize(AdSize.BANNER);
//	    
//	    RelativeLayout layout = (RelativeLayout)findViewById(R.id.ad_layout);
//	    layout.addView(adView);
//	    AdRequest adRequest = new AdRequest.Builder().build();
//	    adView.loadAd(adRequest);
		
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "hdtqihar");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/3138890161");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/4615623362");
		
		addBannerView();
//		init_admob_naive();
		
		layout_listview_favorite = (LinearLayout)findViewById(R.id.layout_listview_favorite);
		layout_nodata = (LinearLayout)findViewById(R.id.layout_nodata);
		action_layout = (LinearLayout)findViewById(R.id.action_layout);
		favorite_mydb = new Favorite_DBopenHelper(context);
		bt_all_select = (Button)findViewById(R.id.bt_all_select);
		bt_play_video = (Button)findViewById(R.id.bt_play_video);
		bt_play_media = (Button)findViewById(R.id.bt_play_media);
		bt_favorite_delete = (Button)findViewById(R.id.bt_favorite_delete);
		bt_plus_coin = (Button)findViewById(R.id.bt_plus_coin);
		bt_home = (Button)findViewById(R.id.bt_home);
		bt_all_select.setOnClickListener(this);
		bt_play_video.setOnClickListener(this);
		bt_play_media.setOnClickListener(this);
		bt_favorite_delete.setOnClickListener(this);
		bt_home.setOnClickListener(this);
		bt_plus_coin.setOnClickListener(this);
		displayList();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
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
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
		txt_favorite_title = (TextView)findViewById(R.id.txt_favorite_title);
		txt_favorite_title.setText(context.getString(R.string.app_name));
		Log.i("dsu", "onRestart");
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
    	if(interstialAd == null) {
        	AdInfo adInfo = new AdInfo("hdtqihar");
//        	adInfo.setTestMode(false);
        	interstialAd = new com.admixer.InterstitialAd(this);
        	interstialAd.setAdInfo(adInfo, this);
        	interstialAd.setInterstitialAdListener(this);
        	interstialAd.startInterstitial();
    	}
    }
	
	public void displayList(){
		List<Favorite_Data>contactsList = getContactsList();
		adapter = new FavoriteAdapter<Favorite_Data>(
    			context, R.layout.favorite_activity_listrow, contactsList);
		listview_favorite = (GridView)findViewById(R.id.listview_favorite);
		listview_favorite.setAdapter(adapter);
		if (SDK_INT >= Build.VERSION_CODES.HONEYCOMB){ //����� ���������� ���� ������ API ���}
			listview_favorite.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    	}
		listview_favorite.setOnItemClickListener(this);
		listview_favorite.setFastScrollEnabled(false);
		listview_favorite.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		if(listview_favorite.getCount() == 0){
			layout_nodata.setVisibility(View.VISIBLE);
			layout_listview_favorite.setVisibility(View.GONE);
		}else{
			layout_nodata.setVisibility(View.GONE);
			layout_listview_favorite.setVisibility(View.VISIBLE);
		}
	}
	
	public List<Favorite_Data> getContactsList() {
		List<Favorite_Data>contactsList = new ArrayList<Favorite_Data>();
		try{
			favorite_mydb = new Favorite_DBopenHelper(context);
			mdb = favorite_mydb.getWritableDatabase();
	        cursor = mdb.rawQuery("select * from favorite_list order by _id desc", null);
	        while (cursor.moveToNext()){
				addContact(contactsList,cursor.getInt(0), cursor.getString(1), cursor.getString(2), 
						cursor.getString(3),cursor.getString(4), cursor.getString(5), cursor.getString(6));
	        }
		}catch (Exception e) {
		}finally{
			cursor.close();
			favorite_mydb.close();
			mdb.close();
		}
		return contactsList;
	}
	
	public void addContact(List<Favorite_Data> contactsList, int _id, String id, String title,String portal, String category, String thumbnail_hq,String duration){
		contactsList.add(new Favorite_Data(_id, id, title, portal, category, thumbnail_hq, duration));
	}
	
	@Override
	public void onClick(View view) {
		if(view == bt_all_select){
			if(bt_all_select.isSelected()){
				bt_all_select.setSelected(false);
				bt_all_select.setText(context.getString(R.string.txt_continue_activity4));
				for(int i=0; i < listview_favorite.getCount(); i++){
					listview_favorite.setItemChecked(i, false);
				}
				action_layout.setVisibility(View.GONE);
			}else{
				bt_all_select.setSelected(true);
				bt_all_select.setText(context.getString(R.string.txt_continue_activity7));
				for(int i=0; i < listview_favorite.getCount(); i++){
					listview_favorite.setItemChecked(i, true);
				}
			}
		}else if(view == bt_play_video){
				SparseBooleanArray sba = listview_favorite.getCheckedItemPositions();
				ArrayList<String> array_videoid = new ArrayList<String>();
				ArrayList<String> array_subject = new ArrayList<String>();
				ArrayList<String> array_portal = new ArrayList<String>();
				if(sba.size() != 0){
						for(int i = 0; i < listview_favorite.getCount(); i++){
							if(sba.get(i)){
								Favorite_Data favorite_data = (Favorite_Data)adapter.getItem(i);
								String videoid = favorite_data.getId();
								String subject = favorite_data.getTitle();
								String portal = favorite_data.getPortal();
								array_videoid.add(videoid);
								array_subject.add(subject);
								array_portal.add(portal);
								sba = listview_favorite.getCheckedItemPositions();
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
				SparseBooleanArray sba = listview_favorite.getCheckedItemPositions();
				ArrayList<String> array_videoid = new ArrayList<String>();
				ArrayList<String> array_subject = new ArrayList<String>();
				ArrayList<String> array_thumb = new ArrayList<String>();
				ArrayList<String> array_portal = new ArrayList<String>();
				ArrayList<String> array_artist = new ArrayList<String>();
				ArrayList<String> array_playtime = new ArrayList<String>();
				if(sba.size() != 0){
				for(int i = listview_favorite.getCount() -1; i>=0; i--){
					if(sba.get(i)){
						Favorite_Data main_data = (Favorite_Data)adapter.getItem(i);
						String videoid = main_data.getId();
						String subject = main_data.getTitle();
						String thumb = main_data.getThumbnail_hq();
						String portal = main_data.getPortal();
						String artist = context.getString(R.string.app_name);
//						String sprit_playtime[] = subject.split("-");
						String playtime = "";
						array_videoid.add(videoid);
						array_subject.add(subject);
						array_thumb.add(thumb);
						array_portal.add(portal);
						array_artist.add(artist);
						array_playtime.add(playtime);
						sba = listview_favorite.getCheckedItemPositions();
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
		}else if(view == bt_favorite_delete){
			SparseBooleanArray sba = listview_favorite.getCheckedItemPositions();
			if(sba.size() != 0){
				for(int i = listview_favorite.getCount() -1; i>=0; i--){
					if(sba.get(i)){
						Favorite_Data favorite_data = (Favorite_Data)adapter.getItem(i);
						int _id = favorite_data.get_id();
						favorite_mydb.getWritableDatabase().delete("favorite_list", "_id" + "=" +_id, null);
						sba = listview_favorite.getCheckedItemPositions();
					}
				}
				displayList();
				if(adapter != null){
					adapter.notifyDataSetChanged();	
				}
				action_layout.setVisibility(View.GONE);
				Toast.makeText(this, context.getString(R.string.txt_favorite_activity1), Toast.LENGTH_SHORT).show();
			}	
			action_layout.setVisibility(View.GONE);
		}else if(view == bt_home){
			onBackPressed();
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
		Favorite_Data favorite_data = (Favorite_Data)adapter.getItem(position);
		String videoid = favorite_data.getId();
		String subject = favorite_data.getTitle();
		String portal = favorite_data.getPortal();
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
	
	public class FavoriteAdapter<T extends Favorite_Data>extends ArrayAdapter<T>{
		public List<T> contactsList;
		ImageLoader imgLoader = new ImageLoader(getApplicationContext());
		public Button bt_favorite_delete;
		public String num = "empty";
		public FavoriteAdapter(Context context, int textViewResourceId, List<T> items) {
			super(context, textViewResourceId, items);
			contactsList = items;
		}
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			try{
				if(view == null){
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.favorite_activity_listrow_sort, null);
				}
				final T contacts = contactsList.get(position);
				TextView txt_favorite_music = (TextView)view.findViewById(R.id.txt_favorite_music);
				txt_favorite_music.setText(contacts.getTitle());
				
				ImageView img_favorite_imageurl = (ImageView)view.findViewById(R.id.img_favorite_imageurl);
				img_favorite_imageurl.setFocusable(false);
				String image_url = contacts.getThumbnail_hq();
				
				Picasso.with(context)
	            .load(image_url)
	            .placeholder(R.drawable.fanroom_list_thumbnail_001)
	            .error(R.drawable.fanroom_list_thumbnail_001)
	            .into(img_favorite_imageurl);
				
				bt_favorite_delete = (Button)view.findViewById(R.id.bt_favorite_delete);
				bt_favorite_delete.setFocusable(false);
				bt_favorite_delete.setSelected(false);
				bt_favorite_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						favorite_mydb.getWritableDatabase().delete("favorite_list", "_id" + "=" +contacts.get_id(), null);
						displayList();
						Toast.makeText(context, context.getString(R.string.txt_favorite_activity1), Toast.LENGTH_SHORT).show();
					}
				});
			}catch (Exception e) {
			}finally{
				cursor.close();
				favorite_mydb.close();
			}
			return view;
		}
	}

	public void AlertShow(String msg) {
        AlertDialog.Builder alert_internet_status = new AlertDialog.Builder(
                 this);
         alert_internet_status.setTitle(context.getString(R.string.txt_favorite_activity2));
         alert_internet_status.setCancelable(false);
         alert_internet_status.setMessage(msg);
         alert_internet_status.setPositiveButton(context.getString(R.string.txt_favorite_activity3),
                 new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss(); // �ݱ�
                         finish();
                     }
                 });
         alert_internet_status.show();
     }
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

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
	
	@Override
	public void onInterstitialAdClosed(InterstitialAd arg0) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1,
			InterstitialAd arg2) {
		interstialAd = null;
		Toast.makeText(context, context.getString(R.string.txt_plus_coin_ment3), Toast.LENGTH_LONG).show();
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
	}
	
}