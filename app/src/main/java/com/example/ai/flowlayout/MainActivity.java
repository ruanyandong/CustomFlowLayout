package com.example.ai.flowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private String[] mButtonText=new String[]{
         "java","android","kotlin","C","PHP","C++",
         "python","ios","swift","HTML",
            "java","android","kotlin","C","PHP","C++",
            "python","ios","swift","HTML",
            "java","android","kotlin","C","PHP","C++",
            "python","ios","swift","HTML"
    };

    private FlowLayout mFlowLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlowLayout=findViewById(R.id.flow_layout);
        initData();
    }

    private void initData(){
        /**
         * 添加按钮
         *
        for (int i=0;i<mButtonText.length;i++){
            Button btn=new Button(this);
            ViewGroup.MarginLayoutParams lp=new ViewGroup.
                    MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT);
            btn.setText(mButtonText[i]);
            mFlowLayout.addView(btn,lp);
        }
         */
        LayoutInflater mInflater=LayoutInflater.from(this);

        for(int i=0;i<mButtonText.length;i++){
            TextView tv=(TextView)mInflater.inflate(R.layout.tv,mFlowLayout,false);
            tv.setText(mButtonText[i]);
            mFlowLayout.addView(tv);
        }
    }

}
