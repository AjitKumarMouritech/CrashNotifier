package com.mouritech.crashnotifier.ui.ui.hospitals;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private GoogleMap mMap;
    private Object mMap2;
    String url;
    private String googlePlacesData;


    @Override
    protected String doInBackground(Object... objects){

        mMap2 = objects[0];
        //String url1 = objects[0][1];
//        mMap = (GoogleMap)objects[0];
        //url = (String)objects[1];
        url = NearHospitalsList.url;

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(String s){

        List<HashMap<String, String>> nearbyPlaceList;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        Log.d("nearbyplacesdata","called parse method");
        showNearbyPlaces(nearbyPlaceList);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
    {
        //todo remove sout
        System.out.println("IN show nearby places , listSize = "+nearbyPlaceList.size());

        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");

            //todo remove sout
            System.out.println("place name = "+placeName);

            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lng"));

            LatLng latLng = new LatLng( lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : "+ vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }


    private static final String PLACES_SEARCH_URL =  "https://maps.googleapis.com/maps/api/place/search/json?";

    private static final boolean PRINT_AS_STRING = false;


    /*public void performSearch() throws Exception {
        try {
            System.out.println("Perform Search ....");
            System.out.println("-------------------");
            HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
            HttpRequest request = httpRequestFactory.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
            request.url.put("key", API_KEY);
            request.url.put("location", latitude + "," + longitude);
            request.url.put("radius", 500);
            request.url.put("sensor", "false");

            if (PRINT_AS_STRING) {
                System.out.println(request.execute().parseAsString());
            } else {

                PlacesList places = request.execute().parseAs(PlacesList.class);
                System.out.println("STATUS = " + places.status);
                for (Place place : places.results) {
                    System.out.println(place);
                }
            }

        } catch (HttpResponseException e) {
            System.err.println(e.response.parseAsString());
            throw e;
        }
    }*/
}
