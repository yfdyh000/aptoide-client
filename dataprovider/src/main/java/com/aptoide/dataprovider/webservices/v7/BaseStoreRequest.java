package com.aptoide.dataprovider.webservices.v7;

import android.support.annotation.NonNull;

import com.aptoide.dataprovider.exceptions.MalformedActionUrlException;
import com.aptoide.dataprovider.exceptions.TicketException;
import com.aptoide.dataprovider.webservices.interfaces.v7.IGetStoreV7WebService;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.Defaults;
import com.aptoide.dataprovider.webservices.models.StoreHomeTab;
import com.aptoide.dataprovider.webservices.models.v7.Apiv7;
import com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets;
import com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.Datalist.WidgetList.Action;
import com.aptoide.dataprovider.webservices.models.v7.ListViewItems;
import com.aptoide.dataprovider.webservices.models.v7.ViewItem;
import com.aptoide.models.displayables.AppItem;
import com.aptoide.models.displayables.BrickAppItem;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.EditorsChoiceRow;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.HomeStoreItem;
import com.aptoide.models.displayables.CategoryRow;
import com.aptoide.models.displayables.AdPlaceHolderRow;
import com.aptoide.models.displayables.CommentPlaceHolderRow;
import com.aptoide.models.displayables.ReviewPlaceHolderRow;
import com.aptoide.models.displayables.TimeLinePlaceHolderRow;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.List;

import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.Datalist.WidgetList.ADS_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.Datalist.WidgetList.APPS_GROUP_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.Datalist.WidgetList.CATEGORIES_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.Datalist.WidgetList.COMMENTS_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.Datalist.WidgetList.REVIEWS_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.Datalist.WidgetList.STORE_GROUP;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.Datalist.WidgetList.TIMELINE_TYPE;

/**
 * Created by hsousa on 17/09/15.
 */
public abstract class BaseStoreRequest<T> extends RetrofitSpiceRequest<StoreHomeTab, IGetStoreV7WebService> {
    public static final String WS2 = "http://";
    public static final String SWS2 = "https://";

    public String nview;
    public String context;
    public String filters;
    public boolean mature;
    //public String country; // country is being validated server-side
    public String lang;
    public int totalSpanSize;
    public int aptoideVercode;

    public boolean loggedIn;

    public int singleSpanSize;
    public int numColumns;

    public BaseStoreRequest(int numColumns) {
        super(StoreHomeTab.class, IGetStoreV7WebService.class);
        this.numColumns = numColumns;
        this.totalSpanSize = numColumns * 2;
        this.singleSpanSize = totalSpanSize / numColumns;
    }

    @Override
    public StoreHomeTab loadDataFromNetwork() throws Exception {

        T response = getResponse(getApi());

        return bind(response);
    }

    public abstract StoreHomeTab bind(T response);


    protected abstract T getResponse(Apiv7 api) throws TicketException;

    public Apiv7 getApi() {
        Apiv7 api = new Apiv7();
        api.nview = nview;
        api.context = context;
        api.q = filters;
        api.mature = mature;
        api.lang = lang;
        api.aptoide_vercode = aptoideVercode;

        Apiv7.WidgetParams paramsApps = new Apiv7.WidgetParams("APPS_GROUP");
        paramsApps.grid_row_size = numColumns;

        Apiv7.WidgetParams paramsStores = new Apiv7.WidgetParams("STORES_GROUP");
        paramsStores.grid_row_size = 2;

        api.setApiParams(paramsStores);
        api.setApiParams(paramsApps);

        return api;
    }


    protected void parseWidgetList(StoreHomeTab tab, List<GetStoreWidgets.Datalist.WidgetList> list) {
        long storeId = -1;
        try {
            storeId = tab.store.nodes.meta.data.id.longValue();
        } catch (Exception e) {
            // on getStoreWidgets there's no storeId concept
        }

        for (GetStoreWidgets.Datalist.WidgetList widget : list) {

            switch (widget.type) {
                case APPS_GROUP_TYPE:

                    // Only create widget if it has apps
                    if (widget.listApps.datalist.itemView != null && !widget.listApps.datalist.itemView.isEmpty()) {

                        // if layout type == BRICK, (for now) it can only be an EditorsChoice.
                        if (widget.data != null && widget.data.layout != null && widget.data.layout.equals(Constants.LAYOUT_BRICK)) {

                            tab.list.add(createFeaturedEditorsChoice(widget.listApps.datalist.itemView, widget.actions, storeId, widget.data.layout));
                        } else {

                            Displayable headerRow = createHeaderRow(widget.title, widget.tag, true, widget.actions, storeId, widget.data.layout);
                            if (headerRow != null) {
                                tab.list.add(headerRow);
                            }

                            for (ViewItem itemList : widget.listApps.datalist.itemView) {
                                tab.list.add(createAppItem(itemList));
                            }
                        }
                    }
                    break;
                case STORE_GROUP:

                    // Only create widget if it has apps
                    if (widget.listApps.datalist.itemView != null && !widget.listApps.datalist.itemView.isEmpty()) {

                        Displayable headerRow = createHeaderRow(widget.title, widget.tag, true, widget.actions, storeId, widget.data.layout);
                        if (headerRow != null) {
                            tab.list.add(headerRow);
                        }

                        for (ViewItem itemList : widget.listApps.datalist.itemView) {
                            tab.list.add(createStoreItem(itemList));
                        }
                    }
                    break;
                case REVIEWS_TYPE:
                    tab.list.add(new ReviewPlaceHolderRow(numColumns));

                    break;
                case ADS_TYPE:
                    tab.list.add(new AdPlaceHolderRow(numColumns));

                    break;
                case CATEGORIES_TYPE:
                    if (widget.listApps.list != null && !widget.listApps.list.isEmpty()) {
                        tab.list.addAll(createCategoryList(widget.listApps.list));
                    }

                    break;
                case TIMELINE_TYPE:
                    if (loggedIn) {
                        tab.list.add(new TimeLinePlaceHolderRow(numColumns));
                    }

                    break;
                case COMMENTS_TYPE:
                    tab.list.add(new CommentPlaceHolderRow(numColumns));
                    break;
            }
        }
    }

