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
import ru.nilsolk.gymapp.utils.TextTranslator
import ru.nilsolk.gymapp.utils.TranslationCallback
import ru.nilsolk.gymapp.utils.TranslationConstants
import ru.nilsolk.gymapp.utils.getImage
import ru.nilsolk.gymapp.viewmodel.ChosenProgramViewModel

class ChosenProgramAdapter(
    private val exercises: ArrayList<BodyPartExercisesItem>,
    private val viewModel: ChosenProgramViewModel
) :
    RecyclerView.Adapter<ChosenProgramAdapter.ItemHolder>() {
    class ItemHolder(val binding: ItemMuscleDetailsBinding) : ViewHolder(binding.root)

    private val textTranslator = TextTranslator()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding =
            ItemMuscleDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun getItemCount(): Int = exercises.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) = with(holder.binding) {
        val exercise = exercises[position]

        getImage(itemMuscleGif, exercise.gifUrl)

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
                    exerciseItem
                )
            it.findNavController().navigate(action)
            viewModel.removeExercise(exerciseItem)
        }
    }

}