package com.example.readblogapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.readblogapp.databinding.ActivityUpdateUserProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancytoastlib.FancyToast

class UpdateUserProfile : AppCompatActivity() {
    private var uri:Uri?=null
    private var dialog:ProgressDialog?=null
    private lateinit var storage: FirebaseStorage
    private val userReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val binding: ActivityUpdateUserProfileBinding by lazy {
        ActivityUpdateUserProfileBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.red))
        dialog = ProgressDialog(this)
        storage = FirebaseStorage.getInstance()
        // back button
        binding.backactivity.setOnClickListener {
            onBackPressed()
        }

        val auth= FirebaseAuth.getInstance()




        // Show user Current Data

        val currentUser: FirebaseUser?= auth.currentUser
        if (currentUser!= null){
            val userId= currentUser.uid

            userReference.child(userId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val userimage= snapshot.child("profile_image").getValue()
                    val username= snapshot.child("name").getValue()
                    val emailid= snapshot.child("email_id").getValue()
                    val password= snapshot.child("password").getValue()


                    Glide.with(applicationContext)
                        .load(userimage)
                        .placeholder(R.drawable.userimage)
                        .centerCrop()
                        .into(binding.userProfile)

                    binding.userName.setText(username.toString())
                    binding.userEmail.setText(emailid.toString())

                    // go to edit
                    binding.updateName.editText?.setText(username.toString())
                    binding.updatePassword.editText?.setText(password.toString())



                }

                override fun onCancelled(error: DatabaseError) {
                    FancyToast.makeText(applicationContext,"Data Not Loaded...${error.message}",
                        FancyToast.LENGTH_LONG,
                        FancyToast.ERROR,false).show()

                }

            })
        }


        // add pic from device

        binding.addImage.setOnClickListener {
            // fetch user updated image
            fatchUserUpdateImage()
        }

        binding.updatebtn.setOnClickListener {
            dialog!!.setTitle("Updating Your Data")
            dialog!!.setMessage("Please Wait....")
            dialog!!.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT)
            dialog!!.show()
            UpdateUserData(uri)
        }


    }

    @SuppressLint("SuspiciousIndentation")
    private fun UpdateUserData(uri: Uri?) {

        val Updatename = binding.updateName.editText?.text.toString().trim()
        val UpdatedPassword = binding.updatePassword.editText?.text.toString().trim()


        val userid = FirebaseAuth.getInstance().currentUser?.uid

        if (userid!= null){
            val data = HashMap<String, Any>()
            data["name"] = Updatename
            data["password"]= UpdatedPassword

                userReference.child(userid).updateChildren(data).addOnSuccessListener {
                    dialog!!.dismiss()

                    FancyToast.makeText(applicationContext,"SuccessFully Data Update...",
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,false).show()
                }
                    .addOnFailureListener{
                        dialog!!.dismiss()
                        FancyToast.makeText(applicationContext,"Data Not Update...",
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,false).show()
                    }

        }




    }

    private fun fatchUserUpdateImage() {

        ImagePicker.with(this)
            .crop()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            uri  = data?.data!!
            // Use Uri object instead of File to avoid storage permissions
            //binding.profliePic.setImageURI(uri)
            Glide.with(this)
                .load(uri)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.userProfile)
            dialog!!.setTitle("Uploading Your Image")
            dialog!!.setProgressStyle(ProgressDialog.BUTTON_NEGATIVE)
            dialog!!.setMessage("Please Wait......")
            dialog!!.show()
            // send to data storage
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val storageReference =storage.reference.child("profile_image").child(userId)

            storageReference.putFile(uri!!).addOnCompleteListener {task->
                if (task.isSuccessful){
                    storageReference.downloadUrl.addOnCompleteListener {imageuri->
                        if (imageuri.isSuccessful){
                            val imageurl = imageuri.result.toString()

                            // save image on firebase database
                            userReference.child(userId).child("profile_image").setValue(imageurl).addOnSuccessListener {

                                dialog!!.dismiss()
                                FancyToast.makeText(applicationContext,"SuccessFully Profile Updated ",
                                    FancyToast.LENGTH_LONG,
                                    FancyToast.SUCCESS,false).show()
                            }


                            //binding.profilePic.setImageURI(uri)
                            Glide.with(this)
                                .load(imageurl)
                                .apply(RequestOptions.centerCropTransform())
                                .into(binding.userProfile)
                        }

                    }

                }
            }



        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            dialog?.dismiss()
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }

    }

}

