package fast.food.android.appnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);


        if(getIntent() != null){
            phone = getIntent().getStringExtra("phone");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Đổi mật khẩu");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReferenceUser = database.getReference("User");

        final EditText txtPassword = findViewById(R.id.txtPassword);
        final EditText txtPasswordConfirm = findViewById(R.id.txtPasswordConfirm);


        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final String password = txtPassword.getText().toString();
                final String passwordConfirm = txtPasswordConfirm.getText().toString();

                if (password.isEmpty() || passwordConfirm.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu không được để trống!", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseReferenceUser.child(phone).child("password").setValue(password)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ChangePasswordActivity.this, "Thay dổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChangePasswordActivity.this, "Thay dổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });


    }
}