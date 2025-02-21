package com.example.adutucart5.adminActivity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.adutucart5.R;
import com.example.adutucart5.activity.SplashScreen;
import com.example.adutucart5.adapter.CustomerRequestViewAdapter;
import com.example.adutucart5.model.Order;
import com.example.adutucart5.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.messaging.FirebaseMessaging;

public class AdminDashboard extends AppCompatActivity {

    private RecyclerView CustomerRequestRecyclerView;
    private Button EditStoreBtn,LogOutBtn,Orders;

    private DatabaseReference databaseReference;

    CustomerRequestViewAdapter customerRequestViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        getSupportActionBar().hide();

        CustomerRequestRecyclerView=findViewById(R.id.customer_request_table_recycler_view);

//        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        EditStoreBtn = findViewById(R.id.store_btn);
        LogOutBtn = findViewById(R.id.logout_btn);
        Orders = findViewById(R.id.orders_btn);


        FirebaseMessaging.getInstance().subscribeToTopic("pickup")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
                    }
                });
        FirebaseMessaging.getInstance().unsubscribeFromTopic("users");

        Orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboard.this,AdminViewOrders.class));
            }
        });

        LogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashboard.this);

                // Set the message show for the Alert time
                builder.setMessage("Do you want to logout ?");

                // Set Alert Title
                builder.setTitle("Alert !");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    SharedPreferences settings = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                    settings.edit().clear().commit();
                    Intent intent = new Intent(AdminDashboard.this, SplashScreen.class);
                    startActivity(intent);
                    finish();

                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

        CustomerRequestRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        databaseReference = FirebaseDatabase.getInstance().getReference(User.class.getSimpleName());

        Query query = databaseReference.orderByChild("status").equalTo("0");

        // It is a class provide by the FirebaseUI to make a
        // query in the database to fetch appropriate data
        FirebaseRecyclerOptions<User> options
                = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        // Connecting object of required Adapter class to
        // the Adapter class itself
        customerRequestViewAdapter = new CustomerRequestViewAdapter(options,this);
        // Connecting Adapter class with the Recycler view*/
        CustomerRequestRecyclerView.setAdapter(customerRequestViewAdapter);

        EditStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this,AdminEditStore.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        customerRequestViewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customerRequestViewAdapter.stopListening();
    }
}