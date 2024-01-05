package com.example.readblogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.readblogapp.blogmodel.BlogAdapter
import com.example.readblogapp.blogmodel.BlogModel
import com.example.readblogapp.databinding.ActivitySaveBlogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SaveBlogActivity : AppCompatActivity() {
    private val binding:ActivitySaveBlogBinding by lazy {
        ActivitySaveBlogBinding.inflate(layoutInflater)
    }
    private val saveBlog = mutableListOf<BlogModel>()
    private lateinit var blogadapter:BlogAdapter
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val userid = auth.currentUser?.uid

        blogadapter = BlogAdapter(saveBlog.filter { it.isSaved }.toMutableList())

        binding.saveblogarticle.adapter= blogadapter
        binding.saveblogarticle.showShimmerAdapter()
        binding.saveblogarticle.layoutManager = LinearLayoutManager(this)

        // back button set on click listener

        binding.backactivity.setOnClickListener {
            finish()
        }

        if (userid!= null){

            val userref :DatabaseReference = FirebaseDatabase.getInstance()
                .getReference("Users").child(userid).child("savePost")

            userref.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (datasnapshot in snapshot.children){
                        val postId = datasnapshot.key
                        val isSaved = datasnapshot.value as Boolean
                        if(postId!= null && isSaved){

                            // fetch data to curresponding using this fun
                            CoroutineScope(Dispatchers.IO).launch {
                                val blogs :BlogModel= fetchData(postId)!!
                                saveBlog.add(blogs)


                                launch ( Dispatchers.Main ){

                                    blogadapter.updateData(saveBlog)
                                    binding.saveblogarticle.hideShimmerAdapter()
                                }
                            }

                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })



        }




    }

    private suspend fun fetchData(postId: String): BlogModel? {

        val blogreferl: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("blogs")

        return try {
            val dataSnapshot = blogreferl.child(postId).get().await()
            val blogModel = dataSnapshot.getValue(BlogModel::class.java)
            blogModel
        }catch (e:Exception){
            null
        }


    }
}