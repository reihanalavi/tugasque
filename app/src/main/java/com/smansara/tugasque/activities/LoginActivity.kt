package com.smansara.tugasque.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.google.firebase.firestore.FirebaseFirestore
import com.smansara.tugasque.R
import com.smansara.tugasque.models.DataEntry
import com.smansara.tugasque.utils.ListSelectors
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton
import kotlin.reflect.full.memberProperties

@Suppress("UNCHECKED_CAST")
class LoginActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        firestore = FirebaseFirestore.getInstance()

        text_daftar.onClick {
            startActivity<LengkapiActivity>("email" to "")
        }
        text_lupa.onClick {
            startActivity<LupaActivity>()
        }
        text_ganti.onClick {
            val email = editText_login_email.text.toString()
            if(email.isEmpty()) {
                alert("Mohon isi email untuk mengubah password", "Gagal"){
                    positiveButton("KEMBALI") { }
                }.show()
            } else {
                startActivity<GantiActivity>("email" to email)
            }
        }

        button_login_layanan.onClick {
            val uri = Uri.parse("smsto:6285875403716")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.`package` = "com.whatsapp"
            startActivity(intent)
        }

        button_login_masuk.setOnClickListener {

            val email = editText_login_email.text.toString()
            val password = editText_login_password.text.toString()

            val emailParsed = email.replace(" ", "")
            Log.d("EMAIL PARSED", emailParsed)

            if(email.isEmpty() || password.isEmpty()) {
                showAlert("Periksa data yang kosong","Gagal")
            } else {
                firestore.collection("main_accounts").whereEqualTo("email", emailParsed).get().addOnSuccessListener{ document ->

                    if(document.documents.isNotEmpty()) {
                        //kalau ada datanya
                        val nisUser = document.documents[0].getString("nis")
                        val namaUser = document.documents[0].getString("nama")
                        val kelasUser = document.documents[0].getString("kelas")
                        val emailUser = document.documents[0].getString("email")
                        val accessUser = document.documents[0].getString("access")
                        val passwordUser = document.documents[0].getString("password")

                        val emailUserParsed = emailUser.replace(" ", "")
                        val emailParsed = email.replace(" ", "")
                        val passUserParsed = passwordUser.replace(" ", "")
                        val passParsed = password.replace(" ", "")

                        Log.d("EMAIL USER PARSED", emailUserParsed)
                        Log.d("EMAIL PARSED", emailParsed)
                        Log.d("PASS USER PARSED", passUserParsed)
                        Log.d("PASS PARSED", passParsed)

                        if(passParsed.equals(passUserParsed) && emailParsed.toLowerCase().equals(emailUser.toLowerCase(), ignoreCase = true)) {
                            //data login benar
                            //aman untuk masuk
                            val spUser = getSharedPreferences("USERPREFS", Context.MODE_PRIVATE)

                            spUser
                                    .edit()
                                    .putString("NIS", nisUser)
                                    .apply()

                            if(accessUser.equals("5")) {
                                startActivity<KepsekActivity>("nis" to nisUser, "nama" to namaUser, "kelas" to kelasUser, "email" to emailUser, "access" to accessUser)
                                finish()
                            } else if(accessUser.equals("4")) {
                                startActivity<GuruActivity>("nis" to nisUser, "nama" to namaUser, "kelas" to kelasUser, "email" to emailUser, "access" to accessUser)
                                finish()
                            } else if(accessUser.equals("6")) {
                                selector("Pilih State", ListSelectors.listAdmin) { dialogInterface, i ->
                                    when(ListSelectors.listAdmin[i]) {
                                        "Kepala Sekolah" -> {
                                            startActivity<KepsekActivity>("nama" to namaUser, "access" to accessUser, "nis" to nisUser, "kelas" to kelasUser, "email" to emailUser)
                                            finish()
                                        }
                                        "Guru" -> {
                                            startActivity<GuruActivity>("nama" to namaUser, "access" to accessUser, "nis" to nisUser, "kelas" to kelasUser, "email" to emailUser)
                                            finish()
                                        }
                                        "Siswa" -> {
                                            startActivity<HomeActivity>("nama" to namaUser, "access" to accessUser, "nis" to nisUser, "kelas" to kelasUser, "email" to emailUser)
                                            finish()
                                        }
                                    }
                                }
                            } else {
                                startActivity<HomeActivity>("nis" to nisUser, "nama" to namaUser, "kelas" to kelasUser, "email" to emailUser, "access" to accessUser)
                                finish()
                            }
                        } else {
                            if(!passParsed.equals(passUserParsed)) {
                                //Password Salah
                                showAlert("Password Salah", "Gagal")
                            }
                        }
                    } else {
                        alert("Tidak dapat menemukan E-Mail ini. Atau mungkin E-Mail yang anda masukkan salah.", "Gagal") {
                            positiveButton("KEMBALI") {
                            }
                        }.show()
                    }
                }
            }
        }

    }

    private fun showAlert(m: String?, title: String?) {
        m?.let {
            alert (it, title){
                yesButton {  }
        }.show()
        }
    }
}
