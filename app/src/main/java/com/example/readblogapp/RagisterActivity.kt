package com.example.readblogapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.readblogapp.databinding.ActivityRagisterBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancytoastlib.FancyToast
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.util.Locale

class RagisterActivity : AppCompatActivity() {
    private var dialog: ProgressDialog? = null
    private var textToSpeech :TextToSpeech? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var storage:FirebaseStorage
   private var uri: Uri? = null
    private  var currentUser:FirebaseUser?=null

    private val binding: ActivityRagisterBinding by lazy{
        ActivityRagisterBinding.inflate(layoutInflater)
    }
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        storage = FirebaseStorage.getInstance()

        // pic image button set on click listener

        binding.picimage.setOnClickListener {
            picImage()
        }



        dialog = ProgressDialog(this)
        database =FirebaseDatabase.getInstance()

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech!!.setLanguage(Locale.UK)
                textToSpeech!!.setSpeechRate(1.0f)
            }
        }


        val auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser




                // change page
        binding.loginpage.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        // register user
        binding.register.setOnClickListener {

            // show progress bar
            dialog!!.setProgressStyle(ProgressDialog.THEME_HOLO_DARK)
            dialog!!.setTitle("Creating Your Account")
            dialog!!.setCancelable(false)
            dialog!!.setMessage("Please wait")
            dialog!!.show()

            val name:String = binding.name.editText?.text.toString()
            val email :String = binding.emailId.editText?.text.toString()
            val password :String = binding.password.editText?.text.toString()



            if (name.isBlank()){
                dialog!!.dismiss()
                FancyToast.makeText(this,"Please Enter Your Name",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show()
                textToSpeech!!.speak("Please Enter Your Name", TextToSpeech.QUEUE_ADD, null)
                return@setOnClickListener
            }
            if (email.isBlank()){
                dialog!!.dismiss()
                FancyToast.makeText(this,"Please Enter Your Email",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show()
                textToSpeech!!.speak("Please Enter Your Email", TextToSpeech.QUEUE_ADD, null)
                return@setOnClickListener
            }
            if (password.isBlank()){
                dialog!!.dismiss()
                FancyToast.makeText(this,"Please Enter Your Password",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show()
                textToSpeech!!.speak("Please Enter Your Password", TextToSpeech.QUEUE_ADD, null)
                return@setOnClickListener
            }else if(password.length <= 6){
                dialog!!.dismiss()
                FancyToast.makeText(this,"You must have Minimum 6 characters in your password",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show()
                textToSpeech!!.speak("You must have Minimum 6 characters in your password", TextToSpeech.QUEUE_ADD, null)
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task->
                    if (task.isSuccessful){
                            dialog!!.dismiss()
                            FancyToast.makeText(this," SuccessFully Registered",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show()
                            textToSpeech!!.speak("SuccessFully Registered",TextToSpeech.QUEUE_ADD,null)
                            //store data
                            val currentuser = auth.currentUser

                            currentuser?.let {
                                val ur = database.getReference("Users")
                            val userid = currentuser!!.uid
                                val user = Users(binding.name.editText?.text.toString(), binding.emailId.editText?.text.toString(),binding.password.editText?.text.toString())

                                ur.child(userid).setValue(user)

                                val storageReference =storage.reference.child("profile_image").child(userid)
                                storageReference.putFile(uri!!).addOnCompleteListener{ task->
                                    if (task.isSuccessful){
                                        storageReference.downloadUrl.addOnCompleteListener{ imageUri->
                                            if (imageUri.isSuccessful){
                                                val imageurl = imageUri.result.toString()
                                                // save user image in database
                                                ur.child(userid).child("profile_image").setValue(imageurl)

                                                //binding.profilePic.setImageURI(uri)
                                                Glide.with(this)
                                                    .load(imageUri)
                                                    .apply(RequestOptions.centerCropTransform())
                                                    .into(binding.profliePic)

                                            }
                                        }

                                    }
                                }

                            }
                            binding.name.editText?.setText("")
                            binding.emailId.editText?.setText("")
                            binding.password.editText?.setText("")
                            startActivity(Intent(this,MainActivity::class.java))
                    }
                        }

                }

        }
  // pic image
    private fun picImage() {
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
            //binding.profilePic.setImageURI(uri)
            Glide.with(this)
                .load(uri)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.profliePic)




        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onStart() {
        super.onStart()
        if (currentUser!= null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }



    }



private operator fun Any.compareTo(i: Int): Int {


    return TODO("Provide the return value")
}

private operator fun Any.not(): Any {


    return TODO("Provide the return value")
}


private operator fun Unit.unaryMinus() {
    TODO("Not yet implemented")
}


private fun String.matches(regex: String): Boolean {

    return TODO("Provide the return value")
}
