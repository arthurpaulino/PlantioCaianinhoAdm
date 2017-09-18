package br.org.udv.plantiocaianinhoadm.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.org.udv.plantiocaianinhoadm.R;
import br.org.udv.plantiocaianinhoadm.model.Team;

public class TeamViewHolder extends RecyclerView.ViewHolder {

    public TextView teamNameTextView;

    public TeamViewHolder(View itemView) {
        super(itemView);
        teamNameTextView = (TextView) itemView.findViewById(R.id.textview_teamname);
    }

    public void bindTeam(Team team) {
        teamNameTextView.setText(team.name);
    }
}