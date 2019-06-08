package com.hp.hp.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Snapshot;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
private DatabaseReference mUserDatabase;
private FirebaseUser mcurrentUser;

private CircleImageView mdisplayImage;
private TextView mName,mstatus;
    private Button mimagebtn;
    static final int GALLERY=1;
//String DownURL="";
    private StorageReference mimagestorage;
ProgressDialog mprogress;
private Button changestatusbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mdisplayImage=findViewById(R.id.profile_image);
        mName=findViewById(R.id.settingsdisplayname);
        mstatus=findViewById(R.id.settingsstatus);
        changestatusbtn=findViewById(R.id.settingschangestatus);
        mimagebtn=findViewById(R.id.settingschangeimage);
       //image storage
        mimagestorage = FirebaseStorage.getInstance().getReference();
        //retrieve data from DB
        mcurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        String currentuid=mcurrentUser.getUid();



        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(currentuid);
mUserDatabase.keepSynced(true);//for offline services
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get data from realtime database and store ina string to set on widgets
                String name=dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();

          //set data
           mName.setText(name);
           mstatus.setText(status);
           //set image if image not null

                if(!image.equals("default")) {
                    Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.ic_launcher_foreground).into(mdisplayImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changestatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stausvalue=mstatus.getText().toString();
                Intent statusintent=new Intent(SettingsActivity.this,StatusActivity.class);
                statusintent.putExtra("statusvalue",stausvalue);
                startActivity(statusintent);

            }
        });

        mimagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryintent=new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryintent,"SELECT IMAGE"),GALLERY);

//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY && resultCode==RESULT_OK){
            //show progress
            mprogress=new ProgressDialog(SettingsActivity.this);
            mprogress.setTitle("UPLOADING IMAGE");
            mprogress.setMessage("PLEASE WAIT");
            mprogress.setCanceledOnTouchOutside(false);
            mprogress.show();


            Uri imageuri=data.getData();
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageuri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
//store cropped image in firebase storage
                String curentuserid=mcurrentUser.getUid();
//create thumbnail and compress

                File thumbFile=new File(resultUri.getPath());
                try {
                    Bitmap thumb_Bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumbFile);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] thumb_byte = baos.toByteArray();


                    final StorageReference filepath = mimagestorage.child("profile_images").child(curentuserid + ".jpg");
                    final StorageReference thumb_filepath = mimagestorage.child("profile_images").child("thumbs").child(curentuserid + ".jpg");


//fetch download url


//fetch download url end

                    UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            final Map<String, String> updateHashmap=new HashMap<String,String>();

                            if(task.isSuccessful())
                            {
                                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                       //download url
                                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                //Log.d(TAG, "onSuccess: uri= "+ uri.toString());
                                                String DownURL = uri.toString();

                                                updateHashmap.put("image",DownURL);

                                                //Toast.makeText(SettingsActivity.this, "new url" + DownURL, Toast.LENGTH_LONG).show();
                                                mUserDatabase.child("image").setValue(DownURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            mprogress.dismiss();
                                                            Toast.makeText(SettingsActivity.this, "success uploading", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                                thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {

                                                        String thumbDownURL=uri.toString();
                                                        updateHashmap.put("thumb_image",thumbDownURL);
                                                        mUserDatabase.child("thumb_image").setValue(thumbDownURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mprogress.dismiss();
                                                                    Toast.makeText(SettingsActivity.this, "success uploading", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        });
                                    }
                                });

                            }else{
                                Toast.makeText(SettingsActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }  catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }


    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
