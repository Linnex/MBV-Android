package com.mbv.pokket.dao.interfaces;

import com.mbv.pokket.dao.enums.ServerEvents;

/**
 * Created by arindamnath on 28/12/15.
 */
public interface ServerResponseListener {
    void onSuccess(int threadId, Object object);
    void onFaliure(ServerEvents serverEvents, Object object);
}
