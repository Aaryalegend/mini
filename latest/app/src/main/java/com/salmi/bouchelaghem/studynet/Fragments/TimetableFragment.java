package com.salmi.bouchelaghem.studynet.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Activities.AddClassActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.SessionsAdapter;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Session;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTimetableBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimetableFragment extends Fragment {

    private FragmentTimetableBinding binding;

    // Days
    private boolean firstTime = true;
    private int currentDay = 1;
    private List<String> days;

    // Rec view
    private List<Session> sessions;
    private SessionsAdapter adapter;

    // Filter
    private Dialog dialog;
    private boolean sectionSelected = false;
    private String selectedSection;
    private boolean filterApplied = true;
    private List<String> allSections;

    // Current user
    private final CurrentUser currentUser = CurrentUser.getInstance();
    private final String currentUserType = currentUser.getUserType();

    // Studynet Api
    private StudynetAPI api;

    private CustomLoadingDialog loadingDialog;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initRecView();

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api
        api = retrofit.create(StudynetAPI.class);

        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;

        binding.selectSectionMsg.setVisibility(View.VISIBLE);

        binding.btnAdd.setVisibility(View.VISIBLE);
        binding.btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddClassActivity.class);
//            intent.putExtra(Utils.ACTION, Utils.ACTION_ADD);
            startActivity(intent);
        });
        //Init loading dialog
//        loadingDialog = new CustomLoadingDialog(requireContext());


        // Init days
        days = Arrays.asList(getResources().getStringArray(R.array.days));

        // Change the day by clicking on the wanted day
        binding.day1.setOnClickListener(v -> goToDay1());

        binding.day2.setOnClickListener(v -> goToDay2());

        binding.day3.setOnClickListener(v -> goToDay3());

        binding.day4.setOnClickListener(v -> goToDay4());

        binding.day5.setOnClickListener(v -> goToDay5());

        binding.day6.setOnClickListener(v -> goToDay6());

        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if (filterApplied) {
//            getSessions(selectedSection);
//        }
//    }

    private void initRecView() {
        sessions = new ArrayList<>();
        Context context = requireContext(); // Use requireContext() to ensure a non-null context
//        binding.classesRecView.setLayoutManager(new LinearLayoutManager(context));
//        binding.classesRecView.addItemDecoration(new DividerItemDecoration(context, LinearLayout.VERTICAL));
        adapter = new SessionsAdapter((List<String>) context);
    }


    /* Go to day X methods */
    private void goToToday() {
        // Used ViewTreeObserver to wait for the UI to be sized and then we can get the the view's width
        binding.day2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.day2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Show today's classes by default
                Calendar calendar = Calendar.getInstance();
                switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                    case 0:
                    case 6: // If its the weekend then show him the classes of the first day of next week
                        goToDay1();
                        break;
                    case 1:
                        goToDay2();
                        break;
                    case 2:
                        goToDay3();
                        break;
                    case 3:
                        goToDay4();
                        break;
                    case 4:
                        goToDay5();
                        break;
                    case 5:
                        goToDay6();
                        break;
                }
            }
        });
    }

    private void goToDay1() {
        currentDay = 1;
        // Adding an animation
        binding.selectedDay.animate().x(0).setDuration(100);
        // Changing text colors
        binding.day1.setTextColor(Color.WHITE);
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(0));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (filterApplied) {
            // Show today's classes
            showTodaySessions(1);
        }
    }

    private void goToDay2() {
        currentDay = 2;
        int size = binding.day2.getWidth();
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(Color.WHITE);
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(1));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if ( filterApplied) {
            // Show today's classes
            showTodaySessions(2);
        }
    }

    private void goToDay3() {
        currentDay = 3;
        int size = binding.day2.getWidth() * 2;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(Color.WHITE);
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(2));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (filterApplied) {
            // Show today's classes
            showTodaySessions(3);
        }
    }

    private void goToDay4() {
        currentDay = 4;
        int size = binding.day2.getWidth() * 3;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(Color.WHITE);
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(3));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (filterApplied) {
            // Show today's classes
            showTodaySessions(4);
        }
    }

    private void goToDay5() {
        currentDay = 5;
        int size = binding.day2.getWidth() * 4;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(Color.WHITE);
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(4));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (filterApplied) {
            // Show today's classes
            showTodaySessions(5);
        }
    }

    private void goToDay6() {
        currentDay = 6;
        int size = binding.day2.getWidth() * 5;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(Color.WHITE);

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(5));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (filterApplied) {
            // Show today's classes
            showTodaySessions(6);
        }
    }

    /* Sessions methods */
//    private void getSessions(String sectionCode) {
//        // Start loading animation
//        binding.loadingAnimation.setVisibility(View.VISIBLE);
//        binding.loadingAnimation.playAnimation();
//        // Get all the sessions for this section
//        Call<List<Session>> getSectionSessionsCall = api.getSectionSessions(sectionCode, "Token " + currentUser.getToken());
//        getSectionSessionsCall.enqueue(new getSectionSessionsCallback());
//    }
    private void showTodaySessions(int today) {
        List<String> todaySessions = new ArrayList<>(Arrays.asList("Computer Networks", "Software Engineering", "Computer Architecture", "Theory of Computation", "Database Systems"));

        // Show the sessions in the recycler view
        if (!todaySessions.isEmpty()) {
            adapter = new SessionsAdapter(todaySessions);
//            binding.classesRecView.setAdapter(adapter);
//            binding.classesRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        } else {
//            binding.classesRecView.setVisibility(View.GONE);
            binding.emptyMsg.setVisibility(View.VISIBLE);
        }
    }

}