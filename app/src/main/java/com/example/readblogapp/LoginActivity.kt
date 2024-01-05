package com.example.readblogapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatDelegate
import com.example.readblogapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.Locale


class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseauth:FirebaseAuth
    var textToSpeech :TextToSpeech? = null
    var currentUser :FirebaseUser? =null
    var dialog:ProgressDialog?=null
    private val binding:ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech!!.setLanguage(Locale.UK)
                textToSpeech!!.setSpeechRate(1.0f)
            }
            firebaseauth = FirebaseAuth.getInstance()
            currentUser= firebaseauth.currentUser
            dialog = ProgressDialog(this)

            binding.ragistarPage.setOnClickListener {
                startActivity(Intent(this, RagisterActivity::class.java))
            }
            binding.login.setOnClickListener {
                dialog!!.setProgressStyle(ProgressDialog.THEME_HOLO_DARK)
                dialog!!.setTitle("Logging")
                dialog!!.setCancelable(false)
                dialog!!.setMessage("Please wait")
                dialog!!.show()

                val login_email: String = binding.loginEmail.editText?.text.toString()
                val login_password: String = binding.loginPassword.editText?.text.toString()
                if (login_email.isBlank()) {
                    dialog!!.dismiss()
                    FancyToast.makeText(this, "Please Enter Your Email", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
                    textToSpeech!!.speak("Please Enter Your Email", TextToSpeech.QUEUE_ADD, null)
                    return@setOnClickListener
                }
                if (login_password.isBlank()) {
                    dialog!!.dismiss()
                    FancyToast.makeText(
                        this, "Please Enter Your Password",
                        FancyToast.LENGTH_LONG,
                        FancyToast.ERROR, false
                    ).show()
                    textToSpeech!!.speak("Please Enter Your Password", TextToSpeech.QUEUE_ADD, null)
                    return@setOnClickListener
                }

                firebaseauth.signInWithEmailAndPassword(login_email,login_password).addOnCompleteListener(this){task->
                    if(task.isSuccessful){
                        dialog!!.dismiss()
                        FancyToast.makeText(this, "SuccessFully Logged In", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show()
                        textToSpeech!!.speak("SuccessFully Logged In", TextToSpeech.QUEUE_ADD, null)
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                        
                    }

                }

            }
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