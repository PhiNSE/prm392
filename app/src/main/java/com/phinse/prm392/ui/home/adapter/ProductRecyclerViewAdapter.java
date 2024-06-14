package com.phinse.prm392.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.phinse.prm392.R;
import com.phinse.prm392.service.model.Product;

import org.w3c.dom.Text;

import java.util.List;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductViewHolder> {

    private List<Product> list;

    public void setList(List<Product> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener<Product> {
        void onItemClick(Product item);
    }

    private OnItemClickListener<Product> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<Product> listener) {
        this.onItemClickListener = listener;
    }

    public ProductRecyclerViewAdapter(List<Product> list, OnItemClickListener<Product> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.list = list;
    }

    @NonNull
    @Override
    public ProductRecyclerViewAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerViewAdapter.ProductViewHolder holder, int position) {
        Product item = list.get(position);
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
        holder.tvProductName.setText(item.getName());
        Glide.with(holder.itemView.getContext()).load(item.getImage()).into(holder.ivProduct);
        holder.tvProductPrice.setText(String.valueOf(item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProductName;
        public ImageView ivProduct;

        public TextView tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);

        }

    }
}
