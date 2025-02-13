package com.thangoghd.cakeotv.ui.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thangoghd.cakeotv.data.model.PlayUrl

class QualityAdapter(
    private var items: List<PlayUrl>,
    private var selectedUrl: PlayUrl?,
    private val onQualitySelected: (PlayUrl) -> Unit
) : RecyclerView.Adapter<QualityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<PlayUrl>, newSelectedUrl: PlayUrl?) {
        items = newItems
        selectedUrl = newSelectedUrl
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onQualitySelected(items[position])
                }
            }
        }

        fun bind(item: PlayUrl) {
            textView.text = item.name
            textView.isSelected = item == selectedUrl
            
            // Set focus handling for Android TV
            itemView.isFocusable = true
            itemView.isFocusableInTouchMode = true
        }
    }
}
