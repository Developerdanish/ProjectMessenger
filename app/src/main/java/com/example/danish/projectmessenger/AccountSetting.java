package com.example.danish.projectmessenger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountSetting extends AppCompatActivity {

    private final static int PICK_IMAGE_REQUEST = 1;

    private Uri mFilePath;

    private CircleImageView mProfileImage;
    private TextView mDisplayName;
    private TextView mStatus;

    private Dialog mDialog;

    private DatabaseReference mDB;

    private FirebaseAuth mAuth;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        mDialog = new Dialog(this);
        mDialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        mDialog.setContentView(R.layout.status_change_dialog);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        mDB = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mDB.keepSynced(true);



        mProfileImage = (CircleImageView)findViewById(R.id.profile_image);
        mDisplayName = (TextView)findViewById(R.id.display_name);
        mStatus = (TextView)findViewById(R.id.status);

        loadData();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        mStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialogForStatus();
            }
        });

        mDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpDialogForDisplayName();
            }
        });



    }

    private void setUpDialogForDisplayName() {
        final TextInputLayout a = (TextInputLayout)mDialog.findViewById(R.id.newStatus_textInput);
        a.getEditText().setHint("New Display Name");
        a.getEditText().setText("");

        ((mDialog).findViewById(R.id.discard_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        ((mDialog).findViewById(R.id.save_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDB.child("displayName").setValue(a.getEditText().getText().toString().trim());
                mDialog.dismiss();
            }
        });

        mDialog.show();

    }


    private void setUpDialogForStatus(){
        final TextInputLayout a = (TextInputLayout)mDialog.findViewById(R.id.newStatus_textInput);
        a.getEditText().setHint("New Display Name");
        a.getEditText().setText("");


        ((mDialog).findViewById(R.id.discard_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        ((mDialog).findViewById(R.id.save_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDB.child("status").setValue(a.getEditText().getText().toString().trim());
                mDialog.dismiss();
            }
        });

        mDialog.show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void loadData() {
        mDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               mDisplayName.setText(dataSnapshot.child("displayName").getValue().toString());
               mStatus.setText(dataSnapshot.child("status").getValue().toString());

              String imageUrl = dataSnapshot.child("thumbnail").getValue().toString();

              if(!imageUrl.equals("default")){
                  Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_picture).into(mProfileImage);

              }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void uploadImage() {

        if(mFilePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setMessage("Please wait while we upload your picture");
            progressDialog.show();

            final StorageReference ref = storageReference.child("profileImages/"+ mAuth.getCurrentUser().getUid());
            ref.putFile(mFilePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            updateImageUrlInDB(ref);
                            progressDialog.dismiss();
                            Toast("Uploaded");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast("Failed");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
    }

    private void uploadThumbnail() {
        File thumbnail_file = new File(mFilePath.getPath());

        Bitmap thumb_bitmap = null;

        try {
            thumb_bitmap = new Compressor(this)
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(75)
                    .compressToBitmap(thumbnail_file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] thumb_byte = baos.toByteArray();

        StorageReference thumb_filepath = storageReference.child("profileImages/").child("thumbnail/" + mAuth.getCurrentUser().getUid() + ".jpg");

        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                String thumb_download_url = task.getResult().getDownloadUrl().toString();

                mDB.child("thumbnail").setValue(thumb_download_url);
            }
        });
    }


    private void updateImageUrlInDB(StorageReference ref){

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                mDB.child("image").setValue(uri.toString());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mDB.child("online").setValue("true");

    }

    @Override
    protected void onPause() {
        super.onPause();
        mDB.child("online").setValue("false");
        mDB.child("time").setValue(ServerValue.TIMESTAMP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(AccountSetting.this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                mFilePath = result.getUri();
                uploadImage();
                uploadThumbnail();
            }
        }
    }


    private void Toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
