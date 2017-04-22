package com.codepath.hungrybird.network;

import com.codepath.hungrybird.model.postmates.DeliveryQuoteRequest;
import com.codepath.hungrybird.model.postmates.DeliveryQuoteResponse;
import com.codepath.hungrybird.model.postmates.DeliveryRequest;
import com.codepath.hungrybird.model.postmates.DeliveryResponse;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ajasuja on 4/18/17.
 */

public class PostmatesClient {

    private static final String BASE_URL = "https://api.postmates.com/v1";
    private static final String CUSTOMER_ID = "cus_LEDzIm22-R1Ui-";

    private static PostmatesClient instance;

    private AsyncHttpClient asyncHttpClient;

    public static PostmatesClient getInstance(){
        if (instance == null) {
            instance = new PostmatesClient();
        }
        return instance;
    }

    public interface DeliveryResponseListener {
        void onSuccess(DeliveryResponse deliveryResponse);
        void onFailure(Exception e);
    }

    public interface DeliveryResponseListListener {
        void onSuccess(List<DeliveryResponse> deliveryResponses);
        void onFailure(Exception e);
    }

    public interface DeliveryQuoteResponseListener {
        void onSuccess(DeliveryQuoteResponse deliveryQuoteResponse);
        void onFailure(Exception e);
    }

    private PostmatesClient() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setBasicAuth("ff887f36-ebcf-43b9-be7d-21157c1a4607", "");
    }

    public void deliveries() {
        String deliveriesUrl = BASE_URL + "/customers" + CUSTOMER_ID + "/deliveries";
        asyncHttpClient.get(deliveriesUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void deliveryQuotes(DeliveryQuoteRequest deliveryQuoteRequest, DeliveryQuoteResponseListener listener) {
        String deliveryQuotesUrl = String.format("%s/customers/%s/delivery_quotes", BASE_URL, CUSTOMER_ID);
        RequestParams params = new RequestParams();
//        params.put("pickup_address", "20 McAllister St, San Francisco, CA");
//        params.put("dropoff_address", "101 Market St, San Francisco, CA");
        params.put("pickup_address", deliveryQuoteRequest.getPickUpAddress());
        params.put("dropoff_address", deliveryQuoteRequest.getDropOffAddress());
        asyncHttpClient.post(deliveryQuotesUrl, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    listener.onSuccess(new DeliveryQuoteResponse(response));
                } catch (Exception e) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    // https://api.postmates.com/v1/customers/cus_LEDzIm22-R1Ui-/deliveries/[deliveryId]
    public void createDelivery(DeliveryRequest deliveryRequest, DeliveryResponseListener listener) {
        String createDeliveryUrl = String.format("%s/customers/%s/deliveries", BASE_URL, CUSTOMER_ID);
        RequestParams params = new RequestParams();
        /*
            manifest:indian food
            pickup_name:abc
            pickup_address:"20 McAllister St, San Francisco, CA"
            pickup_phone_number:111-111-1111
            dropoff_name:xyz
            dropoff_address:"101 Market St, San Francisco, CA"
            dropoff_phone_number:222-222-2222
         */
        params.put("manifest", deliveryRequest.getManifest());
        params.put("pickup_name", deliveryRequest.getPickUpName());
        params.put("pickup_address", deliveryRequest.getPickUpAddress());
        params.put("pickup_phone_number", deliveryRequest.getPickUpPhoneNumber());
        params.put("dropoff_name", deliveryRequest.getDropOffName());
        params.put("dropoff_address", deliveryRequest.getDropOffAddress());
        params.put("dropoff_phone_number", deliveryRequest.getDropOffPhoneNumber());
        asyncHttpClient.post(createDeliveryUrl, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    DeliveryResponse deliveryResponse = new DeliveryResponse(response);
                    listener.onSuccess(deliveryResponse);
                } catch (JSONException e) {
                    listener.onFailure(e);
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // TODO handle it properly.
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    // https://api.postmates.com/v1/customers/cus_LEDzIm22-R1Ui-/deliveries/del_LEMr-qMVyg1ySF
    public void deliveryById(String deliveryId, DeliveryResponseListener listener) {
//        String deliveryId = "del_LEQh2gz74FV0Wk";
        String deliveryByIdUrl = String.format("%s/customers/%s/deliveries/%s", BASE_URL, CUSTOMER_ID, deliveryId);
        asyncHttpClient.get(deliveryByIdUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    DeliveryResponse deliveryResponse = new DeliveryResponse(response);
                    listener.onSuccess(deliveryResponse);
                } catch (JSONException e) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // TODO handle it properly.
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    // https://api.postmates.com/v1/customers/cus_LEDzIm22-R1Ui-/deliveries/del_LEMr-qMVyg1ySF/cancel
    public void cancelDeliveryById() {
        String deliveryId = "";
        String cancelDeliveryByIdUrl = String.format("%s/customers/%s/deliveries/%s/cancel", BASE_URL, CUSTOMER_ID, deliveryId);
        asyncHttpClient.get(cancelDeliveryByIdUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
