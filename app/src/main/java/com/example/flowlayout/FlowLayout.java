package com.example.flowlayout;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FlowLayout extends ViewGroup {
    /**
     * java 使用
     * @param context
     */
    public FlowLayout(Context context) {
        super(context);
        init();
    }

    /**
     * xml 使用
     * @param context
     * @param attrs
     */
    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 主题
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 所有的子View
     */
    ArrayList<ArrayList<View>> lineList = new ArrayList<>();
    /**
     * 每一行  的高度
     */
    ArrayList<Integer> heights = new ArrayList<>();

    /**
     * 子view 横向间距
     */
    private int mHorizontalSpacing  = dp2px(10);
    /**
     * 子view 垂直间距
     */
    private int mVerticalSpacing = dp2px(7);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        init();

         // 所有 子View 需要的宽
        int parentNeedWidth = 0;
         // 所有 子View 需要的高
        int parendNeedsHeight = 0;

        int widthMeasure = MeasureSpec.getSize(widthMeasureSpec);
        int heightMeasure = MeasureSpec.getSize(heightMeasureSpec);

        int paddingW = getPaddingLeft() + getPaddingRight();
        int paddingH = getPaddingBottom() + getPaddingBottom();

        //临时变量
        int lineUsedWidth = 0;
        int lineHeight = 0;

        //每一行的view
        ArrayList<View> itemList = new ArrayList<>();

        //1，先测量 子view
        int count = getChildCount();
        for(int i = 0;i < count;i++){
            System.out.println("count "+ i);
            View view = getChildAt(i);
            LayoutParams params = view.getLayoutParams();
            int childParamsWidth =  params.width;
            int childParamsHeight = params.height;
            //1.1,度量 子view
            int childWidthSpec = getChildMeasureSpec(widthMeasureSpec,paddingW,childParamsWidth);
            int childHeightSpec = getChildMeasureSpec(heightMeasureSpec,paddingH,childParamsHeight);
            view.measure(childWidthSpec,childHeightSpec);

            //2 度量viewGroup
            //2.1 多个子view 换行后 viewGroup的宽高
            int childWidth = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();

            if(lineUsedWidth + childWidth + mHorizontalSpacing > widthMeasure){
                lineList.add(itemList);
                heights.add(lineHeight);

                parentNeedWidth = Math.max(parentNeedWidth,lineUsedWidth);
                parendNeedsHeight = parendNeedsHeight + lineHeight ;

                itemList = new ArrayList<>();
                lineHeight = 0;
                lineUsedWidth = 0;

            }

            lineUsedWidth = lineUsedWidth + childWidth;
            lineHeight = Math.max(lineHeight,childHeight);
            itemList.add(view);

            //处理最后一行
            if(i == count -1){
                parentNeedWidth = Math.max(parentNeedWidth,lineUsedWidth);
                parendNeedsHeight = parendNeedsHeight + lineHeight;
                lineList.add(itemList);
                heights.add(lineHeight);
            }

        }

        int realWidth = 0;//最终测量的viewGroup大小
        int realHeight = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        realWidth = (widthMode == MeasureSpec.EXACTLY)?widthMeasure:parentNeedWidth;
        realHeight = (heightMode == MeasureSpec.EXACTLY)?heightMeasure:parentNeedWidth;

        setMeasuredDimension(realWidth,realHeight);

    }

    private void init() {
        lineList = new ArrayList<>();
        heights = new ArrayList<>();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int left = 0 ;
        int top = 0;
        for(int i = 0;i < lineList.size();i++){
            ArrayList<View> line = lineList.get(i);

            for(int j = 0;j<line.size();j++){
                View child = line.get(j);

                int bottom = top + child.getMeasuredHeight();
                int right = left + child.getMeasuredWidth();
                child.layout(left,top,right,bottom);
                left = right + mHorizontalSpacing;
            }
            left = 0;
            top = top + heights.get(i) +mVerticalSpacing;
        }
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
