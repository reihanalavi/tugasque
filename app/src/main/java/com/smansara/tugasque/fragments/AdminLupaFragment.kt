package com.smansara.tugasque.fragments


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
import com.smansara.tugasque.activities.DetailLupaActivity
import com.smansara.tugasque.adapters.LupaAdapter
import com.smansara.tugasque.adapters.TugasAdapter
import com.smansara.tugasque.models.LupaItems
import kotlinx.android.synthetic.main.fragment_admin_lupa.*
import kotlinx.android.synthetic.main.fragment_admin_lupa.view.*
import kotlinx.android.synthetic.main.fragment_siswa_tugas.view.*
import org.jetbrains.anko.support.v4.startActivity

class AdminLupaFragment : Fragment() {

    lateinit var firestore: FirebaseFirestore
    lateinit var adapter: LupaAdapter
    var lupaItemsList: MutableList<LupaItems> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val views = inflater.inflate(R.layout.fragment_admin_lupa, container, false)

        firestore = FirebaseFirestore.getInstance()
        adapter = LupaAdapter(this.activity!!) {
            startActivity<DetailLupaActivity>(
                    "email" to it.email,
                    "created" to it.created,
                    "nama" to it.nama,
                    "nomor" to it.nomor,
                    "nis" to it.nis,
                    "pbaru" to it.pbaru,
                    "plama" to it.plama
            )
        }

        adapter.listLupa = this.lupaItemsList
        views.recyclerView_adminlupa.layoutManager = LinearLayoutManager(this.activity)
        views.recyclerView_adminlupa.adapter = adapter
        paketRefresh()
        emptyHandler()

        views.swipeRefresh_adminlupa.setOnRefreshListener {
            paketRefresh()
            emptyHandler()
        }

        views.recyclerView_adminlupa.addItemDecoration(
                DividerItemDecoration(
                        context,
                        LinearLayoutManager.VERTICAL
                )
        )

        return views
    }
    
    private fun paketRefresh() {
        lupaItemsList.clear()
        adapter.notifyDataSetChanged()
        view?.swipeRefresh_adminlupa?.visibility = View.VISIBLE
        loadLupa()
    }

    fun loadLupa() {
        firestore.collection("lupa_akun").orderBy("created", Query.Direction.DESCENDING)
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        for (ds in task.result!!) {
                            val lupaItems = ds.toObject(LupaItems::class.java)
                            if(ds.get("nis").toString().isNotEmpty()) {
                                lupaItems.email = ds.get("email").toString()
                                lupaItems.created = ds.get("created").toString()
                                lupaItems.nama= ds.get("nama").toString()
                                lupaItems.nis = ds.get("nis").toString()
                                lupaItems.nomor = ds.get("nomor").toString()
                                lupaItems.pbaru = ds.get("pbaru").toString()
                                lupaItems.plama = ds.get("plama").toString()

                                lupaItemsList.add(lupaItems)
                                adapter.listLupa = this.lupaItemsList
                                adapter.notifyDataSetChanged()

                                view?.swipeRefresh_adminlupa?.isRefreshing = false

                                emptyHandler()
                            }
                        }
                    }
                }
    }

    fun emptyHandler() {
        Handler().postDelayed(object : Runnable {

            override fun run() {
                if (view?.swipeRefresh_adminlupa?.isRefreshing == true || lupaItemsList.size < 1) {
                    view?.swipeRefresh_adminlupa?.isRefreshing = false
                }
            }
        }, 5000)
    }

}
