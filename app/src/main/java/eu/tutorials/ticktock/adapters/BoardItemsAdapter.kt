package eu.tutorials.ticktock.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.models.Board

open class BoardItemsAdapter(private val context: Context, private var list: ArrayList<Board>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return myViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board, parent , false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        val model = list[pos]
        if (holder is myViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.findViewById<CircleImageView>(R.id.iv_board_image))

            holder.itemView.findViewById<TextView>(R.id.tv_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_created_by).text = "Created by: ${model.createdBy}"

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(pos, model)
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(pos: Int, model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    private class myViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }


}