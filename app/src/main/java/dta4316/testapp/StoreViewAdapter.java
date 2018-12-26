package dta4316.testapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.models.nosql.StoreDO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import dta4316.testapp.Common.Common;

public class StoreViewAdapter extends RecyclerView.Adapter<StoreViewAdapter.ViewHolder> implements Filterable {
    private Context m_Context;
    private List<StoreDO> m_StoreList;
    private List<StoreDO> m_FilteredStoreList;
    private LayoutInflater m_Inflater;
    private StoreViewClickedListener m_ClickListener;

    public StoreViewAdapter(Context context, List<StoreDO> storeList) {
        m_Context = context;
        this.m_Inflater = LayoutInflater.from(context);
        this.m_StoreList = storeList;
        this.m_FilteredStoreList = storeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = m_Inflater.inflate(R.layout.view_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StoreDO store = m_FilteredStoreList.get(position);

        holder.tvName.setText(store.getStoreName());
        holder.tvDistance.setText(Common.GetDistanceText(store.getStoreDistance()));
        Glide.with(m_Context)
                .load(Common.GetHeaderImageURL(store.getStoreHeaderImage()))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return m_FilteredStoreList == null ? 0 : m_FilteredStoreList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    m_FilteredStoreList = m_StoreList;
                } else {
                    List<StoreDO> filteredList = new ArrayList<>();
                    for (StoreDO store : m_StoreList) {
                        if (store.getStoreName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(store);
                        }
                    }
                    m_FilteredStoreList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = m_FilteredStoreList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                m_FilteredStoreList = (List<StoreDO>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        TextView tvDistance;
        ImageView ivThumbnail;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            ivThumbnail = itemView.findViewById(R.id.thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (m_ClickListener != null) m_ClickListener.onStoreViewClicked(view, getAdapterPosition());
        }
    }

    public String GetItem(int index){
        String locationName = m_FilteredStoreList.get(index).getStoreName();
        return locationName;
    }

    public StoreDO GetStore(int index){
        if(index >=0 && m_FilteredStoreList != null && index < m_FilteredStoreList.size()) {
            return m_FilteredStoreList.get(index);
        }
        return null;
    }

    public void SetClickListener(StoreViewClickedListener itemClickListener) {
        this.m_ClickListener = itemClickListener;
    }

    public interface StoreViewClickedListener {
        void onStoreViewClicked(View view, int position);
    }
}
