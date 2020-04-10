package com.smansara.tugasque.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore

import com.smansara.tugasque.R
import kotlinx.android.synthetic.main.fragment_admin_entry.*
import kotlinx.android.synthetic.main.fragment_admin_entry.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.alert

@Suppress("UNREACHABLE_CODE", "UNCHECKED_CAST")
class AdminEntryFragment : Fragment() {

    lateinit var firestore: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val views = inflater.inflate(R.layout.fragment_admin_entry, container, false)

        firestore = FirebaseFirestore.getInstance()
        views.button_adminentry_set.onClick { entryDatafull() }
        views.button_adminentry_update.onClick { entryDatacustom() }
        views.button_adminentry_check.onClick { checkSuggestion() }
        views.button_adminentry_insertset.onClick { entryDataindieset() }
        views.button_adminentry_insertupdate.onClick { entryDataindieupdate() }

        return views

    }

    fun entryDataindieupdate() {
        val nis = editText_adminentry_insertnis.text.toString()
        val nama = editText_adminentry_insertnama.text.toString()
        val kelas = editText_adminentry_insertkelas.text.toString()
        val akses = editText_adminentry_insertakses.text.toString()

        if(nis.isNotEmpty()) {
            if(nama.isNotEmpty()) {
                val updateMap = hashMapOf("nama" to nama)
                updateFirestore(nis, updateMap)
            }
            if(kelas.isNotEmpty()) {
                val updateMap = hashMapOf("kelas" to kelas)
                updateFirestore(nis, updateMap)
            }
            if(akses.isNotEmpty()) {
                val updateMap = hashMapOf("akses" to akses)
                updateFirestore(nis, updateMap)
            }
        } else {
            alert("Periksa data yang kosong", "Gagal"){ positiveButton("OK") {} }.show()
        }
    }

    fun updateFirestore(nis: String, map: HashMap<String, String>) {
        firestore.collection("main_accounts").document(nis).update(map as Map<String, Any>)
                .addOnSuccessListener {
                    alert("Entry Data berhasil", "Berhasil"){
                        positiveButton("OK") {}
                    }.show()
                }
    }


    fun entryDataindieset() {
        val nis = editText_adminentry_insertnis.text.toString()
        val nama = editText_adminentry_insertnama.text.toString()
        val kelas = editText_adminentry_insertkelas.text.toString()
        val akses = editText_adminentry_insertakses.text.toString()

        if(nis.isNotEmpty() && nama.isNotEmpty() && kelas.isNotEmpty() && akses.isNotEmpty()) {
            val indieSet = hashMapOf(
                    "nis" to nis,
                    "nama" to nama,
                    "kelas" to kelas,
                    "akses" to akses,
                    "email" to ""
            )
            firestore.collection("main_accounts").document(nis).set(indieSet as HashMap<String, Any>)
                    .addOnSuccessListener {
                        alert("Entry Data berhasil", "Berhasil"){
                            positiveButton("OK") {}
                        }.show()
                    }
        } else {
            alert("Periksa data yang kosong", "Gagal"){ positiveButton("OK") {} }.show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun checkSuggestion() {
        if(view?.editText_adminentry_update?.text?.isNotEmpty()!!) {
            val edittext = editText_adminentry_update.text.toString()
            val s1 = edittext.split(" | ")
            val s2 = s1[0].split("+")

            val ref = s2[0].split(",")[1]
            val key = s2[1].split(",")[0]
            val value = s2[1].split(",")[1]

            view?.textView_adminentry_suggested?.text =
                    "Suggested Custom (KEY : $key, VALUE : $value"
        } else {
            view?.textView_adminentry_suggested?.text =
                    "Masukkan String Format terlebih dahulu"
        }
    }

    fun entryDatafull() {
        val edittext = editText_adminentry_set.text.toString()

        if(edittext.isEmpty() || edittext.equals("")) {
            //Data Kosong
        } else {
            val s1 = edittext.split(" | ")

            for (item in s1) {
                val s2 = item.split(",")
                val nis = s2[0]
                val nama = s2[1]
                val kelas = s2[2]
                val access = "0"
                val email = ""
                val setMap = hashMapOf(
                        "nis" to nis,
                        "nama" to nama,
                        "kelas" to kelas,
                        "access" to access,
                        "email" to email
                )
                Log.d("ENTRY DATA (ALL)", "$nis, $nama, $kelas, $access, $email")
                firestore.collection("main_accounts").document(nis).set(setMap as HashMap<String, Any>)
                        .addOnSuccessListener {
                            alert("Entry Data Berhasil", "Berhasil"){
                                positiveButton("OK") {}
                            }.show()
                        }
            }
        }
    }

    fun entryDatacustom() {
        val edittext = editText_adminentry_update.text.toString()

        if(edittext.isEmpty() || edittext.equals("")) {
            //Data Kosong
        } else {
            val s1 = edittext.split(" | ")

            for (item in s1) {
                val s2 = item.split("+")

                val ref = s2[0].split(",")[1]
                val key = s2[1].split(",")[0]
                val value = s2[1].split(",")[1]

                val updateMap = hashMapOf(
                        key to value
                )
                Log.d("ENTRY DATA (CUSTOM)", "NIS : $ref, KEY : $key, VALUE : $value")
                firestore.collection("main_accounts").document(ref).update(updateMap as HashMap<String, Any>)
                        .addOnSuccessListener {
                            alert("Entry Data Berhasil", "Berhasil"){
                                positiveButton("OK") {}
                            }.show()
                        }
            }
        }
    }
}
