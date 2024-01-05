package com.example.readblogapp.blogmodel

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readblogapp.R
import com.example.readblogapp.ReadMoreActivity
import com.example.readblogapp.SaveBlogActivity
import com.example.readblogapp.databinding.SimpleLayoutBlogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Context
import com.shashank.sony.fancytoastlib.FancyToast
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class BlogAdapter(private var items:MutableList<BlogModel>):
    RecyclerView.Adapter<BlogAdapter.BLogViewHolder>() {

        private val databaseref : DatabaseReference= FirebaseDatabase.getInstance().reference
        private val currentUser  = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogAdapter.BLogViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val binding = SimpleLayoutBlogBinding.inflate(inflater,parent,false)
        return BLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogAdapter.BLogViewHolder, position: Int) {
        val blogitem:BlogModel = items[position]
        holder.bind(blogitem)
    }

    override fun getItemCount(): Int {
       return items.size   }
    inner class BLogViewHolder(private val binding : SimpleLayoutBlogBinding ): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SuspiciousIndentation")
        fun bind(blogModel: BlogModel) {

            val postId = blogModel.postId
                try {
                    if (blogModel.profile_image!=null){
                        Glide.with(binding.userProfile.context)
                            .load(blogModel.profile_image)
                            .placeholder(R.drawable.userimage)
                            .centerCrop()
                            .into(binding.userProfile)
                    }else{
                        Log.d("Error","Something Getting Wrong in Your Profile")
                    }
                }catch (error:IOException){
                    FancyToast.makeText(binding.userProfile.context,"Loading failed${error.message}", FancyToast.LENGTH_LONG, FancyToast.SUCCESS,false).show()
                }

            binding.heading.text = blogModel.heading
            binding.descrption.text = blogModel.post
            binding.userName.text = blogModel.userName
            binding.date.text = blogModel.date
            binding.likecount.setText(blogModel.likeCount.toString())

            binding.readmore.setOnClickListener {
                val intent = Intent(binding.root.context,ReadMoreActivity::class.java)
                intent.putExtra("blogs",blogModel)
                binding.root.context.startActivity(intent)
            }
            // check if the current user has already liked post
            val postref : DatabaseReference = databaseref.child("blogs").child(postId!!).child("likes")

            val currentUserLiked = currentUser?.uid?.let { uid->

                postref.child(uid).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            binding.likebtn.setImageResource(R.drawable.likedheart)
                        }else{
                            binding.likebtn.setImageResource(R.drawable.heart)

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            }

           //  like button set on click listener
            binding.likebtn.setOnClickListener{
                if(currentUser!= null){
                    likeHandleFun(binding,postId,blogModel)
                }else{
                    Toast.makeText(binding.root.context,"Please First Login",Toast.LENGTH_LONG).show()
                }

            }
            // already save User

            val alreadysaveref : DatabaseReference = databaseref.child("Users").child(currentUser?.uid ?:"")
            val postsavref :DatabaseReference = alreadysaveref.child("savePost").child(postId)

            postsavref.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        // if already save blog
                        binding.saveblog.setImageResource(R.drawable.bookmark)
                    }else{
                        // if already not saved blog
                        binding.saveblog.setImageResource(R.drawable.saveinstagram)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


            // save Post
            binding.saveblog.setOnClickListener{
                if (currentUser!= null){
                    savePostFun(binding,postId)
                }


            }

            }
        }

    // save post fun
    private fun savePostFun(binding: SimpleLayoutBlogBinding, postId: String) {

        val userrefernce :DatabaseReference= databaseref.child("Users").child(currentUser!!.uid)


        userrefernce.child("savePost").child(postId).addListenerForSingleValueEvent(object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    userrefernce.child("savePost").child(postId).removeValue()
                        .addOnSuccessListener {
                            // update our UI
                            val clickunsave :BlogModel?= items.find { it.postId == postId }
                            clickunsave?.isSaved= false
                            notifyDataSetChanged()

                            binding.saveblog.setImageResource(R.drawable.saveinstagram)

                        }

                }
                else{
                    userrefernce.child("savePost").child(postId).setValue(true)
                        .addOnSuccessListener {
                            // update Ui
                            val clicksave :BlogModel?= items.find { it.postId == postId }
                            clicksave?.isSaved = false
                            notifyDataSetChanged()
                            binding.saveblog.setImageResource(R.drawable.bookmark)



                        }

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    // search fun
    fun searchblog(searchList: List<BlogModel>){
        items = searchList as MutableList<BlogModel>
        notifyDataSetChanged()
    }

    private fun likeHandleFun(binding: SimpleLayoutBlogBinding, postId: String, blogModel: BlogModel) {


        val userraf :DatabaseReference = databaseref.child("Users").child(currentUser!!.uid)


        val postlikeref :DatabaseReference= databaseref.child("blogs").child(postId).child("likes")


        // if user has already liked post, so unlike this

        postlikeref.child(currentUser.uid).addListenerForSingleValueEvent(object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    userraf.child("likes").child(postId).removeValue()
                        .addOnSuccessListener {

                            postlikeref.child(currentUser.uid).removeValue()
                            blogModel.likedBy?.remove(currentUser.uid)
                            updateLikedButton(binding,false)

                            val newLikedCount = blogModel.likeCount - 1
                            blogModel.likeCount= newLikedCount

                            databaseref.child("blogs").child(postId).child("likeCount").setValue(newLikedCount)
                            notifyDataSetChanged()
                        }
                        .addOnFailureListener{e->
                            Log.e("likedClick", "onDataChange: Failed to unlike the blog $e ")

                        }
                }
                else{
                    // user has the not liked post

                    userraf.child("likes").child(postId).setValue(true)
                        .addOnSuccessListener {
                            postlikeref.child(currentUser.uid).setValue(true)

                            blogModel.likedBy?.add(currentUser.uid)
                            updateLikedButton(binding,true)

                            // increment on the like count
                            val newCount:Int = blogModel.likeCount + 1
                            blogModel.likeCount = newCount

                            databaseref.child("blogs").child(postId).child("likeCount").setValue(newCount)
                            notifyDataSetChanged()

                        }
                        .addOnFailureListener {e->
                            Log.e("likedClick", "onDataChange: Failed to like the blog $e ")

                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })





    }

    private fun updateLikedButton(binding: SimpleLayoutBlogBinding, Liked: Boolean) {
        if (Liked){
            binding.likebtn.setImageResource(R.drawable.heart)
        }else{
            binding.likebtn.setImageResource(R.drawable.likedheart)

        }


    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData( saveBlog: MutableList<BlogModel>) {
        items.clear()
        items.addAll(saveBlog)
        notifyDataSetChanged()


    }
}









