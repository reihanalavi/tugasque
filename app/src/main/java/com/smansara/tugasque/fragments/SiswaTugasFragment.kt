package com.smansara.tugasque.fragments


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smansara.tugasque.R
import com.smansara.tugasque.activities.AdminActivity
import com.smansara.tugasque.activities.BuatActivity
import com.smansara.tugasque.activities.DetailActivity
import com.smansara.tugasque.adapters.TugasAdapter
import com.smansara.tugasque.models.TugasItems
import kotlinx.android.synthetic.main.fragment_siswa_tugas.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SiswaTugasFragment : Fragment() {

    lateinit var firestore: FirebaseFirestore
    lateinit var adapter: TugasAdapter
    var tugasItemsList: MutableList<TugasItems> = mutableListOf()

    var sortBy: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val views = inflater.inflate(R.layout.fragment_siswa_tugas, container, false)

        firestore = FirebaseFirestore.getInstance()

        //Load Name & Class
        sortBy = "tanggalSelesai"
        views.button_siswa_sortwaktu.textColor = Color.WHITE
        views.button_siswa_sortjenis.textColor = Color.parseColor("#0F7896")
        views.button_siswa_sortwaktu.setBackgroundResource(R.drawable.selected_button)
        views.button_siswa_sortjenis.setBackgroundResource(R.drawable.unselected_button)

        val access = arguments?.getString("access")
        Log.d("ACCESS ON FRAGMENT", access)
        Log.d("COMMONS ON FRAGMENT", arguments?.getString("kelas"))
        views.textView_siswa_kelas.text = arguments?.getString("kelas")
        views.textView_siswa_nama.text = arguments?.getString("nama")

        //Load "BUAT TUGAS" Button
        if (!access.equals("0")) {
            views.button_siswa_buat.visibility = View.VISIBLE
            views.button_siswa_buat.isEnabled = true
        } else {
            views.button_siswa_buat.visibility = View.INVISIBLE
            views.button_siswa_buat.isEnabled = false
        }

        if(access.equals("6")) {
            views.button_siswa_admin.visibility = View.VISIBLE
            views.button_siswa_admin.isEnabled = true
        } else {
            views.button_siswa_admin.visibility = View.INVISIBLE
            views.button_siswa_admin.isEnabled = false
        }

        views.button_siswa_buat.setOnClickListener {
            startActivity<BuatActivity>("state" to "CREATE", "nama" to arguments?.getString("nama"), "access" to arguments?.getString("access"), "nis" to arguments?.getString("nis"), "kelas" to arguments?.getString("kelas"), "email" to arguments?.getString("email"))
        }

        views.button_siswa_admin.setOnClickListener {
            startActivity<AdminActivity>("nama" to arguments?.getString("nama"), "access" to arguments?.getString("access"), "nis" to arguments?.getString("nis"), "kelas" to arguments?.getString("kelas"), "email" to arguments?.getString("email"))
        }

        adapter = TugasAdapter(this.activity!!) {
            when {
                it.download3?.isNotEmpty()!! -> context?.startActivity<DetailActivity>(
                        "nama" to it.nama, "deskripsi" to it.deskripsi, "jenis" to it.jenis, "lastUpdatedBy" to it.lastUpdatedBy,
                        "kelas" to it.kelas, "guru" to it.guru, "mapel" to it.mapel,
                        "download1" to it.download1, "download2" to it.download2, "download3" to it.download3,
                        "tanggalMulai" to it.tanggalMulai, "tanggalSelesai" to it.tanggalSelesai, "dateCreated" to it.dateCreated,
                        "id" to it.idKelas, "intPic" to "3", "dateCreated" to it.dateCreated, "namaUser" to arguments?.getString("nama"),
                        "access" to arguments?.getString("access"), "nis" to arguments?.getString("nis")
                        , "kelasUser" to arguments?.getString("kelas"), "email" to arguments?.getString("email")
                )
                it.download2?.isNotEmpty()!! -> context?.startActivity<DetailActivity>(
                        "nama" to it.nama, "deskripsi" to it.deskripsi, "jenis" to it.jenis, "lastUpdatedBy" to it.lastUpdatedBy,
                        "kelas" to it.kelas, "guru" to it.guru, "mapel" to it.mapel,
                        "download1" to it.download1, "download2" to it.download2,
                        "tanggalMulai" to it.tanggalMulai, "tanggalSelesai" to it.tanggalSelesai, "dateCreated" to it.dateCreated,
                        "id" to it.idKelas, "intPic" to "2", "dateCreated" to it.dateCreated, "namaUser" to arguments?.getString("nama"),
                        "access" to arguments?.getString("access"), "nis" to arguments?.getString("nis")
                        , "kelasUser" to arguments?.getString("kelas"), "email" to arguments?.getString("email")
                )
                it.download1?.isNotEmpty()!! -> context?.startActivity<DetailActivity>(
                        "nama" to it.nama, "deskripsi" to it.deskripsi, "jenis" to it.jenis, "lastUpdatedBy" to it.lastUpdatedBy,
                        "kelas" to it.kelas, "guru" to it.guru, "mapel" to it.mapel,
                        "download1" to it.download1,
                        "tanggalMulai" to it.tanggalMulai, "tanggalSelesai" to it.tanggalSelesai, "dateCreated" to it.dateCreated,
                        "id" to it.idKelas, "intPic" to "1", "dateCreated" to it.dateCreated, "namaUser" to arguments?.getString("nama"),
                        "access" to arguments?.getString("access"), "nis" to arguments?.getString("nis")
                        , "kelasUser" to arguments?.getString("kelas"), "email" to arguments?.getString("email")
                )
                it.download1?.isEmpty()!! -> context?.startActivity<DetailActivity>(
                        "nama" to it.nama, "deskripsi" to it.deskripsi, "jenis" to it.jenis, "lastUpdatedBy" to it.lastUpdatedBy,
                        "kelas" to it.kelas, "guru" to it.guru, "mapel" to it.mapel,
                        "tanggalMulai" to it.tanggalMulai, "tanggalSelesai" to it.tanggalSelesai, "dateCreated" to it.dateCreated,
                        "id" to it.idKelas, "intPic" to "0", "dateCreated" to it.dateCreated, "namaUser" to arguments?.getString("nama"),
                        "access" to arguments?.getString("access"), "nis" to arguments?.getString("nis")
                        , "kelasUser" to arguments?.getString("kelas"), "email" to arguments?.getString("email")
                )
            }
        }
        adapter.listTugas = this.tugasItemsList
        views.recyclerView_siswa_tugas.layoutManager = LinearLayoutManager(this.activity)
        views.recyclerView_siswa_tugas.adapter = adapter
        paketRefresh(arguments?.getString("kelas"), sortBy)
        emptyHandler()

        views.swipeRefresh_siswaTugas.setOnRefreshListener {
            paketRefresh(arguments?.getString("kelas"), sortBy)
            emptyHandler()
        }

        views.recyclerView_siswa_tugas.addItemDecoration(
                DividerItemDecoration(
                        context,
                        LinearLayoutManager.VERTICAL
                )
        )

        views.button_siswa_sortwaktu.onClick {
            sortBy = "tanggalSelesai"
            views.button_siswa_sortwaktu.textColor = Color.WHITE
            views.button_siswa_sortjenis.textColor = Color.parseColor("#0F7896")
            views.button_siswa_sortwaktu.setBackgroundResource(R.drawable.selected_button)
            views.button_siswa_sortjenis.setBackgroundResource(R.drawable.unselected_button)

            tugasItemsList.sortBy {
                it.tanggalSelesai
            }
            adapter.notifyDataSetChanged()
        }

        views.button_siswa_sortjenis.onClick {
            sortBy = "jenis"
            views.button_siswa_sortwaktu.textColor = Color.parseColor("#0F7896")
            views.button_siswa_sortjenis.textColor = Color.WHITE
            views.button_siswa_sortwaktu.setBackgroundResource(R.drawable.unselected_button)
            views.button_siswa_sortjenis.setBackgroundResource(R.drawable.selected_button)

            tugasItemsList.sortBy {
                it.jenis
            }
            adapter.notifyDataSetChanged()
        }

        return views
    }

    private fun paketRefresh(kelas: String?, sortBy: String?) {
        tugasItemsList.clear()
        adapter.notifyDataSetChanged()
        view?.swipeRefresh_siswaTugas?.visibility = View.VISIBLE
        view?.imageView_siswa_none?.visibility = View.INVISIBLE
        loadTugasAnyar(kelas, sortBy)
    }

    @SuppressLint("SimpleDateFormat")
    private fun loadTugasAnyar(kelas: String?, sortBy: String?) {
        firestore.collection("list_tugas").document("tugas").collection(kelas.toString()).orderBy(sortBy.toString(), Query.Direction.ASCENDING)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (ds in task.result!!) {
                            val tanggalSeleai = ds.get("tanggalSelesai").toString()
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            try {
                                val strDate = sdf.parse(tanggalSeleai)
                                val calNow = Date()

                                val tugasItems = ds.toObject(TugasItems::class.java)
                                if (!calNow.after(strDate)) {
                                    if(ds.get("kelas").toString().equals(kelas)) {
                                        tugasItems.deskripsi = ds.get("deskripsi").toString()
                                        tugasItems.download1 = ds.get("download_1").toString()
                                        tugasItems.download2 = ds.get("download_2").toString()
                                        tugasItems.download3 = ds.get("download_3").toString()
                                        tugasItems.guru = ds.get("guru").toString()
                                        tugasItems.jenis = ds.get("jenis").toString()
                                        tugasItems.kelas = ds.get("kelas").toString()
                                        tugasItems.mapel = ds.get("mapel").toString()
                                        tugasItems.nama = ds.get("nama").toString()
                                        tugasItems.tanggalMulai = ds.get("tanggalMulai").toString()
                                        tugasItems.tanggalSelesai = ds.get("tanggalSelesai").toString()
                                        tugasItems.dateCreated = ds.get("date_created").toString()
                                        tugasItems.idKelas = ds.id
                                        tugasItems.state = "MAPEL"
                                        tugasItems.mine = "NO"
                                        tugasItems.lastUpdatedBy = ds.get("lastUpdatedBy").toString()

                                        tugasItemsList.add(tugasItems)
                                        Log.d("SIZE", "${tugasItemsList.size}")

                                        adapter.listTugas = this.tugasItemsList
                                        adapter.notifyDataSetChanged()

                                        view?.progressBar_siswa?.visibility = View.INVISIBLE
                                        view?.swipeRefresh_siswaTugas?.isRefreshing = false

                                        emptyHandler()
                                    }
                                }

                            } catch (e1: ParseException) {
                                e1.printStackTrace()
                            }
                        }
                    }
                }
    }

    fun emptyHandler() {
        Handler().postDelayed(object : Runnable {

            override fun run() {
                if (view?.swipeRefresh_siswaTugas?.isRefreshing == true || view?.progressBar_siswa?.visibility == View.VISIBLE || tugasItemsList.size < 1) {
                    view?.progressBar_siswa?.visibility = View.INVISIBLE
                    view?.imageView_siswa_none?.visibility = View.VISIBLE
                    view?.swipeRefresh_siswaTugas?.isRefreshing = false
                }
            }
        }, 5000)
    }

} // Required empty public constructor
