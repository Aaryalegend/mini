package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Models.Department;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Specialty;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.Serializers;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivitySignUpBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Collections;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private List<Department> departments;
    private List<Specialty> specialties;
    private List<Section> sections;

    // Flags
    private boolean departmentSelected = false;
    //    private boolean specialitySelected = false;
    private boolean sectionSelected = false;
    private boolean groupSelected = false;

    // Fields
    private int group;
    private Section studentSection;

    private CustomLoadingDialog loadingDialog;

    // Studynet Api
    private StudynetAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loadingDialog = new CustomLoadingDialog(SignUpActivity.this);
        Gson gson = new GsonBuilder().setLenient().create();

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Init our api, this will implement the code of all the methods in the interface
        api = retrofit.create(StudynetAPI.class);

        // Setup departments list
        getAllDepartments();

        binding.btnSignUp.setOnClickListener(v -> performSignup());

        // When the user chooses a department
//        binding.txtDepartment.setOnItemClickListener((parent, view14, position, id) -> {
//            // Get the selected item
//            departmentSelected = true;
//            binding.departmentTextInputLayout.setError(null);
//
//            // Disable other spinners
//            binding.txtSpeciality.setText("");
//            specialitySelected = false;
//
//            binding.sectionTextInputLayout.setEnabled(false);
//            binding.txtSection.setText("");
//            sectionSelected = false;
//
//            binding.groupTextInputLayout.setEnabled(false);
//            binding.txtGroup.setText("");
//            groupSelected = false;
//
//            // Set up the specialities spinner
//            binding.specialityTextInputLayout.setEnabled(true);
//            setupSpecialitiesSpinner(departments.get(position));
//        });

        // When the user chooses a speciality
        binding.txtDepartment.setOnItemClickListener((parent, view13, position, id) -> {
//             Get the selected item
            departmentSelected = true;
            binding.departmentTextInputLayout.setError(null);

//             Disable other spinners
            binding.txtSection.setText("");
            sectionSelected = false;

//            binding.groupTextInputLayout.setEnabled(false);
//            binding.txtGroup.setText("");
//            groupSelected = false;

            // Set up the sections spinner
            binding.sectionTextInputLayout.setEnabled(true);
            setupSectionsSpinner();
                        sectionSelected = true;
        });

        // When the user chooses a section
//        binding.txtSection.setOnItemClickListener((parent, view12, position, id) -> {
//            // Get the selected item
//            sectionSelected = true;
////            studentSection = sections.get(position);
////            binding.sectionTextInputLayout.setError(null);
//            // Disable other spinners
//            binding.txtGroup.setText("");
//            groupSelected = false;
//
//            // Set up the groups spinner
//            binding.groupTextInputLayout.setEnabled(true);
////            setupGroupsSpinner();
//        });

        // When the user chooses a group
//        binding.txtGroup.setOnItemClickListener((parent, view1, position, id) -> {
//            // Get the selected item
//            groupSelected = true;
//            group = position + 1;
//            binding.groupTextInputLayout.setError(null);
//        });

    }

