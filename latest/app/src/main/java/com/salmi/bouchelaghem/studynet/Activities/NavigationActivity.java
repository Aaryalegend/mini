package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;

import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;

import com.salmi.bouchelaghem.studynet.databinding.ActivityNavigationBinding;


public class NavigationActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;
    private final CurrentUser currentUser = CurrentUser.getInstance();
    public ImageView btnFilter;

    //Loading dialog
    private CustomLoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(this);
        btnFilter = binding.btnFilter;
        binding.navigationView.inflateMenu(R.menu.drawer_admin_menu);
//        if (currentUser != null && currentUser.getUserType() != null) {
//            if (currentUser.getUserType().equals(Utils.TEACHER_ACCOUNT)) {
//                // If its a teacher then show the teacher's drawer menu
//                binding.navigationView.getMenu().clear();
//                binding.navigationView.inflateMenu(R.menu.drawer_teacher_menu);
//            } else if (currentUser.getUserType().equals(Utils.ADMIN_ACCOUNT)) {
//                // If its a admin then show the admin drawer menu
//                binding.navigationView.getMenu().clear();
//                binding.navigationView.inflateMenu(R.menu.drawer_admin_menu);
//            }
//        } else {
//            Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
//            // Close the app
//            finishAffinity();
//        }

        // If its a student then the default drawer menu will do
//        binding.btnOpenDrawer.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(binding.navigationView, navController);

        // Change toolbar title to the fragment's title
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> binding.toolBarTitle.setText(destination.getLabel()));

        MenuItem btnLogout = binding.navigationView.getMenu().findItem(R.id.nav_logout);
        btnLogout.setOnMenuItemClickListener(item -> {
            if (currentUser != null && currentUser.getUserType() != null) {
                loadingDialog.show();
            } else {
                // Take the user to the login page.
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(NavigationActivity.this, getString(R.string.logout_msg), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
                finish();
            }
            return true;
        });
    }
}