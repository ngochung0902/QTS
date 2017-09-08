package com.peppa.app.listener;

/**
 * Created by Rup barad on 25-11-2016.
 */
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;


    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    private int startingPageIndex = 0;
    private int currentPage = 0;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView
                .getLayoutManager();

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLayoutManager.getItemCount();
        firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
        onScroll(firstVisibleItem, visibleItemCount, totalItemCount);

    }
    public void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount)
        {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0)
            {
                this.loading = true;
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount))
        {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem +
                visibleThreshold))
        {
            onLoadMore(currentPage + 1);
            loading = true;
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page);


   // public abstract void onLoadMore(int current_page);
}