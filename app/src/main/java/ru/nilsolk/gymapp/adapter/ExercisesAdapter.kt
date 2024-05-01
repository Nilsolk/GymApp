package ru.nilsolk.gymapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.ItemMuscleDetailsBinding
import ru.nilsolk.gymapp.fragment.MusclesDetailFragmentDirections
import ru.nilsolk.gymapp.model.BodyPartExercises

class ExercisesAdapter(private val bodyPartExercises: BodyPartExercises) :
    RecyclerView.Adapter<ExercisesAdapter.ItemHolder>() {

    inner class ItemHolder(val itemMuscleDetailsBinding: ItemMuscleDetailsBinding) :
        RecyclerView.ViewHolder(itemMuscleDetailsBinding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = DataBindingUtil.inflate<ItemMuscleDetailsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_muscle_details,
            parent,
            false
        )
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return bodyPartExercises.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        with(holder.itemMuscleDetailsBinding) {

            exercise = bodyPartExercises[position]
            muscleDetailLinear.setOnClickListener {
                val action =
                    MusclesDetailFragmentDirections.actionMusclesDetailFragmentToExerciseOverviewFragment(
                        bodyPartExercises[position]
                    )
                it.findNavController().navigate(action)
            }
        }


}