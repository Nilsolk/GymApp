package ru.nilsolk.gymapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.nilsolk.gymapp.databinding.ItemStatisticBinding
import ru.nilsolk.gymapp.model.ToDoModel
import ru.nilsolk.gymapp.utils.TextTranslator
import ru.nilsolk.gymapp.utils.TranslationCallback
import ru.nilsolk.gymapp.utils.TranslationConstants

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
        val textTranslator = TextTranslator()

        val translatedMuscle =
            TranslationConstants.mapEnglishToRussianMuscleGroups[statistic[position].muscleGroup]
                ?: statistic[position].muscleGroup

        muscleGroup.text = translatedMuscle
        statisticDate.text = statistic[position].selectedDate
        textTranslator.translate(TranslationConstants.SOURCE, TranslationConstants.TARGET,
            statistic[position].todoText.toString(), object : TranslationCallback {
                override fun onTranslationComplete(translatedText: String) {
                    statisticData.text = translatedText
                }

            })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newStatistic: List<ToDoModel>) {
        statistic = newStatistic as ArrayList<ToDoModel>
        notifyDataSetChanged()
    }

}