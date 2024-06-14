package com.phinse.prm392.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.phinse.prm392.R;
import com.phinse.prm392.databinding.ActivityHomeBinding;
import com.phinse.prm392.service.model.Product;
import com.phinse.prm392.ui.auth.FacebookSignInActivity;
import com.phinse.prm392.ui.chatbox.ChatBoxActivity;
import com.phinse.prm392.ui.home.adapter.ProductRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ActivityHomeBinding binding;
    private MutableLiveData<List<Product>> products = new MutableLiveData<>(new ArrayList<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        setupView();
        setupObserver();

    }

    private void setupView() {
        fetchData();

        RecyclerView rvProduct = binding.rvProduct;
        rvProduct.setAdapter(new ProductRecyclerViewAdapter(products.getValue(), item -> {
        }));
        rvProduct.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                if (searchView != null) {
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            return false;
                        }
                    });
                }
            } else if (item.getItemId() == R.id.action_cart) {

            } else if (item.getItemId() == R.id.action_logout) {
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, FacebookSignInActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.action_chatbox) {
                Intent intent = new Intent(HomeActivity.this, ChatBoxActivity.class);
                startActivity(intent);
            }
            return true;
        });
    }

    private void fetchData() {
//        PublicRetrofit.getProductApi().getProducts().enqueue(new retrofit2.Callback<List<Product>>() {
//            @Override
//            public void onResponse(retrofit2.Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
//                products.setValue(response.body());
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
//                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Product> productList = new ArrayList<>();
                task.getResult().getDocuments().forEach(document -> {
                    productList.add(document.toObject(Product.class));
                });
                products.setValue(productList);
            } else {
                Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupObserver() {
        products.observe(this, products -> {
            RecyclerView rvProduct = binding.rvProduct;
            ProductRecyclerViewAdapter adapter = (ProductRecyclerViewAdapter) rvProduct.getAdapter();
            assert adapter != null;
            adapter.setList(products);
            adapter.setOnItemClickListener(
                    item -> {
                        Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra("product", gson.toJson(item));
                        startActivity(intent);
                    }
            );
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            MenuItem searchItem = findViewById(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            if (searchView != null) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
            }
        } else if (item.getItemId() == R.id.action_cart) {

        } else if (item.getItemId() == R.id.action_logout) {

        }
        return super.onOptionsItemSelected(item);
    }
}