package com.hengxuan.eht.massager.MyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.eht.massager.R;

public class CircleButtons extends RelativeLayout{

	int count = 8;
//	int mWidth, mHeight;
	int[] bgDrawables;
	float r,btnW;
	float pivotX, pivotY;
	
	//group radio button
	//��¼ǰһ�ΰ��µİ�ť
	int preBtnId = -1;
	int[] selectedBgs;
	Button[] buttons;

    private onBtnsClickListener mOnBtnsClickListener;
    public interface onBtnsClickListener{
        void onBtnsClick(int id);
    }
    public void setOnBtnsClickListener(onBtnsClickListener l){
        mOnBtnsClickListener = l;
    }
	public CircleButtons(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		Log.d("daizhx", "circle construct");
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleButtons, 0, 0);
		
		try{
			r = a.getDimension(R.styleable.CircleButtons_radiuss, 0);
			btnW = a.getDimension(R.styleable.CircleButtons_buttonWidth, 0);
			count = a.getInt(R.styleable.CircleButtons_buttonNum, 0);
		}finally{
			a.recycle();
		}
		pivotX = r + btnW;
        buttons = new Button[count];
	}

	public void addButtons() {
		// TODO Auto-generated method stub
		for (int i = 0; i < count; i++) {
			Button b = new Button(getContext());
			b.setBackgroundResource(bgDrawables[i]);
			LayoutParams rlp = new LayoutParams(
					(int)btnW, (int)btnW);
			rlp.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
			rlp.topMargin = (int) (pivotX - btnW/2) + (int)((r+btnW/2)*Math.cos(((2*Math.PI)/count)*i));
			rlp.leftMargin = (int)(pivotX - btnW/2) - (int)((r+btnW/2)*Math.sin(((2*Math.PI)/count)*i));
			b.setLayoutParams(rlp);
			b.setId(i);
			this.addView(b,i);
            buttons[i] = b;
		}
	}
	
	public void addButtons(boolean isRadioGroup) {
		// TODO Auto-generated method stub
		for (int i = 0; i < count; i++) {
			Button b = new Button(getContext());
			b.setBackgroundResource(bgDrawables[i]);
			LayoutParams rlp = new LayoutParams(
					(int)btnW, (int)btnW);
			rlp.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
			rlp.topMargin = (int) (pivotX - btnW/2) + (int)((r+btnW/2)*Math.cos(((2*Math.PI)/count)*i));
			rlp.leftMargin = (int)(pivotX - btnW/2) - (int)((r+btnW/2)*Math.sin(((2*Math.PI)/count)*i));
			b.setLayoutParams(rlp);
			b.setId(i);
			if(isRadioGroup){
				b.setOnClickListener(listener);
			}
			addView(b, i);
            buttons[i] = b;
		}
	}

    /**
     * 设置可以点击，单选
     */
    public void setCheckEnable(){
        for(int i=0; i<buttons.length;i++){
            buttons[i].setOnClickListener(listener);
        }
    }

    /**
     * 重置按钮的状态
     */
    public void resetBtns(){
        if(preBtnId >= 0){
            getChildAt(preBtnId).setBackgroundResource(bgDrawables[preBtnId]);
            preBtnId = -1;
        }
    }
	Listener listener = new Listener();
	public final class Listener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if(id != preBtnId){
				v = getChildAt(id);
				v.setBackgroundResource(selectedBgs[id]);
				if(preBtnId >= 0){
					getChildAt(preBtnId).setBackgroundResource(bgDrawables[preBtnId]);
				}
				preBtnId = id;
			}
            //回调接口
            if(mOnBtnsClickListener != null){
                mOnBtnsClickListener.onBtnsClick(id);
            }
		}
		
	}

    public void setChecked(int index){
        resetBtns();
        View v = getChildAt(index);
        v.setBackgroundResource(selectedBgs[index]);
        preBtnId = index;
    }
	/*
	 * ��addButtons()֮ǰ����
	 */
	public void setButtonsBg(int[] bgs) {
		bgDrawables = bgs;
	}
	public void setSelectedBgs(int[] bgs){
		selectedBgs = bgs;
	}
	public void setCount(int c){
		count = c;
	}
	public void setRadius(int r){
		this.r = r;
	}
	public void setButtonWidth(int w){
		btnW = w;
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		Log.d("daizhx", "circle Measure--" + widthMeasureSpec + ", "
				+ heightMeasureSpec);
//		measure(widthMeasureSpec, heightMeasureSpec);
//		int w = getMeasuredWidth();
//		int h = getMeasuredHeight();
//		Log.d("daizhx", "w = "+ w+",h="+h);
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int mW = getMeasuredWidth();
		int mH = getMeasuredHeight();
		Log.d("daizhx", "mW = "+ mW + "mH =" + mH);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}


    /**
     * 返回内圆半径
     * @return
     */
    public float getInnerRadius(){
        return r;
    }
}
