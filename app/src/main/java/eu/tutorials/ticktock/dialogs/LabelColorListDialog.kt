package eu.tutorials.ticktock.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.adapters.LabelColorListItemsAdapter

abstract class LabelColorListDialog(context: Context, private var list: ArrayList<String>, private val title: String = "", private var mSelectedColor: String = "", ): Dialog(context) {
    // class vars
    private var adapter: LabelColorListItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        val rvList: RecyclerView? = view.findViewById<RecyclerView>(R.id.rvList)

        view.findViewById<TextView>(R.id.tv_title).text = title
        rvList?.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        rvList?.adapter = adapter

        adapter!!.onItemClickListener =
            object: LabelColorListItemsAdapter.OnItemClickListener {
                override fun onClick(pos: Int, color: String) {
                    dismiss()
                    onItemSelected(color)
                }
        }
    }

    protected abstract fun onItemSelected(color: String)
}