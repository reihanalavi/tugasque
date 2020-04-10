package com.smansara.tugasque.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.smansara.tugasque.R
import com.smansara.tugasque.fragments.KepsekAkunFragment
import com.smansara.tugasque.fragments.KepsekTugasFragment
import kotlinx.android.synthetic.main.activity_kepsek.*

class KepsekActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    var access: String? = ""
    var nama: String? = ""
    var nis: String? = ""
    var kelas: String? = ""
    var email: String? = ""
    lateinit var i: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kepsek)

        val window = this.window
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        i = intent
        access = i.getStringExtra("access")
        nama = i.getStringExtra("nama")
        nis = i.getStringExtra("nis")
        kelas = i.getStringExtra("kelas")
        email = i.getStringExtra("email")

        bottomNavigation_kepsek.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_tugaskepsek -> {
                    loadTugasKepsek(savedInstanceState)
                }
                R.id.nav_akunkepsek -> {
                    loadAkunKepsek(savedInstanceState)
                }
                R.id.nav_kelaskepsek -> {
                    loadKelasKepsek(savedInstanceState)
                }
            }
            true
        }

        if(access.equals("6")) {
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/admin")
        }

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/${i.getStringExtra("nis")}")
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/all")
        loadKelasKepsek(savedInstanceState)

    }

    fun loadTugasKepsek(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val fragment = KepsekTugasFragment()
            val bundle = Bundle()
            bundle.putString("access", "$access")
            bundle.putString("nama", "$nama")
            bundle.putString("nis", "$nis")
            bundle.putString("kelas", "$kelas")
            bundle.putString("email", "$email")
            bundle.putString("STATE", "GURU")
            fragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameLayout_kepsek, fragment)
                    .commitAllowingStateLoss()
        }
    }

    fun loadKelasKepsek(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val fragment = KepsekTugasFragment()
            val bundle = Bundle()
            bundle.putString("access", "$access")
            bundle.putString("nama", "$nama")
            bundle.putString("nis", "$nis")
            bundle.putString("kelas", "$kelas")
            bundle.putString("email", "$email")
            bundle.putString("STATE", "KELAS")
            fragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameLayout_kepsek, fragment)
                    .commitAllowingStateLoss()
        }
    }

    fun loadAkunKepsek(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val fragment = KepsekAkunFragment()
            val bundle = Bundle()
            bundle.putString("access", "$access")
            bundle.putString("nama", "$nama")
            bundle.putString("nis", "$nis")
            bundle.putString("kelas", "$kelas")
            bundle.putString("email", "$email")
            fragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameLayout_kepsek, fragment)
                    .commitAllowingStateLoss()
        }
    }

}
