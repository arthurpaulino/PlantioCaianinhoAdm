package br.org.udv.plantiocaianinhoadm.viewholder;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.org.udv.plantiocaianinhoadm.R;
import br.org.udv.plantiocaianinhoadm.model.User;

public class UserViewHolder extends RecyclerView.ViewHolder {

    private TextView txtName;
    private TextView txtEmail;
    private ImageView imgAuthorization;

    public UserViewHolder(View itemView) {
        super(itemView);
        txtName = (TextView) itemView.findViewById(R.id.txt_name);
        txtEmail = (TextView) itemView.findViewById(R.id.txt_email);
        imgAuthorization = (ImageView) itemView.findViewById(R.id.img_authorization);
    }

    public void bindUser(User user) {
        if (user.isAuthorized)
            imgAuthorization.setImageResource(R.drawable.authorized);
        else
            imgAuthorization.setImageResource(R.drawable.unauthorized);
        if (user.isCoordinator) {
            txtName.setText(user.name + " (Coord.)");
            txtName.setTypeface(Typeface.DEFAULT_BOLD);

        }
        else {
            txtName.setText(user.name);
            txtName.setTypeface(Typeface.DEFAULT);
        }
        txtEmail.setText(user.email);
    }
}