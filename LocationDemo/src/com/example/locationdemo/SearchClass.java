package com.example.locationdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class SearchClass extends Fragment {

	private static final String API_KEY = "AIzaSyDRzC7fPjdftgNK2o1I6EVhUH2c-kopIOc";
	private static final String RADIUS = "1000";

	private ExpandableListView elv;
	private Button showPlaces;
	private double latitude, longitude;
	private EditText lat, lon;
	private ProgressDialog dialog;
	private HashMap<String, List<String>> allPlacesMap;
	private List<String> nameList;
	private int flag;
	private MyExpandableListViewAdapter adapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_fragmenta, container,
				false);

		lat = (EditText) view.findViewById(R.id.editTextLat);
		lon = (EditText) view.findViewById(R.id.editTextLong);
		showPlaces = (Button) view.findViewById(R.id.buttonShowPlaces);
		elv = (ExpandableListView) view
				.findViewById(R.id.expandableListViewMap);

		allPlacesMap = new HashMap<String, List<String>>();

		dialog = new ProgressDialog(getActivity());
		dialog.setMessage("Loading");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		nameList = new ArrayList<String>();
		nameList.add("Schools");
		nameList.add("Restaurants");
		nameList.add("Hospitals");

		if (allPlacesMap != null) {
			elv.setAdapter(adapter);
		}

		showPlaces.setOnClickListener(new OnClickListener() {

			public void onClick(View paramView) {
				flag = 0;
				// Check for blank EditText
				if (lat.getText().toString().equals("")
						|| lon.getText().toString().equals("")) {
					Toast.makeText(getActivity(), "Field(s) Blank !!!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// Check for internet Connection
				if (!isInternet()) {
					Toast.makeText(getActivity(), "No Internet Connection",
							Toast.LENGTH_SHORT).show();
					return;
				}
				latitude = Double.parseDouble(lat.getText().toString());
				longitude = Double.parseDouble(lon.getText().toString());
				// Methods for generating places
				generatePlaces("school");
				generatePlaces("restaurant");
				generatePlaces("hospital");

				adapter = new MyExpandableListViewAdapter(getActivity(),
						nameList, allPlacesMap);
				elv.setAdapter(adapter);
			}
		});

		return view;

	}

	/**
	 * Method which executes AsyncTask for fetching data from URL
	 */
	private void generatePlaces(String place) {
		new GooglePlacesReadTask().execute(setupURL(place));
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
	 * @param String
	 * @return String
	 * 
	 *         Creates URL for Google Web Service API
	 */
	private String setupURL(String place) {
		StringBuilder googlePlacesUrl = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
		googlePlacesUrl.append("location=" + latitude + "," + longitude);
		googlePlacesUrl.append("&radius=" + RADIUS);
		googlePlacesUrl.append("&types=" + place);
		googlePlacesUrl.append("&sensor=true");
		googlePlacesUrl.append("&key=" + API_KEY);
		return googlePlacesUrl.toString();
	}

	/**
	 * Private Class for performing Networking operation
	 */
	private class GooglePlacesReadTask extends
			AsyncTask<String, Integer, String> {
		String googlePlacesData = null;

		protected void onPreExecute() {
			dialog.show();
		}

		/*
		 * Connects to the URL for JSON Object
		 */
		protected String doInBackground(String... in) {
			try {
				Http http = new Http();
				googlePlacesData = http.read(in[0]);
			} catch (Exception e) {
				Log.d("SN", e.toString());
			}
			return googlePlacesData;
		}

		/*
		 * Setting places into Lists and maps for displaying
		 */
		protected void onPostExecute(String result) {

			List<String> eListViewList = new ArrayList<String>();
			List<HashMap<String, String>> googlePlacesList = null;
			JSONPlaceHelper placeJsonParser = new JSONPlaceHelper();
			JSONObject googlePlacesJson;

			try {
				googlePlacesJson = new JSONObject(result);
				googlePlacesList = placeJsonParser.parse(googlePlacesJson);
			} catch (Exception e) {
				Log.d("SN", e.toString());
			}

			for (int i = 0; i < googlePlacesList.size(); i++) {
				HashMap<String, String> googlePlace = googlePlacesList.get(i);
				eListViewList.add(googlePlace.get("place_name") + "\n"
						+ googlePlace.get("vicinity"));
			}
			allPlacesMap.put(nameList.get(flag), eListViewList);
			flag++;
			dialog.dismiss();

		}
	}

}