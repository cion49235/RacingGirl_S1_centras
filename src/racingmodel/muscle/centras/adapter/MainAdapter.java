package racingmodel.muscle.centras.adapter;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import racingmodel.muscle.centras.R;
import racingmodel.muscle.centras.activity.MainActivity;
import racingmodel.muscle.centras.data.Main_Data;
import racingmodel.muscle.centras.util.ImageLoader;
import racingmodel.muscle.centras.util.RoundedTransform;


public class MainAdapter extends BaseAdapter{
	public Context context;
	public ImageLoader imgLoader;
	public int _id = -1;
	public String id = "empty";
	public Cursor cursor;
	public Button bt_favorite;
	public ArrayList<Main_Data> list;
	public GridView listview_main;
	public MainAdapter(Context context, ArrayList<Main_Data> list, GridView listview_main) {
		this.imgLoader = new ImageLoader(context.getApplicationContext());
		this.context = context;
		this.list = list;
		this.listview_main = listview_main;
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		try{
			if(view == null){	
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				view = layoutInflater.inflate(R.layout.main_acitivty_listrow_sort, parent, false); 
			}
			ImageView img_imageurl = (ImageView)view.findViewById(R.id.img_imageurl);
			img_imageurl.setFocusable(false);
			String image_url = list.get(position).thumbnail_hq;

			Picasso.with(context)
            .load(image_url)
            .placeholder(R.drawable.fanroom_list_thumbnail_001)
            .error(R.drawable.fanroom_list_thumbnail_001)
            .into(img_imageurl);

			TextView txt_music = (TextView)view.findViewById(R.id.txt_music);
			txt_music.setText(list.get(position).title);
			
			bt_favorite = (Button)view.findViewById(R.id.bt_favorite);
			bt_favorite.setFocusable(false);
			bt_favorite.setSelected(false);
			cursor = MainActivity.favorite_mydb.getReadableDatabase().rawQuery(
					"select * from favorite_list where id = '"+list.get(position).id+"'", null);
			if(null != cursor && cursor.moveToFirst()){
				id = cursor.getString(cursor.getColumnIndex("id"));
				_id = cursor.getInt(cursor.getColumnIndex("_id"));
			}else{
				id = "empty";
				_id = -1;
			}
			if(id.equals("empty")){
				bt_favorite.setText(context.getString(R.string.txt_main_activity38));
				bt_favorite.setBackgroundResource(R.drawable.bg_favorite_normal);
			}else{
				bt_favorite.setText(context.getString(R.string.txt_main_activity37));
				bt_favorite.setBackgroundResource(R.drawable.bg_favorite_pressed);	
			}
			
			bt_favorite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cursor = MainActivity.favorite_mydb.getReadableDatabase().rawQuery(
							"select * from favorite_list where id = '"+list.get(position).id+"'", null);
					if(null != cursor && cursor.moveToFirst()){
						id = cursor.getString(cursor.getColumnIndex("id"));
						_id = cursor.getInt(cursor.getColumnIndex("_id"));
					}else{
						id = "empty";
						_id = -1;
					}
					if(id.equals("empty")){
						bt_favorite.setText(context.getString(R.string.txt_main_activity37));
						bt_favorite.setBackgroundResource(R.drawable.bg_favorite_pressed);
						ContentValues cv = new ContentValues();
						cv.put("id", list.get(position).id);
						cv.put("title", list.get(position).title);
						cv.put("portal", list.get(position).portal);
						cv.put("category", list.get(position).category);
						cv.put("thumbnail_hq", list.get(position).thumbnail_hq);
						cv.put("duration", list.get(position).duration);
						MainActivity.favorite_mydb.getWritableDatabase().insert("favorite_list", null, cv);
						MainActivity.main_adapter.notifyDataSetChanged();
						Toast.makeText(context, context.getString(R.string.txt_main_activity11), Toast.LENGTH_SHORT).show();
					}else{
						bt_favorite.setText(context.getString(R.string.txt_main_activity38));
						bt_favorite.setBackgroundResource(R.drawable.bg_favorite_normal);
						MainActivity.favorite_mydb.getWritableDatabase().delete("favorite_list", "_id" + "=" +_id, null);
						MainActivity.main_adapter.notifyDataSetChanged();
						Toast.makeText(context, context.getString(R.string.txt_main_activity12), Toast.LENGTH_SHORT).show();
					}
					
				}
			});
		}catch (Exception e) {
		}finally{
			MainActivity.favorite_mydb.close();
			if(cursor != null){
				cursor.close();	
			}
		}
		return view;
	}
}