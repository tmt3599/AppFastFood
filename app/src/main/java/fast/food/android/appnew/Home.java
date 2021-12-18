package fast.food.android.appnew;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import fast.food.android.appnew.Common.Common;
import fast.food.android.appnew.Interface.ItemClickListener;
import fast.food.android.appnew.Service.ListenOrder;
import fast.food.android.appnew.ViewHolder.MenuViewHolder;
import fast.food.android.appnew.model.Category;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recyler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("DANH MỤC");
        setSupportActionBar(toolbar);


        //view swipe
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInterner(getBaseContext()))
                    loadMenu();
                else
                    Toast.makeText(getBaseContext(), "Kiểm tra lại kết nối!!", Toast.LENGTH_SHORT).show();
                return;

            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInterner(getBaseContext()))
                    loadMenu();
                else
                    Toast.makeText(getBaseContext(), "Kiểm tra lại kết nối!!", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        //init FB

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }

        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set Name
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        //Load menu
        recyler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recyler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyler_menu.setLayoutManager(layoutManager);

        loadMenu();


        ///Register service
        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, fast.food.android.appnew.model.Category model, int position) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Category clickitem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get category id
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        //Because Category id is key
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }
        };
        recyler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_Carts) {
            Intent cartIntent = new Intent(this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_add_category) {
            Intent categoryIntent = new Intent(this, AddTheCategoryActivity.class);
            startActivity(categoryIntent);

        } else if (id == R.id.nav_Log_out) {

            Intent SignIn = new Intent(this, Signin.class);
            SignIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(SignIn);

        } else if (id == R.id.nav_change_pwd) {

            String phone = Common.currentUser.getPhone();

            Intent changePassIntent = new Intent(this, ChangePasswordActivity.class);
            changePassIntent.putExtra("phone", phone);
            startActivity(changePassIntent);

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
