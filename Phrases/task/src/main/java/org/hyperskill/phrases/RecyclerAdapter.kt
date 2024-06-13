package org.hyperskill.phrases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(
    private var dataList: List<Phrase>,
    private val onDeleteClickListener: OnDeleteClickListener,
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
        holder.deleteTextView.setOnClickListener {
            onDeleteClickListener.onDeleteClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newDataList: List<Phrase>) {
        dataList = newDataList
        notifyDataSetChanged()
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val phraseTextView: TextView = itemView.findViewById(R.id.phraseTextView)
        val deleteTextView: TextView = itemView.findViewById(R.id.deleteTextView)

        fun bind(item: Phrase) {
            phraseTextView.text = item.phrase
        }
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(phrase: Phrase)
    }
}