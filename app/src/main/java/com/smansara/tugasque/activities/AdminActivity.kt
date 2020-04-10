package com.smansara.tugasque.activities

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.smansara.tugasque.R
import com.smansara.tugasque.fragments.AdminEntryFragment
import com.smansara.tugasque.fragments.AdminLupaFragment
import com.smansara.tugasque.fragments.AdminNotifFragment
import com.smansara.tugasque.fragments.SiswaTugasFragment
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_home.*

class AdminActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_admin)

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

        bottomNavigation_admin.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_person -> {
                    loadEntry(savedInstanceState)
                }
                R.id.nav_forgot -> {
                    loadForgot(savedInstanceState)
                }
                R.id.nav_custom -> {
                    loadCustom(savedInstanceState)
                }
            }
            true
        }
        loadForgot(savedInstanceState)
        bottomNavigation_admin.selectedItemId = R.id.nav_forgot

    }

    fun loadForgot(savedInstanceState: Bundle?) {
        if(savedInstanceState == null) {
            val fragment = AdminLupaFragment()
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
                    .replace(R.id.frameLayout_admin, fragment)
                    .commitAllowingStateLoss()
        }
    }

    fun loadEntry(savedInstanceState: Bundle?) {
        if(savedInstanceState == null) {
            val fragment = AdminEntryFragment()
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
                    .replace(R.id.frameLayout_admin, fragment)
                    .commitAllowingStateLoss()
        }
    }

    fun loadCustom(savedInstanceState: Bundle?) {
        if(savedInstanceState == null) {
            val fragment = AdminNotifFragment()
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
                    .replace(R.id.frameLayout_admin, fragment)
                    .commitAllowingStateLoss()
        }
    }

}
