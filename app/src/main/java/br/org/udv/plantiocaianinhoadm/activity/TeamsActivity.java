package br.org.udv.plantiocaianinhoadm.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.org.udv.plantiocaianinhoadm.R;
import br.org.udv.plantiocaianinhoadm.model.Team;
import br.org.udv.plantiocaianinhoadm.util.Defs;
import br.org.udv.plantiocaianinhoadm.viewholder.TeamViewHolder;

public class TeamsActivity extends AppCompatActivity {

    private static Boolean isPersistenceSet = false;

    private DatabaseReference mTeamsDatabaseReference;

    private FirebaseRecyclerAdapter<Team, TeamViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        if (!isPersistenceSet) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            FirebaseDatabase.getInstance().getReference().keepSynced(true);
            isPersistenceSet = true;
        }

        mTeamsDatabaseReference = FirebaseDatabase.getInstance().getReference(Defs.DB_TEAMS);

        mRecycler = (RecyclerView) findViewById(R.id.teams_list);
        mRecycler.setHasFixedSize(true);

        mManager = new LinearLayoutManager(TeamsActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        Query teamsQuery = mTeamsDatabaseReference;
        mAdapter = new FirebaseRecyclerAdapter<Team, TeamViewHolder>(Team.class, R.layout.item_team,
                TeamViewHolder.class, teamsQuery) {
            @Override
            protected void populateViewHolder(TeamViewHolder teamViewHolder, final Team team, int position) {
                final DatabaseReference teamRef = getRef(position);
                final String teamKey = teamRef.getKey();
                teamViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TeamsActivity.this, UsersActivity.class);
                        intent.putExtra(UsersActivity.EXTRA_TEAM_NAME, team.name);
                        startActivity(intent);
                    }
                });
                teamViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        new AlertDialog.Builder(TeamsActivity.this)
                                .setMessage(getString(R.string.remove_data_message, team.name))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        mTeamsDatabaseReference.child(teamKey).setValue(null);
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                        return false;
                    }
                });
                teamViewHolder.bindTeam(team);
            }
        };
        mRecycler.setAdapter(mAdapter);

        findViewById(R.id.fab_new_team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(TeamsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_team);
                Button addButton = (Button) dialog.findViewById(R.id.button_add);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText teamNameEditText = (EditText) dialog.findViewById(R.id.edittext_team_name);
                        String teamName = teamNameEditText.getText().toString();
                        if (TextUtils.isEmpty(teamName)) {
                            teamNameEditText.setError(getString(R.string.required));
                            return;
                        }
                        Team team = new Team();
                        team.name = teamName;
                        String key = mTeamsDatabaseReference.push().getKey();
                        mTeamsDatabaseReference.child(key).setValue(team);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }
}
