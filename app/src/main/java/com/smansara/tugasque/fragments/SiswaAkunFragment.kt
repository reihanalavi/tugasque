package com.smansara.tugasque.fragments


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.smansara.tugasque.R
import com.smansara.tugasque.activities.LoginActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_siswa_akun.*
import kotlinx.android.synthetic.main.fragment_siswa_akun.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity


class SiswaAkunFragment : Fragment() {

    lateinit var firestore: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val views = inflater.inflate(R.layout.fragment_siswa_akun, container, false)

        firestore = FirebaseFirestore.getInstance()

        //Set Accounts
        val access = arguments?.getString("access")
        views.textView_siswa_kelasAkunE.text = arguments?.getString("kelas")
        views.textView_siswa_namalengkapE.text = arguments?.getString("nama")
        views.textView_siswa_emailE.text = arguments?.getString("email")
        var aksesTeks = ""
        when(access) {
            "0" -> {
                aksesTeks = "Siswa "
            }
            "1" -> {
                aksesTeks = "Sekretaris "
            }
            "2" -> {
                aksesTeks = "Wakil Ketua Kelas "
            }
            "3" -> {
                aksesTeks = "Ketua Kelas"
            }
            "4" -> {
                aksesTeks = "Guru"
            }
            "6" -> {
                aksesTeks = "Admin"
            }
        }

        views.textView_siswa_aksesE.text = aksesTeks

        views.textView_siswa_layanan.onClick {
            val uri = Uri.parse("smsto:6285875403716")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.`package` = "com.whatsapp"
            startActivity(intent)
        }

        val spUser = this.activity?.getSharedPreferences("USERPREFS", Context.MODE_PRIVATE)
        views.button_siswa_keluar.setOnClickListener {
            alert("Apakah anda yakin ingin keluar?", "Keluar"){
                negativeButton("KEMBALI"){  }
                positiveButton("YA") {

                    spUser?.edit()?.putString("NIS", "")?.apply()

                    FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/admin") //for reihan
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/${arguments?.getString("nis")}") //for his class
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/${arguments?.getString("kelas")}") //for his class
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/all")

                    startActivity<LoginActivity>()
                }
            }.show()
        }

        return views
    }

}// Required empty public constructor
