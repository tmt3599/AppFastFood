package fast.food.android.appnew.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fast.food.android.appnew.Interface.ItemClickListener;
import fast.food.android.appnew.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name;
    public ImageView food_image,fav_image,share_image;

    private ItemClickListener itemClickListener;

    public FoodViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
        super(itemView);

        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        food_name = (TextView)itemView.findViewById(R.id.food_name);
        food_image = (ImageView)itemView.findViewById(R.id.food_image);
        fav_image = (ImageView)itemView.findViewById(R.id.fav);
        share_image = (ImageView)itemView.findViewById(R.id.btnShare);

        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;

    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
