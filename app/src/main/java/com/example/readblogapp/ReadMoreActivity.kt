package com.example.readblogapp

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.readblogapp.blogmodel.BlogModel
import com.example.readblogapp.databinding.ActivityReadMoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast
import java.io.IOException

@Suppress("DEPRECATION")
class ReadMoreActivity : AppCompatActivity() {
    private  val binding:ActivityReadMoreBinding by lazy {
        ActivityReadMoreBinding.inflate(layoutInflater)
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    // back button set on click listener
        binding.backbutton.setOnClickListener {
            onBackPressed()
        }


        // get data

        val blog= intent.getParcelableExtra<BlogModel>("blogs")


        if (blog != null) {
            binding.blogerName.text = blog.userName
            binding.blogTitle.text = blog.heading
            binding.userPost.text= blog.post
            binding.date.text = blog.date
        }
        try {

            Glide.with(this)
                .load(blog!!.profile_image)
                .centerCrop()
                .into(binding.userProfile)
        }catch (e:IOException){
            Log.d("Image","Something Getting Wrong in Your Profile")

        }


    }

    }
