package eu.tutorials.ticktock.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.activities.TaskListActivity
import eu.tutorials.ticktock.models.Card
import eu.tutorials.ticktock.models.SelectedMembers

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

            if ((context as TaskListActivity).mAssignedMemberDetailList.size > 0) {
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
                for (i in context.mAssignedMemberDetailList.indices) {
                    for (j in model.assignedTo) {
                        if (context.mAssignedMemberDetailList[i].id == j) {
                            val selectedMembers = SelectedMembers(
                                context.mAssignedMemberDetailList[i].id,
                                context.mAssignedMemberDetailList[i].image
                            )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }
                if (selectedMembersList.size > 0) {
                    if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility = View.GONE
                    } else {
                        val recyclerView = holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list)
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.layoutManager = GridLayoutManager(context, 4)
                        val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)
                        recyclerView.adapter = adapter
                        adapter.setOnClickListener(object:
                            CardMemberListItemsAdapter.OnClickListener {
                            override fun onClick() {
                                if (onClickListener != null) {
                                    onClickListener!!.onClick(pos)
                                }
                            }
                            })
                    }
                } else {
                    holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility = View.GONE
                }
            }

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