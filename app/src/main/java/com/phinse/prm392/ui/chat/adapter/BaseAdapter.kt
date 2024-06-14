package com.phinse.prm392.ui.chat.adapter

import android.content.Context
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T : Any, VH : BaseAdapter.BaseItemViewHolder<T, *>> :
    RecyclerView.Adapter<VH>() {

    private var onItemClickListener: ((T) -> Unit)? = null

    private var setItemOrderBy: ((List<T>) -> Unit)? = null

    protected abstract fun differCallBack(): DiffUtil.ItemCallback<T>

    protected val differ = AsyncListDiffer(this, differCallBack())

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
        onItemClickListener?.let { listener ->
            holder.itemView.setOnClickListener { _ ->
                listener(item)
            }
        }
    }

    open fun submitList(list: MutableList<T>) {
        setItemOrderBy?.let {
            it(list.toList())
        }
        differ.submitList(list.toList())
        notifyDataSetChanged()
    }

    fun setItemOrderBy(listener: (List<T>) -> Unit) {
        setItemOrderBy = listener
    }

    fun setItemOnclickListener(listener: (T) -> Unit) {
        onItemClickListener = listener
    }

    abstract class BaseItemViewHolder<T : Any, VB : ViewBinding>(val binding: VB) :
        RecyclerView.ViewHolder(binding.root) {

        private val context: Context = binding.root.context
        abstract fun bind(item: T)

    }

}