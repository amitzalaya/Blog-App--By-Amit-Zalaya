package com.example.readblogapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.readblogapp.databinding.ActivityCreateBlogBinding
import com.example.readblogapp.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.Locale

class UserProfileActivity : AppCompatActivity() {
    var textToSpeech: TextToSpeech? = null
    private val userReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val auth = FirebaseAuth.getInstance()


    private val binding: ActivityUserProfileBinding by lazy {
        ActivityUserProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.profile_bg))

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech!!.setLanguage(Locale.UK)
                textToSpeech!!.setSpeechRate(1.0f)
            }

            // fetch user data
            val currentUser:FirebaseUser?= auth.currentUser
            if (currentUser!= null){
                val userId= currentUser.uid

                userReference.child(userId).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val userimage= snapshot.child("profile_image").getValue()
                        val username= snapshot.child("name").getValue()


                        Glide.with(applicationContext)
                            .load(userimage)
                            .placeholder(R.drawable.userimage)
                            .centerCrop()
                            .into(binding.userProfile)

                        binding.userName.setText(username.toString())


                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
            val auth = FirebaseAuth.getInstance()


            // back button
            binding.backbutton.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
            }

            // update user profile
            binding.updateProfile.setOnClickListener {
                startActivity(Intent(this, UpdateUserProfile::class.java))
            }


            // user blog
            binding.yourarticle.setOnClickListener {
               startActivity(Intent(this,UserBlogActivity::class.java))
            }

            // create article

            binding.newarticle.setOnClickListener {
                startActivity(Intent(this, CreateBlogActivity::class.java))
            }

            // logout
            binding.logout.setOnClickListener {
                auth.signOut()
                FancyToast.makeText(this, "SuccessFully Logged Out", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show()
                startActivity(Intent(this, LoginActivity::class.java))
                textToSpeech?.speak("SuccessFully Logged Out",TextToSpeech.QUEUE_ADD,null)
                finish()
            }
        }


    }
}










