package ru.nilsolk.gymapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.nilsolk.gymapp.databinding.ItemMuscleDetailsBinding
import ru.nilsolk.gymapp.fragment.ChosenProgramFragmentDirections
import ru.nilsolk.gymapp.model.BodyPartExercisesItem

class ChosenProgramAdapter(private val exercises: List<BodyPartExercisesItem>) :
    RecyclerView.Adapter<ChosenProgramAdapter.ItemHolder>() {
    class ItemHolder(val binding: ItemMuscleDetailsBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding =
            ItemMuscleDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun getItemCount(): Int = exercises.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) = with(holder.binding) {
        exercise = exercises[position]
        Log.d("count", exercise.toString())
        muscleDetailLinear.setOnClickListener {
            val action =
                ChosenProgramFragmentDirections.actionChosenProgramFragmentToExerciseOverviewFragment(
                    exercises[position]
                )
            it.findNavController().navigate(action)
        }
    }
}