package com.smansara.tugasque.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.smansara.tugasque.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_foto.*

class DetailFotoActivity : AppCompatActivity() {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_foto)

        val i = intent

        supportActionBar?.title = i.getStringExtra("picDetail")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        storage.reference.child(i.getStringExtra("picDetail")).downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(imageView_detailfoto)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
