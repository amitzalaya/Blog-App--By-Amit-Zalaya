package com.example.readblogapp

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.readblogapp.blogmodel.BlogModel
import com.example.readblogapp.databinding.ActivityEditBlogBinding
import com.example.readblogapp.databinding.ActivitySaveBlogBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shashank.sony.fancytoastlib.FancyToast

class EditBlogActivity : AppCompatActivity() {

    private var dialog : ProgressDialog?= null
    val binding : ActivityEditBlogBinding by lazy {
        ActivityEditBlogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val blogModel = intent.getParcelableExtra<BlogModel>("blogs")
        dialog =ProgressDialog(this)

        // back button
        binding.backbutton.setOnClickListener {
            finish()
        }


        if (blogModel!= null){
            binding.blogTitle.editText?.setText(blogModel.heading)
            binding.blog.editText?.setText(blogModel.post)


            binding.editBlog.setOnClickListener {

                dialog!!.setTitle("Edit Your Blog")
                dialog!!.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT)
                dialog!!.setMessage("Please Wait....")
                dialog!!.show()

                val updateTitle = binding.blogTitle.editText?.text.toString().trim()
                val updateBlog = binding.blog.editText?.text.toString().trim()

                if (updateTitle.isEmpty()|| updateBlog.isEmpty()){
                    dialog!!.dismiss()
                    FancyToast.makeText(applicationContext,"Please Fill The Box", FancyToast.LENGTH_LONG, FancyToast.WARNING,false).show()
                    return@setOnClickListener
                }else{

                    blogModel.post = updateBlog
                    blogModel.heading = updateTitle
                    updateBlog(blogModel)

                }

            }
        }


    }

    private fun updateBlog(blogModel: BlogModel) {

        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("blogs").
        child(blogModel.postId.toString())
        databaseRef.setValue(blogModel).addOnSuccessListener {
            dialog!!.dismiss()
            FancyToast.makeText(applicationContext,"Successfully Edit Blog", FancyToast.LENGTH_LONG, FancyToast.SUCCESS,false).show()

        }



    }
}


