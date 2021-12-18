package fast.food.android.appnew;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import fast.food.android.appnew.Common.Common;
import fast.food.android.appnew.model.User;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone, edtName, edtPassword;
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhone= (MaterialEditText)findViewById(R.id.edtPhone);
        edtName= (MaterialEditText)findViewById(R.id.edtName);
        edtPassword= (MaterialEditText)findViewById(R.id.edtPassword);

        btnSignUp=(Button)findViewById(R.id.btnSignUp);

        //init firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInterner(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Vui Lòng Chờ...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                //Toast.makeText(SignUp.this, "Số điện thoại đã được đăng ký", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(edtName.getText().toString(), edtPassword.getText().toString());
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                //Toast.makeText(SignUp.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                Intent SignIn = new Intent(SignUp.this, Signin.class);
                                SignIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(SignIn);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else {
                    Toast.makeText(SignUp.this, "Check connettion!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
