package com.smansara.tugasque.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.smansara.tugasque.R
import com.smansara.tugasque.fragments.GuruAkunFragment
import com.smansara.tugasque.fragments.GuruTugasFragment
import kotlinx.android.synthetic.main.activity_guru.*

class GuruActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    lateinit var i: Intent
    var access: String? = ""
    var nama: String? = ""
    var nis: String? = ""
    var kelas: String? = ""
    var email: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guru)

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

        bottomNavigation_guru.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_tugaskepsek -> {
                    loadTugasGuru(savedInstanceState)
                }
                R.id.nav_akunkepsek -> {
                    loadAkunGuru(savedInstanceState)
                }
                R.id.nav_kelaskepsek -> {
                    loadKelasGuru(savedInstanceState)
                }
            }
            true
        }

        if(access.equals("6")) {
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/admin")
        }

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/${i.getStringExtra("nis")}")
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/all")
        loadKelasGuru(savedInstanceState)

    }

    fun loadTugasGuru(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val fragment = GuruTugasFragment()
            val bundle = Bundle()
            bundle.putString("access", "$access")
            bundle.putString("nama", "$nama")
            bundle.putString("nis", "$nis")
            bundle.putString("kelas", "$kelas")
            bundle.putString("email", "$email")
            bundle.putString("state", "PRIBADI")
            fragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameLayout_guru, fragment)
                    .commitAllowingStateLoss()
        }
    }

    fun loadAkunGuru(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val fragment = GuruAkunFragment()
            val bundle = Bundle()
            bundle.putString("access", "$access")
            bundle.putString("nama", "$nama")
            bundle.putString("nis", "$nis")
            bundle.putString("kelas", "$kelas")
            bundle.putString("email", "$email")
            fragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameLayout_guru, fragment)
                    .commitAllowingStateLoss()
        }
    }

    fun loadKelasGuru(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val fragment = GuruTugasFragment()
            val bundle = Bundle()
            bundle.putString("access", "$access")
            bundle.putString("nama", "$nama")
            bundle.putString("nis", "$nis")
            bundle.putString("kelas", "$kelas")
            bundle.putString("email", "$email")
            bundle.putString("state", "KELAS")
            fragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameLayout_guru, fragment)
                    .commitAllowingStateLoss()
        }
    }

}
