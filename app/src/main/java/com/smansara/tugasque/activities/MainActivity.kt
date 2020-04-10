package com.smansara.tugasque.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.google.firebase.firestore.FirebaseFirestore
import com.smansara.tugasque.R
import com.smansara.tugasque.utils.ListSelectors
import com.smansara.tugasque.utils.ListSelectors.listAdmin
import kotlinx.android.synthetic.main.activity_buat.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.selector
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    var access: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val spUser = getSharedPreferences("USERPREFS", Context.MODE_PRIVATE)

        val nis = spUser?.getString("NIS", "")

        firestore = FirebaseFirestore.getInstance()
        if(nis?.isNotEmpty()!!) {
            firestore.collection("main_accounts").document(nis).get()
                    .addOnSuccessListener { document ->
                        if(document.getString("email").toString().isEmpty() || document.getString("email").toString().equals("")){
                            //Logout, Session Expired
                            alert("Sesi anda telah berakhir. Silahkan masuk kembali", "Masuk"){
                                positiveButton("MASUK") {
                                    startActivity<LoginActivity>()
                                    finish()
                                }
                            }.show()
                        } else {
                            val nisGet = document.getString("nis")
                            val namaGet = document.getString("nama")
                            val accessGet = document.getString("access")
                            val kelasGet = document.getString("kelas")
                            val emailGet = document.getString("email")
                            Handler().postDelayed(object : Runnable {

                                @SuppressLint("NewApi")
                                override fun run() {
                                    when (accessGet) {
                                        "6" -> {
                                            selector("Pilih State", listAdmin) { dialogInterface, i ->
                                                when(listAdmin[i]) {
                                                    "Kepala Sekolah" -> {
                                                        startActivity<KepsekActivity>("nama" to namaGet, "access" to accessGet, "nis" to nisGet, "kelas" to kelasGet, "email" to emailGet)
                                                        finish()
                                                    }
                                                    "Guru" -> {
                                                        startActivity<GuruActivity>("nama" to namaGet, "access" to accessGet, "nis" to nisGet, "kelas" to kelasGet, "email" to emailGet)
                                                        finish()
                                                    }
                                                    "Siswa" -> {
                                                        startActivity<HomeActivity>("nama" to namaGet, "access" to accessGet, "nis" to nisGet, "kelas" to kelasGet, "email" to emailGet)
                                                        finish()
                                                    }
                                                }
                                            }
                                        }
                                        "5" -> {
                                            startActivity<KepsekActivity>("nama" to namaGet, "access" to accessGet, "nis" to nisGet, "kelas" to kelasGet, "email" to emailGet)
                                            finish()
                                        }
                                        "4" -> {
                                            startActivity<GuruActivity>("nama" to namaGet, "access" to accessGet, "nis" to nisGet, "kelas" to kelasGet, "email" to emailGet)
                                            finish()
                                        }
                                        else -> {
                                            startActivity<HomeActivity>("nama" to namaGet, "access" to accessGet, "nis" to nisGet, "kelas" to kelasGet, "email" to emailGet)
                                            finish()
                                        }
                                    }
                                }
                            }, 1000)
                        }
                    }
        } else {
            Handler().postDelayed(object : Runnable {

                @SuppressLint("NewApi")
                override fun run() {
                    startActivity<LoginActivity>()
                    finish()
                }
            }, 3000)
        }

        firestore = FirebaseFirestore.getInstance()

    }
}
