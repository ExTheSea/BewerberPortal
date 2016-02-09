package com.example.data;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

public class GeoHelper {
	public static String[] getKoordinaten(String requestContent){
		String[] arr_results = new String[2];
		arr_results[0] = "";
		arr_results[1] = "";
		if(requestContent.isEmpty())
			return arr_results;
		requestContent += ", Germany";
		GeoApiContext request = new GeoApiContext().setApiKey("AIzaSyD63sKn7bwTu-hkAspQaymZn4WMInvSgNg");
		GeocodingResult[] results = null;
		try {
			results = GeocodingApi.geocode(request, requestContent).await();
			arr_results[0] = results[0].geometry.location.lat+"";
			arr_results[1] = results[0].geometry.location.lng+"";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr_results;
		
	}
	public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == "K") {
			dist = dist * 1.609344;
		} else if (unit == "N") {
			dist = dist * 0.8684;
		}
		System.out.println(dist);
		return (dist);
	}
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	public static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
}