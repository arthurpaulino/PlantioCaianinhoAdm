package br.org.udv.plantiocaianinhoadm.viewholder;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.org.udv.plantiocaianinhoadm.R;
import br.org.udv.plantiocaianinhoadm.model.User;

public class UserViewHolder extends RecyclerView.ViewHolder {

    public TextView userNameTextView;
    public ImageView authorizedImageView;

    public UserViewHolder(View itemView) {
        super(itemView);
        userNameTextView = (TextView) itemView.findViewById(R.id.textview_username);
        authorizedImageView = (ImageView) itemView.findViewById(R.id.imageview_authorized);
    }

    public void bindUser(User user) {
        if (user.isAuthorized) {
            authorizedImageView.setImageResource(R.drawable.authorized);
        }
        else {
            authorizedImageView.setImageResource(R.drawable.unauthorized);
        }
        if (user.isCoordinator) {
            userNameTextView.setText(user.name + " (Coord.)");
            userNameTextView.setTypeface(Typeface.DEFAULT_BOLD);

        }
        else {
            userNameTextView.setText(user.name);
            userNameTextView.setTypeface(Typeface.DEFAULT);
        }
    }
}