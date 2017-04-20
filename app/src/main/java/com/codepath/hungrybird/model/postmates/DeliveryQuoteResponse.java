package com.codepath.hungrybird.model.postmates;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

import static com.codepath.hungrybird.common.DateUtils.toDate;

/**
 * Created by ajasuja on 4/18/17.
 *
 *
 {
 "kind": "delivery_quote",
 "fee": 850,
 "created": "2017-04-18T11:22:04Z",
 "expires": "2017-04-18T11:27:04Z",
 "currency": "usd",
 "duration": 90,
 "dropoff_eta": "2017-04-18T12:57:04Z",
 "id": "dqt_LEMqr95gikvukF"
 }
 */

public class DeliveryQuoteResponse {

    private String quoteId;
    private double fee;
    private Date created;
    private Date expires;
    private int duration;
    private Date dropOffEta;

    public DeliveryQuoteResponse(JSONObject jsonObject) throws JSONException, ParseException {
        this.quoteId = jsonObject.getString("id");
        this.fee = jsonObject.getDouble("fee");
        this.created = toDate(jsonObject.getString("created"));
        this.expires = toDate(jsonObject.getString("expires"));
        this.duration = jsonObject.getInt("duration");
        this.dropOffEta = toDate(jsonObject.getString("dropoff_eta"));
    }

    public String getQuoteId() {
        return quoteId;
    }

    public double getFee() {
        return fee;
    }

    public Date getCreated() {
        return created;
    }

    public Date getExpires() {
        return expires;
    }

    public int getDuration() {
        return duration;
    }

    public Date getDropOffEta() {
        return dropOffEta;
    }
}
