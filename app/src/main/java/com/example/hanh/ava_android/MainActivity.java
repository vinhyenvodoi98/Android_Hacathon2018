package com.example.hanh.ava_android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.example.hanh.ava_android.BuildConfig;

import com.example.hanh.ava_android.adapter.PlaceAutocompleteAdapter;
import com.example.hanh.ava_android.model.EndLocation;
import com.example.hanh.ava_android.model.Local;
import com.example.hanh.ava_android.model.PlaceInfo;
import com.example.hanh.ava_android.model.StartLocation;
import com.example.hanh.ava_android.remote.ApiUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback , GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private LocationManager locationManager;
    private static final String TAG = "MainActivity";
    private static final int LOCATION_UPDATE_MIN_TIME = 5000;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private MapFragment mapFragment;
    private Location location = null;
    // private boolean CheckDirection = false;

    private StartLocation startLocation;
    private EndLocation endLocation;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient googleApiClient;
    private Marker marker;
    private PlaceInfo placeInfo;


    private List<Address> list = new ArrayList<>();
    private static final String CONTENT_TYPE = "application/json";
    private Address address;

    @BindView(R.id.btnActionFloat)
    FloatingActionButton btnFLoat;
    private AutoCompleteTextView edtDestination;
    @BindView(R.id.imgClearDesti)
    ImageView imgClear;
    private ViewGroup layoutDirection;
    private ImageView imgDetail;

    private LocationListener locationListener = new LocationListener() {
        @SuppressLint("DefaultLocale")
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d(TAG, String.format("%f, %f", location.getLatitude(), location.getLongitude()));
                drawMaker(location);
                locationManager.removeUpdates(locationListener);

            } else {
                Log.d(TAG, "Loaction is null");
            }

        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        edtDestination = (AutoCompleteTextView) findViewById(R.id.edtDesti);
        imgClear = (ImageView) findViewById(R.id.imgClearDesti);
        layoutDirection = (ViewGroup) findViewById(R.id.layoutDirection);
        imgDetail = (ImageView) findViewById(R.id.imgShowDetail);
        layoutDirection.setVisibility(View.GONE);
        imgDetail.setVisibility(View.GONE);
        //local = new Local();

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtDestination.setText("");
            }
        });

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(this);


        //  map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initMap();
        getCurrentLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.onResume();
        getCurrentLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapFragment.onPause();
        locationManager.removeUpdates(locationListener);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        btnFLoat = (FloatingActionButton) findViewById(R.id.btnActionFloat);
        btnFLoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
        // map.setMyLocationEnabled(true);

        initMap();
        init();
    }

    private void init() {
        Log.d(TAG, "init:initializing");

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
                .build();

        edtDestination.setOnItemClickListener(autoCompleteListener);

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, googleApiClient,
                LAT_LNG_BOUNDS, null);

        edtDestination.setAdapter(placeAutocompleteAdapter);
        edtDestination.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }

                return false;
            }
        });



        hideSoftKeyboard();
    }

    private void geoLocate() {
        hideSoftKeyboard();
        Log.d(TAG, "geoLoacte: geoLocating");

        String searchString = edtDestination.getText().toString();

        Geocoder geocoder = new Geocoder(this);

        try {
            list = geocoder.getFromLocationName(searchString, 1);

        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOExeption: " + e.getMessage());
        }

        if (list.size() > 0) {

            address = list.get(0);
            Log.d(TAG, "geoLocate: found a location " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
            imgDetail.setVisibility(View.VISIBLE);
            imgDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, " Click show InformationDetail");
                    try {
                        if (marker.isInfoWindowShown()) {
                            marker.hideInfoWindow();
                        } else {
                            Log.d(TAG, "Click. Place info: " + address.toString());
                            marker.showInfoWindow();
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG, "onClick: NullPoiterExeption + " + e.getMessage());
                    }
                }
            });


            if (location == null) {
                Toast.makeText(getApplicationContext(), "please get your location!", Toast.LENGTH_LONG).show();
                return;
            } else {
                layoutDirection.setVisibility(View.VISIBLE);
                layoutDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LatLng start = new LatLng(location.getLatitude(), location.getLongitude());
                        LatLng end = new LatLng(address.getLatitude(), address.getLongitude());
                        Log.i(TAG, "start: " + location.getLatitude() + location.getLongitude());
                        Log.i(TAG, "end : " + address.getLatitude() + address.getLongitude());
                        route(start, end);
                    }
                });

            }

        }

    }

    protected void route(final LatLng sourcePosition, LatLng destPosition) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    Document doc = (Document) msg.obj;
