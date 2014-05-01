package com.sukohi.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.FontAwesomeText;
import com.beardedhen.androidbootstrap.R;

public class BootstrapSlideButton extends FrameLayout {

	public static final int STATE_CLOSE = 0;
	public static final int STATE_RIGHT = 1;
	public static final int STATE_LEFT = 2;
	private int currentState = STATE_CLOSE;
	private int prevState;
	private int prevX;
	private int siledeWidthLeft = -1;
	private int siledeWidthRight = -1;
	private boolean initFlag = true;
	private boolean movableFlag = true;
	private boolean restorableFlag = false;
	private LinearLayout slideLinearLayout;
	private TextView rightTextView, centerTextView, leftTextView;
	private FontAwesomeText rightFontAwesomeText, leftFontAwesomeText, rightHideFontAwesomeText, leftHideFontAwesomeText;
	private Context context;
	private View layout;
	private BootstrapSlideButtonSlideCallback rightCallback, leftCallback;
	private TypeColor currentTypeColor;
	private enum TypeColor {
		
		DEFAULT("default", R.color.bbutton_default, R.color.bbutton_default_pressed), 
		PRIMARY("primary", R.color.bbutton_primary, R.color.bbutton_primary_pressed), 
		SUCCESS("success", R.color.bbutton_success, R.color.bbutton_success_pressed), 
		INFO("info", R.color.bbutton_info, R.color.bbutton_info_pressed), 
		WARNING("warning", R.color.bbutton_warning, R.color.bbutton_warning_pressed), 
		DANGER("danger", R.color.bbutton_danger, R.color.bbutton_danger_pressed), 
		INVERSE("inverse", R.color.bbutton_inverse, R.color.bbutton_inverse_pressed);

		private String symbol;
		private int defaultColorResourceId;
		private int pressedColorResourceId;
		
		private TypeColor(String symbol, int defaultColorResourceId, int pressedColorResourceId) {
			
			this.symbol = symbol;
			this.defaultColorResourceId = defaultColorResourceId;
			this.pressedColorResourceId = pressedColorResourceId;
			
		}
		
	}
	private SlideMode currentSlideMode;
	public enum SlideMode {
		
		RIGHT, 
		LEFT, 
		BOTH;
		
	}
	
