package eu.tutorials.ticktock.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.models.Card

open class CardListItemsAdapter (private val context: Context, private var list: ArrayList<Card>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // class vars
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        val model = list[pos]
        if (holder is MyViewHolder) {
            if (model.labelColor.isNotEmpty()) {
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.VISIBLE
                holder.itemView.findViewById<View>(R.id.view_label_color).setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.GONE
            }

            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name

            holder.itemView.setOnClickListener{
                if (onClickListener != null) {
                    onClickListener!!.onClick(pos)
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(pos: Int)
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}