//                    GMapV2Direction md = new GMapV2Direction();
                    ArrayList<LatLng> directionPoint =  getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(10).color(getResources()
                            .getColor(R.color.colorDirection));
                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    Polyline polylin = map.addPolyline(rectLine);
                    getDurationText(doc);
                    startLocation = new StartLocation(location.getLatitude(), location.getLongitude());
                    endLocation = new EndLocation(address.getLatitude(), address.getLongitude());

                    Map<String, Map> coorDinate = new HashMap<>();
                    Map<String, ArrayList> localMap = new HashMap<>();

                    ArrayList<StartLocation> startLocations = new ArrayList<>();
                    startLocations.add(startLocation);
                    localMap.put("start_location", startLocations);

                    ArrayList<EndLocation> endLocations = new ArrayList<>();
                    endLocations.add(endLocation);
                    localMap.put("end_location", endLocations);

                    coorDinate.put("Local", localMap);
                    callApiLocation(coorDinate);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        new GMapV2Direction(handler, sourcePosition, destPosition).execute();
    }


    private void callApiLocation(Map coorDinate) {
        ApiUtils.getMapService().createMap(CONTENT_TYPE, coorDinate)
                .enqueue(new Callback<com.example.hanh.ava_android.model.Map>() {
                    @Override
                    public void onResponse(Call<com.example.hanh.ava_android.model.Map> call, Response<com.example.hanh.ava_android.model.Map> response) {

                        Log.i(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onFailure(Call<com.example.hanh.ava_android.model.Map> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });

    }


    private void initMap() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int googlePlayStatus = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (googlePlayStatus != ConnectionResult.SUCCESS) {
            googleApiAvailability.getErrorDialog(this, googlePlayStatus, -1).show();
            finish();
        } else {
            if (map != null) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.getUiSettings().setAllGesturesEnabled(true);
            }
        }
    }

    private void getCurrentLocation() {
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!(isGPSEnabled || isNetworkEnabled)) {
            Toast.makeText(this, R.string.error_location_provider, Toast.LENGTH_LONG).show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MIN_TIME,
                    LOCATION_UPDATE_MIN_DISTANCE, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }

        if (location != null) {
            Log.d(TAG, String.format("getCurrentLocation(%f,%f)", location.getLatitude(), location.getLongitude()));
            drawMaker(location);
        }
    }

    private void drawMaker(Location location) {
        if (map != null) {
            map.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
//            map.addMarker(new MarkerOptions().position(gps).title("Current position"));
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 17));
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (listAddress != null && listAddress.size() > 0) {
                    String nameLocation = listAddress.get(0).getAddressLine(0);
                    Log.d(TAG, "drawMaker: found a location " + listAddress.toString());
                    moveCamera(gps, DEFAULT_ZOOM, nameLocation);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        map.addMarker(options);
        hideSoftKeyboard();
    }


    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera: moving camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        map.clear();

        if (placeInfo != null) {
            String snippet = "Address: " + placeInfo.getAddress() + "\n"
                    + "Phone number: " + placeInfo.getPhoneNumber() + "\n"
                    + "Website: " + placeInfo.getWebsiteUri() + "\n"
                    + "Price Rating: " + placeInfo.getRating() + "\n";

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(placeInfo.getName())
                    .snippet(snippet);
            marker = map.addMarker(markerOptions);
        } else {
            map.addMarker(new MarkerOptions().position(latLng));
        }

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //get GMapV2Direction

    public ArrayList<LatLng> getDirection(Document doc) {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2
                        .item(getNodeIndex(nl2, "start_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));

                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "points"));
                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                for (int j = 0; j < arr.size(); j++) {
                    listGeopoints.add(new LatLng(arr.get(j).latitude, arr
                            .get(j).longitude));
                }

                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "lat"));
                lat = Double.parseDouble(latNode.getTextContent());
                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));
            }
        }

        return listGeopoints;
    }

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

    public String getDurationText(Document doc) {
        try {

            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "text"));
            Log.i("DurationText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "0";
        }
    }

    public int getDurationValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DurationValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    public String getDistanceText(Document doc) {
        /*
         * while (en.hasMoreElements()) { type type = (type) en.nextElement();
         *
         * }
         */

        try {
            NodeList nl1;
            nl1 = doc.getElementsByTagName("distance");

            Node node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = null;
            nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.d("DistanceText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

        /*
         * NodeList nl1; if(doc.getElementsByTagName("distance")!=null){ nl1=
         * doc.getElementsByTagName("distance");
         *
         * Node node1 = nl1.item(nl1.getLength() - 1); NodeList nl2 = null; if
         * (node1.getChildNodes() != null) { nl2 = node1.getChildNodes(); Node
         * node2 = nl2.item(getNodeIndex(nl2, "value")); Log.d("DistanceText",
         * node2.getTextContent()); return node2.getTextContent(); } else return
         * "-1";} else return "-1";
         */
    }

    public int getDistanceValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("distance");
            Node node1 = null;
            node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DistanceValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
        /*
         * NodeList nl1 = doc.getElementsByTagName("distance"); Node node1 =
         * null; if (nl1.getLength() > 0) node1 = nl1.item(nl1.getLength() - 1);
         * if (node1 != null) { NodeList nl2 = node1.getChildNodes(); Node node2
         * = nl2.item(getNodeIndex(nl2, "value")); Log.i("DistanceValue",
         * node2.getTextContent()); return
         * Integer.parseInt(node2.getTextContent()); } else return 0;
         */
    }

    public String getStartAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("start_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }

    public String getEndAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("end_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }
    }

    public String getCopyRights(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("copyrights");
            Node node1 = nl1.item(0);
            Log.i("CopyRights", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //
    private AdapterView.OnItemClickListener autoCompleteListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(updatePlaceCallback);
        }
    };

    private ResultCallback<PlaceBuffer> updatePlaceCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()) {
                Log.e(TAG, "Result: Place query did not complete successfully");
                places.release();
                return;
            }

            final Place place = places.get(0);

            placeInfo = new PlaceInfo();
            placeInfo.setName(place.getName().toString());
            placeInfo.setAddress(place.getAddress().toString());
            placeInfo.setId(place.getId());
            placeInfo.setLatlng(place.getLatLng());
            placeInfo.setRating(place.getRating());
            placeInfo.setPhoneNumber(place.getPhoneNumber().toString());
            placeInfo.setWebsiteUri(place.getWebsiteUri());

            Log.d(TAG, "onResult: " + place.getName() + "," + place.getAddress() + "," + place.getId()
            + "," + place.getLatLng() + "," + place.getRating() + "," + place.getPhoneNumber() + place.getWebsiteUri());

            Log.d(TAG, "onResult: placeInfo : " + place.getName() + "," + place.getAddress() + "," + place.getId()
                    + "," + place.getLatLng() + "," + place.getRating() + "," + place.getPhoneNumber() + place.getWebsiteUri());

            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport()
                    .getCenter().longitude), DEFAULT_ZOOM, placeInfo);

            places.release();
        }
    };
}