//    private void setupGroupsSpinner() {
//        // Create a list with one group
//        List<String> groups = new ArrayList<>();
//        groups.add("1"); // Assuming there is only one group
//
//        // Set up the spinner
//        ArrayAdapter<String> groupsAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, groups);
//        binding.txtGroup.setAdapter(groupsAdapter);
//    }

    // Get all the sections for the selected spec
    private void setupSectionsSpinner() {
        // Get reference to Firestore collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference sectionsRef = db.collection("sections");

        // Query Firestore for the "SY" section
        sectionsRef.document("SY")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve section name from Firestore document
                            String sectionName = documentSnapshot.getString("code");
                            if (sectionName != null) {
                                // Set up the spinner
                                ArrayAdapter<String> sectionsAdapter = new ArrayAdapter<>(SignUpActivity.this, R.layout.dropdown_item, Collections.singletonList(sectionName));
                                binding.txtSection.setAdapter(sectionsAdapter);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Section name is null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Section not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Error fetching section: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Get all the specialities for the selected department
//    private void setupSpecialitiesSpinner(Department department) {
//        Call<List<Specialty>> call = api.getSpecialities(department.getCode());
//        call.enqueue(new Callback<List<Specialty>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Specialty>> call, @NonNull Response<List<Specialty>> response) {
//                if (response.isSuccessful()) {
//                    specialties = response.body();
//
//                    // Get names
//                    List<String> specialitiesNames = new ArrayList<>();
//                    for (Specialty s : specialties) {
//                        specialitiesNames.add(s.getCode());
//                    }
//
//                    // Set up the spinner
//                    ArrayAdapter<String> specialitiesAdapter = new ArrayAdapter<>(SignUpActivity.this, R.layout.dropdown_item, specialitiesNames);
//                    binding.txtSpeciality.setAdapter(specialitiesAdapter);
//                } else {
//                    Toast.makeText(SignUpActivity.this, getString(R.string.error) + response.code(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<Specialty>> call, @NonNull Throwable t) {
//                Toast.makeText(SignUpActivity.this, getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void getAllDepartments() {
        // Get reference to Firestore collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference departmentsRef = db.collection("departments");

        // Query Firestore for departments
        departmentsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<String> departmentNames = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Retrieve document ID (department name)
                        String departmentName = documentSnapshot.getId();
                        // Add department name (document ID) to the list
                        departmentNames.add(departmentName);
                    }

                    // Set up spinner
                    ArrayAdapter<String> departmentsAdapter = new ArrayAdapter<>(SignUpActivity.this, R.layout.dropdown_item, departmentNames);
                    binding.txtDepartment.setAdapter(departmentsAdapter);
                } else {
                    Toast.makeText(SignUpActivity.this, "No departments found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Error fetching departments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateRegistrationNumber() {
        String regNumber = binding.txtRegistrationNumber.getEditText().getText().toString().trim();

        if (regNumber.isEmpty()) {
            binding.txtRegistrationNumber.setError(getString(R.string.empty_reg_number_msg));
            return false;
        } else {
            binding.txtRegistrationNumber.setError(null);
            return true;
        }
    }

    public boolean validateFirstName() {
        String firstName = binding.txtFirstName.getEditText().getText().toString().trim();

        if (firstName.isEmpty()) {
            binding.txtFirstName.setError(getString(R.string.empty_first_name_msg));
            return false;
        } else {
            binding.txtFirstName.setError(null);
            return true;
        }
    }

    public boolean validateLastName() {
        String lastName = binding.txtLastName.getEditText().getText().toString().trim();

        if (lastName.isEmpty()) {
            binding.txtLastName.setError(getString(R.string.empty_last_name_msg));
            return false;
        } else {
            binding.txtLastName.setError(null);
            return true;
        }
    }

    public boolean validateEmail() {
        String email = binding.txtEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            binding.txtEmail.setError(getString(R.string.email_msg1));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtEmail.setError(getString(R.string.email_msg2));
            return false;
        } else {
            binding.txtEmail.setError(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String password = binding.txtPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            binding.txtPassword.setError(getString(R.string.empty_password_msg));
            return false;
        } else if (password.length() < 6) {
            binding.txtPassword.setError(getString(R.string.password_msg2));
            return false;
        } else {
            binding.txtPassword.setError(null);
            return true;
        }
    }

    private void performSignup() {
        if (validateRegistrationNumber() & validateFirstName() & validateLastName() & validateEmail() & validatePassword() &
                departmentSelected & sectionSelected /*& specialitySelected*/ /*& groupSelected*/) {

            loadingDialog.show();
            String registrationNumber = binding.txtRegistrationNumber.getEditText().getText().toString().trim();
            String firstName = binding.txtFirstName.getEditText().getText().toString().trim();
            String lastName = binding.txtLastName.getEditText().getText().toString().trim();
            String email = binding.txtEmail.getEditText().getText().toString().trim();
            //Get the raw password (no trim either).
            String password = binding.txtPassword.getEditText().getText().toString();

            // Use Firebase Authentication for Google Sign-In
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Store user data in Firestore
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("firstName", firstName);
                                    userData.put("lastName", lastName);
                                    userData.put("registrationNumber", registrationNumber);
                                    userData.put("email", email);

                                    // Add user data to Firestore
                                    db.collection("users").document(user.getUid())
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // User data saved successfully
                                                    Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                    // Save user data locally
                                                    loadingDialog.dismiss();
                                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
//                                                    saveCurrentUser();
                                                    // Proceed with your app flow
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Error saving user data to Firestore
                                                    Toast.makeText(SignUpActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                                                    loadingDialog.dismiss();
                                                }
                                            });
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        }
                    });
        } else {
            if (!departmentSelected) {
                binding.departmentTextInputLayout.setError(getString(R.string.empty_department_msg));
            }
            // if (!specialitySelected) {
            //     binding.specialityTextInputLayout.setError(getString(R.string.empty_speciality_msg));
            // }
            if (!sectionSelected) {
                binding.sectionTextInputLayout.setError(getString(R.string.empty_section_msg));
            }
            // if (!groupSelected) {
            //     binding.groupTextInputLayout.setError(getString(R.string.empty_group_msg));
            // }
        }
    }

//    private void saveCurrentUser() {
//        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.SHARED_PREFERENCES_USER_DATA, MODE_PRIVATE);
//        CurrentUser currentUser = CurrentUser.getInstance();
//
//        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
//        // Save the token
//        prefsEditor.putString(Utils.SHARED_PREFERENCES_TOKEN, currentUser.getToken());
//        prefsEditor.putBoolean(Utils.SHARED_PREFERENCES_LOGGED_IN, true);
//        prefsEditor.apply();
//    }
}