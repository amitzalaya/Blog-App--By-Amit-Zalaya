package com.example.readblogapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.readblogapp.blogmodel.BlogModel
import com.example.readblogapp.blogmodel.UserBlogAdapter
import com.example.readblogapp.blogmodel.UserBlogAdapter.OnItemClickListener
import com.example.readblogapp.databinding.ActivityUserBlogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast
import es.dmoral.toasty.Toasty

class UserBlogActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    var dialog : ProgressDialog?= null
    val blogsavedlist= ArrayList<BlogModel>()
    lateinit var blogadpater : UserBlogAdapter
    private val auth = FirebaseAuth.getInstance()
    private val binding: ActivityUserBlogBinding by lazy {
        ActivityUserBlogBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        dialog = ProgressDialog(this)
        binding.userBlogrc.layoutManager = LinearLayoutManager(this)

        // back button seton click Listener
        binding.backbutton.setOnClickListener {
            onBackPressed()
        }

        val currentuserId= auth.currentUser?.uid
        if (currentuserId!= null){

            blogadpater = UserBlogAdapter(this, blogsavedlist, object : OnItemClickListener{
                override fun onEdit(blogitems: BlogModel) {
                    val intent = Intent(applicationContext,EditBlogActivity::class.java)
                    intent.putExtra("blogs",blogitems)
                    startActivityForResult(intent,123)
                }

                override fun onReadmore(blogitems: BlogModel) {
                    val intent = Intent(applicationContext,ReadMoreActivity::class.java)
                    intent.putExtra("blogs",blogitems)
                    startActivity(intent)

                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onDelete(blogitems: BlogModel) {
                    dialog!!.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    dialog!!.setTitle("Deleting Your Post")
                    dialog!!.setMessage("Please Wait....")
                    dialog!!.show()
                    deletePost(blogitems)

                }
            })
        }
        binding.userBlogrc.adapter = blogadpater
        binding.userBlogrc.showShimmerAdapter()

        //  get data
        databaseReference = FirebaseDatabase.getInstance().getReference("blogs")

        databaseReference.addListenerForSingleValueEvent(object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                blogsavedlist.clear()
                for (postsnapshot in snapshot.children){
                    val blogsaved = postsnapshot.getValue(BlogModel::class.java)
                    if (blogsaved!= null && currentuserId==blogsaved.userId){
                        blogsavedlist.add(blogsaved)
                    }
                    blogadpater.saveData(blogsavedlist)
                    blogadpater.notifyDataSetChanged()

                    binding.userBlogrc.hideShimmerAdapter()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                FancyToast.makeText(applicationContext,"Loading Failed", FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show()

            }
        })



    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deletePost(blogitems: BlogModel) {

        databaseReference.child(blogitems.postId!!).removeValue().addOnSuccessListener { task->
            dialog!!.dismiss()
            Toasty.error(this, "Blog Deleted", Toast.LENGTH_SHORT, false).show();

        }.addOnFailureListener{
            FancyToast.makeText(this, "Blog can not Delete ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()

        }


    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==123 && resultCode == Activity.RESULT_OK){}
    }
}

