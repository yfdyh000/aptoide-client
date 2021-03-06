package com.aptoide.dataprovider.webservices.models.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsousa on 15/09/15.
 */
    public class GetStoreWidgets {

    public Info info;
    public Datalist datalist;

    public static class Datalist {

        public Number total;
        public Number count;
        public Number next;
        public Number offset;
        public Number limit;
        public Number hidden;
        @JsonProperty("list")
        public List<WidgetList> widgetList = new ArrayList<>();

        public static class WidgetList {

            /** Constants for values of type */
            public static final String ADS_TYPE = "ADS";
            public static final String APPS_GROUP_TYPE = "APPS_GROUP";
            public static final String CATEGORIES_TYPE = "DISPLAYS";
            public static final String TIMELINE_TYPE = "TIMELINE";
            public static final String REVIEWS_TYPE = "REVIEWS";
            public static final String COMMENTS_TYPE = "COMMENTS";
            public static final String STORE_GROUP = "STORES_GROUP";


            public String type;
            public String tag;
            public String title; // Highlighted, Games, Categories, Timeline, Recommended for you, Aptoide Publishers
            @JsonProperty("view")
            public ListViewItems listApps;
            public List<Action> actions = new ArrayList<>();
            public Data data;

            public static class Data {

                public String layout; // GRID, LIST, BRICK
                public String icon;
                public List<Categories> categories = new ArrayList<>(); //only present if type": "DISPLAYS"

                public static class Categories {
                    public Number id;
                    public String ref_id;
                    public String parent_id;
                    public String parent_ref_id;
                    public String name;
                    public String graphic;
                    public String icon;
                    public Number ads_count;
                }
            }

            public static class Action {
                public String type; // button
                public String label;
                public String tag;
                public Event event;

                public static class Event {

                    public static final String GET_STORE_TAB = "getStore";
                    public static final String GET_STORE_WIDGETS_TAB = "getStoreWidgets";
                    public static final String GET_APK_COMMENTS_TAB = "getApkComments";
                    public static final String GET_REVIEWS_TAB = "getReviews";

                    public static final String API_V7_TYPE = "API";
                    public static final String API_V3_TYPE = "v3";

                    public static final String EVENT_LIST_APPS = "listApps";
                    public static final String EVENT_LIST_STORES = "listStores";
                    public static final String EVENT_GETSTOREWIDGETS = "getStoreWidgets";
                    public static final String EVENT_GETAPKCOMMENTS = "getApkComments";

                    public String type; // API, v3
                    public String name; // listApps, getStore, getStoreWidgets, getApkComments
                    public String action;
                }
            }
        }
    }
}