	public BootstrapSlideButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initialize(attrs);
	}

	public BootstrapSlideButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initialize(attrs);
	}

	public BootstrapSlideButton(Context context) {
		super(context);
		this.context = context;
		initialize(null);
	}
	
	private void initialize(AttributeSet attrs) {
		
        layout = LayoutInflater.from(context).inflate(R.layout.bootstrap_slide_button, this);
        slideLinearLayout = (LinearLayout) layout.findViewById(R.id.slide_linearlayout);
        slideLinearLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(initFlag || (!movableFlag && !restorableFlag)) {
					
					return true;
					
				}
				
				int x = (int) event.getRawX();
				int left = slideLinearLayout.getLeft() + (x - prevX);

				switch (event.getAction()) {
				
				case MotionEvent.ACTION_UP:

					if(left > -siledeWidthRight && left < siledeWidthLeft) {

						if(left != 0) {

							_clear(true);
							
						} else {

							_clear(false);
							
						}
						
					}
					
					break;
				
				case MotionEvent.ACTION_MOVE:
					
					if(left < 0) {
						
						if(currentSlideMode == SlideMode.RIGHT) {

							left = 0;
							currentState = STATE_CLOSE;
							
						} else if(left < -siledeWidthRight) {

							left = -siledeWidthRight;
							movableFlag = false;
							slideLinearLayout.setBackgroundColor(getResources().getColor(currentTypeColor.pressedColorResourceId));
							
							if(leftCallback != null) {
								
								leftCallback.result();
								
							}
							
							currentState = STATE_RIGHT;
							
						}
						
					} else if(left > 0) {
						
						if(currentSlideMode == SlideMode.LEFT) {

							left = 0;
							currentState = STATE_CLOSE;
							
						} else if(left > siledeWidthLeft) {

							left = siledeWidthLeft;
							movableFlag = false;
							slideLinearLayout.setBackgroundColor(getResources().getColor(currentTypeColor.pressedColorResourceId));
							
							if(rightCallback != null) {
								
								rightCallback.result();
								
							}
							
							currentState = STATE_LEFT;
							
						}
						
					}
					
					slideLinearLayout.layout(left, 0, left + slideLinearLayout.getWidth(), slideLinearLayout.getHeight());
					
					if(currentState != prevState && currentState != STATE_CLOSE) {

						prevState = currentState;
						playTickSound(v);
						
					}
					
					break;
					
				}

				prevX = x;
				return true;
				
			}
			
		});
        
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BootstrapSlideButton);
        rightTextView = (TextView) layout.findViewById(R.id.textview_right);
        centerTextView = (TextView) layout.findViewById(R.id.textview_center);
        leftTextView = (TextView) layout.findViewById(R.id.textview_left);
        rightFontAwesomeText = (FontAwesomeText) layout.findViewById(R.id.fontawesome_right);
        leftFontAwesomeText = (FontAwesomeText) layout.findViewById(R.id.fontawesome_left);
        rightHideFontAwesomeText = (FontAwesomeText) layout.findViewById(R.id.fontawesome_hide_right);
        leftHideFontAwesomeText = (FontAwesomeText) layout.findViewById(R.id.fontawesome_hide_left);
        
		//defaults
		
        int paddingTop = 0;
        int paddingRight = 0;
        int paddingBottom = 0;
        int paddingLeft = 0;
		float textSize = 16f;
		String bootstrapType = "default";
		String rightIcon = "fa-question";
		String leftIcon = "fa-question";
		SlideMode slideMode = SlideMode.BOTH;
		
		//attribute values

		if (a.getString(R.styleable.BootstrapSlideButton_bsb_type) != null) {
			bootstrapType = a.getString(R.styleable.BootstrapSlideButton_bsb_type);
		}
		
		if (a.getString(R.styleable.BootstrapSlideButton_bsb_slide_mode) != null) {
			String slideModeText = a.getString(R.styleable.BootstrapSlideButton_bsb_slide_mode);
			
			if(slideModeText.equals("right")) {
				
				slideMode = SlideMode.RIGHT;
				
			} else if(slideModeText.equals("left")) {

				slideMode = SlideMode.LEFT;
				
			}
			
		}

		if(a.getString(R.styleable.BootstrapSlideButton_bsb_right_text) != null) {
			String rightText = a.getString(R.styleable.BootstrapSlideButton_bsb_right_text);
			setRightText(rightText);
		}

		if(a.getString(R.styleable.BootstrapSlideButton_bsb_center_text) != null) {
			String centerText = a.getString(R.styleable.BootstrapSlideButton_bsb_center_text);
			setCenterText(centerText);
		}

		if(a.getString(R.styleable.BootstrapSlideButton_bsb_left_text) != null) {
			String leftText = a.getString(R.styleable.BootstrapSlideButton_bsb_left_text);
			setLeftText(leftText);
		}

		if(a.getString(R.styleable.BootstrapSlideButton_bsb_right_icon) != null) {
			rightIcon = a.getString(R.styleable.BootstrapSlideButton_bsb_right_icon);
		}

		if(a.getString(R.styleable.BootstrapSlideButton_bsb_left_icon) != null) {
			leftIcon = a.getString(R.styleable.BootstrapSlideButton_bsb_left_icon);
		}

		if(a.getString(R.styleable.BootstrapSlideButton_bsb_text_size) != null) {
			textSize = a.getFloat(R.styleable.BootstrapSlideButton_bsb_text_size, textSize);
		}
        
		if(a.getString(R.styleable.BootstrapSlideButton_bsb_padding_top) != null) {
			paddingTop = a.getInt(R.styleable.BootstrapSlideButton_bsb_padding_top, paddingTop);
		}
        
		if(a.getString(R.styleable.BootstrapSlideButton_bsb_padding_right) != null) {
			paddingRight = a.getInt(R.styleable.BootstrapSlideButton_bsb_padding_right, paddingRight);
		}
        
		if(a.getString(R.styleable.BootstrapSlideButton_bsb_padding_bottom) != null) {
			paddingBottom = a.getInt(R.styleable.BootstrapSlideButton_bsb_padding_bottom, paddingBottom);
		}
        
		if(a.getString(R.styleable.BootstrapSlideButton_bsb_padding_left) != null) {
			paddingLeft = a.getInt(R.styleable.BootstrapSlideButton_bsb_padding_left, paddingLeft);
		}
        
		if(a.getString(R.styleable.BootstrapSlideButton_bsb_padding) != null) {
			paddingTop = a.getInt(R.styleable.BootstrapSlideButton_bsb_padding, paddingTop);
			paddingRight = a.getInt(R.styleable.BootstrapSlideButton_bsb_padding, paddingRight);
			paddingBottom = a.getInt(R.styleable.BootstrapSlideButton_bsb_padding, paddingBottom);
			paddingLeft = a.getInt(R.styleable.BootstrapSlideButton_bsb_padding, paddingLeft);
		}
        
		if(a.getString(R.styleable.BootstrapSlideButton_bsb_restorable) != null) {
			restorableFlag = a.getBoolean(R.styleable.BootstrapSlideButton_bsb_restorable, false);
		}
		
		a.recycle();
		
		setType(bootstrapType);
		setSlideMode(slideMode);
		setTextSize(textSize);
		setRightIcon(rightIcon);
		setLeftIcon(leftIcon);
		setSlidePadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		
	}
	
	public int getState() {
		
		return currentState;
		
	}
	
	public void setType(String bootstrapType) {
		
		for (TypeColor typeColor : TypeColor.values()) {
			
			if(bootstrapType.equals(typeColor.symbol)) {

				currentTypeColor = typeColor;
				
			}
			
		}
		
		slideLinearLayout.setBackgroundColor(getResources().getColor(currentTypeColor.defaultColorResourceId));
		setBackgroundColor(getResources().getColor(currentTypeColor.pressedColorResourceId));
		
	}
	
	public void setSlideMode(SlideMode mode) {
		
		currentSlideMode = mode;
		
		if(mode == SlideMode.BOTH) {
		
			rightFontAwesomeText.setVisibility(View.VISIBLE);
			rightTextView.setVisibility(View.VISIBLE);
			leftFontAwesomeText.setVisibility(View.VISIBLE);
			leftTextView.setVisibility(View.VISIBLE);
			
		} else if(mode == SlideMode.RIGHT) {
		
			rightFontAwesomeText.setVisibility(View.VISIBLE);
			rightTextView.setVisibility(View.VISIBLE);
			leftFontAwesomeText.setVisibility(View.GONE);
			leftTextView.setVisibility(View.GONE);
			
		} else if(mode == SlideMode.LEFT) {
		
			rightFontAwesomeText.setVisibility(View.GONE);
			rightTextView.setVisibility(View.GONE);
			leftFontAwesomeText.setVisibility(View.VISIBLE);
			leftTextView.setVisibility(View.VISIBLE);
			
		}
		
	}
	
	public void setRightText(String text) {
		
		rightTextView.setText(text);
		
	}
	
	public void setCenterText(String text) {
		
		centerTextView.setText(text);
		
	}
	
	public void setLeftText(String text) {
		
		leftTextView.setText(text);
		
	}
	
	public void setTextSize(float size) {

        rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        centerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        rightFontAwesomeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        leftFontAwesomeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        rightHideFontAwesomeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        leftHideFontAwesomeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		
	}
	
	public void setRightIcon(String icon) {
		
		rightHideFontAwesomeText.setIcon(icon);
		
	}
	
	public void setLeftIcon(String icon) {

		leftHideFontAwesomeText.setIcon(icon);
		
	}
	
	public void setSlidePadding(int left, int top, int right, int bottom) {
		
		slideLinearLayout.setPadding(left, top, right, bottom);
		
	}
	
	public void clear() {
		
		_clear(true);
		
	}
	
	private void _clear(boolean soundFlag) {
		
		if(soundFlag && 
				(currentState == STATE_RIGHT || currentState == STATE_LEFT)) {
			
			playTickSound(slideLinearLayout);
			
		}

		slideLinearLayout.layout(0, 0, slideLinearLayout.getWidth(), slideLinearLayout.getHeight());
		slideLinearLayout.setBackgroundColor(getResources().getColor(currentTypeColor.defaultColorResourceId));
		currentState = STATE_CLOSE;
		
	}
	
	public void setOnRightSlideListener(BootstrapSlideButtonSlideCallback callback) {
		
		rightCallback = callback;
		
	}
	
	public void setOnLeftSlideListener(BootstrapSlideButtonSlideCallback callback) {
		
		leftCallback = callback;
		
	}
	
	public static class BootstrapSlideButtonSlideCallback {
		
		public void result() {}
		
	}
	
	private void playTickSound(View v) {
		
		v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		if(initFlag) {
			
			initFlag = false;
			FontAwesomeText fontAwesomeTextHideLeft = (FontAwesomeText) layout.findViewById(R.id.fontawesome_hide_left);
			siledeWidthLeft = fontAwesomeTextHideLeft.getWidth();
			FontAwesomeText fontAwesomeTextHideRight = (FontAwesomeText) layout.findViewById(R.id.fontawesome_hide_right);
			siledeWidthRight = fontAwesomeTextHideRight.getWidth();
			
		}
		
	}
	
}
/*** Example

	// XML

    <com.sukohi.lib.BootstrapSlideButton 
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        bootstrap:bsb_type="success"
        bootstrap:bsb_slide_mode="both"
        bootstrap:bsb_right_text="Right Text"
        bootstrap:bsb_center_text="Center Text"
        bootstrap:bsb_left_text="Left Text"
        bootstrap:bsb_text_size="16"
        bootstrap:bsb_right_icon="fa-floppy-o"
        bootstrap:bsb_left_icon="fa-times-circle-o"
        bootstrap:bsb_restorable="true"
        bootstrap:bsb_padding="10"
        bootstrap:bsb_padding_top="10"
        bootstrap:bsb_padding_right="10"
        bootstrap:bsb_padding_bottom="10"
        bootstrap:bsb_padding_left="10" />

	// Code

	BootstrapSlideButton bootstrapSlideButton = new BootstrapSlideButton(this);
	bootstrapSlideButton.setType("success");
	bootstrapSlideButton.setRightText("Right Text");
	bootstrapSlideButton.setCenterText("Center Text");
	bootstrapSlideButton.setLeftText("Left Text");
	bootstrapSlideButton.setTextSize(16F);
	bootstrapSlideButton.setRightIcon("fa-question");
	bootstrapSlideButton.setLeftIcon("fa-question");
	bootstrapSlideButton.setSlideMode(BootstrapSlideButton.SlideMode.BOTH);	// or RIGHT LEFT
	bootstrapSlideButton.setSlidePadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    bootstrapSlideButton.setOnRightSlideListener(new BootstrapSlideButtonSlideCallback(){
    	
    	@Override
    	public void result() {
    		
    		// Something...
    		
    	}
    	
    });
    bootstrapSlideButton.setOnLeftSlideListener(new BootstrapSlideButtonSlideCallback(){
    	
    	@Override
    	public void result() {
    		
    		// Something...
    		
    	}
    	
    });
    
	switch (bootstrapSlideButton.getState()) {
	case BootstrapSlideButton.STATE_CLOSE:
		// close
		break;
	case BootstrapSlideButton.STATE_RIGHT:
		// right
		break;
	case BootstrapSlideButton.STATE_LEFT:
		// left
		break;

	default:
		break;
	}
	
	bootstrapSlideButton.clear();
    
***/
