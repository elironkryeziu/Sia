package com.example.fiek.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fiek.Adapters.CommentAdapter;
import com.example.fiek.Models.Comment;
import com.example.fiek.Models.Post;
import com.example.fiek.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    TextView txtPostDesc,txtPostDateName, txtPostTitle, txtUsername,txtNrLikes;
    EditText editTextComment;
    ImageView btnDelete,btnLike;
    Button btnAddComment;
    String PostKey;
    String title;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabasePosts;
    DatabaseReference mDatabaseLike;

    FirebaseDatabase firebaseDatabase;
    RecyclerView RvComments;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY = "comment";
    boolean mLikeProcess = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        RvComments = findViewById(R.id.rv_comment);
        txtPostTitle = findViewById(R.id.txtPostTitle);
        txtPostDateName = findViewById(R.id.txtPostDateName);
        txtPostDesc = findViewById(R.id.txtPostDesc);
        editTextComment = findViewById(R.id.post_comment);
        btnAddComment = findViewById(R.id.add_comment_btn);
        txtUsername = findViewById(R.id.txtUsername);
        btnDelete = findViewById(R.id.btnDelete);
        btnLike = findViewById(R.id.btnLike);
        txtNrLikes = findViewById(R.id.txtNrLikes);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabasePosts = FirebaseDatabase.getInstance().getReference().child("Posts");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey).push();
                String commentContent = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();

                Comment comment = new Comment(commentContent,uid, uname);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        showMessage("Komenti u regjistrua me sukses");
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Ka ndodhur nje gabim gjate komentimit"+e.getMessage());
                    }
                });

            }
        });
        title = getIntent().getExtras().getString("title");
        PostKey = getIntent().getExtras().getString("postKey");
        txtPostTitle.setText(title);
        String postDesc = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDesc);
        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);
        String userName = getIntent().getExtras().getString("userName");
        txtUsername.setText("| Postuar nga: " + userName);
        if (userName.equals(mAuth.getCurrentUser().getDisplayName())) {
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabasePosts.child(PostKey).removeValue();
                Intent mainActivity = new Intent(getApplicationContext(), Home.class);
                startActivity(mainActivity);
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLikeProcess = true;
                mDatabaseLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mLikeProcess) {
                            if (dataSnapshot.child(PostKey).hasChild(mAuth.getCurrentUser().getUid())) {
                                mDatabaseLike.child(PostKey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                mLikeProcess=false;
                            } else {
                                mDatabaseLike.child(PostKey).child(mAuth.getCurrentUser().getUid()).setValue("like");
                                mLikeProcess=false;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        setBtnLike(PostKey);
        getNrLikes(PostKey);
        iniRvComment();
    }

    private void getNrLikes(final String postKey) {
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int nrLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                //System.out.println(nrLikes);
                if (nrLikes == 0){
                    txtNrLikes.setText("Askush nuk e pelqen kete");
                }
                else if(nrLikes == 1){
                    txtNrLikes.setText("Nje njeri e pelqen kete");
                }
                else {
                    txtNrLikes.setText(nrLikes+" njerez e pelqejne kete");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setBtnLike(final String postKey) {

        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(postKey).hasChild(mAuth.getCurrentUser().getUid())){
                    btnLike.setImageResource(R.drawable.thumb_up_blue_18dp);
                }else {
                    btnLike.setImageResource(R.drawable.thumb_up_black_18dp);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void iniRvComment() {

        RvComments.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()){

                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment);
                }
                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
                RvComments.setAdapter(commentAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
    }


    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }
}
