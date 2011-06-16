package com.joe.utilities.core.listhandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchResult<REC> extends ArrayList<REC> implements List<REC> {

    private int qryCount;

    /**
     * @param initialCapacity
     */
    public SearchResult(int initialCapacity) {
        super(initialCapacity);
        qryCount = 0;
    }

    public SearchResult() {
        super();
        qryCount = 0;
    }

    public SearchResult(Collection<? extends REC> arg0, int qryCount) {
        if (arg0 != null) {
            this.addAll(arg0);
        }
        this.qryCount = qryCount;
    }

    public int getQryCount() {
        return qryCount;
    }

    public void setQryCount(int qryCount) {
        this.qryCount = qryCount;
    }
}
