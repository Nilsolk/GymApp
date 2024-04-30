package ru.nilsolk.gymapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.nilsolk.gymapp.databinding.ItemStatisticBinding
import ru.nilsolk.gymapp.model.ToDoModel

class StatisticAdapter(private var statistic: ArrayList<ToDoModel>) :
    RecyclerView.Adapter<StatisticAdapter.ItemHolder>() {
    class ItemHolder(val binding: ItemStatisticBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding =
            ItemStatisticBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun getItemCount(): Int = statistic.size
    override fun onBindViewHolder(holder: ItemHolder, position: Int) = with(holder.binding) {
        muscleGroup.text = statistic[position].muscleGroup
        statisticDate.text = statistic[position].selectedDate
        statisticData.text = statistic[position].todoText
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newStatistic: List<ToDoModel>) {
        statistic = newStatistic as ArrayList<ToDoModel>
        notifyDataSetChanged()
    }

}