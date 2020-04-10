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
import com.smansara.tugasque.utils.ListSelectors.listKelas
import kotlinx.android.synthetic.main.fragment_guru_tugas.*
import kotlinx.android.synthetic.main.fragment_guru_tugas.view.*
import kotlinx.android.synthetic.main.fragment_siswa_tugas.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GuruTugasFragment : Fragment() {

    private var access: String? = null
    lateinit var firestore: FirebaseFirestore
    lateinit var adapter: TugasAdapter
    var tugasItemsList: MutableList<TugasItems> = mutableListOf()

    var sortBy = ""

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val views = inflater.inflate(R.layout.fragment_guru_tugas, container, false)

        firestore = FirebaseFirestore.getInstance()

        sortBy = "tanggalSelesai"
        views.button_guru_sortwaktu.textColor = Color.WHITE
        views.button_guru_sortjenis.textColor = Color.parseColor("#0F7896")
        views.button_guru_sortwaktu.setBackgroundResource(R.drawable.selected_button)
        views.button_guru_sortjenis.setBackgroundResource(R.drawable.unselected_button)

        access = arguments?.getString("access")
        Log.d("ACCESS ON FRAGMENT", access)
        Log.d("COMMONS ON FRAGMENT", arguments?.getString("nama"))
        views.textView_guru_nama.text = arguments?.getString("nama")

        if(access.equals("6")) {
            views.button_guru_admin.visibility = View.VISIBLE
            views.button_guru_admin.isEnabled = true
        } else {
            views.button_guru_admin.visibility = View.INVISIBLE
            views.button_guru_admin.isEnabled = false
        }

        views.button_guru_kelas.setOnClickListener {
            selector("Pilih Kelas", listKelas) { dialogInterface, i ->
                button_guru_kelas.text = listKelas[i]
                paketRefresh(views.button_guru_kelas.text.toString(), sortBy)
                emptyHandler()
            }
        }

        views.button_guru_buat.setOnClickListener {
            startActivity<BuatActivity>("state" to "CREATE", "nama" to arguments?.getString("nama"), "access" to arguments?.getString("access"), "nis" to arguments?.getString("nis"), "kelas" to arguments?.getString("kelas"), "email" to arguments?.getString("email"))
        }

        views.button_guru_admin.setOnClickListener {
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
                        "access" to arguments?.getString("access"), "nis" to arguments?.getString("nis"),
                        "kelasUser" to arguments?.getString("kelas"), "email" to arguments?.getString("email")
                )
                it.download1?.isNotEmpty()!! -> context?.startActivity<DetailActivity>(
                        "nama" to it.nama, "deskripsi" to it.deskripsi, "jenis" to it.jenis, "lastUpdatedBy" to it.lastUpdatedBy,
                        "kelas" to it.kelas, "guru" to it.guru, "mapel" to it.mapel,
                        "download1" to it.download1,
                        "tanggalMulai" to it.tanggalMulai, "tanggalSelesai" to it.tanggalSelesai, "dateCreated" to it.dateCreated,
                        "id" to it.idKelas, "intPic" to "1", "dateCreated" to it.dateCreated, "namaUser" to arguments?.getString("nama"),
                        "access" to arguments?.getString("access"), "nis" to arguments?.getString("nis"),
                        "kelasUser" to arguments?.getString("kelas"), "email" to arguments?.getString("email")
                )
                it.download1?.isEmpty()!! -> context?.startActivity<DetailActivity>(
                        "nama" to it.nama, "deskripsi" to it.deskripsi, "jenis" to it.jenis, "lastUpdatedBy" to it.lastUpdatedBy,
                        "kelas" to it.kelas, "guru" to it.guru, "mapel" to it.mapel,
                        "tanggalMulai" to it.tanggalMulai, "tanggalSelesai" to it.tanggalSelesai, "dateCreated" to it.dateCreated,
                        "id" to it.idKelas, "intPic" to "0", "dateCreated" to it.dateCreated, "namaUser" to arguments?.getString("nama"),
                        "access" to arguments?.getString("access"), "nis" to arguments?.getString("nis"),
                        "kelasUser" to arguments?.getString("kelas"), "email" to arguments?.getString("email")
                )
            }
        }

        adapter.listTugas = this.tugasItemsList
        views.recyclerView_guru_tugas.layoutManager = LinearLayoutManager(this.activity)
        views.recyclerView_guru_tugas.adapter = adapter

        views.swipeRefresh_guruTugas.setOnRefreshListener {
            if(arguments?.getString("state").equals("KELAS")) {
                if(views.button_guru_kelas.text.toString().equals("PILIH KELAS")) {
                    alert("Mohon pilih kelas terlebih dahulu", "Gagal"){ positiveButton("KEMBALI") {} }.show()
                    views.swipeRefresh_guruTugas.isRefreshing = false
                } else {
                    paketRefresh(views.button_guru_kelas.text.toString(), sortBy)
                    emptyHandler()
                }
            } else if(arguments?.getString("state").equals("PRIBADI")) {
                paketRefresh("${arguments?.getString("nis")}   ${arguments?.getString("nama")}", sortBy)
            }
        }

        views.recyclerView_guru_tugas.addItemDecoration(
                DividerItemDecoration(
                        context,
                        LinearLayoutManager.VERTICAL
                )
        )

        views.button_guru_sortwaktu.onClick {
            sortBy = "tanggalSelesai"
            views.button_guru_sortwaktu.textColor = Color.WHITE
            views.button_guru_sortjenis.textColor = Color.parseColor("#0F7896")
            views.button_guru_sortwaktu.setBackgroundResource(R.drawable.selected_button)
            views.button_guru_sortjenis.setBackgroundResource(R.drawable.unselected_button)

            tugasItemsList.sortBy {
                it.tanggalSelesai
            }
            adapter.notifyDataSetChanged()
        }

        views.button_guru_sortjenis.onClick {
            sortBy = "jenis"
            views.button_guru_sortwaktu.textColor = Color.parseColor("#0F7896")
            views.button_guru_sortjenis.textColor = Color.WHITE
            views.button_guru_sortwaktu.setBackgroundResource(R.drawable.unselected_button)
            views.button_guru_sortjenis.setBackgroundResource(R.drawable.selected_button)

            if(arguments?.getString("state").equals("KELAS")) {
                tugasItemsList.sortBy {
                    it.mine
                }
            } else if(arguments?.getString("state").equals("PRIBADI")) {
                tugasItemsList.sortBy {
                    it.jenis
                }
            }

            adapter.notifyDataSetChanged()
        }

        if(arguments?.getString("state").equals("KELAS")) {
            views.button_guru_tugassekolah.visibility = View.INVISIBLE
            views.button_guru_tugasproyek.visibility = View.INVISIBLE
            views.button_guru_tugaspraktek.visibility = View.INVISIBLE
            views.button_guru_tugasrumah.text = "Pribadi"

            views.button_guru_kelas.isEnabled = true
            views.button_guru_kelas.text = "PILIH KELAS"
        } else if(arguments?.getString("state").equals("PRIBADI")) {
            views.button_guru_tugassekolah.visibility = View.VISIBLE
            views.button_guru_tugasproyek.visibility = View.VISIBLE
            views.button_guru_tugaspraktek.visibility = View.VISIBLE
            views.button_guru_tugasrumah.text = "Rumah"

            views.button_guru_kelas.isEnabled = false
            views.button_guru_kelas.text = "Tugas Saya"
            paketRefresh("${arguments?.getString("nis")}   ${arguments?.getString("nama")}", sortBy)
        }

        return views
    }

    private fun paketRefresh(kelas: String?, sortBy: String?) {
        tugasItemsList.clear()
        adapter.notifyDataSetChanged()
        view?.swipeRefresh_guruTugas?.visibility = View.VISIBLE
        view?.imageView_guru_none?.visibility = View.INVISIBLE
        view?.progressBar_siswa?.visibility = View.INVISIBLE
        loadTugasAnyar(kelas, sortBy)
    }

    @SuppressLint("SimpleDateFormat")
    private fun loadTugasAnyar(kelas: String?, sortBy: String?) {
        if(arguments?.getString("state").equals("KELAS")) {
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
                                            tugasItems.idGuru = ds.id
                                            tugasItems.state = "KELAS"
                                            if(ds.get("guru").toString().equals("${arguments?.getString("nis")}   ${arguments?.getString("nama")}")) {
                                                tugasItems.mine = "YES"
                                            } else {
                                                tugasItems.mine = "UNYES"
                                            }
                                            tugasItems.lastUpdatedBy = ds.get("lastUpdatedBy").toString()

                                            Log.d("GURU LIST", ds.get("guru").toString())
                                            Log.d("GURU TRACED", "${arguments?.getString("nis")}   ${arguments?.getString("nama")}")

                                            tugasItemsList.add(tugasItems)
                                            Log.d("SIZE", "${tugasItemsList.size}")

                                            adapter.listTugas = this.tugasItemsList
                                            adapter.notifyDataSetChanged()

                                            view?.swipeRefresh_guruTugas?.isRefreshing = false

                                            emptyHandler()

                                        }

                                    }

                                } catch (e1: ParseException) {
                                    e1.printStackTrace()
                                }
                            }
                        }
                    }
        } else if(arguments?.getString("state").equals("PRIBADI")) {
            firestore.collection("list_guru").document("guru").collection(kelas.toString()).orderBy(sortBy.toString(), Query.Direction.ASCENDING)
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
                                        if(ds.get("guru").toString().equals(kelas)) {
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
                                            tugasItems.idGuru = ds.id
                                            tugasItems.state = "GURU"
                                            tugasItems.mine = "NO"
                                            tugasItems.lastUpdatedBy = ds.get("lastUpdatedBy").toString()

                                            Log.d("GURU LIST", ds.get("guru").toString())
                                            Log.d("GURU TRACED", "${arguments?.getString("nis")}   ${arguments?.getString("nama")}")

                                            tugasItemsList.add(tugasItems)
                                            Log.d("SIZE", "${tugasItemsList.size}")

                                            adapter.listTugas = this.tugasItemsList
                                            adapter.notifyDataSetChanged()

                                            view?.swipeRefresh_guruTugas?.isRefreshing = false

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
    }

    fun emptyHandler() {
        Handler().postDelayed(object : Runnable {

            override fun run() {
                if (view?.swipeRefresh_guruTugas?.isRefreshing == true || tugasItemsList.size < 1) {
                    view?.imageView_guru_none?.visibility = View.VISIBLE
                    view?.swipeRefresh_guruTugas?.isRefreshing = false
                }
            }
        }, 5000)
    }

}
