package ru.nilsolk.gymapp.ui.calendar_todo

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.repository.model.CalendarDateModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarAdapter(
    private val recyclerView: RecyclerView,
    private val listener: (calendarDateModel: CalendarDateModel, position: Int) -> Unit
) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var list = ArrayList<CalendarDateModel>()
    var adapterPosition = -1
    var isFirstTime = true
    var currentYear: String =
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time)


    interface onItemClickListener {
        fun onItemClick(text: String, day: String)
    }

    private var mListener: onItemClickListener? = null

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.date_layout, parent, false)
        return CalendarViewHolder(view)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val itemList = list[position]
        val customRedColor = "#FF0010"
        val parsedColor = Color.parseColor(customRedColor)
        holder.calendarDay.text = itemList.calendarDay
        holder.calendarDate.text = itemList.calendarDate

        holder.itemView.setOnClickListener {
            adapterPosition = position
            notifyItemRangeChanged(0, list.size)

            val text = itemList.calendarYear
            val day = itemList.calendarDay
            isFirstTime = false
            mListener?.onItemClick(text, day)
        }


        if (isFirstTime && currentYear == itemList.calendarYear) {
            holder.calendarDay.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
            holder.calendarDate.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
            holder.linear.background = holder.itemView.context.getDrawable(R.drawable.current_day)

        } else {
            if (position == adapterPosition) {
                holder.calendarDay.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
                holder.calendarDate.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
                holder.linear.background =
                    holder.itemView.context.getDrawable(R.drawable.rectangle_fill)
            } else {
                holder.calendarDay.setTextColor(parsedColor)
                holder.calendarDate.setTextColor(parsedColor)
                holder.linear.background =
                    holder.itemView.context.getDrawable(R.drawable.rectangle_outline)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val calendarDay: TextView = itemView.findViewById(R.id.tv_calendar_day)
        val calendarDate: TextView = itemView.findViewById(R.id.tv_calendar_date)
        val linear: LinearLayout = itemView.findViewById(R.id.linear_calendar)
    }

    fun scrollToPosition() {
        val today = CalendarDateModel(Date())
        val index = list.indexOfFirst { it.calendarDate == today.calendarDate }
        recyclerView.scrollToPosition(index)
    }

    fun setData(calendarList: ArrayList<CalendarDateModel>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }
}