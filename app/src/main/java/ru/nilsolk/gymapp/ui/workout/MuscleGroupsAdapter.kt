package ru.nilsolk.gymapp.ui.workout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.ItemMuscleGroupsBinding
import ru.nilsolk.gymapp.repository.model.MuscleGroupModel
import ru.nilsolk.gymapp.translation.TranslationConstants
import ru.nilsolk.gymapp.utils.getImage
import java.util.Locale

class MuscleGroupsAdapter(private val muscleGroups: ArrayList<MuscleGroupModel>) :
    RecyclerView.Adapter<MuscleGroupsAdapter.ItemHolder>() {

    inner class ItemHolder(val itemMuscleGroupsBinding: ItemMuscleGroupsBinding) :
        RecyclerView.ViewHolder(itemMuscleGroupsBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = DataBindingUtil.inflate<ItemMuscleGroupsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_muscle_groups,
            parent,
            false
        )
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return muscleGroups.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) =
        with(holder.itemMuscleGroupsBinding) {
            val muscle = muscleGroups[position]
            getImage(muscleImage, muscle.muscleImageURL)
            val muscleNameText = if (Locale.getDefault().language == "ru") {
                TranslationConstants.mapEnglishToRussianMuscleGroups[muscle.muscleName]
                    ?: muscle.muscleName
            } else {
                muscle.muscleName
            }
            muscleName.text = muscleNameText
            muscleRootFrame.setOnClickListener {
                val action = WorkoutFragmentDirections.actionWorkoutFragmentToMusclesDetailFragment(
                    muscleGroups[position]
                )
                it.findNavController().navigate(action)
            }
        }


}