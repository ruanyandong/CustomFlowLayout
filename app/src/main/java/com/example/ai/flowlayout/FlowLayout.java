package com.example.ai.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * getMeasuredWidth()获取的是view原始的大小，也就是这个view在XML文件中配置或者是代码中设置的大小。
 * getWidth（）获取的是这个view最终显示的大小，这个大小有可能等于原始的大小也有可能不等于原始大小。
 * getMeasuredWidth（）的值是measure阶段结束之后得到的view的原始的值
 * 在onLayout中还可以改变大小，getWidth()得到显示在屏幕上的最终大小
 */

public class FlowLayout extends ViewGroup {

    private static final String TAG="FlowLayout";

    public FlowLayout(Context context) {
        this(context,null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 当在布局文件里用了自定义的属性时，就会调用三个构造函数的方法
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //布局文件中设置的容器的宽度
        int sizeWidth=MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth=MeasureSpec.getMode(widthMeasureSpec);


        int sizeHeight=MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight=MeasureSpec.getMode(heightMeasureSpec);

        //如果容器的高宽是wrap_content，那么要重新计算容器高宽
        int width=0;
        int height=0;

        //记录每一行的宽度与高度
        int lineWidth=0;
        int lineHeight=0;

        //得到内部元素的个数
        int childCount=getChildCount();

        for (int i=0;i<childCount;i++){

            View child=getChildAt(i);
            //测量每个子view的宽和高
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
            //得到LayoutParams
            MarginLayoutParams lp=(MarginLayoutParams)child.getLayoutParams();
            //子view占据的宽度
            int childWidth=child.getMeasuredWidth()+lp.leftMargin+lp.rightMargin;
            //子view占据的高度
            int childHeight=child.getMeasuredHeight()+lp.topMargin+lp.bottomMargin;

            //如果大于，就要换行了
            if (lineWidth+childWidth>sizeWidth-getPaddingLeft()-getPaddingRight()){

                //对比得到最大的宽度
                width=Math.max(width,lineWidth);
                //重置lineWidth
                lineWidth=childWidth;
                //记录行高
                height+=lineHeight;

                //重置lineHeight
                lineHeight=childHeight;

            }else{
                //不换行的情况
                //叠加行宽
                lineWidth+=childWidth;
                //得到当前行最大高度
                lineHeight= Math.max(lineHeight,childHeight);
            }

            //最后一个控件
            if(i==childCount-1){
                width=Math.max(width,lineWidth);
                height+=lineHeight;
            }


        }
        Log.d(TAG,"sizeWidth="+sizeWidth);
        Log.d(TAG, "sizeHeight="+sizeHeight);
        //简洁写法
        setMeasuredDimension(
                modeWidth==MeasureSpec.EXACTLY?sizeWidth:width+getPaddingLeft()+getPaddingRight(),
                modeHeight==MeasureSpec.EXACTLY?sizeHeight:height+getPaddingTop()+getPaddingBottom());


    }

    /**
     * 存储所有的子View，一行一行存储
     */
    private List<List<View>> mAllViews=new ArrayList<List<View>>();

    /**
     * 每一行的高度
     */
    private List<Integer> mLineHeight=new ArrayList<Integer>();



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mAllViews.clear();
        mLineHeight.clear();

        /**
         * getWidth(): 得到的是view在父Layout中布局好后的宽度值，如果没有父布局，那麼默认的父布局是整个屏幕
         * getWidth(): View在设定好布局后整个View的宽度。
           getMeasuredWidth(): 对View上的内容进行测量后得到的View内容佔据的宽度，
           前提是你必须在父布局的onLayout()方法或者此View的onDraw()方法裡调 用measure(0,0);
           (measure 参数的值你可以自己定义)，否则你得到的结果和getWidth()得到的结果一样。
         */
        //当前ViewGroup的宽度
        int width=getWidth();

        int lineWidth=0;
        int lineHeight=0;

        List<View> lineViews=new ArrayList<View>();

        int childCount=getChildCount();

        for(int i=0;i<childCount;i++){
            View child=getChildAt(i);
            MarginLayoutParams lp=(MarginLayoutParams)child.getLayoutParams();

            int childWidth=child.getMeasuredWidth();
            int childHeight=child.getMeasuredHeight();

            //如果需要换行
            if (childWidth+lineWidth+lp.leftMargin+lp.rightMargin>width-getPaddingLeft()-getPaddingRight()){
                //记录LineHeight
                mLineHeight.add(lineHeight);
                //记录当前行的Views
                mAllViews.add(lineViews);

                //重置我们的行宽和行高
                lineWidth=0;

                lineHeight=childHeight+lp.topMargin+lp.bottomMargin;

                //重置我们的View集合
                lineViews=new ArrayList<>();

            }

            lineWidth+=childWidth+lp.leftMargin+lp.rightMargin;
            lineHeight=Math.max(lineHeight,childHeight+lp.topMargin+lp.bottomMargin);

            lineViews.add(child);
        }
        //处理最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        //设置子View的位置
        int left=getPaddingLeft();
        int top=getPaddingTop();

        //行数
        int lineNum=mAllViews.size();

        for (int i=0;i<lineNum;i++){

            //当前行的所有的View
            lineViews=mAllViews.get(i);
            //当前行的行高
            lineHeight=mLineHeight.get(i);

            for (int j=0;j<lineViews.size();j++){

                View child=lineViews.get(j);
                //判断child的状态，GONE就不显示，跳过
                if(child.getVisibility()==View.GONE){
                    continue;
                }

                MarginLayoutParams lp=(MarginLayoutParams)child.getLayoutParams();

                int lc=left+lp.leftMargin;
                int tc=top+lp.topMargin;
                int rc=lc+child.getMeasuredWidth();
                int bc=tc+child.getMeasuredHeight();

                //为子View进行布局
                child.layout(lc,tc,rc,bc);

                left+=child.getMeasuredWidth()+lp.leftMargin+lp.rightMargin;

            }
            left=getPaddingLeft();
            top+=lineHeight;

        }


    }

    /**
     * 与当前ViewGroup对应的LayoutParams
     * 每一个ViewGroup对应一个LayoutParams
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }
}
