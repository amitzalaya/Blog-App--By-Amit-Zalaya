package com.example.readblogapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.readblogapp.blogmodel.BlogModel
import com.example.readblogapp.databinding.ActivityCreateBlogBinding
import com.google.android.material.expandable.ExpandableWidget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast
import java.io.IOException
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class CreateBlogActivity : AppCompatActivity() {
    private  val binding :ActivityCreateBlogBinding by lazy {
        ActivityCreateBlogBinding.inflate(layoutInflater)
    }
    var dialog: ProgressDialog? = null

    private val databaseReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("blogs")
    private val userReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dialog = ProgressDialog(this)


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding.backbutton.setOnClickListener {
               onBackPressed()
        }

        // speak title
        binding.mic1title.setOnClickListener {
            // speak fun for Blog Title
            speakforTitle()
        }

        // speak blog
        binding.mic2descrition.setOnClickListener {
            // speak fun for post or Our Blog
            speakforPost()
        }

        // createblog
        binding.addBlog.setOnClickListener {
            

                dialog!!.setProgressStyle(ProgressDialog.THEME_HOLO_DARK)
                dialog!!.setTitle("Creating Your Blog")
                dialog!!.setCancelable(false)
                dialog!!.setMessage("Please wait")
                dialog!!.show()
                val title = binding.blogTitle.editText?.text.toString().trim()
                val descripationblog = binding.blog.editText?.text.toString().trim()
                if (title.isEmpty()||descripationblog.isEmpty()){
                    dialog!!.dismiss()
                    FancyToast.makeText(this,"Please Fill The all Box", FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show()
                    return@setOnClickListener
                }

                val user:FirebaseUser?= auth.currentUser

                if (user!=null){
                    val userId: String= user.uid

                    // fetch username and user profile from database
                    userReference.child(userId).addListenerForSingleValueEvent(object:ValueEventListener{
                        @SuppressLint("SimpleDateFormat")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userdata:Users?= snapshot.getValue(Users::class.java)
                            if (userdata!= null){
                                val username:String = userdata.name
                                val userimage: String = userdata.profile_image

                                val currentdate = SimpleDateFormat("dd-MM-yyyy").format(Date())

                                // blog items
                                val blogmodel= BlogModel(title,username,currentdate,descripationblog,userimage,userId,0)

                                // ganerate key for blog
                                val key = databaseReference.push().key
                                if (key!=null){
                                    blogmodel.postId= key
                                    val blogreferce= databaseReference.child(key)
                                    blogreferce.setValue(blogmodel).addOnCompleteListener{
                                        if (it.isSuccessful){
                                            dialog!!.dismiss()
                                            FancyToast.makeText(applicationContext,"Successfully blog created", FancyToast.LENGTH_LONG, FancyToast.SUCCESS,false).show()
                                            startActivity(Intent(applicationContext,MainActivity::class.java))

                                        }
                                    }
                                }

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
        }



    }

    private fun speakforPost() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())

        try {
            startActivityForResult(intent,102)

        }catch (er:IOException){
            Toast.makeText(applicationContext, "Please Try Again", Toast.LENGTH_SHORT).show()
        }

    }

    private fun speakforTitle() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())

        try {
            startActivityForResult(intent,101)

        }catch (er:IOException){}

    }

    // for blog title
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==101 &&resultCode== RESULT_OK && data!= null ) {
            try {
                val result: ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!
                binding.blogTitle.editText!!.setText(result.toString().trim())
            }catch (e :IOException){}

        }else {
            try {
                val result: ArrayList<String> = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!
                binding.blog.editText!!.setText(result.toString().trim())
            } catch (e: IOException) {
            }

        }
    }

}