package ru.nilsolk.gymapp.ui.training_program

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.nilsolk.gymapp.databinding.ItemDailyStatisticBinding
import ru.nilsolk.gymapp.repository.db.DailyProgramStatistic
import ru.nilsolk.gymapp.repository.db.ExerciseDAO
import ru.nilsolk.gymapp.translation.TextTranslator
import ru.nilsolk.gymapp.utils.ExerciseMapper


class DailyStatisticAdapter(
    private val statistics: List<DailyProgramStatistic>,
    private val exerciseDAO: ExerciseDAO,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<DailyStatisticAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemDailyStatisticBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDailyStatisticBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val statistic = statistics[position]

        lifecycleOwner.lifecycleScope.launch {
            val id = statistic.exerciseDoneId
            val exerciseDone = exerciseDAO.getExerciseDoneById(id)
            val bodyExercise = exerciseDone?.let { ExerciseMapper.mapToBodyPartExercisesItem(it) }
            holder.binding.textViewExerciseName.text =
                bodyExercise?.let { TextTranslator().translateExercise(it) }


        }
//        holder.binding.textViewDate.text = statistic.date
        holder.binding.textViewWeight.text = statistic.weight.toString()
        holder.binding.textViewSets.text = statistic.completedSets.toString()
        holder.binding.textViewReps.text = statistic.completedReps.toString()
    }

    override fun getItemCount() = statistics.size
}