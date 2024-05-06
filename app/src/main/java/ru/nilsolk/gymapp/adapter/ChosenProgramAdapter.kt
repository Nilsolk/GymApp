package ru.nilsolk.gymapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.nilsolk.gymapp.databinding.ItemMuscleDetailsBinding
import ru.nilsolk.gymapp.db.Exercise
import ru.nilsolk.gymapp.fragment.ChosenProgramFragmentDirections
import ru.nilsolk.gymapp.model.BodyPartExercisesItem
import ru.nilsolk.gymapp.utils.TextTranslator
import ru.nilsolk.gymapp.utils.TranslationCallback
import ru.nilsolk.gymapp.utils.TranslationConstants
import ru.nilsolk.gymapp.utils.getImage
import ru.nilsolk.gymapp.viewmodel.ChosenProgramViewModel

class ChosenProgramAdapter(
    private var exercises: List<Exercise>,
    private val viewModel: ChosenProgramViewModel
) : RecyclerView.Adapter<ChosenProgramAdapter.ItemHolder>() {

    class ItemHolder(val binding: ItemMuscleDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemMuscleDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemHolder(itemBinding)
    }

    override fun getItemCount(): Int = exercises.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        with(holder.binding) {
            Log.d("Load data", exercises.toString() + "From adapter")
            val exercise = exercises[position]
            getImage(itemMuscleGif, exercise.gifUrl)
            val textTranslator = TextTranslator()
            textTranslator.translate(
                TranslationConstants.SOURCE,
                TranslationConstants.TARGET, exercise.name, object : TranslationCallback {
                    override fun onTranslationComplete(translatedText: String) {
                        itemMuscleName.text = translatedText
                    }
                })
            if (TranslationConstants.TARGET == "ru") {
                itemMuscleTarget.text =
                    TranslationConstants.englishMusclesTextMap[exercise.target] ?: exercise.target
                itemMuscleEquipment.text =
                    TranslationConstants.englishEquipmentToRussianMap[exercise.equipment]
                        ?: exercise.equipment
            }
            muscleDetailLinear.setOnClickListener {
                val exerciseItem = exercises[position]
                val action =
                    ChosenProgramFragmentDirections.actionChosenProgramFragmentToExerciseOverviewFragment(
                        BodyPartExercisesItem(
                            exerciseItem.bodyPart,
                            exerciseItem.equipment,
                            exerciseItem.gifUrl,
                            exerciseItem.id,
                            exerciseItem.instructions,
                            exerciseItem.name,
                            exerciseItem.secondaryMuscles,
                            exerciseItem.target
                        )
                    )
                viewModel.removeExercise(exerciseItem)
                exercises = exercises.filterNot { it.id == exerciseItem.id }
                notifyDataSetChanged()
                it.findNavController().navigate(action)
            }
        }
    }

    fun updateExercises(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }
}