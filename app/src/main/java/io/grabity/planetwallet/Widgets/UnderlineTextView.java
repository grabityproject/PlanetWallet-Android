package io.grabity.planetwallet.Widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import io.grabity.planetwallet.R;


@SuppressLint("AppCompatCustomView")
public class UnderlineTextView extends TextView {

    private int UnderlineColor;
    private int UnderlineWidth;

    private Paint paint;

    private float width , height;

    public UnderlineTextView( Context context) {
        super(context);

        UnderlineColor = Color.TRANSPARENT;
        UnderlineWidth = 0;
    }

    public UnderlineTextView( Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UnderlineTextView( Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes( attrs , R.styleable.UnderlineTextView , defStyleAttr , 0);

        UnderlineColor = typedArray.getColor( R.styleable.UnderlineTextView_underlineColor , Color.TRANSPARENT);
        UnderlineWidth = typedArray.getDimensionPixelSize( R.styleable.UnderlineTextView_underlineWidth,0);

        typedArray.recycle();
        this.viewInit();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void viewInit(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setUnderlineText(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(UnderlineColor);
        paint.setStrokeWidth(UnderlineWidth);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        /**
         * 텍스트뷰에 밑줄을 그려준다.
         * drawLine 매개변수 startX , startY , stopX , stopY , paint
         * 텍스트뷰의 앞부분 startX 부터 텍스트뷰의 길이만큼 그려주고
         * 그리는 위치는 텍스트뷰의 하단부(height)로 그린다.
         */



        canvas.drawLine(0,height,width,height,paint);

        super.onDraw(canvas);
    }


    public int getUnderlineColor() {
        return UnderlineColor;
    }

    public void setUnderlineColor(int underlineColor) {
        UnderlineColor = underlineColor;
    }

    public int getUnderlineWidth() {
        return UnderlineWidth;
    }

    public void setUnderlineWidth(int underlineWidth) {
        UnderlineWidth = underlineWidth;
    }
}
