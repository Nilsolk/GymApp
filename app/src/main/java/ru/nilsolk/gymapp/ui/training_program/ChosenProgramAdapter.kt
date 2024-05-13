package ru.nilsolk.gymapp.ui.training_program

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.nilsolk.gymapp.databinding.ItemMuscleProgramBinding
import ru.nilsolk.gymapp.repository.db.Exercise
import ru.nilsolk.gymapp.translation.TextTranslator
import ru.nilsolk.gymapp.translation.TranslationConstants
import ru.nilsolk.gymapp.ui.workout.ExerciseRemovedListener
import ru.nilsolk.gymapp.utils.ExerciseMapper
import ru.nilsolk.gymapp.utils.getImage

class ChosenProgramAdapter(
    private var exercises: List<Exercise>,
    private val viewModel: ChosenProgramViewModel
) : RecyclerView.Adapter<ChosenProgramAdapter.ItemHolder>() {

    class ItemHolder(val binding: ItemMuscleProgramBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemMuscleProgramBinding.inflate(
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
            getImage(itemMuscleGifProgram, exercise.gifUrl)
            val textTranslator = TextTranslator()

            if (TranslationConstants.TARGET == "ru") {
                itemMuscleTargetProgram.text =
                    TranslationConstants.englishMusclesTextMap[exercise.target] ?: exercise.target
                itemMuscleEquipmentProgram.text =
                    TranslationConstants.englishEquipmentToRussianMap[exercise.equipment]
                        ?: exercise.equipment
                needSets.text = "Кол-во сетов ${exercise.sets}"
                needReps.text = "Кол-во повторов ${exercise.reps}"
            } else {
                itemMuscleTargetProgram.text = exercise.target
                itemMuscleEquipmentProgram.text = exercise.equipment
                needSets.text = "Sets value ${exercise.sets}"
                needReps.text = "Reps value ${exercise.reps}"

            }

            val bodyPartExercisesItem = ExerciseMapper.mapToBodyPartExercisesItem(exercise)
            itemMuscleNameProgram.text = textTranslator.translateExercise(bodyPartExercisesItem)

            muscleDetailLinearProgram.setOnClickListener {
                val listener = object : ExerciseRemovedListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onExerciseRemoved() {
                        viewModel.removeExercise(exercise, exercise.programName)
                        removeExercise(exercise)
                        notifyDataSetChanged()
                    }

                }

                val action =
                    ChosenProgramFragmentDirections.actionChosenProgramFragmentToExerciseOverviewFragment(
                        bodyPartExercisesItem,
                        ChosenProgramFragment::class.java.name, listener
                    )
                it.findNavController().navigate(action)
            }
        }
    }

    fun removeExercise(exercise: Exercise) {
        exercises = exercises.filter { it.id != exercise.id }
        notifyDataSetChanged()
    }


    fun updateExercises(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }

}