package com.smansara.tugasque.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smansara.tugasque.R
import com.smansara.tugasque.models.TugasItems
import com.smansara.tugasque.utils.DateParser
import kotlinx.android.synthetic.main.items_tugas.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TugasAdapter(val context: Context, private val listener: (TugasItems) -> Unit): RecyclerView.Adapter<TugasAdapter.ViewHolder>() {

    var listTugas: MutableList<TugasItems> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.items_tugas, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTugas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listTugas[position], listener)
    }

    class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bindData(data: TugasItems, listener: (TugasItems) -> Unit) {
            if(data.mine.equals("YES")) {
                itemView.circle_items_jenis.visibility = View.VISIBLE
                itemView.circle_items_jenis.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorBlue))
            } else if(data.mine.equals("UNYES")) {
                itemView.circle_items_jenis.visibility = View.INVISIBLE
            } else {
                if(data.jenis.equals("Tugas Rumah")) {
                    itemView.circle_items_jenis.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorBlue))
                }
                if(data.jenis.equals("Tugas Sekolah")) {
                    itemView.circle_items_jenis.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorRed))
                }
                if(data.jenis.equals("Tugas Proyek")) {
                    itemView.circle_items_jenis.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorYellow))
                }
                if(data.jenis.equals("Tugas Praktek")) {
                    itemView.circle_items_jenis.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorGreen))
                }
            }
            if(data.state.equals("GURU")) {
                itemView.textView_items_mapel.text = data.kelas
            } else {
                itemView.textView_items_mapel.text = data.mapel
            }
            itemView.textView_items_nama.text = data.nama
            if(DateParser.getLongDate(data.tanggalSelesai.toString()).equals("yang lalu")) {
                itemView.textView_items_deadline.text = "Baru saja"
            } else {
                itemView.textView_items_deadline.text = DateParser.getLongDate(data.tanggalSelesai.toString())
            }

            //Load Time Ago
            val updated = data.dateCreated
            if (!TextUtils.isEmpty(updated)) {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

                var convertedDate = Date()

                try {
                    convertedDate = sdf.parse(updated)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                val p = PrettyTime()
                val dateAfter = p.format(convertedDate)

                itemView.textView_items_ago.text = dateAfter
            }

            itemView.onClick {
                listener(data)
            }

        }
    }
}