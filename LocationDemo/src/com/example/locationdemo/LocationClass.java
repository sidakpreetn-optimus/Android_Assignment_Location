package com.example.locationdemo;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class LocationClass extends Fragment {

	private static final String API_KEY = "AIzaSyDRzC7fPjdftgNK2o1I6EVhUH2c-kopIOc";
	private static final String RADIUS = "1000";

	private String URL = "";

	private Button showRestaurants;
	private GoogleMap map;
	private MapView mapview;
	private Location location;
	private ProgressDialog dialog;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_fragmentb, container,
				false);

		showRestaurants = (Button) view.findViewById(R.id.buttonRestaurants);

		dialog = new ProgressDialog(getActivity());
		dialog.setMessage("Loading");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		if (!isGooglePlayServicesAvailable()) {
			Toast.makeText(getActivity(),
					"Configure Google Play Services First !!!",
					Toast.LENGTH_LONG).show();
			return null;
		}

		mapview = (MapView) view.findViewById(R.id.map);
		mapview.onCreate(savedInstanceState);
		map = mapview.getMap();

		// Getting the current Location
		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		location = locationManager.getLastKnownLocation(provider);

		// Postioning map camera to the current location
		try {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			LatLng latLng = new LatLng(latitude, longitude);
			MapsInitializer.initialize(getActivity());
			map.addMarker(new MarkerOptions()
					.position(latLng)
					.title("Current Location")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			map.animateCamera(CameraUpdateFactory.zoomTo(15));
		} catch (Exception ex) {
			Toast.makeText(getActivity(), "Location Services Off",
					Toast.LENGTH_SHORT).show();
		}

		showRestaurants.setOnClickListener(new OnClickListener() {
			public void onClick(View paramView) {
				setupURL();
				// Check for Internet Connection
				if (!isInternet()) {
					Toast.makeText(getActivity(), "No Internet Connection",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// Executing the asynctask for network operation
				GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
				Object[] toPass = new Object[2];
				toPass[0] = map;
				toPass[1] = URL;
				googlePlacesReadTask.execute(toPass);
			}
		});
		return view;
	}

	/**
	 * @param String
	 * @return String
	 * 
	 *         Creates URL for Google Web Service API
	 */
	private void setupURL() {
		try {
			StringBuilder googlePlacesUrl = new StringBuilder(
					"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
			googlePlacesUrl.append("location=" + location.getLatitude() + ","
					+ location.getLongitude());
			googlePlacesUrl.append("&radius=" + RADIUS);
			googlePlacesUrl.append("&types=restaurant");
			googlePlacesUrl.append("&sensor=true");
			googlePlacesUrl.append("&key=" + API_KEY);
			URL = googlePlacesUrl.toString();
		} catch (Exception ex) {
			Toast.makeText(getActivity(), "Location Services Off",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void onResume() {
		mapview.onResume();
		super.onResume();
	}

	public void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
	}

	/**
	 * Check for updated google play services presence
	 */
	private boolean isGooglePlayServicesAvailable() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity());
		if (ConnectionResult.SUCCESS == status) {
			return true;
		} else {
			GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0)
					.show();
			return false;
		}
	}

	/**
	 * @param String
	 *            places
	 * @param GoogleMap
	 *            map
	 * 
	 *            Takes the JSON from URL converts it into list and map and sets
	 *            markers to the GoogleMap object
	 */
	private void addPlacesToMap(GoogleMap map, String places) {
		List<HashMap<String, String>> googlePlacesList = null;
		JSONPlaceHelper placeJsonParser = new JSONPlaceHelper();
		JSONObject googlePlacesJson;

		try {
			googlePlacesJson = new JSONObject(places);
			googlePlacesList = placeJsonParser.parse(googlePlacesJson);
		} catch (Exception e) {
			Log.d("LocationDemo", e.toString());
		}

		map.clear();
		// adds the current location marker
		map.addMarker(new MarkerOptions()
				.position(
						new LatLng(location.getLatitude(), location
								.getLongitude()))
				.title("Current Location")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
		// extracts individual places from list and sets marker
		for (int i = 0; i < googlePlacesList.size(); i++) {
			MarkerOptions markerOptions = new MarkerOptions();
			HashMap<String, String> googlePlace = googlePlacesList.get(i);
			double lat = Double.parseDouble(googlePlace.get("lat"));
			double lng = Double.parseDouble(googlePlace.get("lng"));
			String placeName = googlePlace.get("place_name");
			String vicinity = googlePlace.get("vicinity");
			LatLng latLng = new LatLng(lat, lng);
			markerOptions.position(latLng);
			markerOptions.title(placeName + " : " + vicinity);
			map.addMarker(markerOptions);
		}
	}

	/**
	 * Returns true if connected to Internet
	 */
	private boolean isInternet() {
		ConnectivityManager connMgr = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	/**
	 * Private Class for performing Networking operation
	 */
	private class GooglePlacesReadTask extends
			AsyncTask<Object, Integer, String> {
		String googlePlacesData = null;
		GoogleMap googleMap;

		protected void onPreExecute() {
			dialog.show();
		}

		protected String doInBackground(Object... inputObj) {
			try {
				googleMap = (GoogleMap) inputObj[0];
				String googlePlacesUrl = (String) inputObj[1];
				Http http = new Http();
				googlePlacesData = http.read(googlePlacesUrl);
			} catch (Exception e) {
				Log.d("LocationDemo", e.toString());
			}
			return googlePlacesData;
		}

		protected void onPostExecute(String result) {
			addPlacesToMap(googleMap, result);
			dialog.dismiss();
		}
	}
}
