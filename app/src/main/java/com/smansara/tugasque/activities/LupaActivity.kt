package com.smansara.tugasque.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_lupa.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*


@Suppress("UNCHECKED_CAST")
class LupaActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    var plama = ""

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.smansara.tugasque.R.layout.activity_lupa)

        firestore = FirebaseFirestore.getInstance()

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        button_lupa_kembali.onClick {
            onBackPressed()
        }

        button_lupa_masuk.onClick {
            val nis = editText_lupa_nis.text.toString()
            val nomor = editText_lupa_nomor.text.toString()

            if(nis.isEmpty() || nomor.isEmpty()) {
                alert("Periksa data yang kosong", "Gagal"){
                    positiveButton("KEMBALI") {

                    }
                }.show()
            } else {
                val dateFormat = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
                val dateNow = Date()
                val created = dateFormat.format(dateNow).toString()

                firestore.collection("main_accounts").document(nis).get()
                        .addOnSuccessListener { documentSnapshot ->
                            if(documentSnapshot.exists()) {
                                val namaGet = documentSnapshot.getString("nama")
                                val email = documentSnapshot.getString("email")

                                if(email.isNotEmpty()) {
                                    plama = documentSnapshot.getString("password")

                                    val rnd = Random()
                                    val number = rnd.nextInt(999999)

                                    val pbaru = String.format("%06d", number)

                                    //Send the report
                                    val lupaMap = hashMapOf(
                                            "nis" to nis,
                                            "nama" to namaGet,
                                            "nomor" to nomor,
                                            "email" to email,
                                            "plama" to plama,
                                            "pbaru" to pbaru,
                                            "created" to created
                                    )

                                    firestore.collection("lupa_akun").add(lupaMap as HashMap<String, Any>)
                                            .addOnSuccessListener {}

                                    //Update the old password to the recovery password
                                    val recoveryMap = hashMapOf(
                                            "password" to pbaru
                                    )
                                    firestore.collection("main_accounts").document(nis).update(recoveryMap as HashMap<String, Any>)
                                            .addOnSuccessListener {
                                                alert("Mohon tunggu kami untuk menghubungi anda", "Berhasil"){
                                                    positiveButton("KEMBALI") { onBackPressed() }
                                                }.show()
                                            }

                                } else if (email.isEmpty() || email.equals("")) {
                                    alert("Kamu belum terdaftar di Tugasque. Silahkan daftar terlebih dahulu", "Gagal"){
                                        positiveButton("DAFTAR") {
                                            startActivity<LengkapiActivity>()
                                        }
                                    }.show()
                                }
                            } else {
                                alert("NIS/Kode Guru tidak ditemukan", "Gagal"){
                                    positiveButton("KEMBALI") {}
                                }.show()
                            }
                        }
            }
        }

    }
}
