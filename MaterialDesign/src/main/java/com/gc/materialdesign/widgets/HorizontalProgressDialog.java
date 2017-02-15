package com.gc.materialdesign.widgets;

import android.content.Context;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.gc.materialdesign.R;
import com.gc.materialdesign.views.ProgressBarDeterminate;

/**
 * Created by liluhe on 2016/8/17.
 * com.gc.materialdesign.views.ProgressBarDeterminate
 */
public class HorizontalProgressDialog extends android.app.Dialog {

    Context context;
    View view;
    View backView;
    String title;
    TextView titleTextView;

    int progressColor = -1;
    private ProgressBarDeterminate progressBarCircularIndeterminate;
    private TextView tvProgress;
    private TextView tvState;

    public HorizontalProgressDialog(Context context, String title) {
        super(context, android.R.style.Theme_Translucent);
        this.title = title;
        this.context = context;
    }

    public void updateProgress(double p,long total) {
        int progress = (int) (100 * p);
        if (progress >=100) {
            progress = 100;
            tvState.setText("下载完成，");
        }else{
            tvState.setText("正在下载，");
        }
        progressBarCircularIndeterminate.setProgress(progress);
        setTitle(progress + "%");
        long current= (long) (total*p);
        tvProgress.setText("，"+ Formatter.formatFileSize(context,current)+"/"+Formatter.formatFileSize(context,total));
    }

    public HorizontalProgressDialog(Context context, String title, int progressColor) {
        super(context, android.R.style.Theme_Translucent);
        this.title = title;
        this.progressColor = progressColor;
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(com.gc.materialdesign.R.layout.horizontal_progress_dialog);
        progressBarCircularIndeterminate = (ProgressBarDeterminate) findViewById(com.gc.materialdesign.R.id.progressBarCircularIndetermininate);
        tvProgress = (TextView) findViewById(com.gc.materialdesign.R.id.tv_progress);
        tvState = (TextView) findViewById(com.gc.materialdesign.R.id.tv_state);
        view = findViewById(com.gc.materialdesign.R.id.contentDialog);
        backView = findViewById(com.gc.materialdesign.R.id.dialog_rootView);
        backView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < view.getLeft()
                        || event.getX() > view.getRight()
                        || event.getY() > view.getBottom()
                        || event.getY() < view.getTop()) {
                    // 注释掉下面这句话使得setCanceledOnTouchOutside(false)生效
//					dismiss();
                }
                return false;
            }
        });

        this.titleTextView = (TextView) findViewById(com.gc.materialdesign.R.id.title);
        setTitle(title);
        if (progressColor != -1) {
            progressBarCircularIndeterminate.setProgress(0);
            progressBarCircularIndeterminate.setBackgroundColor(progressColor);
        }


    }

    @Override
    public void show() {
        // TODO 自动生成的方法存根
        super.show();
        // set dialog enter animations
        view.startAnimation(AnimationUtils.loadAnimation(context, com.gc.materialdesign.R.anim.dialog_main_show_amination));
        backView.startAnimation(AnimationUtils.loadAnimation(context, com.gc.materialdesign.R.anim.dialog_root_show_amin));
    }

    // GETERS & SETTERS

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (title == null)
            titleTextView.setVisibility(View.GONE);
        else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

	/*@Override
    public void dismiss() {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.dialog_main_hide_amination);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.post(new Runnable() {
					@Override
					public void run() {
			        	ProgressDialog.super.dismiss();
			        }
			    });

			}
		});
		Animation backAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_root_hide_amin);

		view.startAnimation(anim);
		backView.startAnimation(backAnim);
		super.dismiss();
	}*/


}
