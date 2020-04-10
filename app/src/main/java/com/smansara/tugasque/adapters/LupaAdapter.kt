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
import com.smansara.tugasque.models.LupaItems
import com.smansara.tugasque.utils.DateParser
import kotlinx.android.synthetic.main.items_lupa.view.*
import kotlinx.android.synthetic.main.items_tugas.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LupaAdapter(val context: Context, private val listener: (LupaItems) -> Unit): RecyclerView.Adapter<LupaAdapter.ViewHolder>(){

    var listLupa: MutableList<LupaItems> = mutableListOf()

    class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){
        @SuppressLint("SimpleDateFormat")
        fun bindData(data: LupaItems, listener: (LupaItems) -> Unit) {
            itemView.textView_items_namalupa.text = data.nama
            itemView.textView_items_nomorlupa.text = data.nomor
            itemView.textView_items_createdlupa.text = data.created

            val updated = data.created
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

                itemView.textView_items_agolupa.text = dateAfter
            }

            if(data.email?.isEmpty()!! || data.email.equals("")) {
                //Belum Diganti
                itemView.circle_items_daftarlupa.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorRed))
            } else {
                //Sudah Diganti
                itemView.circle_items_daftarlupa.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorGreen))
            }

            itemView.onClick {
                listener(data)
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.items_lupa, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listLupa.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindData(listLupa[p1], listener)
    }
}