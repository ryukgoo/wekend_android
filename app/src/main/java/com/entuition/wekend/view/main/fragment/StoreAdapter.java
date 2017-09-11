package com.entuition.wekend.view.main.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.googleservice.billing.GoogleBillingController;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 8. 31..
 */
public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private Context context;
    private ItemClickListener listener;
    private List<String> arrPrice;
    private List<String> arrBonus;

    public StoreAdapter(Context context) {
        this.context = context;
        arrPrice = Arrays.asList(context.getResources().getStringArray(R.array.store_price));
        arrBonus = Arrays.asList(context.getResources().getStringArray(R.array.store_bonus));
    }

    public void setClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gridlist_item_store, parent, false);
        StoreViewHolder viewHolder = new StoreViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, int position) {
        holder.txtPrice.setText(arrPrice.get(position));
        holder.txtBonus.setText(arrBonus.get(position));
    }

    @Override
    public int getItemCount() {
        return GoogleBillingController.getInstance(context).getSkuList().size();
    }

    public interface ItemClickListener {
        void onItemClicked(View view, int position);
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtPrice;
        private TextView txtBonus;

        public StoreViewHolder(View itemView) {
            super(itemView);

            txtPrice = (TextView) itemView.findViewById(R.id.id_griditem_store_price);
            txtBonus = (TextView) itemView.findViewById(R.id.id_griditem_store_bonus);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(v, getAdapterPosition());
            }
        }
    }
}
