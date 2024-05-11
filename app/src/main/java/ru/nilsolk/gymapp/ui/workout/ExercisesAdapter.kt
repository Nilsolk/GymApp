package ru.nilsolk.gymapp.ui.workout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.ItemMuscleDetailsBinding
import ru.nilsolk.gymapp.repository.model.BodyPartExercises
import ru.nilsolk.gymapp.translation.TextTranslator
import ru.nilsolk.gymapp.translation.TranslationCallback
import ru.nilsolk.gymapp.translation.TranslationConstants
import ru.nilsolk.gymapp.translation.TranslationConstants.englishEquipmentToRussianMap
import ru.nilsolk.gymapp.translation.TranslationConstants.englishMusclesTextMap
import ru.nilsolk.gymapp.utils.getImage

class ExercisesAdapter(
    private val bodyPartExercises: BodyPartExercises,
) :
    RecyclerView.Adapter<ExercisesAdapter.ItemHolder>() {
    private val textTranslator = TextTranslator()

    inner class ItemHolder(val itemMuscleDetailsBinding: ItemMuscleDetailsBinding) :
        RecyclerView.ViewHolder(itemMuscleDetailsBinding.root)

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
            val exercise = bodyPartExercises[position]
            getImage(itemMuscleGif, exercise.gifUrl)

            textTranslator.translate(
                TranslationConstants.SOURCE,
                TranslationConstants.TARGET,
                exercise.name,
                object : TranslationCallback {
                    override fun onTranslationComplete(translatedText: String) {
                        itemMuscleName.text = translatedText
                    }
                })
            if (TranslationConstants.TARGET == "ru") {
                itemMuscleTarget.text = englishMusclesTextMap[exercise.target] ?: exercise.target
                itemMuscleEquipment.text =
                    englishEquipmentToRussianMap[exercise.equipment] ?: exercise.equipment
            }

            muscleDetailLinear.setOnClickListener {
                val action =
                    MusclesDetailFragmentDirections.actionMusclesDetailFragmentToExerciseOverviewFragment(
                        bodyPartExercises[position],
                        MusclesDetailFragment::class.java.name
                    )
                it.findNavController().navigate(action)
            }
        }


}