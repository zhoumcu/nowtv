package com.pccw.nowplayer.model;

import com.pccw.nowplayer.model.exceptions.LibraryException;
import com.pccw.nowplayer.model.node.Node;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;
import com.pccw.nowtv.nmaf.npx.catalog.NPXCatalog;

import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.impl.DeferredObject;

/**
 * Created by kriz on 20/7/2016.
 */
public class SearchClient {
    private static SearchClient instance;

    private SearchClient() {
    }

    public static SearchClient getInstance() {
        if (instance == null) {
            synchronized (SearchClient.class) {
                if (instance == null) instance = new SearchClient();
            }
        }
        return instance;
    }

    public Promise<Node, Throwable, Float> search(String query) {

        final DeferredObject<Node, Throwable, Float> deferred = new DeferredObject<>();

        NPXCatalog.getSharedInstance().epgAutocompleteSearch(query, new NPXCatalog.NPXCatalogCallback<DataModels.NPXEpgAutocompleteSearchDataModel>() {
            @Override
            public void onRequestFailed(int i) {
                deferred.reject(new LibraryException(i));
            }

            @Override
            public void onRequestSuccessful(DataModels.NPXEpgAutocompleteSearchDataModel response) {
                deferred.resolve(Node.create(response, null));
            }
        });

        return new AndroidDeferredObject<>(deferred);
    }
}
