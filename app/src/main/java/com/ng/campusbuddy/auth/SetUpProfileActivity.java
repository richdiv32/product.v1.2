package com.ng.campusbuddy.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.model.User;
import com.ng.campusbuddy.profile.EditProfileActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class SetUpProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference reference;
    ProgressDialog pd;

    FirebaseUser firebaseUser;
    DatabaseReference User_reference;
    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;

    ImageView Profile_image;
    EditText Fullname,Birthday,Bio,Telephone,Institution,Faculty,Department, Gender, Relationship_status;
    Button Finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        User_reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        Fullname = findViewById(R.id.fullname);
        Birthday = findViewById(R.id.birthday);
        Relationship_status = findViewById(R.id.relationship_status);
        Telephone = findViewById(R.id.telephone_number);
        Institution = findViewById(R.id.institution);
        Faculty = findViewById(R.id.faculty);
        Department = findViewById(R.id.department);
        Bio = findViewById(R.id.bio);
        Gender = findViewById(R.id.gender);
        Profile_image = findViewById(R.id.profile_image);

        Finish = findViewById(R.id.btn_finish);


        Init();
    }

    private void Init() {




        Profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(SetUpProfileActivity.this);
            }
        });

        DatabaseReference Image_reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        Image_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(Profile_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveInfo();
            }
        });
    }

    private void SaveInfo() {

        pd = new ProgressDialog(SetUpProfileActivity.this);
        pd.setTitle("That's All");
        pd.setMessage("Welcome to Campus Buddy");
        pd.show();
        pd.setCanceledOnTouchOutside(true);

        String str_fullname = Fullname.getText().toString();
        String str_birthday = Birthday.getText().toString();
        String str_relationship_status = Relationship_status.getText().toString().toLowerCase();
        String str_telephone = Telephone.getText().toString();
        String str_institution = Institution.getText().toString().toUpperCase();
        String str_faculty = Faculty.getText().toString().toUpperCase();
        String str_department = Department.getText().toString().toUpperCase();
        String str_bio = Bio.getText().toString();
        String str_gender = Gender.getText().toString().toLowerCase();

        if (TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_birthday) || TextUtils.isEmpty(str_relationship_status)
                || TextUtils.isEmpty(str_telephone) || TextUtils.isEmpty(str_institution)
                || TextUtils.isEmpty(str_faculty) || TextUtils.isEmpty(str_department) || TextUtils.isEmpty(str_bio)
                || TextUtils.isEmpty(str_bio) || TextUtils.isEmpty(str_gender)){
            Toast.makeText(SetUpProfileActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
        }

        else {
            HashMap<String, Object> map = new HashMap<>();
            map.put("fullname", str_fullname);
            map.put("birthday", str_birthday);
            map.put("telephone", str_telephone);
            map.put("relationship_status", str_relationship_status);
            map.put("institution", str_institution);
            map.put("faculty", str_faculty);
            map.put("department", str_department);
            map.put("bio", str_bio);
            map.put("gender", str_gender);

            User_reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        pd.dismiss();
                        startActivity(new Intent(SetUpProfileActivity.this, HomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    else {
                        pd.dismiss();
                        String message = task.getException().getMessage();
                        Toast.makeText(SetUpProfileActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if (mImageUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("imageurl", ""+miUrlOk);
                        reference.updateChildren(map1);

                        pd.dismiss();

                    } else {
                        Toast.makeText(SetUpProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetUpProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(SetUpProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage();

        } else {
            Toast.makeText(SetUpProfileActivity.this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}
