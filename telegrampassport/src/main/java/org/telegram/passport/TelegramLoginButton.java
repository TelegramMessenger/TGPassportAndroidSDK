package org.telegram.passport;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

/**
 * A {@link android.widget.Button} for use with Telegram login.
 * <br/>
 * Please note that this button doesn't perform any actions on its own. You're supposed to set an {@link android.view.View.OnClickListener} on it to perform actual login.
 */
public class TelegramLoginButton extends Button{

	private float dp=getResources().getDisplayMetrics().density;
	private DynamicRoundRectDrawable background;
	private DynamicRoundRectDrawable overlay;

	public TelegramLoginButton(Context context){
		super(context);
		init(null);
	}

	public TelegramLoginButton(Context context, AttributeSet attrs){
		super(context, attrs);
		init(attrs);
	}

	public TelegramLoginButton(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs){
		background=new DynamicRoundRectDrawable();
		setTextColor(0xFFFFFFFF);
		setText(R.string.PassportSDK_LogInWithTelegram);
		setAllCaps(false);
		setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		setGravity(Gravity.CENTER);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
			setElevation(0);
			setStateListAnimator(null);
			setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

			ColorStateList stateList=new ColorStateList(new int[][]{{android.R.attr.state_pressed},{}}, new int[]{0x20000000, 0x20000000});
			RippleDrawable ripple=new RippleDrawable(stateList, background, background);
			setBackgroundDrawable(ripple);
		}else{
			setTypeface(Typeface.DEFAULT_BOLD);

			overlay=new DynamicRoundRectDrawable();
			overlay.setColor(0x18000000);
			StateListDrawable stateList=new StateListDrawable();
			stateList.addState(new int[]{android.R.attr.state_pressed}, overlay);
			stateList.addState(new int[]{}, new ColorDrawable(0));
			stateList.setExitFadeDuration(250);
			stateList.setEnterFadeDuration(100);
			LayerDrawable layerList=new LayerDrawable(new Drawable[]{background, stateList});
			setBackgroundDrawable(layerList);
		}
		setPadding(Math.round(16*dp), 0, Math.round(21*dp), 0);
		if(attrs!=null){
			TypedArray a=getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TelegramLoginButton, 0, 0);
			try{
				float roundness=a.getFloat(R.styleable.TelegramLoginButton_cornerRoundness, .3f);
				setCornerRoundness(roundness);
			}finally{
				a.recycle();
			}
		}
		setCompoundDrawablesWithIntrinsicBounds(R.drawable.telegram_logo, 0, 0, 0);
		setCompoundDrawablePadding(Math.round(16*dp));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, Math.round(47*dp) | MeasureSpec.EXACTLY);
	}

	/**
	 * Set the roundness of the button corners. Default value is 0.3.
	 * @param roundness the amount of corner roundness, where 0 is a completely straight corner and 1 is completely round.
	 */
	public void setCornerRoundness(float roundness){
		background.setRadiusPercent(roundness);
		if(overlay!=null)
			overlay.setRadiusPercent(roundness);
	}

	private class DynamicRoundRectDrawable extends Drawable{

		private RectF rect=new RectF();
		private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		private float radiusPercent=0f;
		private int initialAlpha=255;

		public DynamicRoundRectDrawable(){
			paint.setColor(0xFF349ff3);
		}

		@Override
		public void draw(Canvas canvas){
			rect.set(getBounds());
			float radius=rect.height()/2f*radiusPercent;
			canvas.drawRoundRect(rect, radius, radius, paint);
		}

		@Override
		public void setAlpha(int alpha){
			paint.setAlpha(Math.round(initialAlpha*alpha/255f));
			invalidateSelf();
		}

		@Override
		public void setColorFilter(ColorFilter colorFilter){

		}

		@Override
		public int getOpacity(){
			return 0;
		}

		public void setColor(int color){
			paint.setColor(color);
			initialAlpha=paint.getAlpha();
		}

		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		@Override
		public void getOutline(Outline outline){
			outline.setRoundRect(getBounds(), getBounds().height()/2f*radiusPercent);
		}

		public void setRadiusPercent(float radiusPercent){
			this.radiusPercent=radiusPercent;
			invalidateSelf();
		}
	}
}
