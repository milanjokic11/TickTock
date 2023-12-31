package eu.tutorials.ticktock.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.activities.TaskListActivity
import eu.tutorials.ticktock.models.Task
import java.util.Collections

open class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // class vars
    private var mPosDraggedFrom = -1
    private var mPosDraggedTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view
            = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams
            = LinearLayout.LayoutParams((parent.width * .7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp().toPx()), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("CutPasteId")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") pos: Int) {
        val model = list[pos]
        if (holder is MyViewHolder) {
            if (pos == list.size - 1) {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.GONE
            } else {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.VISIBLE
            }
            holder.itemView.findViewById<TextView>(R.id.tv_task_list_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.VISIBLE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.GONE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener {
                val listName = holder.itemView.findViewById<EditText>(R.id.et_task_list_name).text.toString()
                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                } else {
                    Toast.makeText(context, "Please enter list name...", Toast.LENGTH_SHORT).show()
                }
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_edit_list_name).setOnClickListener {
                holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).setText(model.title)
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility = View.VISIBLE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view).setOnClickListener {
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility = View.GONE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name).setOnClickListener {
                val listName = holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).text.toString()
                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.updateTaskList(pos, listName, model)
                    }
                } else {
                    Toast.makeText(context, "Please enter list name...", Toast.LENGTH_SHORT).show()
                }
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list).setOnClickListener {
                alertDialogForDeleteList(pos, model.title)
            }
            holder.itemView.findViewById<TextView>(R.id.tv_add_card).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.VISIBLE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_card_name).setOnClickListener{
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.GONE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_card_name).setOnClickListener {
                val cardName = holder.itemView.findViewById<EditText>(R.id.et_card_name).text.toString()
                if (cardName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.addCardToTask(pos, cardName)
                    }
                } else {
                    Toast.makeText(context, "Please enter card name...", Toast.LENGTH_SHORT).show()
                }
            }
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).layoutManager = LinearLayoutManager(context)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).setHasFixedSize(true)
            val adapter = CardListItemsAdapter(context, model.cards)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).adapter = adapter

            adapter.setOnClickListener(object: CardListItemsAdapter.OnClickListener {
                override fun onClick(cardPos: Int) {
                    if (context is TaskListActivity) {
                        context.cardDetails(pos, cardPos)
                    }
                }
            })

            val dividerItemDecor = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).addItemDecoration(dividerItemDecor)
            val helper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
            ){
                override fun onMove(
                    recyclerView: RecyclerView,
                    dragged: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedPos = dragged.adapterPosition
                    val targetPos = target.adapterPosition
                    if (mPosDraggedFrom == -1) {
                        mPosDraggedFrom = draggedPos
                    }
                    mPosDraggedTo = targetPos
                    Collections.swap(list[pos].cards, draggedPos, targetPos)
                    adapter.notifyItemMoved(draggedPos, targetPos)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    TODO("Not yet implemented")
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    if (mPosDraggedFrom != -1 && mPosDraggedTo != -1 && mPosDraggedFrom != mPosDraggedTo) {
                        (context as TaskListActivity).updateCardsInTaskList(pos, list[pos].cards)
                    }
                    mPosDraggedTo = -1
                    mPosDraggedFrom = -1
                }
            })
            helper.attachToRecyclerView(holder.itemView.findViewById(R.id.rv_card_list))
        }
    }

    private fun alertDialogForDeleteList(pos: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss()
            if (context is TaskListActivity) {
                context.deleteTaskList(pos)
            }
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}