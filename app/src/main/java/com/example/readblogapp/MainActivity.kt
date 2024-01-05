package com.example.readblogapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.readblogapp.blogmodel.BlogAdapter
import com.example.readblogapp.blogmodel.BlogModel
import com.example.readblogapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.Collections


class MainActivity : AppCompatActivity() {

    private val userReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val blogReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("blogs")

    val bloglist= ArrayList<BlogModel>()
    val adapter = BlogAdapter(bloglist)
    private val auth = FirebaseAuth.getInstance()

    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.blogrv.layoutManager = LinearLayoutManager(this)
        binding.blogrv.showShimmerAdapter()


        //  fetch userdata
        val currentUser:FirebaseUser?= auth.currentUser
        if (currentUser!= null){
            val userId= currentUser.uid

            userReference.child(userId).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val userimage= snapshot.child("profile_image").getValue()

                    Glide.with(applicationContext)
                        .load(userimage)
                        .placeholder(R.drawable.userimage)
                        .centerCrop()
                        .into(binding.userProfile)

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        // add blogs activity
        binding.createBlog.setOnClickListener {
            startActivity(Intent(this,CreateBlogActivity::class.java))
        }

        // go to user Activity
        binding.userProfile.setOnClickListener {
            startActivity(Intent(this,UserProfileActivity::class.java))
            finish()
        }

        // save blogs set on click listener
        binding.saveblog.setOnClickListener {
            startActivity(Intent(this,SaveBlogActivity::class.java))

        }


        // search blogs
        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {

                // search Fun ->
                searchList(query)
                return true
            }
        })

        // fetch blog details
         blogReference.addListenerForSingleValueEvent(object :ValueEventListener{
             @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
             override fun onDataChange(snapshot: DataSnapshot) {
                 if (snapshot.exists()){
                     bloglist.clear()
                     for (blogsSnapShots in snapshot.children) {
                         val blogModel: BlogModel? = blogsSnapShots.getValue(BlogModel::class.java)
                             blogModel?.let {
                                 bloglist.add(it)
                                 bloglist.reverse()
                                 binding.blogrv.adapter = adapter
                                 adapter.notifyDataSetChanged()
                                 binding.blogrv.hideShimmerAdapter()

                             }



                     }

                 }
             }

             override fun onCancelled(error: DatabaseError) {
                 FancyToast.makeText(applicationContext,"SomeThing Getting Wrong${error.message}", FancyToast.LENGTH_LONG, FancyToast.SUCCESS,false).show()

             }

         })
    }

    private fun searchList(query: String) {
        val searchList = ArrayList<BlogModel>()

        for (blogs in bloglist){
            if (blogs.heading?.lowercase()?.contains(query.lowercase())==true){
                searchList.add(blogs)
            }
        }
        adapter.searchblog(searchList)





    }


}