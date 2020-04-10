package com.smansara.tugasque.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.smansara.tugasque.R
import com.smansara.tugasque.utils.DateParser
import com.smansara.tugasque.utils.FileUtil
import com.smansara.tugasque.utils.ListSelectors.listGuru
import com.smansara.tugasque.utils.ListSelectors.listJenis
import com.smansara.tugasque.utils.ListSelectors.listKelas
import com.smansara.tugasque.utils.ListSelectors.listMapel
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_buat.*
import kotlinx.android.synthetic.main.bottom_sheet_buat.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.imageURI
import org.jetbrains.anko.selector
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class BuatActivity : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var oriMulai: String? = ""
    private var oriSelesai: String? = ""

    private var uri1: Uri? = null
    private var uri2: Uri? = null
    private var uri3: Uri? = null

    private var download1: String? = ""
    private var download2: String? = ""
    private var download3: String? = ""

    private lateinit var i: Intent
    private var idGuru = ""
    private var idKelas = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        i = intent

        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {

        val calMulai = Calendar.getInstance()
        val yearMulai = calMulai.get(Calendar.YEAR)
        val monthMulai = calMulai.get(Calendar.MONTH)
        val dayMulai = calMulai.get(Calendar.DAY_OF_MONTH)

        val calSelesai = Calendar.getInstance()
        val yearSelesai = calSelesai.get(Calendar.YEAR)
        val monthSelesai = calSelesai.get(Calendar.MONTH)
        val daySelesai = calSelesai.get(Calendar.DAY_OF_MONTH)

        button_buat_kelas.setOnClickListener {
            selector("Kelas", listKelas) { dialogInterface, i ->
                button_buat_kelas.text = listKelas[i]
            }
        }

        button_buat_guru.setOnClickListener {
            selector("Guru Pengampu", listGuru) {dialogInterface, i ->
                button_buat_guru.text = listGuru[i]
            }
        }

        button_buat_mapel.setOnClickListener {
            selector("Mata Pelajaran", listMapel) {dialogInterface, i ->
                button_buat_mapel.text = listMapel[i]
            }
        }

        button_buat_jenis.setOnClickListener {
            selector("Jenis Tugas", listJenis) {dialogInterface, i ->
                button_buat_jenis.text = listJenis[i]
            }
        }

        button_buat_kembali.setOnClickListener {
            alert("Yakin ingin kembali dan membatalkan isian tugas?", "Kembali"){
                positiveButton("YA") {
                    onBackPressed()
                }
                negativeButton("TIDAK") {  }
            }.show()
        }

        button_buat_tanggalMulai.setOnClickListener {

            val dateMulai = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                textView_buat_tanggalMulai.text = DateParser.getLongDate("$year-${monthOfYear+1}-$dayOfMonth")
                oriMulai = "$year-${monthOfYear+1}-$dayOfMonth"
            }, yearMulai, monthMulai, dayMulai)
            dateMulai.show()

        }

        button_buat_tanggalSelesai.setOnClickListener {

            val dateSelesai = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                textView_buat_tanggalSelesai.text = DateParser.getLongDate("$year-${monthOfYear+1}-$dayOfMonth")
                oriSelesai = "$year-${monthOfYear+1}-$dayOfMonth"
            }, yearSelesai, monthSelesai, daySelesai)
            dateSelesai.show()

        }

        button_buat.setOnClickListener {
            if(button_buat_kelas.text.toString().equals(i.getStringExtra("kelas")) && button_buat_guru.text.toString().equals(i.getStringExtra("guru"))
                    && button_buat_mapel.text.toString().equals(i.getStringExtra("mapel")) && button_buat_jenis.toString().equals(i.getStringExtra("jenis"))
                    && editText_buat_nama.text.toString().equals(i.getStringExtra("nama")) && editText_buat_deskripsi.toString().equals(i.getStringExtra("deskripsi"))
                    && oriMulai.toString().equals(i.getStringExtra("tanggalMulai")) && oriSelesai.toString().equals(i.getStringExtra("tanggalSelesai"))
                    && textView_buat_foto1.text.toString().equals(i.getStringExtra("download1")) && textView_buat_foto2.text.toString().equals(i.getStringExtra("download2"))
                    && textView_buat_foto3.text.toString().equals(i.getStringExtra("download3"))) {
                //GA DIUBAH.
                alert("Tidak ada isian yang berubah", "Gagal"){ positiveButton("KEMBALI") { } }
            } else {
                uploadTugas()
            }
        }

        constraintLayout_bottomSheet_header.setOnClickListener {
            slideBottomSheet()
        }

        bottomSheetBehavior = BottomSheetBehavior.from<ConstraintLayout>(bottomSheet_buat)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                when(slideOffset) {
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

        button_buat_unggah.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                selectImage()
            }
        }

        if(i.getStringExtra("state").equals("EDIT")) {
            Log.d("GURU", i.getStringExtra("guru"))
            Log.d("KELAS", i.getStringExtra("kelas"))
            Log.d("DATE CREATED", i.getStringExtra("dateCreated"))
        }
        if(i.getStringExtra("state").equals("EDIT")) {
            firestore.collection("list_guru").document("guru").collection(i.getStringExtra("guru")).whereEqualTo("date_created", i.getStringExtra("dateCreated"))
                    .get().addOnSuccessListener { document ->
                        Log.d("DOCUMENTS GURU", document.documents.toString())
                        idGuru = document.documents[0].id
                        Log.d("ID GURU", idGuru)
                    }
            firestore.collection("list_tugas").document("tugas").collection(i.getStringExtra("kelas")).whereEqualTo("date_created", i.getStringExtra("dateCreated"))
                    .get().addOnSuccessListener { document ->
                        Log.d("DOCUMENTS TUGAS", document.documents.toString())
                        idKelas = document.documents[0].id
                        Log.d("ID KELAS", idKelas)
                    }
            textView_buat_header.text = "UBAH TUGAS"

            when(i.getStringExtra("intPic")) {
                "3" -> {
                    imageView_buat_foto1.visibility = View.VISIBLE
                    imageView_buat_foto2.visibility = View.VISIBLE
                    imageView_buat_foto3.visibility = View.VISIBLE

                    textView_buat_foto1.visibility = View.VISIBLE
                    textView_buat_foto2.visibility = View.VISIBLE
                    textView_buat_foto3.visibility = View.VISIBLE

                    download1 = i.getStringExtra("download1")
                    download2 = i.getStringExtra("download2")
                    download3 = i.getStringExtra("download3")

                    textView_buat_foto1.text = download1
                    textView_buat_foto2.text = download2
                    textView_buat_foto3.text = download3
                }
                "2" -> {
                    imageView_buat_foto1.visibility = View.VISIBLE
                    imageView_buat_foto2.visibility = View.VISIBLE
                    imageView_buat_foto3.visibility = View.GONE

                    textView_buat_foto1.visibility = View.VISIBLE
                    textView_buat_foto2.visibility = View.VISIBLE
                    textView_buat_foto3.visibility = View.GONE

                    download1 = i.getStringExtra("download1")
                    download2 = i.getStringExtra("download2")

                    textView_buat_foto1.text = download1
                    textView_buat_foto2.text = download2
                }
                "1" -> {
                    imageView_buat_foto1.visibility = View.VISIBLE
                    imageView_buat_foto2.visibility = View.GONE
                    imageView_buat_foto3.visibility = View.GONE

                    textView_buat_foto1.visibility = View.VISIBLE
                    textView_buat_foto2.visibility = View.GONE
                    textView_buat_foto3.visibility = View.GONE

                    download1 = i.getStringExtra("download1")

                    textView_buat_foto1.text = download1
                }
                "0" -> {
                    imageView_buat_foto1.visibility = View.GONE
                    imageView_buat_foto2.visibility = View.GONE
                    imageView_buat_foto3.visibility = View.GONE

                    textView_buat_foto1.visibility = View.GONE
                    textView_buat_foto2.visibility = View.GONE
                    textView_buat_foto3.visibility = View.GONE
                }
            }

            button_buat_kelas.text = i.getStringExtra("kelas")
            button_buat_mapel.text = i.getStringExtra("mapel")
            button_buat_guru.text = i.getStringExtra("guru")
            button_buat_jenis.text = i.getStringExtra("jenis")
            button_buat.text = "UBAH TUGAS"
            editText_buat_nama.setText(i.getStringExtra("nama"))
            editText_buat_deskripsi.setText(i.getStringExtra("deskripsi"))
            textView_buat_tanggalMulai.text = DateParser.getLongDate(i.getStringExtra("tanggalMulai"))
            textView_buat_tanggalSelesai.text = DateParser.getLongDate(i.getStringExtra("tanggalSelesai"))
            oriMulai = i.getStringExtra("tanggalMulai")
            oriSelesai = i.getStringExtra("tanggalSelesai")
            Log.d("ORI MULAI", i.getStringExtra("tanggalMulai").toString())
            Log.d("ORI SELESAI", i.getStringExtra("tanggalSelesai").toString())
        } else {
            textView_buat_header.text = "BUAT TUGAS"
            imageView_buat_foto1.visibility = View.GONE
            imageView_buat_foto2.visibility = View.GONE
            imageView_buat_foto3.visibility = View.GONE

            textView_buat_foto1.visibility = View.GONE
            textView_buat_foto2.visibility = View.GONE
            textView_buat_foto3.visibility = View.GONE

            textView_buat_foto1.text = ""
            textView_buat_foto2.text = ""
            textView_buat_foto3.text = ""

            button_buat_kelas.text = "PILIH KELAS"
            button_buat_mapel.text = "MATA PELAJARAN"
            button_buat_guru.text = "PILIH GURU PENGAMPU"
            button_buat_jenis.text = "JENIS TUGAS"
            button_buat.text = "BUAT TUGAS"
            editText_buat_nama.setText("")
            editText_buat_deskripsi.setText("")
            textView_buat_tanggalMulai.text = "Tanggal Mulai"
            textView_buat_tanggalSelesai.text = "Tanggal Selesai"
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun uploadTugas() {

        val kelas = button_buat_kelas.text
        val guru = button_buat_guru.text
        val mapel = button_buat_mapel.text
        val jenis = button_buat_jenis.text
        val nama = editText_buat_nama.text
        val deskripsi = editText_buat_deskripsi.text


        if(kelas == "PILIH KELAS" || guru == "PILIH GURU PENGAMPU" || mapel == "MATA PELAJARAN" || jenis == "JENIS TUGAS" || nama?.isEmpty()!!  || deskripsi?.isEmpty()!!
                || oriMulai == "Tanggal Selesai" || oriSelesai == "Tanggal Selesai") {
            alert("Periksa data untuk menambah tugas", "Error"){
                negativeButton("KEMBALI") {

                }
            }.show()
        } else {
            if(i.getStringExtra("state").equals("EDIT")) {
                alert("Yakin mengubah tugas untuk kelas $kelas?", "Ubah Tugas"){
                    negativeButton("KEMBALI") {  }
                    positiveButton("YA") {

                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        setViewDisabled()
                        alert("Jangan operasikan smartphone sampai proses pembuatan tugas selesai", "Mohon Tunggu"){ negativeButton("OK") {} }.show()

                        //------------------UPLOAD TUGAS TO CLOUD STORAGE-----------------------
                        if(imageView_buat_foto1.visibility == View.VISIBLE) {
                            if(!textView_buat_foto1.text.equals(i.getStringExtra("download1"))) {
                                uri1?.let { it1 ->
                                    storage.reference.child(textView_buat_foto1.text.toString())
                                            .putFile(it1).addOnSuccessListener { }
                                }
                            }
                        }
                        if(imageView_buat_foto2.visibility == View.VISIBLE) {
                            if(!textView_buat_foto2.text.equals(i.getStringExtra("download2"))) {
                                uri2?.let { it2 ->
                                    storage.reference.child(textView_buat_foto2.text.toString())
                                            .putFile(it2).addOnSuccessListener { }
                                }
                            }
                        }
                        if(imageView_buat_foto3.visibility == View.VISIBLE) {
                            if(!textView_buat_foto3.text.equals(i.getStringExtra("download3"))) {
                                uri3?.let { it3 ->
                                    storage.reference.child(textView_buat_foto3.text.toString())
                                            .putFile(it3).addOnSuccessListener { }
                                }
                            }
                        }

                        val dateFormat = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
                        val dateNow = Date()
                        val created = dateFormat.format(dateNow).toString()

                        //------------------UPLOAD TUGAS TO CLOUD FIRESTORE-----------------------
                        val buatMap = hashMapOf(
                                "kelas" to "$kelas",
                                "guru" to "$guru",
                                "kode" to guru.split(" ").get(0),
                                "mapel" to "$mapel",
                                "jenis" to "$jenis",
                                "nama" to "$nama",
                                "deskripsi" to "$deskripsi",
                                "tanggalMulai" to "$oriMulai",
                                "tanggalSelesai" to "$oriSelesai",
                                "download_1" to "${textView_buat_foto1.text}",
                                "download_2" to "${textView_buat_foto2.text}",
                                "download_3" to "${textView_buat_foto3.text}",
                                "date_created" to created,
                                "lastUpdatedBy" to i.getStringExtra("namaUser")
                        )

                        firestore.collection("list_tugas").document("tugas").collection("$kelas").document(idKelas).set(buatMap as Map<String, Any>)
                                .addOnSuccessListener {  }
                        firestore.collection("list_tugas").document("tugas").collection(i.getStringExtra("kelas")).document(idKelas).set(buatMap as Map<String, Any>)
                                .addOnSuccessListener {  }
                        firestore.collection("list_guru").document("guru").collection(i.getStringExtra("guru")).document(idGuru).set(buatMap as Map<String, Any>)
                                .addOnSuccessListener {  }
                        firestore.collection("list_guru").document("guru").collection("$guru").document(idGuru).set(buatMap as Map<String, Any>)
                                .addOnSuccessListener {
                                    Log.d("FIRESTORE PROGRESS", "Task uploaded")
                                    alert("Tugas berhasil diubah", "Berhasil"){
                                        positiveButton("KEMBALI") {
                                            onBackPressed() }
                                    }.show()
                                }
                    }
                }.show()
            } else {
                alert("Yakin menambahkan tugas untuk kelas $kelas?", "Buat Tugas"){
                    negativeButton("KEMBALI") {  }
                    positiveButton("YA") {

                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        setViewDisabled()
                        alert("Jangan operasikan smartphone sampai proses pembuatan tugas selesai", "Mohon Tunggu"){ negativeButton("OK") {} }.show()

                        //------------------UPLOAD TUGAS TO CLOUD STORAGE-----------------------
                        if(imageView_buat_foto1.visibility == View.VISIBLE) {
                            uri1?.let { it1 ->
                                storage.reference.child(textView_buat_foto1.text.toString())
                                        .putFile(it1).addOnSuccessListener { }
                            }
                        }
                        if(imageView_buat_foto2.visibility == View.VISIBLE) {
                            uri2?.let { it2 ->
                                storage.reference.child(textView_buat_foto2.text.toString())
                                        .putFile(it2).addOnSuccessListener { }
                            }
                        }
                        if(imageView_buat_foto3.visibility == View.VISIBLE) {
                            uri3?.let { it3 ->
                                storage.reference.child(textView_buat_foto3.text.toString())
                                        .putFile(it3).addOnSuccessListener { }
                            }
                        }

                        val dateFormat = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
                        val dateNow = Date()
                        val created = dateFormat.format(dateNow).toString()

                        //------------------UPLOAD TUGAS TO CLOUD FIRESTORE-----------------------
                        val buatMap = hashMapOf(
                                "kelas" to "$kelas",
                                "guru" to "$guru",
                                "kode" to guru.split(" ").get(0),
                                "mapel" to "$mapel",
                                "jenis" to "$jenis",
                                "nama" to "$nama",
                                "deskripsi" to "$deskripsi",
                                "tanggalMulai" to "$oriMulai",
                                "tanggalSelesai" to "$oriSelesai",
                                "download_1" to "${textView_buat_foto1.text}",
                                "download_2" to "${textView_buat_foto2.text}",
                                "download_3" to "${textView_buat_foto3.text}",
                                "date_created" to created,
                                "lastUpdatedBy" to i.getStringExtra("nama")
                        )

                        firestore.collection("list_tugas").document("tugas").collection("$kelas").add(buatMap as Map<String, Any>)
                                .addOnSuccessListener {  }
                        firestore.collection("list_guru").document("guru").collection("$guru").add(buatMap as Map<String, Any>)
                                .addOnSuccessListener {
                                    Log.d("FIRESTORE PROGRESS", "Task uploaded")
                                    alert("Tugas berhasil dibuat", "Berhasil"){
                                        positiveButton("KEMBALI") {
                                            onBackPressed() }
                                    }.show()
                                }
                    }
                }.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Unggah Foto"), IMAGE_PICKED)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_PICKED && resultCode == Activity.RESULT_OK) {

            imageView_buat_foto1.visibility = View.GONE
            imageView_buat_foto2.visibility = View.GONE
            imageView_buat_foto3.visibility = View.GONE
            textView_buat_foto1.visibility = View.GONE
            textView_buat_foto2.visibility = View.GONE
            textView_buat_foto3.visibility = View.GONE

            textView_buat_foto1.text = ""
            textView_buat_foto2.text = ""
            textView_buat_foto3.text = ""

            if(data?.clipData != null) {

                when(data.clipData?.itemCount) {
                    2 -> {
                        imageView_buat_foto1.visibility = View.VISIBLE
                        imageView_buat_foto2.visibility = View.VISIBLE
                        textView_buat_foto1.visibility = View.VISIBLE
                        textView_buat_foto2.visibility = View.VISIBLE

                        //image 1
                        val uri1 = data.clipData?.getItemAt(0)?.uri
                        val actImage1 = FileUtil.from(this, uri1)
                        val image1 = Compressor(this).compressToFile(actImage1)

                        //image 2
                        val uri2 = data.clipData?.getItemAt(1)?.uri
                        val actImage2 = FileUtil.from(this, uri2)
                        val image2 = Compressor(this).compressToFile(actImage2)

                        Picasso.get().load(Uri.parse(image1.toString())).fit().into(imageView_buat_foto1)
                        Picasso.get().load(Uri.parse(image2.toString())).fit().into(imageView_buat_foto2)

                        getCompressedAndUri("1", uri1, image1, textView_buat_foto1, imageView_buat_foto1)
                        getCompressedAndUri("2", uri2, image2, textView_buat_foto2, imageView_buat_foto2)
                    }
                    3 -> {
                        imageView_buat_foto1.visibility = View.VISIBLE
                        imageView_buat_foto2.visibility = View.VISIBLE
                        imageView_buat_foto3.visibility = View.VISIBLE
                        textView_buat_foto1.visibility = View.VISIBLE
                        textView_buat_foto2.visibility = View.VISIBLE
                        textView_buat_foto3.visibility = View.VISIBLE

                        //image 1
                        val uri1 = data.clipData?.getItemAt(0)?.uri
                        val actImage1 = FileUtil.from(this, uri1)
                        val image1 = Compressor(this).compressToFile(actImage1)

                        //image 2
                        val uri2 = data.clipData?.getItemAt(1)?.uri
                        val actImage2 = FileUtil.from(this, uri2)
                        val image2 = Compressor(this).compressToFile(actImage2)

                        //image 3
                        val uri3 = data.clipData?.getItemAt(2)?.uri
                        val actImage3 = FileUtil.from(this, uri3)
                        val image3 = Compressor(this).compressToFile(actImage3)

                        Picasso.get().load(Uri.parse(image1.toString())).fit().into(imageView_buat_foto1)
                        Picasso.get().load(Uri.parse(image2.toString())).fit().into(imageView_buat_foto2)
                        Picasso.get().load(Uri.parse(image3.toString())).fit().into(imageView_buat_foto3)

                        getCompressedAndUri("1", uri1, image1, textView_buat_foto1, imageView_buat_foto1)
                        getCompressedAndUri("2", uri2, image2, textView_buat_foto2, imageView_buat_foto2)
                        getCompressedAndUri("3", uri3, image3, textView_buat_foto3, imageView_buat_foto3)

                    }
                }

            } else if(data?.data != null) {
                imageView_buat_foto1.visibility = View.VISIBLE
                imageView_buat_foto2.visibility = View.GONE
                imageView_buat_foto3.visibility = View.GONE
                textView_buat_foto1.visibility = View.VISIBLE
                textView_buat_foto2.visibility = View.GONE
                textView_buat_foto3.visibility = View.GONE

                //image 1
                val uri1 = data.data
                val actImage1 = FileUtil.from(this, uri1)
                val image1 = Compressor(this).compressToFile(actImage1)

                Picasso.get().load(Uri.parse(image1.toString())).fit().into(imageView_buat_foto1)

                getCompressedAndUri("1", uri1, image1, textView_buat_foto1, imageView_buat_foto1)

            }


        }

    }

    @SuppressLint("SetTextI18n")
    private fun getCompressedAndUri(id: String?, uri: Uri?, compressedFile: File?, textView: TextView?, imageView: ImageView) {

        textView?.text = "tugasque_${button_buat_kelas.text}_${System.currentTimeMillis()}.${uri?.let { getFileExtension(it)}}"
        imageView.imageURI = Uri.parse(uri.toString())

        val size = getReadableFileSize(compressedFile!!.length())
        val uriBig = compressedFile.toURI()
        val uriImage = Uri.fromFile(compressedFile)

        if(id.equals("1")) {
            uri1 = uriImage
        }
        if(id.equals("2")) {
            uri2 = uriImage
        }
        if(id.equals("3")) {
            uri3 = uriImage
        }

        Log.d("RESULTS", "Size : $size, URI : $uriBig, Uri : $uriImage")

    }

    private fun getFileExtension(uri: Uri): String? {
        val cr = this.contentResolver
        val mr = MimeTypeMap.getSingleton()
        return mr.getExtensionFromMimeType(cr.getType(uri))
    }

    private fun setViewEnabled() {
        button_buat_kelas.isClickable = true
        button_buat_guru.isClickable = true
        button_buat_mapel.isClickable = true
        button_buat_jenis.isClickable = true
        editText_buat_nama.isEnabled = true
        editText_buat_deskripsi.isEnabled = true
        button_buat_tanggalMulai.isClickable = true
        button_buat_tanggalSelesai.isClickable = true
        button_buat_kembali.isClickable = true
        button_buat.isClickable = true
    }

    private fun setViewDisabled() {
        button_buat_kelas.isClickable = false
        button_buat_guru.isClickable = false
        button_buat_mapel.isClickable = false
        button_buat_jenis.isClickable = false
        editText_buat_nama.isEnabled = false
        editText_buat_deskripsi.isEnabled = false
        button_buat_tanggalMulai.isClickable = false
        button_buat_tanggalSelesai.isClickable = false
        button_buat_kembali.isClickable = false
        button_buat.isClickable = false
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

    private fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(
                size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    companion object {
        private const val IMAGE_PICKED = 1
    }

}
