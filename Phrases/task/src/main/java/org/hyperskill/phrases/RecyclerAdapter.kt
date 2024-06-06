package org.hyperskill.phrases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(
    private val dataList: MutableList<DataClass>,
    private val onDeleteClickListener: OnDeleteClickListener,
) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
        holder.deleteTextView.setOnClickListener {
            onDeleteClickListener.onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val phraseTextView: TextView = itemView.findViewById(R.id.phraseTextView)
        val deleteTextView: TextView = itemView.findViewById(R.id.deleteTextView)

        fun bind(item: DataClass) {
            phraseTextView.text = item.phrase
        }
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }
}