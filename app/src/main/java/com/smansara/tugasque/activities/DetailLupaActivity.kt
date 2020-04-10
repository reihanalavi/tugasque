package com.smansara.tugasque.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.smansara.tugasque.R
import kotlinx.android.synthetic.main.activity_detail_lupa.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
class DetailLupaActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    lateinit var i: Intent

    @SuppressLint("SetTextI18n", "IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_lupa)

        firestore = FirebaseFirestore.getInstance()
        i = intent

        val nama = i.getStringExtra("nama")
        val nomor = i.getStringExtra("nomor")
        val plama = i.getStringExtra("plama")
        val pbaru = i.getStringExtra("pbaru")
        val nis = i.getStringExtra("nis")

        textView_detaillupa_namaE.text = nama
        textView_detaillupa_nomorE.text = nomor
        textView_detaillupa_plamaE.text = plama
        textView_detaillupa_pbaruE.text = pbaru

        firestore.collection("main_accounts").document(nis.toString()).get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val ds = task.result
                        val password = ds.getString("password")

                        if(password.equals(pbaru)) {
                            //Belum Diganti
                            textView_detaillupa_statusE.text = "Belum Diganti"
                        } else {
                            textView_detaillupa_statusE.text = "Sudah Diganti"
                        }
                    }
                }

        button_detaillupa_reset.onClick {
            val resetMap = hashMapOf( "password" to plama )

            firestore.collection("main_accounts").document(nis).update(resetMap as HashMap<String, Any>)
                    .addOnSuccessListener {
                        alert("Password berhasil dikembalikan", "Berhasil"){
                            positiveButton("OK") {}
                        }.show()
                    }
        }

        button_detaillupa_hubungi.onClick {
            val a = nomor.replace(" ","")
            val b = a.replace("-","")
            val c = b.replace("+62","62")

            val nomorParsed: String = c

            val pesan = "Hai *$nama*, password sementara kamu adalah *$pbaru*. Silahkan ganti password dengan segera." +
                    "\n\n" +
                    "Tugasque Team"

            val beforeParsed = "http://api.whatsapp.com/send?phone=$nomorParsed&text=$pesan"
            val wauri = beforeParsed.replace("send?phone=0", "send?phone=62")

            Log.d("NOMOR PARSED", wauri)
            //val uri = Uri.parse("smsto:$nomorParsed")
            //val intent = Intent(Intent.ACTION_SENDTO, uri)
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse("http://api.whatsapp.com/send?phone=$wauri&text=$pesan")
            intent.data = uri
            intent.`package` = "com.whatsapp.w4b"
            startActivity(intent)

        }
    }
}
