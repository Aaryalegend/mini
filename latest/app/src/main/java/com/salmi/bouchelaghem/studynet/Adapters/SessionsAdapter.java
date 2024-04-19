package com.salmi.bouchelaghem.studynet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Activities.ClassDetailsActivity;
import com.salmi.bouchelaghem.studynet.Models.Session;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.LayoutClassBinding;

import java.util.ArrayList;
import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder> {

    private final List<String> sessions;
    private Context context;

    public SessionsAdapter(List<String> sessions) {
        this.sessions = sessions;
    }
//    public void Session() {
//        // Initialize concernedGroups with all groups
//        this.concernedGroups = getAllGroups();
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutClassBinding binding = LayoutClassBinding.inflate(inflater, parent, false);
        ViewHolder holder = new ViewHolder(binding);

        holder.binding.classMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prepare the intent and pass in the session.
                Intent intent = new Intent(context, ClassDetailsActivity.class);
                intent.putExtra("SESSION", sessions.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Creating a hardcoded Session object
        Session session = createHardcodedSession(position);
        LayoutClassBinding binding = holder.binding;

        // Set data to views
        binding.txtClassSubject.setText(session.getModule());
        // Session's groups
        StringBuilder groupText = new StringBuilder();
        List<Integer> concernedGroups = session.getConcernedGroups();
        if (concernedGroups.size() > 1) { // There are more than 1 groups
            for (int grp = 0; grp < concernedGroups.size() - 1; grp++) {
                groupText.append((grp + 1)).append(", ");
            }
            groupText.append(concernedGroups.size()); // The last group doesn't have a ',' after it
        } else if (!concernedGroups.isEmpty()) { // There is only one group
            groupText.append(concernedGroups.get(0));
        }
        binding.txtClassGroup.setText(groupText.toString());

        // Time
        binding.txtClassStartHour.setText(session.getLocalTimeStartTime().toString());
        binding.txtClassEndHour.setText(session.getLocalTimeEndTime().toString());

        // Module
        binding.txtClassType.setText(session.getModuleType());
        binding.imgBookmark.setVisibility(View.VISIBLE);
    }
    private Session createHardcodedSession(int position) {
        Session session = new Session();
        // Setting module based on position
        switch (position % 5) {
            case 0:
                session.setModule("Computer Networks");
                break;
            case 1:
                session.setModule("Software Engineering");
                break;
            case 2:
                session.setModule("Computer Architecture");
                break;
            case 3:
                session.setModule("Theory of Computation");
                break;
            case 4:
                session.setModule("Database Systems");
                break;
            default:
                session.setModule("Unknown Module");
                break;
        }
        // Setting other properties as needed
        return session;
    }
    @Override
    public int getItemCount() {
        // Return the total number of sessions
        return sessions.size();
    }

//    public void setSessions(List<Session> todaySessions) {
//        sessions = new ArrayList<>();
//
//        // Hardcode sessions
//        Session session1 = new Session();
//        session1.setModule("Computer Networks");
//        sessions.add(session1);
//
//        Session session2 = new Session();
//        session2.setModule("Software Engineering");
//        sessions.add(session2);
//
//        Session session3 = new Session();
//        session3.setModule("Computer Architecture");
//        sessions.add(session3);
//
//        Session session4 = new Session();
//        session4.setModule("Theory of Computation");
//        sessions.add(session4);
//    }

//    public JsonObject getSessions() {1
//        // Ensure sessions are initialized
//        setSessions(todaySessions);
//        return null;
//    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LayoutClassBinding binding;

        public ViewHolder(LayoutClassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
