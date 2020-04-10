package com.smansara.tugasque.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.google.firebase.firestore.FirebaseFirestore
import com.smansara.tugasque.R
import kotlinx.android.synthetic.main.activity_ganti.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick

class GantiActivity : AppCompatActivity() {

    lateinit var i: Intent
    lateinit var firestore: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        i = intent
        firestore = FirebaseFirestore.getInstance()

        val email = i.getStringExtra("email")
        textView_ganti_deskripsi.text = "Ubah password untuk akun\n$email"

        button_ganti_masuk.onClick {
            val lama = editText_ganti_lama.text.toString()
            val baru = editText_ganti_baru.text.toString()
            val konfirm = editText_ganti_konfirm.text.toString()

            if(lama.isEmpty() || baru.isEmpty() || konfirm.isEmpty()) {
                //ada yang kosong
                alert("Periksa data yang kosong", "Gagal"){
                    positiveButton("KEMBALI") {}
                }.show()
            } else {
                if(baru.equals(konfirm)) {
                    //jika konfirmasi berhasil
                    if(baru.length < 6) {
                        alert("Password harus lebih dari 6 huruf/angka", "Gagal"){
                            positiveButton("KEMBALI") {}
                        }.show()
                    } else {
                        //jika password panjang
                        firestore.collection("main_accounts").whereEqualTo("email", email).get()
                                .addOnSuccessListener { document ->
                                    if(!document.documents.isEmpty()) {

                                        val nisGet = document.documents[0].getString("nis")
                                        val passwordGet = document.documents[0].getString("password")
                                        if (passwordGet.equals(lama)) {
                                            //data login benar
                                            val includeMap = hashMapOf(
                                                    "password" to editText_ganti_baru.text.toString()
                                            )
                                            firestore.collection("main_accounts").document(nisGet).update(includeMap as Map<String, Any>).addOnSuccessListener {
                                                alert("Password telah diubah. Silahkan masuk kembali"){
                                                    positiveButton("KEMBALI") {
                                                        onBackPressed()
                                                    }
                                                }.show()
                                            }
                                        } else {
                                            alert("Password lama salah", "Gagal") {
                                                positiveButton("KEMBALI") {}
                                            }.show()
                                        }
                                    } else {
                                        alert("Email tidak ditemukan", "Gagal") {
                                            positiveButton("KEMBALI") {onBackPressed()}
                                        }.show()
                                    }
                                }
                    }
                } else {
                    alert("Password tidak sesuai", "Gagal"){
                        positiveButton("KEMBALI") {}
                    }.show()
                }
            }
        }

    }
}