    protected HomeStoreItem createStoreItem(ViewItem store) {

        HomeStoreItem storeItem = new HomeStoreItem(numColumns);
        storeItem.setSpanSize(singleSpanSize);
        storeItem.id = store.id.longValue();
        storeItem.repoName = store.name;
        storeItem.avatar = store.avatar;
        storeItem.modified = store.modified;
        storeItem.added = store.added;
        storeItem.description = store.appearance.description;
        storeItem.theme = store.appearance.theme;
        storeItem.view = store.appearance.view;
        storeItem.storeApps = store.stats.apps.longValue();
        storeItem.storeDwnNumber = store.stats.downloads.longValue();
        storeItem.storeSubscribers = store.stats.subscribers.longValue();

        return storeItem;
    }

    protected AppItem createAppItem(ViewItem item) {
        return createAppItem(item, null);
    }

    protected AppItem createAppItem(ViewItem item, AppItem appItem){

        if (appItem == null) {
            appItem = new AppItem(numColumns);
        }

        appItem.setSpanSize(singleSpanSize);
        appItem.appName = item.name;
        appItem.downloads = item.stats.downloads.longValue();
        appItem.icon = item.icon;
        appItem.rating = item.stats.rating.avg.floatValue();
        appItem.storeName = item.store.name;
        appItem.storeId = item.store.id.longValue();
        appItem.id = item.id.intValue();
        appItem.fileSize = item.size.longValue();
        appItem.featuredGraphic = item.graphic;
        appItem.md5sum = item.file.md5sum;
        appItem.packageName = item.packageName;
        appItem.versionName = item.file.vername;
        appItem.modified = item.modified;
        appItem.updated = item.updated;

        return appItem;
    }

    protected BrickAppItem createBrickItem(ViewItem item){

        BrickAppItem appItem = new BrickAppItem(numColumns);
        createAppItem(item, appItem);

        return appItem;
    }


    /**
     * The Action with button is turned into an headerRow
     * @param name
     * @param hasMore
     * @param actions
     * @return
     */
    protected HeaderRow createHeaderRow(String name, String tag, boolean hasMore, List<Action> actions, long storeId, String layout) {

        if (actions != null && !actions.isEmpty()) {
            for (Action action : actions) {
                if (action != null && action.event != null && action.event.action != null && action.type.equals("button")) {
                    return createHeaderRow(name, tag, hasMore, action, storeId, layout);
                }
            }
        } else {
            return new HeaderRow(name, false, numColumns);
        }
        return null;
    }

    private HeaderRow createHeaderRow(String name, String tag, boolean hasMore, Action action, long storeId, String layout) {

        HeaderRow header = new HeaderRow(name, tag, hasMore, action.event.action, action.event.type, action.event.name, layout, numColumns, storeId == Defaults.DEFAULT_STORE_ID, storeId);
        header.setSpanSize(totalSpanSize);

        return header;
    }

    public Displayable createFeaturedEditorsChoice(List<ViewItem> itemList, List<Action> actions, long storeId, String layout) {

        EditorsChoiceRow row;

        if (actions != null && actions.size() > 0) {
            Action action = actions.get(0);
            row = new EditorsChoiceRow(numColumns, true, action.event.action, action.label, action.tag, action.type, action.event.name, layout, storeId == Defaults.DEFAULT_STORE_ID, storeId);
        } else {
            row = new EditorsChoiceRow(numColumns);
        }

        row.setSpanSize(totalSpanSize);
        for (ViewItem apk : itemList) {
            row.appItemList.add(createAppItem(apk));
        }

        return row;
    }

    private List<Displayable> createCategoryList(List<ListViewItems.DisplayList> list) {
        List<Displayable> displayables = new ArrayList<>();

        for (ListViewItems.DisplayList display : list) {

            CategoryRow categ = new CategoryRow(numColumns);
            categ.setSpanSize(singleSpanSize);
            categ.setLabel(display.label);
            categ.setGraphic(display.graphic);
            categ.setEventType(display.event.type);
            categ.setEventName(display.event.name);
            categ.setEventActionUrl(display.event.action);
            displayables.add(categ);
        }

        return displayables;
    }



    @NonNull
    protected String setActionUrl(String actionUrl) throws MalformedActionUrlException {

        String tmp = "";
        if (actionUrl.startsWith(WS2)) {
            tmp = actionUrl.replace(WS2, "");
        } else if (actionUrl.startsWith(SWS2)) {
            tmp = actionUrl.replace(SWS2, "");
        }

        return tmp;
    }

}
