package com.smansara.tugasque.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.smansara.tugasque.utils.DateParser
import com.smansara.tugasque.utils.ListSelectors.listKelas
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_detail.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*


@Suppress("UNCHECKED_CAST")
class DetailActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var i: Intent
    lateinit var firestore: FirebaseFirestore
    var access = ""

    var uri1: Uri? = null
    var uri2: Uri? = null
    var uri3: Uri? = null

    var download1 = ""
    var download2 = ""
    var download3 = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.smansara.tugasque.R.layout.activity_detail)

        constraintLayout_bottomSheet_header.setOnClickListener {
            slideBottomSheet()
        }

        firestore = FirebaseFirestore.getInstance()
        i = intent
        access = i.getStringExtra("access")
        Log.d("THIS USER IS", i.getStringExtra("nama"))
        Log.d("THIS TASK UPDATED BY", i.getStringExtra("lastUpdatedBy").toString())

        val spUser = this.getSharedPreferences("USERPREFS", Context.MODE_PRIVATE)
        if (spUser?.getString("ACCESS", "").toString() != "0") {
            constraintLayout_detail_teruskan.visibility = View.VISIBLE
        } else {
            constraintLayout_detail_teruskan.visibility = View.GONE
        }

        val adapterKelas = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, listKelas)
        button_detail_kelas.adapter = adapterKelas
        button_detail_kelas.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                button_detail_kelas.setSelection(position)
            }

        }

        button_detail_teruskan.onClick {
            if (button_detail_kelas.selectedItem.toString().equals(i.getStringExtra("kelas"))) {
                alert("Tidak dapat meneruskan tugas ke kelas yang sama", "Gagal"){
                    positiveButton("Kembali") {} }.show()
            } else {
                alert("Yakin ingin meneruskan tugas ${i.getStringExtra("nama")} ke kelas ${button_detail_kelas.selectedItem}?", "Teruskan Tugas"){
                    positiveButton("YA") {
                        teruskanTugas(button_detail_kelas.selectedItem.toString())
                        alert("Jangan operasikan smartphone sampai proses pembuatan tugas selesai", "Mohon Tunggu"){ negativeButton("OK") {} }.show()
                    }
                    negativeButton("KEMBALI") {}
                }.show()
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(bottomSheet_detail)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                when (slideOffset) {
                    0f -> {
                        setViewEnabled()
                    }
                    1f -> {
                        setViewDisabled()
                    }
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        setViewEnabled()
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        setViewDisabled()
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })

        button_detail_kembali.setOnClickListener {
            onBackPressed()
        }
        
        button_detail_ubah.onClick {
            when(i.getStringExtra("intPic")) {
                "3" -> {
                    startActivity<BuatActivity>(
                            "state" to "EDIT",
                            "nama" to i.getStringExtra("nama"), "deskripsi" to i.getStringExtra("deskripsi"), "jenis" to i.getStringExtra("jenis"),
                            "kelas" to i.getStringExtra("kelas"), "guru" to i.getStringExtra("guru"), "mapel" to i.getStringExtra("mapel"),
                            "tanggalMulai" to i.getStringExtra("tanggalMulai"), "tanggalSelesai" to i.getStringExtra("tanggalSelesai"), "dateCreated" to i.getStringExtra("dateCreated"),
                            "download1" to i.getStringExtra("download1"), "download2" to i.getStringExtra("download2"), "download3" to i.getStringExtra("download3"),
                            "id" to i.getStringExtra("id"), "intPic" to "3", "dateCreated" to i.getStringExtra("dateCreated"), "namaUser" to i.getStringExtra("namaUser"),
                            "access" to i.getStringExtra("access"), "nis" to i.getStringExtra("nis"),
                            "kelasUser" to i.getStringExtra("kelasUser"), "email" to i.getStringExtra("email")
                    )
                }
                "2" -> {
                    startActivity<BuatActivity>(
                            "state" to "EDIT",
                            "nama" to i.getStringExtra("nama"), "deskripsi" to i.getStringExtra("deskripsi"), "jenis" to i.getStringExtra("jenis"),
                            "kelas" to i.getStringExtra("kelas"), "guru" to i.getStringExtra("guru"), "mapel" to i.getStringExtra("mapel"),
                            "tanggalMulai" to i.getStringExtra("tanggalMulai"), "tanggalSelesai" to i.getStringExtra("tanggalSelesai"), "dateCreated" to i.getStringExtra("dateCreated"),
                            "download1" to i.getStringExtra("download1"), "download2" to i.getStringExtra("download2"),
                            "id" to i.getStringExtra("id"), "intPic" to "2", "dateCreated" to i.getStringExtra("dateCreated"), "namaUser" to i.getStringExtra("namaUser"),
                            "access" to i.getStringExtra("access"), "nis" to i.getStringExtra("nis"),
                            "kelasUser" to i.getStringExtra("kelasUser"), "email" to i.getStringExtra("email")
                    )
                }
                "1" -> {
                    startActivity<BuatActivity>(
                            "state" to "EDIT",
                            "nama" to i.getStringExtra("nama"), "deskripsi" to i.getStringExtra("deskripsi"), "jenis" to i.getStringExtra("jenis"),
                            "kelas" to i.getStringExtra("kelas"), "guru" to i.getStringExtra("guru"), "mapel" to i.getStringExtra("mapel"),
                            "tanggalMulai" to i.getStringExtra("tanggalMulai"), "tanggalSelesai" to i.getStringExtra("tanggalSelesai"), "dateCreated" to i.getStringExtra("dateCreated"),
                            "download1" to i.getStringExtra("download1"),
                            "id" to i.getStringExtra("id"), "intPic" to "1", "dateCreated" to i.getStringExtra("dateCreated"), "namaUser" to i.getStringExtra("namaUser"),
                            "access" to i.getStringExtra("access"), "nis" to i.getStringExtra("nis"),
                            "kelasUser" to i.getStringExtra("kelasUser"), "email" to i.getStringExtra("email")
                    )
                }
                "0" -> {
                    startActivity<BuatActivity>(
                            "state" to "EDIT",
                            "nama" to i.getStringExtra("nama"), "deskripsi" to i.getStringExtra("deskripsi"), "jenis" to i.getStringExtra("jenis"),
                            "kelas" to i.getStringExtra("kelas"), "guru" to i.getStringExtra("guru"), "mapel" to i.getStringExtra("mapel"),
                            "tanggalMulai" to i.getStringExtra("tanggalMulai"), "tanggalSelesai" to i.getStringExtra("tanggalSelesai"), "dateCreated" to i.getStringExtra("dateCreated"),
                            "id" to i.getStringExtra("idKelas"), "intPic" to "0", "dateCreated" to i.getStringExtra("dateCreated"), "namaUser" to i.getStringExtra("namaUser"),
                            "access" to i.getStringExtra("access"), "nis" to i.getStringExtra("nis"),
                            "kelasUser" to i.getStringExtra("kelasUser"), "email" to i.getStringExtra("email")
                    )
                }
            }
        }

        if(access.equals("4") || access.equals("5") || access.equals("6")) {
            if(i.getStringExtra("lastUpdatedBy").equals(i.getStringExtra("namaUser"))) {
                textView_detail_lastupdated.text = "Terakhir update pada ${i.getStringExtra("dateCreated")}\noleh anda"
            } else {
                textView_detail_lastupdated.text = "Terakhir update pada ${i.getStringExtra("dateCreated")}\noleh ${i.getStringExtra("lastUpdatedBy")}"
            }
        } else {
            if(i.getStringExtra("lastUpdatedBy").equals(i.getStringExtra("namaUser"))) {
                textView_detail_lastupdated.text = "Terakhir update pada ${i.getStringExtra("dateCreated")}\noleh anda"
            } else {
                textView_detail_lastupdated.text = "Terakhir update pada ${i.getStringExtra("dateCreated")}"
            }
        }

        setViewEnabled()
        loadInfo()
    }

    private fun loadInfo() {

        textView_detail_kelasE.text = i.getStringExtra("kelas")
        textView_detail_guruE.text = i.getStringExtra("guru")

        textView_detail_mapelE.text = i.getStringExtra("mapel")
        textView_detail_jenisE.text = i.getStringExtra("jenis")

        textView_detail_namaE.text = i.getStringExtra("nama")
        textView_detail_deskripsiE.text = i.getStringExtra("deskripsi")

        textView_detail_tanggalMulai.text = DateParser.getLongDate(i.getStringExtra("tanggalMulai"))
        textView_detail_tanggalSelesai.text = DateParser.getLongDate(i.getStringExtra("tanggalSelesai"))

        val intPic = i.getStringExtra("intPic")

        loadImage(intPic)

    }

    @SuppressLint("SetTextI18n")
    private fun loadImage(intPic: String?) {
        when(intPic.toString()) {
            "3" -> {
                download1= i.getStringExtra("download1")
                download2= i.getStringExtra("download2")
                download3= i.getStringExtra("download3")

                textView_detail_foto.text = "FOTO TUGAS (3)"
                imageView_detail_foto1.visibility = View.VISIBLE
                imageView_detail_foto2.visibility = View.VISIBLE
                imageView_detail_foto3.visibility = View.VISIBLE
                textView_detail_fotonone.visibility = View.INVISIBLE

                storage.reference.child(download1).downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(imageView_detail_foto1)
                    Log.d("DOWNLOADED URI", it.toString())
                    uri1 = it
                    imageView_detail_foto1.onClick {
                        startActivity<DetailFotoActivity>(
                                "picDetail" to download1, "kelas" to i.getStringExtra("kelas")
                        )
                        finish()
                    }
                }
                storage.reference.child(download2).downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(imageView_detail_foto2)
                    uri2 = it
                    imageView_detail_foto2.onClick {
                        startActivity<DetailFotoActivity>(
                                "picDetail" to download2, "kelas" to i.getStringExtra("kelas")
                        )
                        finish()
                    }
                }
                storage.reference.child(download3).downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(imageView_detail_foto3)
                    uri3 = it
                    imageView_detail_foto3.onClick {
                        startActivity<DetailFotoActivity>(
                                "picDetail" to download3, "kelas" to i.getStringExtra("kelas")
                        )
                        finish()
                    }
                }

            }
            "2" -> {
                download1 = i.getStringExtra("download1")
                download2 = i.getStringExtra("download2")

                textView_detail_foto.text = "FOTO TUGAS (2)"
                imageView_detail_foto1.visibility = View.VISIBLE
                imageView_detail_foto2.visibility = View.VISIBLE
                imageView_detail_foto3.visibility = View.INVISIBLE
                textView_detail_fotonone.visibility = View.INVISIBLE

                storage.reference.child(download1).downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(imageView_detail_foto1)
                    uri1 = it
                    imageView_detail_foto1.onClick {
                        startActivity<DetailFotoActivity>(
                                "picDetail" to download1, "kelas" to i.getStringExtra("kelas")
                        )
                    }
                }
                storage.reference.child(download2).downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(imageView_detail_foto2)
                    uri2 = it
                    imageView_detail_foto2.onClick {
                        startActivity<DetailFotoActivity>(
                                "picDetail" to download2, "kelas" to i.getStringExtra("kelas")
                        )
                    }
                }
            }
            "1" -> {
                download1 = i.getStringExtra("download1")

                textView_detail_foto.text = "FOTO TUGAS (1)"
                imageView_detail_foto1.visibility = View.VISIBLE
                imageView_detail_foto2.visibility = View.INVISIBLE
                imageView_detail_foto3.visibility = View.INVISIBLE
                textView_detail_fotonone.visibility = View.INVISIBLE

                storage.reference.child(download1).downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(imageView_detail_foto1)
                    uri1 = it
                    imageView_detail_foto1.onClick {
                        startActivity<DetailFotoActivity>(
                                "picDetail" to download1, "kelas" to i.getStringExtra("kelas")
                        )
                    }
                }
            }
            "0" -> {
                textView_detail_foto.text = "FOTO TUGAS"
                imageView_detail_foto1.visibility = View.INVISIBLE
                imageView_detail_foto2.visibility = View.INVISIBLE
                imageView_detail_foto3.visibility = View.INVISIBLE
                textView_detail_fotonone.visibility = View.VISIBLE
            }

        }
    }

    private fun setViewEnabled() {
        button_detail_kelas.isClickable = true
        button_detail_kembali.isClickable = true
        button_detail_teruskan.isClickable = true
        button_detail_ubah.isClickable = true
    }

    private fun setViewDisabled() {
        button_detail_kelas.isClickable = false
        button_detail_kembali.isClickable = false
        button_detail_teruskan.isClickable = false
        button_detail_ubah.isClickable = false
    }

    @SuppressLint("SimpleDateFormat")
    private fun teruskanTugas(kelas: String?) {
        val dateFormat = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
        val dateNow = Date()
        val created = dateFormat.format(dateNow).toString()

        val teruskanMap = hashMapOf(
                "kelas" to kelas,
                "guru" to i.getStringExtra("guru"),
                "kode" to i.getStringExtra("guru").split(" ").get(0),
                "mapel" to i.getStringExtra("mapel"),
                "jenis" to i.getStringExtra("jenis"),
                "nama" to i.getStringExtra("nama"),
                "deskripsi" to i.getStringExtra("deskripsi"),
                "tanggalMulai" to i.getStringExtra("tanggalMulai"),
                "tanggalSelesai" to i.getStringExtra("tanggalSelesai"),
                "download_1" to download1,
                "download_2" to download2,
                "download_3" to download3,
                "date_created" to created,
                "lastUpdatedBy" to i.getStringExtra("namaUser")
        )

        firestore.collection("list_tugas").document("tugas").collection(kelas.toString()).add(teruskanMap as Map<String, Any>)
                .addOnSuccessListener {  }
        firestore.collection("list_guru").document("guru").collection(i.getStringExtra("guru")).add(teruskanMap as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d("FIRESTORE PROGRESS", "Task uploaded")
                    alert("Tugas berhasil diteruskan", "Berhasil"){
                        positiveButton("KEMBALI") {
                            onBackPressed() }
                    }.show()
                }

    }

    private fun slideBottomSheet() {
        if(bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            setViewDisabled()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            setViewEnabled()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

}
