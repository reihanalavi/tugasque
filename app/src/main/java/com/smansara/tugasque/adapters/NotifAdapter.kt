package com.smansara.tugasque.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smansara.tugasque.R
import com.smansara.tugasque.models.NotifItems
import kotlinx.android.synthetic.main.items_notif.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NotifAdapter(val context: Context, private val listener: (NotifItems) -> Unit): RecyclerView.Adapter<NotifAdapter.ViewHolder>(){

    var listNotif: MutableList<NotifItems> = mutableListOf()

    class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){
        @SuppressLint("SimpleDateFormat")
        fun bindData(data: NotifItems, listener: (NotifItems) -> Unit) {
            itemView.items_notif_pesan.text = data.pesan
            itemView.items_notif_topics.text = data.untuk

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

                itemView.items_notif_ago.text = dateAfter
            }

            itemView.onClick {
                listener(data)
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.items_notif, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listNotif.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindData(listNotif[p1], listener)
    }
}