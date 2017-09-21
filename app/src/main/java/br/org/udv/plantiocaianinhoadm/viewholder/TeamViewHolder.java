package br.org.udv.plantiocaianinhoadm.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.org.udv.plantiocaianinhoadm.R;
import br.org.udv.plantiocaianinhoadm.model.Team;

public class TeamViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName;

    public TeamViewHolder(View itemView) {
        super(itemView);
        txtName = (TextView) itemView.findViewById(R.id.txt_name);
    }

    public void bindTeam(Team team) {
        txtName.setText(team.name);
    }
}