package com.ESSTHS.Telcom.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.ESSTHS.Telecom.Statistique.Statistique;
import com.ESSTHS.Telecom.help.CurlActivity;
import com.ESSTHS.Telecom.info.Details;
import com.ESSTHS.Telecom.info.Map;
import com.example.ttttt.R;

public class MainActivity extends Activity implements AnimationListener {
	ImageView setting, help, map, stat;
	TextView tv;
	Animation animMove;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setting = (ImageView) findViewById(R.id.iconsetting);
		help = (ImageView) findViewById(R.id.imagehelp);
		map = (ImageView) findViewById(R.id.imagemap);
		stat = (ImageView) findViewById(R.id.imagesatat);
		tv = (TextView) findViewById(R.id.textView3);

		// step1 create rotate animation object
		RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
		// step2 set properties to animation
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(700);
		// step3 start animation the image
		tv.startAnimation(anim);
		// step4 stop animation
		tv.setAnimation(null);
		// load the animation
		animMove = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.blink);
		animMove.setAnimationListener(this);
		tv.startAnimation(animMove);

		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// start the animation
				tv.startAnimation(animMove);
			}
		});
		setting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// start the animation

				Intent intent = new Intent(getApplicationContext(),
					com.ESSTHS.Telecom.Tabhost.MainActivity.class);

				startActivity(intent);
			}
		});
		stat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// start the animation

				Intent intent = new Intent(getApplicationContext(),
						Statistique.class);

				startActivity(intent);
			}
		});

		map.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// start the animation

				Intent intent = new Intent(getApplicationContext(),
						Map.class);

				startActivity(intent);
			}
		});
		help.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// start the animation

				Intent intent = new Intent(getApplicationContext(),
						CurlActivity.class);

				startActivity(intent);
			}
		});

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// Take any action after completing the animation

		// check for zoom in animation
		if (animation == animMove) {
		}

	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub

	}

}
