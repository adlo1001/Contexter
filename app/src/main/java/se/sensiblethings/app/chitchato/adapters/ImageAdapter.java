package se.sensiblethings.app.chitchato.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

import se.sensiblethings.app.R;

public class ImageAdapter extends BaseAdapter {
	private Context context_;
	protected ArrayList<Integer> imageList = new ArrayList<Integer>();
	protected ArrayList<String> imageList_ = new ArrayList<String>();
	protected int adapter_serial = -1;
	private ImageView iv = null;
	private int index = 0;

	public ImageAdapter(Context c) {
		this.context_ = c;
	}

	public ImageAdapter(Context c, int i) {
		this.context_ = c;
		this.adapter_serial = i;
	}

	public void add(String path) {
		imageList_.add(path);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// /if (!this.imageList.isEmpty() || this.imageList != null)
		// /return imageList.size();
		// /else

		return mIcons.length;

	}

	@Override
	public ImageView getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.getSelectedImage();
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	private Integer[] mIcons = { R.drawable.profile_image,
			R.drawable.profile_image, R.drawable.profile_image,
			R.drawable.profile_image, R.drawable.profile_image,
			R.drawable.profile_image, R.drawable.profile_image,
			R.drawable.profile_image

	};

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageview = null;
		if (this.adapter_serial == -1) {

			if (convertView == null) {

				imageview = new ImageView(context_);
				imageview.setLayoutParams(new GridView.LayoutParams(100, 100));
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageview.setPadding(8, 8, 8, 8);

				// imageview.setPadding(5, 0, 60, 30);

			} else {
				imageview = (ImageView) convertView;

			}

			imageview.setImageResource(mIcons[position]);
		} else if (this.adapter_serial == 0) {

			if (convertView == null) {

				imageview = new ImageView(context_);
				imageview.setLayoutParams(new GridView.LayoutParams(100, 100));
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageview.setPadding(8, 8, 8, 8);

				// imageview.setPadding(5, 0, 60, 30);

			} else {
				imageview = (ImageView) convertView;

			}
			if (this.imageList.isEmpty() || this.imageList == null)
				imageview.setImageResource(mIcons[position]);
			else
				imageview.setImageResource(imageList.get(position));
		}

		if (index == position)
			iv = imageview;
		return imageview;
	}

	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			int reqHeight) {

		Bitmap bm = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	public void setImageList(ArrayList<Integer> al_Image) {
		this.imageList = al_Image;
	}

	public ImageView getSelectedImage() {

		return iv;
	}

	public void setIndex(int i) {
		this.index = i;
	}

}
