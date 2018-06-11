package com.example.danish.projectmessenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    ArrayList<Users> mUsersList;
    Context context;

    public UsersListAdapter(Context context, ArrayList<Users> users){
        this.context = context;
        this.mUsersList = users;
    }

    public void add(Users user){
        mUsersList.add(user);
    }

    public void addAll(List<Users> users){
        mUsersList.addAll(users);
    }

    public void clear(){
        mUsersList.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users currentUser = mUsersList.get(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, currentUser.getTime()+"", Toast.LENGTH_SHORT).show();

                final CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0){
                            Toast.makeText(context, options[0], Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, options[1], Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.show();
            }
        });

        holder.mStatus.setText(currentUser.getStatus());
        holder.mDisplayName.setText(currentUser.getDisplayName());

        Picasso.get().load(currentUser.getThumbnail()).into(holder.mProfileImage);

        if(currentUser.getOnline().equals("false")){
            holder.mOnlineStatusImage.setVisibility(View.INVISIBLE);
        }else {
            holder.mOnlineStatusImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private CircleImageView mProfileImage;
        private TextView mDisplayName;
        private TextView mStatus;
        private CircleImageView mOnlineStatusImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mProfileImage = (CircleImageView)itemView.findViewById(R.id.profile_image_listItem);
            mDisplayName = (TextView)itemView.findViewById(R.id.display_name_listItem);
            mStatus = (TextView)itemView.findViewById(R.id.status_listItem);
            mOnlineStatusImage = (CircleImageView)itemView.findViewById(R.id.online_status_image);
        }
    }
}
