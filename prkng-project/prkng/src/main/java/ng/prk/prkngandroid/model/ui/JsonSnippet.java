package ng.prk.prkngandroid.model.ui;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import ng.prk.prkngandroid.Const;

public class JsonSnippet {
    private final static String TAG = "JsonSnippet";

    private String title;
    private String id;
    private Integer available;
    private Integer capacity;
    private Integer fuel;
    private String company;
    private String partnerId;
    private boolean isCheckin;
    private boolean isSearch;

    private JsonSnippet(Builder builder) {
        this.title = builder.title;
        this.id = builder.id;
        this.available = builder.available;
        this.capacity = builder.capacity;
        this.fuel = builder.fuel;
        this.company = builder.company;
        this.partnerId = builder.partnerId;
        this.isCheckin = builder.isCheckin;
        this.isSearch = builder.isSearch;
    }

    public static JsonSnippet fromJson(String json) {
        return new Gson().fromJson(json, JsonSnippet.class);
    }

    public static String parseId(String json) {
        return fromJson(json).getId();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(JsonSnippet.class, new SnippetAdapter())
                .create();
    }

    public static String toJson(JsonSnippet snippet) {
        return toJson(snippet, null);
    }

    public static String toJson(JsonSnippet snippet, Gson gson) {
        if (gson == null) {
            gson = getGson();
        }

        return gson.toJson(snippet);
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public int getAvailable() {
        return available == null ? Const.UNKNOWN_VALUE : available;
    }

    public int getCapacity() {
        return capacity == null ? Const.UNKNOWN_VALUE : capacity;
    }

    public int getFuel() {
        return fuel == null ? Const.UNKNOWN_VALUE : fuel;
    }

    public String getCompany() {
        return company;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public boolean isCarshareLot() {
        return !TextUtils.isEmpty(company);
    }

    public boolean isCheckin() {
        return isCheckin;
    }

    public boolean isSearch() {
        return isSearch;
    }

    @Override
    public String toString() {
        return "JsonSnippet{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", available=" + available +
                ", capacity=" + capacity +
                ", fuel=" + fuel +
                ", company='" + company + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", isCheckin=" + isCheckin +
                ", isSearch=" + isSearch +
                '}';
    }

    public static class Builder {
        private String title;
        private String id;
        private int available;
        private int capacity;
        private int fuel;
        private String company;
        private String partnerId;
        private boolean isCheckin;
        private boolean isSearch;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder available(int available) {
            this.available = available;
            return this;
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder fuel(int fuel) {
            Log.v(TAG, "fuel");

            this.fuel = fuel;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder partnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public Builder checkin() {
            this.isCheckin = true;
            return this;
        }

        public Builder search() {
            this.isSearch = true;
            return this;
        }

        public JsonSnippet build() {
            return new JsonSnippet(this);
        }
    }

    public static class SnippetAdapter extends TypeAdapter<JsonSnippet> {

        @Override
        public void write(JsonWriter out, JsonSnippet snippet) throws IOException {
            out.beginObject();
            if (!TextUtils.isEmpty(snippet.getTitle())) {
                out.name("title").value(snippet.getTitle());
            }
            if (!TextUtils.isEmpty(snippet.getId())) {
                out.name("id").value(snippet.getId());
            }
            if (!TextUtils.isEmpty(snippet.getCompany())) {
                // CarShare, Spots and Vehicles
                out.name("company").value(snippet.getCompany());
                if (!TextUtils.isEmpty(snippet.getPartnerId())) {
                    out.name("partnerId").value(snippet.getPartnerId());
                }
                if (snippet.getAvailable() != Const.UNKNOWN_VALUE) {
                    out.name("available").value(snippet.getAvailable());
                }
                if (snippet.getCapacity() != Const.UNKNOWN_VALUE) {
                    out.name("capacity").value(snippet.getCapacity());
                }
                if (snippet.getFuel() != Const.UNKNOWN_VALUE) {
                    out.name("fuel").value(snippet.getFuel());
                }
            }
            if (snippet.isCheckin()) {
                out.name("isCheckin").value(true);
            }
            if (snippet.isSearch()) {
                out.name("isSearch").value(true);
            }
            out.endObject();
        }

        @Override
        public JsonSnippet read(JsonReader in) throws IOException {
            return null;
        }
    }
}
