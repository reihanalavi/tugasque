package com.smansara.tugasque.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smansara.tugasque.R
import com.smansara.tugasque.adapters.NotifAdapter
import com.smansara.tugasque.models.NotifItems
import kotlinx.android.synthetic.main.fragment_admin_notif.*
import kotlinx.android.synthetic.main.fragment_admin_notif.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import java.text.SimpleDateFormat
import java.util.*

class AdminNotifFragment : Fragment() {

    lateinit var firestore: FirebaseFirestore
    lateinit var adapter: NotifAdapter
    var notifItemsList: MutableList<NotifItems> = mutableListOf()

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val views = inflater.inflate(R.layout.fragment_admin_notif, container, false)

        firestore = FirebaseFirestore.getInstance()
        adapter = NotifAdapter(this.activity!!) {}

        adapter.listNotif = this.notifItemsList
        views.recyclerView_adminnotif.layoutManager = LinearLayoutManager(this.activity)
        views.recyclerView_adminnotif.adapter = adapter
        paketRefresh()
        emptyHandler()

        views.swipeRefresh_adminnotif.setOnRefreshListener {
            paketRefresh()
            emptyHandler()
        }

        views.recyclerView_adminnotif.addItemDecoration(
                DividerItemDecoration(
                        context,
                        LinearLayoutManager.VERTICAL
                )
        )

        views.button_adminnotif_check.onClick {
            val nama = editText_adminnotif_nama.text.toString()
            if(nama.isNotEmpty()) {
                firestore.collection("main_accounts").whereEqualTo("nama", nama).get()
                        .addOnSuccessListener { document ->
                            if(document.documents.isEmpty()) {
                                //Tidak ditemukan
                                alert("Data siswa tidak ditemukan", "Gagal"){ positiveButton("OK") {} }.show()
                            } else {
                                val nis = document.documents[0].getString("nis").toString()
                                editText_adminnotif_topic.setText(nis)
                            }
                        }
            }
        }

        views.button_adminnotif_kirim.onClick {
            val topic = editText_adminnotif_topic.text.toString()
            val pesan = editText_adminnotif_pesan.text.toString()

            if(topic.isNotEmpty() && pesan.isNotEmpty()) {
                val dateFormat = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
                val dateNow = Date()
                val created = dateFormat.format(dateNow).toString()

                val notifMap = hashMapOf(
                        "created" to created,
                        "untuk" to topic,
                        "pesan" to pesan
                )

                firestore.collection("custom_notif").document().set(notifMap as Map<String, Any>)
                        .addOnSuccessListener {
                            alert("Notifikasi berhasil dikirim", "Berhasil"){ positiveButton("OK") {} }.show()
                        }
            } else {
                alert("Periksa data yang kosong", "Gagal"){ positiveButton("OK") {} }.show()
            }
        }

        return views
    }

    private fun paketRefresh() {
        notifItemsList.clear()
        adapter.notifyDataSetChanged()
        view?.swipeRefresh_adminnotif?.visibility = View.VISIBLE
        loadNotif()
    }

    fun loadNotif() {
        firestore.collection("custom_notif").orderBy("created", Query.Direction.DESCENDING)
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        for (ds in task.result!!) {
                            val notifItems = ds.toObject(NotifItems::class.java)

                            notifItems.untuk = ds.get("untuk").toString()
                            notifItems.created = ds.get("created").toString()
                            notifItems.pesan = ds.get("pesan").toString()

                            notifItemsList.add(notifItems)
                            adapter.listNotif = this.notifItemsList
                            adapter.notifyDataSetChanged()

                            view?.swipeRefresh_adminnotif?.isRefreshing = false

                            emptyHandler()
                        }
                    }
                }
    }

    fun emptyHandler() {
        Handler().postDelayed(object : Runnable {

            override fun run() {
                if (view?.swipeRefresh_adminnotif?.isRefreshing == true || notifItemsList.size < 1) {
                    view?.swipeRefresh_adminnotif?.isRefreshing = false
                }
            }
        }, 5000)
    }


}
