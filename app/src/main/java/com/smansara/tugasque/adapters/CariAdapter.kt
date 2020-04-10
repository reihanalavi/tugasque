package com.smansara.tugasque.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smansara.tugasque.R
import com.smansara.tugasque.models.CariItems
import kotlinx.android.synthetic.main.items_cari.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class CariAdapter(val context: Context, private val listener: (CariItems) -> Unit): RecyclerView.Adapter<CariAdapter.ViewHolder>(){

    var listCari: MutableList<CariItems> = mutableListOf()

    class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){
        @SuppressLint("SimpleDateFormat")
        fun bindData(data: CariItems, listener: (CariItems) -> Unit) {
            itemView.items_cari_field.text = data.field
            when(data.field) {
                "nama" -> { itemView.items_cari_nama.text = data.nama }
                "kelas" -> { itemView.items_cari_nama.text = data.kelas }
                "access" -> { itemView.items_cari_nama.text = data.access }
                "email" -> { itemView.items_cari_nama.text = data.email }
                "nis" -> { itemView.items_cari_nama.text = data.nis }
            }

            itemView.onClick {
                listener(data)
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.items_cari, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listCari.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindData(listCari[p1], listener)
    }
}