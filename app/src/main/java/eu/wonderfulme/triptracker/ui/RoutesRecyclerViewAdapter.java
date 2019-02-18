package eu.wonderfulme.triptracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationHeaderData;

public class RoutesRecyclerViewAdapter extends RecyclerView.Adapter<RoutesRecyclerViewAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(LocationHeaderData item);
    }

    private ItemClickListener mItemClickListener;
    private final List<LocationHeaderData> mItems;
    private final Context mContext;
    private boolean mIsClickable;

    public RoutesRecyclerViewAdapter(Context context, List<LocationHeaderData> items) {
        this.mItems = items;
        this.mContext = context;
        this.mIsClickable = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_route, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        if (mItems != null)
            return mItems.size();
        else
            return 0;
    }

    void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    void setClickable(boolean clickable) { mIsClickable = clickable;}

    void swapData(List<LocationHeaderData> newData) {
        if (newData == null || newData.size() == 0) {
            mItems.clear();
        } else {
            mItems.clear();
            mItems.addAll(newData);
        }
        notifyDataSetChanged();
    }



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_route_name) TextView mRouteNamesTextView;
        LocationHeaderData mItem;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemKey = mItem.getItem_key();
            if(mItemClickListener != null && mIsClickable &&
                    itemKey != -1 && !mRouteNamesTextView.getText().toString().isEmpty())
                mItemClickListener.onItemClick(mItem);
        }

        void bindView(LocationHeaderData item) {
            mItem = item;
            mRouteNamesTextView.setText(item.getRouteName());
        }
    }
}
