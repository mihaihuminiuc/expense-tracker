package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.vinsol.expensetracker.utils.FileDelete;

public class ShowCameraActivity extends Activity implements OnClickListener {

	private RelativeLayout dateBarRelativeLayout;
	private Button show_text_voice_camera_delete;
	private DatabaseAdapter mDatabaseAdapter;
	private ImageView show_text_voice_camera_image_display;
	private TextView show_text_voice_camera_header_title;
	private LinearLayout show_text_voice_camera_camera_details;
	private Button show_text_voice_camera_edit;
	private Bundle intentExtras;
	private ArrayList<String> mShowList;
	private Long _id = null;
	private static final int EDIT_RESULT = 35;
	protected static String favID = null;
	private FavoriteHelper mFavoriteHelper;
	private ShowHelper mShowHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_text_voice_camera);

		// ///// ****** Assigning memory ******* /////////
		dateBarRelativeLayout = (RelativeLayout) findViewById(R.id.show_text_voice_camera_date_bar); 
		show_text_voice_camera_delete = (Button) findViewById(R.id.show_text_voice_camera_delete);
		show_text_voice_camera_image_display = (ImageView) findViewById(R.id.show_text_voice_camera_image_display);
		show_text_voice_camera_header_title = (TextView) findViewById(R.id.show_text_voice_camera_header_title);
		show_text_voice_camera_camera_details = (LinearLayout) findViewById(R.id.show_text_voice_camera_camera_details);
		show_text_voice_camera_edit = (Button) findViewById(R.id.show_text_voice_camera_edit);
		mDatabaseAdapter = new DatabaseAdapter(this);
		
		
		dateBarRelativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.date_bar_bg_wo_shadow));
		
		// //////********* Get id from intent extras ******** ////////////

		setGraphicsCamera();

		intentExtras = getIntent().getBundleExtra("cameraShowBundle");
		mShowHelper = new ShowHelper(this, intentExtras,R.string.voice,R.string.finished_voiceentry,R.string.unfinished_voiceentry);
		if (intentExtras.containsKey("mDisplayList")) {
			getData();
			File mFile = new File("/sdcard/ExpenseTracker/" + _id+ "_small.jpg");
			if (mFile.canRead()) {
				Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
				if(mDrawable.getIntrinsicHeight() > mDrawable.getIntrinsicWidth()) {
					final float scale = this.getResources().getDisplayMetrics().density;
					int width = (int) (84 * scale + 0.5f);
					int height = (int) (111 * scale + 0.5f);
					show_text_voice_camera_image_display.setLayoutParams(new LayoutParams(width, height));
				}
				
				show_text_voice_camera_image_display.setImageDrawable(mDrawable);
			} else {
				show_text_voice_camera_image_display.setImageResource(R.drawable.no_image_small);
			}
			mFavoriteHelper = new FavoriteHelper(this, mShowList);
		}

		show_text_voice_camera_image_display.setOnClickListener(this);
		show_text_voice_camera_delete.setOnClickListener(this);
		show_text_voice_camera_edit.setOnClickListener(this);

	}

	private void setGraphicsCamera() {
		// ///// ***** Sets Title Camera Entry *********///////
		show_text_voice_camera_header_title.setText("Camera Entry");

		// //// ****** Shows Camera Details ********////////
		show_text_voice_camera_camera_details.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.show_text_voice_camera_image_display) {
			if (_id != null) {
				
				Intent intent = new Intent(this, ImagePreview.class);
				intent.putExtra("id", _id);
				startActivity(intent);

			} else {
				Toast.makeText(this, "Error Opening Image", Toast.LENGTH_SHORT).show();
			}
		}

		if (v.getId() == R.id.show_text_voice_camera_delete) {
			if (_id != null) {
				new FileDelete(_id);

				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(_id));
				mDatabaseAdapter.close();
				Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}

		}
		
		if(v.getId() == R.id.show_text_voice_camera_edit){
			Intent editIntent = new Intent(this, CameraActivity.class);
			intentExtras.putBoolean("isFromShowPage", true);
			mShowList.set(4, favID);
			intentExtras.remove("mDisplayList");
			intentExtras.putStringArrayList("mDisplayList", mShowList);
			editIntent.putExtra("cameraBundle", intentExtras);
			startActivityForResult(editIntent,EDIT_RESULT);
		}

	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (EDIT_RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				intentExtras = data.getBundleExtra("cameraShowBundle");
				mShowHelper.doTaskOnActivityResult(intentExtras);
				if (intentExtras.containsKey("mDisplayList")) {
					getData();
					File mFile = new File("/sdcard/ExpenseTracker/" + _id+ "_small.jpg");
					if (mFile.canRead()) {Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
						if(mDrawable.getIntrinsicHeight() > mDrawable.getIntrinsicWidth()) {
							final float scale = this.getResources().getDisplayMetrics().density;
							int width = (int) (84 * scale + 0.5f);
							int height = (int) (111 * scale + 0.5f);
							show_text_voice_camera_image_display.setLayoutParams(new LayoutParams(width, height));
						}
						show_text_voice_camera_image_display.setImageDrawable(mDrawable);
					} else {
						show_text_voice_camera_image_display.setImageResource(R.drawable.no_image_small);
					}
					mFavoriteHelper.setShowList(mShowList);
				}

				show_text_voice_camera_image_display.setOnClickListener(this);
				show_text_voice_camera_delete.setOnClickListener(this);
				show_text_voice_camera_edit.setOnClickListener(this);
			}
		}
		

		if(resultCode == Activity.RESULT_CANCELED){
			finish();
		}
	}
	
	private void getData(){
		favID = mShowHelper.getFavID();
		_id = mShowHelper.getId();
		mShowList = mShowHelper.getShowList();
	}

}
