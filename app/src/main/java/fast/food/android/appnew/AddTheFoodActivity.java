package fast.food.android.appnew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import fast.food.android.appnew.model.Category;
import fast.food.android.appnew.model.Food;

public class AddTheFoodActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();

    AppCompatImageView imageSelected;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference databaseReferenceCategory = database.getReference("Foods");

    ProgressDialog mDialog = null;

    String categoryId = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            imageSelected.setImageURI(selectedImage);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_the_food);

        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");
        }

        imageSelected = findViewById(R.id.imageSelected);

        mDialog = new ProgressDialog(AddTheFoodActivity.this);
        mDialog.setMessage("Vui lòng chờ ...");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final EditText edtNameProduct = findViewById(R.id.edtNameProduct);
        final EditText edtDescription = findViewById(R.id.edtDescription);
        final EditText edtPrice = findViewById(R.id.edtPrice);
        final EditText edtDiscount = findViewById(R.id.edtDiscount);


        AppCompatTextView textUpload = findViewById(R.id.textUpload);

        textUpload.setPaintFlags(textUpload.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        textUpload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 123);
            }
        });

        AppCompatImageView imgUpload = findViewById(R.id.imgUploadCategory);

        Button btnAddCategory = findViewById(R.id.btnAddCategory);

        btnAddCategory.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                long time= System.currentTimeMillis();

                final String nameUpload = time + ".jpg";

                final StorageReference imageRef = storageRef.child(nameUpload);

                final String edtNameProduction = edtNameProduct.getText().toString().trim();

                if(edtNameProduction.isEmpty() ){
                    Toast.makeText(AddTheFoodActivity.this, "Vui lòng nhập tên sản phẩm!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String edtDescriptionCheck = edtDescription.getText().toString().trim();

                if(edtDescriptionCheck.isEmpty() ){
                    Toast.makeText(AddTheFoodActivity.this, "Vui lòng nhập mô tả!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String edtPriceCheck = edtPrice.getText().toString().trim();

                if(edtPriceCheck.isEmpty() ){
                    Toast.makeText(AddTheFoodActivity.this, "Vui lòng nhập giá!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String edtDiscountCheck = edtDiscount.getText().toString().trim();

                if(edtDiscountCheck.isEmpty() ){
                    Toast.makeText(AddTheFoodActivity.this, "Vui lòng nhập giảm giá!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if( imageSelected.getDrawable() == null){
                    Toast.makeText(AddTheFoodActivity.this, "Vui lòng chọn hình sản phẩm!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mDialog.show();

                // Get the data from an ImageView as bytes
                imageSelected.setDrawingCacheEnabled(true);
                imageSelected.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imageSelected.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ((Bitmap) bitmap).compress(Bitmap.CompressFormat.JPEG, 100,
                        baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = imageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(AddTheFoodActivity.this, "Upload hình thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {

                                    if(mDialog != null){
                                        mDialog.dismiss();
                                    }
                                    Uri downloadUri = task.getResult();

                                    long time = System.currentTimeMillis();

                                    Food category = new Food(edtNameProduction, downloadUri.toString(), edtDescriptionCheck, edtPriceCheck, edtDiscountCheck, categoryId);

                                    databaseReferenceCategory.child(time+"").setValue(category)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(AddTheFoodActivity.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                                                    onBackPressed();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AddTheFoodActivity.this, "Thêm danh mục thất bại!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {

                                }
                            }
                        });

                    }
                });
            }
        });

    }
}