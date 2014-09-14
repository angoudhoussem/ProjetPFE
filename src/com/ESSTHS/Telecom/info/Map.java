package com.ESSTHS.Telecom.info;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ttttt.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class Map extends FragmentActivity implements OnMarkerClickListener {
	TextView tvDistanceDuration;
	static GoogleMap map;
	LocationManager locationManager;
	Location location;
	GPSTracker gps;
	HashMap<Double, Double> longAndLat = new HashMap<Double, Double>();
	ArrayList<LatLng> allBTSMarker = new ArrayList<LatLng>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);
		longAndLat.put(36.8403905, 10.589522);

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setTrafficEnabled(true);
		map.setMyLocationEnabled(true);
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation cellLocation = (GsmCellLocation) tm.getCellLocation();

		int cid = cellLocation.getCid();
		String strphoneType = "";
		int phoneType = tm.getPhoneType();
		switch (phoneType) {
		case (TelephonyManager.PHONE_TYPE_CDMA):
			strphoneType = "CDMA";
			break;
		case (TelephonyManager.PHONE_TYPE_GSM):
			strphoneType = "GSM";
			break;
		case (TelephonyManager.PHONE_TYPE_NONE):
			strphoneType = "NONE";
			break;
		}

		map.setOnMarkerClickListener(this);
		gps = new GPSTracker(Map.this);
		// Check if GPS enabled

		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			LatLng Mypos = new LatLng(latitude, longitude);
			Marker m = map.addMarker(new MarkerOptions()
					.position(Mypos)
					.title("You are here and  connected to cell " + cid)
					.snippet("Type :" + strphoneType)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ic_map)));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(Mypos, 20));
			allBTSMarker.add(Mypos);
			Toast.makeText(
					getApplicationContext(),
					"Your Location is - \nLat: " + latitude + "\nLong: "
							+ longitude, Toast.LENGTH_LONG).show();

		} else {

			gps.showSettingsAlert();
		}
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		for (Double key : longAndLat.keySet()) {

			final LatLng lactunis = new LatLng(key, longAndLat.get(key));
			Marker m = map
					.addMarker(new MarkerOptions()
							.position(lactunis)
							.title("BTS à CID=" + cid)
							.snippet("bts")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.btss)));
		

		}

		final LatLng lactunis = new LatLng(35.834082, 10.589633);
		Marker m = map.addMarker(new MarkerOptions().position(lactunis)
				.title("BTS à CID=" + cid)
				.snippet("international telecommunication services ")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.btss)));
		allBTSMarker.add(lactunis);
		if (allBTSMarker.size() >= 2) {
			LatLng origin = allBTSMarker.get(0);
			LatLng dest = allBTSMarker.get(1);

			// Getting URL to the Google Directions API
			String url = getDirectionsUrl(origin, dest);

			DownloadTask downloadTask = new DownloadTask();

			// Start downloading json data from Google Directions API
			downloadTask.execute(url);
		}
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			Toast.makeText(
					getBaseContext(),
					"Location changed: Lat: " + loc.getLatitude() + " Lng: "
							+ loc.getLongitude(), Toast.LENGTH_SHORT).show();
			String longitude = "Longitude: " + loc.getLongitude();
			Log.v("lo", longitude);
			String latitude = "Latitude: " + loc.getLatitude();
			Log.v("la", latitude);

			/*------- To get city name from coordinates -------- */
			String cityName = null;
			Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
			List<Address> addresses;
			try {
				addresses = gcd.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
				if (addresses.size() > 0)
					System.out.println(addresses.get(0).getLocality());
				cityName = addresses.get(0).getLocality();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
					+ cityName;
			Log.v("s", s);

		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		if (marker.getSnippet().matches("btss")) {
			CircleOptions circleOptions = new CircleOptions()
					.center(marker.getPosition()).radius(5000)
					.fillColor(0xFFF0B000).strokeColor(Color.rgb(71, 143, 150))
					.strokeWidth(2);

			map.addCircle(circleOptions);
		}

		return false;
	}

	private double calculateDistance(double fromLong, double fromLat,
			double toLong, double toLat) {
		double d2r = Math.PI / 180;
		double dLong = (toLong - fromLong) * d2r;
		double dLat = (toLat - fromLat) * d2r;
		double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
				* Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367000 * c;
		return Math.round(d);

	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			String distance = "";
			String duration = "";

			if (result.size() < 1) {
				Toast.makeText(getBaseContext(), "No Points",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if (j == 0) { // Get distance from the list
						distance = (String) point.get("distance");
						continue;
					} else if (j == 1) { // Get duration from the list
						duration = (String) point.get("duration");
						continue;
					}

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(7);
				lineOptions.color(Color.RED);

			}

			tvDistanceDuration.setText("Distance:" + distance + ", Duration:"
					+ duration);

			// Drawing polyline in the Google Map for the i-th route
			map.addPolyline(lineOptions);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
