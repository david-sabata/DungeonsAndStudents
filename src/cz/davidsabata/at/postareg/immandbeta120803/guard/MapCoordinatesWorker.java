/**
 * 
 */
package cz.davidsabata.at.postareg.immandbeta120803.guard;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import cz.davidsabata.at.postareg.immandbeta120803.R;

/**
 * @author Tomas Sychra
 *
 */
public class MapCoordinatesWorker {

	private final Context context;
	private final RelativeLayout map;
	private final int originalWidth;
	private final int originalHeight;
	private final float wpi;
	private final float hpi;
	private float scaleFactor = 1.0f;

	private float posX = 0.0f;
	private float posY = 0.0f;

	private final float heightCross;
	private final float widthCross;

	//	private final int crossResId;


	public MapCoordinatesWorker(Context c, RelativeLayout v, int idResource, int origWidth, int origHeight) {
		context = c;
		map = v;
		originalWidth = origWidth;
		originalHeight = origHeight;
		//		crossResId = idResource;

		//Detect realsize of image floor
		BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.basement_1st);
		int heightMap = bd.getBitmap().getHeight();
		int widthMap = bd.getBitmap().getWidth();

		//Size of image that i paint on map
		BitmapDrawable bd2 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_launcher);
		heightCross = bd2.getBitmap().getHeight();
		widthCross = bd2.getBitmap().getWidth();

		wpi = widthMap / (float) originalWidth;
		hpi = heightMap / (float) originalHeight;


	}


	/*
	 * Add Icon on place in map 
	 * This place is specified by coordinates with real coordinates in real map
	 */
	public ImageView addCrossToMap(int x, int y, int resId) {
		int id = resId;
		ImageView imageView = new ImageView(context);
		RelativeLayout.LayoutParams vp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		imageView.setLayoutParams(vp);

		imageView.setScaleType(ScaleType.MATRIX);
		imageView.setImageResource(id);
		map.addView(imageView);

		//center image
		//vp.setMargins((int) (x * wpi - (widthCross / 2.0)), (int) (y * hpi - (heightCross / 2.0)), 0, 0);

		Matrix mtrx = new Matrix();
		mtrx.postTranslate((float) (x * scaleFactor * wpi - (widthCross / 2.0)), (float) (y * scaleFactor * hpi - (heightCross / 2.0)));
		imageView.setImageMatrix(mtrx);

		//remember width
		imageView.setTag(R.id.idWidth, (float) (x * scaleFactor * wpi - (widthCross / 2.0)));
		imageView.setTag(R.id.idHeight, (float) (y * scaleFactor * hpi - (heightCross / 2.0)));
		return imageView;

	}

	/*
	 * Get Relative real coordinates from relative x, y
	 */
	public RealCoordinates getRealFromRelativeCoord(float relX, float relY) {
		RealCoordinates realCoord = new RealCoordinates();
		realCoord.setX(relX / scaleFactor / wpi);
		realCoord.setY(relY / scaleFactor / hpi);
		return realCoord;
	}


	public void setScaleFactor(float scaleFactor2) {
		scaleFactor = scaleFactor2;
	}


	public float getScaleFactor() {
		return scaleFactor;
	}


	public float getPosX() {
		return posX;
	}

	public void setPosX(float pPosX) {
		posX = pPosX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float pPosY) {
		posY = pPosY;
	}





}
