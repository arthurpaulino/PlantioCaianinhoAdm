package br.org.udv.plantiocaianinhoadm.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.org.udv.plantiocaianinhoadm.R;
import br.org.udv.plantiocaianinhoadm.adapter.CustomSpinnerAdapter;
import br.org.udv.plantiocaianinhoadm.model.Team;
import br.org.udv.plantiocaianinhoadm.util.Defs;
import br.org.udv.plantiocaianinhoadm.viewholder.TeamViewHolder;

public class TeamsActivity extends AppCompatActivity {

    private static Boolean isPersistenceSet = false;

    private DatabaseReference teamsReference;

    private FirebaseAuth auth;

    private TextView txtStatus;
    private Button btnTryAgain;
    private Spinner spnRegion;
    private FloatingActionButton fabNewTeam;

    private FirebaseRecyclerAdapter<Team, TeamViewHolder> teamsAdapter;
    private RecyclerView teamsRecycler;
    private LinearLayoutManager teamsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        txtStatus = (TextView) findViewById(R.id.txt_status);
        btnTryAgain = (Button) findViewById(R.id.btn_try_again);
        spnRegion = (Spinner) findViewById(R.id.spn_region);
        teamsRecycler = (RecyclerView) findViewById(R.id.teams_recycler);
        fabNewTeam = (FloatingActionButton) findViewById(R.id.fab_new_team);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            attemptLogin();
        } else {
            spnRegion.setVisibility(View.VISIBLE);
            teamsRecycler.setVisibility(View.VISIBLE);
        }

        if (!isPersistenceSet) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            FirebaseDatabase.getInstance().getReference().keepSynced(true);
            isPersistenceSet = true;
        }

        setSpinner();

        teamsReference = FirebaseDatabase.getInstance().getReference(Defs.DB_TEAMS);
        teamsRecycler.setHasFixedSize(true);

        teamsLayoutManager = new LinearLayoutManager(TeamsActivity.this);
        teamsLayoutManager.setReverseLayout(true);
        teamsLayoutManager.setStackFromEnd(true);
        teamsRecycler.setLayoutManager(teamsLayoutManager);

        fabNewTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(TeamsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_team);

                Button addButton = (Button) dialog.findViewById(R.id.btn_add);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText inputTeamName = (EditText) dialog.findViewById(R.id.input_team_name);
                        String teamName = inputTeamName.getText().toString();
                        String region = spnRegion.getSelectedItem().toString();
                        if (TextUtils.isEmpty(teamName)) {
                            inputTeamName.setError(getString(R.string.required));
                            return;
                        }
                        if (region.equals(getResources().getStringArray(R.array.array_regions)[0])) {
                            return;
                        }
                        Team team = new Team();
                        team.name = teamName;
                        team.region = region;
                        String key = teamsReference.push().getKey();
                        teamsReference.child(key).setValue(team);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void updateRecycler() {
        Query teamsQuery = teamsReference.orderByChild(Defs.DB_TEAMS_REGION_ATTR).equalTo(spnRegion.getSelectedItem().toString());
        teamsAdapter = new FirebaseRecyclerAdapter<Team, TeamViewHolder>(Team.class, R.layout.item_team,
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
                                        teamsReference.child(teamKey).setValue(null);
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                        return false;
                    }
                });
                teamViewHolder.bindTeam(team);
            }
        };
        teamsRecycler.setAdapter(teamsAdapter);
    }

    private void attemptLogin() {
        txtStatus.setText(R.string.connecting);
        txtStatus.setVisibility(View.VISIBLE);
        btnTryAgain.setVisibility(View.GONE);
        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    txtStatus.setVisibility(View.GONE);
                    btnTryAgain.setVisibility(View.GONE);
                    spnRegion.setVisibility(View.VISIBLE);
                    teamsRecycler.setVisibility(View.VISIBLE);
                } else {
                    txtStatus.setText(R.string.authentication_failed);
                    btnTryAgain.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setSpinner() {
        CustomSpinnerAdapter regionSpinnerAdapter = new CustomSpinnerAdapter(TeamsActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.array_regions));
        regionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRegion.setAdapter(regionSpinnerAdapter);
        spnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateRecycler();
                if (i>0)
                    fabNewTeam.setVisibility(View.VISIBLE);
                else
                    fabNewTeam.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (teamsAdapter != null) {
            teamsAdapter.cleanup();
        }
    }
}
