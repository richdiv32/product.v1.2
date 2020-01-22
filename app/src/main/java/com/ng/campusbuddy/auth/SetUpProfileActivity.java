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

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.post.PostActivity;
import com.ng.campusbuddy.profile.EditProfileActivity;
import com.ng.campusbuddy.social.SocialActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpProfileActivity extends AppCompatActivity {

    DatabaseReference reference;

    private Uri mImageUri;
    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;

    ProgressDialog pd;

    HashMap<String, Object> map = new HashMap<>();


    EditText Username,Fullname,Birthday,Relationship_status,Telephone,Institution
            ,Faculty,Department,Bio,Gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);


        storageRef = FirebaseStorage.getInstance().getReference("posts");

        Username = findViewById(R.id.username);
        Fullname = findViewById(R.id.fullname);
        Birthday = findViewById(R.id.birthday);
        Relationship_status = findViewById(R.id.relationship_status);
        Telephone = findViewById(R.id.telephone_number);
        Institution = findViewById(R.id.institution);
        Faculty = findViewById(R.id.faculty);
        Department = findViewById(R.id.department);
        Bio = findViewById(R.id.bio);
        Gender = findViewById(R.id.gender);

        Init();
    }

    private void Init() {

        ImageView Profile_image = findViewById(R.id.profile_image);
        Profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(SetUpProfileActivity.this);
            }
        });


        Button Finish = findViewById(R.id.btn_finish);
        Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

//    private void SaveInfo() {
//
//        pd = new ProgressDialog(SetUpProfileActivity.this);
//        pd.setTitle("That's All");
//        pd.setMessage("Setting up a profile page for you, Please Wait.....");
//        pd.show();
//        pd.setCanceledOnTouchOutside(true);
//
//
//
//
//        else if (mImageUri == null){
//            Toast.makeText(this, "Select a profile image", Toast.LENGTH_SHORT).show();
//        }
//
//        else {
//            UploadData(str_username, str_fullname, str_birthday, str_gender, str_relationship_status,
//                    str_telephone, str_institution, str_faculty, str_department, str_bio);
//
//            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//            DatabaseReference User_reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
//
//            User_reference.updateChildren(map)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()){
//                        pd.dismiss();
//                        Toast.makeText(SetUpProfileActivity.this, "Welcome to Campus Buddy", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(SetUpProfileActivity.this, HomeActivity.class)
//                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                        Animatoo.animateSlideUp(SetUpProfileActivity.this);
//                        finish();
//                    }
//                    else {
//                        pd.dismiss();
//                        String message = task.getException().getMessage();
//                        Toast.makeText(SetUpProfileActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }


    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void upload(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Saving");
        pd.show();

        final String str_username = Username.getText().toString().toLowerCase();
        final String str_fullname = Fullname.getText().toString();
        final String str_birthday = Birthday.getText().toString();
        final String str_relationship_status = Relationship_status.getText().toString().toLowerCase();
        final String str_telephone = Telephone.getText().toString();
        final String str_institution = Institution.getText().toString().toUpperCase();
        final String str_faculty = Faculty.getText().toString().toUpperCase();
        final String str_department = Department.getText().toString().toUpperCase();
        final String str_bio = Bio.getText().toString();
        final String str_gender = Gender.getText().toString().toLowerCase();

        if (TextUtils.isEmpty(str_username)  || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_birthday) || TextUtils.isEmpty(str_relationship_status)
                || TextUtils.isEmpty(str_telephone) || TextUtils.isEmpty(str_institution)
                || TextUtils.isEmpty(str_faculty) || TextUtils.isEmpty(str_department) || TextUtils.isEmpty(str_bio)
                || TextUtils.isEmpty(str_bio) || TextUtils.isEmpty(str_gender)){
            Toast.makeText(SetUpProfileActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
        }
        else if (mImageUri != null){
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_image");
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
                        miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageurl", miUrlOk);
                        map.put("username", str_username);
                        map.put("fullname", str_fullname);
                        map.put("birthday", str_birthday);
                        map.put("gender", str_gender);
                        map.put("relationship_status", str_relationship_status);
                        map.put("telephone", str_telephone);
                        map.put("institution", str_institution);
                        map.put("faculty", str_faculty);
                        map.put("department", str_department);
                        map.put("bio", str_bio);
                        map.put("id", userid);
                        map.put("online_status", "online");
                        map.put("profile_status", "Hey there, I am on Campus Buddy");

                        reference.child(userid).setValue(map);

                        pd.dismiss();

                        startActivity(new Intent(SetUpProfileActivity.this, HomeActivity.class));
                        finish();

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

            CircleImageView Profile_image = findViewById(R.id.profile_image);
            Profile_image.setImageURI(mImageUri);
        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SetUpProfileActivity.this, SetUpProfileActivity.class));
            finish();
        }
    }

}
