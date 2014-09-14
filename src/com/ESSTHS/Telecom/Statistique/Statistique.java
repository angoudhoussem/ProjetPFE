package com.ESSTHS.Telecom.Statistique;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Statistique extends Activity {

	private String[] mMonth = new String[] { "Jan", "Feb", "Mar", "Apr", "May",
			"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	int[] distribution;
	int[] colors;
	private int signalDBM ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GetParams listener = new GetParams();
		TelephonyManager TelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		TelManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		final Handler handler = new Handler();
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//				List<NeighboringCellInfo> NeighboringList = telephonyManager.getNeighboringCellInfo();
//				System.out.println("count " + NeighboringList.size());
//				System.out.println("signalDBM " + signalDBM);
//
//				distribution = new int[NeighboringList.size() + 1];
//				colors = new int[NeighboringList.size() + 1];
//				code = new String[NeighboringList.size() + 1];
//
//				distribution[0] = signalDBM;
//				colors[0] = color[0];
//				code[0] = "MyCell";
//
//				for (int i = 0; i < NeighboringList.size(); i++) {
//
//					distribution[i + 1] = NeighboringList.get(i).getRssi();
//					colors[i + 1] = color[i + 1];
//					code[i + 1] = String.valueOf(NeighboringList.get(i)
//							.getCid());
//				}
//
//				
//			}
//		}, 1000);}
//
//
//	}
		openChart();
	}

	private void openChart() {

		// Pie Chart Section Names
		String[] code = new String[] { "Eclair & Older", "Froyo",
				"Gingerbread", "Honeycomb", "IceCream Sandwich", "Jelly Bean" };

		// Pie Chart Section Value
		double[] distribution = { 3.9, 12.9, 55.8, 1.9, 23.7, 1.8 };

		// Color of each Pie Chart Sections
		int[] colors = { Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN,
				Color.RED, Color.YELLOW };

		// Instantiating CategorySeries to plot Pie Chart
		CategorySeries distributionSeries = new CategorySeries(getResources()
				.getString(com.example.ttttt.R.string.title_activity_main));
		for (int i = 0; i < distribution.length; i++) {
			// Adding a slice with its values and name to the Pie Chart
			distributionSeries.add(code[i], distribution[i]);
		}

		// Instantiating a renderer for the Pie Chart
		DefaultRenderer defaultRenderer = new DefaultRenderer();
		for (int i = 0; i < distribution.length; i++) {
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setColor(colors[i]);
			defaultRenderer.setApplyBackgroundColor(true);
			seriesRenderer.setDisplayChartValues(true);
			// Adding a renderer for a slice
			defaultRenderer.addSeriesRenderer(seriesRenderer);
		}

		defaultRenderer.setChartTitle(getResources().getString(
				com.example.ttttt.R.string.title_activity_main));
		defaultRenderer.setChartTitleTextSize(30);
		defaultRenderer.setZoomButtonsVisible(true);
		defaultRenderer.setApplyBackgroundColor(true);
		// Creating an intent to plot bar chart using dataset and
		// multipleRenderer
		Intent intent = ChartFactory
				.getPieChartIntent(getBaseContext(), distributionSeries,
						defaultRenderer, "AChartEnginePieChartDemo");

		// Start Activity
		startActivity(intent);

	}
	public class GetParams extends PhoneStateListener {

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			if (signalStrength.isGsm()) {
				signalDBM = signalStrength.getGsmSignalStrength();
				if (signalStrength.getGsmSignalStrength() != 99)
					signalDBM = signalStrength.getGsmSignalStrength() * 2 - 113;
				else
					signalDBM = signalStrength.getGsmSignalStrength();

				Log.v("value", String.valueOf(signalDBM));
			}

			else {
				final int cdmaDbm = signalStrength.getCdmaDbm();
				signalDBM = cdmaDbm;

			}

		}
	}
}