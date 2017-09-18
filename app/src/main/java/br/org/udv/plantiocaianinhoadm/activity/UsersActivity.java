package br.org.udv.plantiocaianinhoadm.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.org.udv.plantiocaianinhoadm.R;
import br.org.udv.plantiocaianinhoadm.model.User;
import br.org.udv.plantiocaianinhoadm.util.Defs;
import br.org.udv.plantiocaianinhoadm.viewholder.UserViewHolder;

public class UsersActivity extends AppCompatActivity {

    public static final String EXTRA_TEAM_NAME = "team_name";

    private DatabaseReference mUsersDatabaseReference;

    private FirebaseRecyclerAdapter<User, UserViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    private String mTeamName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mTeamName = getIntent().getStringExtra(EXTRA_TEAM_NAME);

        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference(Defs.DB_USERS);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecycler = (RecyclerView) findViewById(R.id.users_list);
        mRecycler.setHasFixedSize(true);

        mManager = new LinearLayoutManager(UsersActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        Query usersQuery = getQuery(mUsersDatabaseReference);
        mAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class, R.layout.item_user,
                UserViewHolder.class, usersQuery) {
            @Override
            protected void populateViewHolder(UserViewHolder userViewHolder, final User user, int position) {
                final DatabaseReference userRef = getRef(position);
                final String userKey = userRef.getKey();
                userViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (user.isAuthorized) {
                            new AlertDialog.Builder(UsersActivity.this)
                                    .setMessage(getString(R.string.unauthorize_user_message, user.name))
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            user.isAuthorized = false;
                                            user.isCoordinator = false;
                                            mUsersDatabaseReference.child(userKey).setValue(user);
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                        else {
                            user.isAuthorized = true;
                            mUsersDatabaseReference.child(userKey).setValue(user);
                        }
                    }
                });
                userViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!user.isCoordinator) {
                            new AlertDialog.Builder(UsersActivity.this)
                                    .setPositiveButton(R.string.promote_to_coordinator, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            new AlertDialog.Builder(UsersActivity.this)
                                                    .setMessage(getString(R.string.promote_to_coordinator_message, user.name))
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            user.isAuthorized = true;
                                                            user.isCoordinator = true;
                                                            mUsersDatabaseReference.child(userKey).setValue(user);
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.no, null).show();
                                        }
                                    })
                                    .setNegativeButton(R.string.remove_user, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new AlertDialog.Builder(UsersActivity.this)
                                                    .setMessage(getString(R.string.remove_data_message, user.name))
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            mUsersDatabaseReference.child(userKey).setValue(null);
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.no, null).show();
                                        }
                                    }).show();
                        }
                        else {
                            new AlertDialog.Builder(UsersActivity.this)
                                    .setPositiveButton(R.string.remove_coordinator, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            new AlertDialog.Builder(UsersActivity.this)
                                                    .setMessage(getString(R.string.remove_coordinator_message, user.name))
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            user.isCoordinator = false;
                                                            mUsersDatabaseReference.child(userKey).setValue(user);
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.no, null).show();
                                        }
                                    })
                                    .setNegativeButton(R.string.remove_user, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new AlertDialog.Builder(UsersActivity.this)
                                                    .setMessage(getString(R.string.remove_data_message, user.name))
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            mUsersDatabaseReference.child(userKey).setValue(null);
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.no, null).show();
                                        }
                                    }).show();
                        }
                        return false;
                    }
                });
                userViewHolder.bindUser(user);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    private Query getQuery(DatabaseReference usersDatabaseReference) {
        return usersDatabaseReference.orderByChild(Defs.DB_USERS_TEAM_ATTR).equalTo(mTeamName);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }
}
