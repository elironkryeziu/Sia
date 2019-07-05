package com.example.fiek.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fiek.Activities.PostDetailActivity;
import com.example.fiek.Models.Post;
import com.example.fiek.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context mContext;
    List<Post> mData;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseLike;
    DatabaseReference mDatabaseSaved;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.tvUser.setText("Postuar nga: " + mData.get(position).getUserName());
        final String post_key = mData.get(position).getPostKey();


        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                    holder.btnLike.setImageResource(R.drawable.thumb_up_blue_18dp);
                }else {
                    holder.btnLike.setImageResource(R.drawable.thumb_up_black_18dp);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mDatabaseSaved.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(post_key)){
                    holder.btnSave.setImageResource(R.drawable.save_filled);
                }
                else {
                    holder.btnSave.setImageResource(R.drawable.save);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle,tvUser;
        ImageView btnAnswer, btnLike, btnSave;

        boolean mProcessLike = false;
        boolean mProcessSave = false;

        public MyViewHolder(View itemView){
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            tvUser = itemView.findViewById(R.id.tvUser);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnAnswer = itemView.findViewById(R.id.btnAnswer);
            btnSave = itemView.findViewById(R.id.btnSave);
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseSaved = FirebaseDatabase.getInstance().getReference().child("Saved");
            mAuth = FirebaseAuth.getInstance();


            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    final String post_key = mData.get(position).getPostKey();
                    mProcessSave = true;

                    mDatabaseSaved.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (mProcessSave){
                                if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(post_key)){
                                    mDatabaseSaved.child(mAuth.getCurrentUser().getUid()).child(post_key).removeValue();
                                    mProcessSave=false;
                                }
                                else{
                                    mDatabaseSaved.child(mAuth.getCurrentUser().getUid()).child(post_key).setValue("saved");
                                    mProcessSave=false;
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });


            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("title",mData.get(position).getTitle());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userName",mData.get(position).getUserName());

                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate",timestamp);
                    mContext.startActivity(postDetailActivity);

                }
            });


            btnLike.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    final String post_key = mData.get(position).getPostKey();
                    mProcessLike = true;

                    mDatabaseLike.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (mProcessLike) {
                                if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                    mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                    mProcessLike=false;
                                } else {
                                    mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("liked");
                                    mProcessLike=false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            });


            btnAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("title",mData.get(position).getTitle());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userName",mData.get(position).getUserName());

                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate",timestamp);
                    mContext.startActivity(postDetailActivity);

                }
            });


        }

    }

}
