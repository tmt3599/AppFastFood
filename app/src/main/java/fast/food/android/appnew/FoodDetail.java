package fast.food.android.appnew;

import androidx.annotation.NonNull;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import fast.food.android.appnew.Database.Database;
import fast.food.android.appnew.model.Food;
import fast.food.android.appnew.model.Order;

public class FoodDetail extends AppCompatActivity {

    TextView food_name, food_price, food_description;
    ImageView food_img;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String foodId = "";
    FirebaseDatabase database;
    DatabaseReference foods;

    Toolbar toolbar;


    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);


        //firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");

        toolbar =  findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //init viewn
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));

                Toast.makeText(FoodDetail.this, "THÊM VÀO GIỎ HÀNG", Toast.LENGTH_SHORT).show();
            }
        });

        food_description = (TextView) findViewById(R.id.food_description);
        food_name = (TextView) findViewById(R.id.food_name);
        food_price = (TextView) findViewById(R.id.food_price);

        food_img = (ImageView) findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);

        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);


        //get food id from intent
        if (getIntent() != null) {
            foodId = getIntent().getStringExtra("FoodId");
            if (!foodId.isEmpty()) {
                getDetailFood(foodId);
            }

        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                //set Img
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_img);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());

                food_name.setText(currentFood.getName());

                food_description.setText(currentFood.getDecscription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
