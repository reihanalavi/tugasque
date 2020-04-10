package com.smansara.tugasque.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.google.firebase.firestore.FirebaseFirestore
import com.smansara.tugasque.R
import com.smansara.tugasque.utils.ListSelectors
import kotlinx.android.synthetic.main.activity_lengkapi.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.startActivity

class LengkapiActivity : AppCompatActivity() {

    lateinit var i: Intent
    lateinit var firestore: FirebaseFirestore
    var email = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lengkapi)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        i = intent

        firestore = FirebaseFirestore.getInstance()
        email = i.getStringExtra("email")
        if(email.isEmpty()) {
            editText_lengkapi_email.hint = "E-Mail"
        } else {
            editText_lengkapi_email.setText(email)
        }

        button_lengkapi_masuk.onClick {
            if(editText_lengkapi_email.text.toString().isEmpty() || editText_lengkapi_password.text.toString().isEmpty() ||
                    editText_lengkapi_nama.text.toString().isEmpty() || editText_lengkapi_nis.text.toString().isEmpty()) {
                alert("Periksa data yang kosong", "Gagal"){ positiveButton("KEMBALI") {} }.show()
            } else {
                if(editText_lengkapi_password.text.toString().trim().length < 6) {
                    alert("Password harus lebih dari 6 huruf/angka", "Gagal") {
                        positiveButton("KEMBALI") {}
                    }.show()
                } else {
                    masuk(editText_lengkapi_nama.text.toString(), editText_lengkapi_nis.text.toString())
                }
            }
        }

        button_lengkapi_kembali.onClick {
            onBackPressed()
        }

    }

    private fun masuk(nama: String, nis: String) {
        firestore.collection("main_accounts").whereEqualTo("nis", editText_lengkapi_nis.text.toString()).get()
                .addOnSuccessListener { document ->
                    if(!document.documents.isEmpty()) {
                        val nisGet = document.documents[0].getString("nis")
                        val namaGet = document.documents[0].getString("nama")
                        val accessGet = document.documents[0].getString("access")
                        val kelasGet = document.documents[0].getString("kelas")
                        val emailGet = document.documents[0].getString("email")

                        val namaGetParsed = namaGet.replace(" ", "")
                        val namaParsed = nama.replace(" ", "")

                        Log.d("NAMA GET PARSED", namaGetParsed)
                        Log.d("NAMA PARSED", namaParsed)
                        if (namaGetParsed.toUpperCase().equals(namaParsed.toUpperCase(), ignoreCase = true) && nisGet.equals(nis)) {
                            //data login benar
                            if (emailGet.isEmpty() || emailGet.equals("")) {
                                //aman untuk disinggahi.
                                val spUser = getSharedPreferences("USERPREFS", Context.MODE_PRIVATE)

                                spUser
                                        .edit()
                                        .putString("NIS", nisGet)
                                        .apply()

                                val includeMap = hashMapOf(
                                        "email" to editText_lengkapi_email.text.toString(),
                                        "password" to editText_lengkapi_password.text.toString()
                                )
                                firestore.collection("main_accounts").document(nisGet).update(includeMap as Map<String, Any>).addOnSuccessListener {
                                    when (accessGet) {
                                        "6" -> {
                                            selector("Pilih State", ListSelectors.listAdmin) { dialogInterface, i ->
                                                when(ListSelectors.listAdmin[i]) {
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
                                        "5" -> startActivity<KepsekActivity>("nama" to namaGet, "access" to accessGet, "nis" to nisGet, "kelas" to kelasGet, "email" to editText_lengkapi_email.text.toString())
                                        "4" -> startActivity<GuruActivity>("nama" to namaGet, "access" to accessGet, "nis" to nisGet, "kelas" to kelasGet, "email" to editText_lengkapi_email.text.toString())
                                        else -> startActivity<HomeActivity>("nama" to namaGet, "access" to accessGet, "nis" to nisGet, "kelas" to kelasGet, "email" to editText_lengkapi_email.text.toString())
                                    }
                                }
                            } else if (emailGet.equals(editText_lengkapi_email.text.toString()) || emailGet.isNotEmpty()) {
                                //ada seseorang.
                                alert("Akun ini sudah dipakai. Gunakan akun lain", "Gagal") {
                                    positiveButton("KEMBALI") {
                                        onBackPressed()
                                    }
                                }.show()
                            }
                        } else {
                            if(!namaGetParsed.toUpperCase().equals(namaParsed.toUpperCase(), ignoreCase = true)) {
                                alert("Periksa isian Nama Lengkap. Pengisian gelar diperlukan untuk Guru.", "Gagal") {
                                    positiveButton("KEMBALI") {}
                                }.show()
                            }
                        }
                    } else {
                        alert("NIS/Kode guru tidak ditemukan", "Gagal") {
                            positiveButton("KEMBALI") {onBackPressed()}
                        }.show()
                    }
                }
    }
}
