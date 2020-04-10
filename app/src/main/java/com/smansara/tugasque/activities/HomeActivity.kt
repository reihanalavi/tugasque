package com.smansara.tugasque.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.smansara.tugasque.R
import com.smansara.tugasque.R.id.nav_akun
import com.smansara.tugasque.R.id.nav_tugas
import com.smansara.tugasque.fragments.SiswaAkunFragment
import com.smansara.tugasque.fragments.SiswaTugasFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    var access: String? = ""
    var nama: String? = ""
    var nis: String? = ""
    var kelas: String? = ""
    var commons: String? = ""
    var email: String? = ""
    lateinit var i: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val window = this.window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        i = intent
        access = i.getStringExtra("access")
        nama = i.getStringExtra("nama")
        nis = i.getStringExtra("nis")
        kelas = i.getStringExtra("kelas")
        email = i.getStringExtra("email")

        bottomNavigation_home.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                nav_tugas -> {
                    loadTugasSiswa(savedInstanceState)
                }
                nav_akun -> {
                    loadAkunSiswa(savedInstanceState)
                }
            }
            true
        }

        if(access.equals("6")) {
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/admin")
        }
        
        loadTugasSiswa(savedInstanceState)
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$nis")
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$kelas")
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/all")
        Log.d("SUBSCRIBE SISWA", "$kelas")
    }

    fun loadTugasSiswa(savedInstanceState: Bundle?) {
        if(savedInstanceState == null) {
            val fragment = SiswaTugasFragment()
            val bundle = Bundle()
            bundle.putString("access", "$access")
            bundle.putString("nama", "$nama")
            bundle.putString("nis", "$nis")
            bundle.putString("kelas", "$kelas")
            bundle.putString("email", "$email")
            bundle.putString("commons", "$commons")
            fragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameLayout_home, fragment)
                    .commitAllowingStateLoss()
        }
    }

    fun loadAkunSiswa(savedInstanceState: Bundle?) {
        if(savedInstanceState == null) {
            val fragment = SiswaAkunFragment()
            val bundle = Bundle()
            bundle.putString("access", "$access")
            bundle.putString("nama", "$nama")
            bundle.putString("nis", "$nis")
            bundle.putString("kelas", "$kelas")
            bundle.putString("email", "$email")
            bundle.putString("commons", "$commons")
            fragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameLayout_home, fragment)
                    .commitAllowingStateLoss()
        }
    }

}
