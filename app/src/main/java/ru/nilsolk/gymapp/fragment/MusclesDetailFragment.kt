package ru.nilsolk.gymapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.adapter.ExercisesAdapter
import ru.nilsolk.gymapp.adapter.RussianToEnglishAdapter
import ru.nilsolk.gymapp.databinding.FragmentMusclesDetailBinding
import ru.nilsolk.gymapp.model.BodyPartExercises
import ru.nilsolk.gymapp.model.MuscleGroupModel
import ru.nilsolk.gymapp.utils.TranslationConstants
import ru.nilsolk.gymapp.viewmodel.MuscleExercisesViewModel
import java.util.Locale

class MusclesDetailFragment : Fragment() {

    private lateinit var exercisesAdapter: ExercisesAdapter
    private lateinit var bodyPartExercises: BodyPartExercises
    private lateinit var binding: FragmentMusclesDetailBinding
    private val muscleExercisesViewModel: MuscleExercisesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusclesDetailBinding.inflate(layoutInflater)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility =
            View.VISIBLE

        bodyPartExercises = BodyPartExercises()
        exercisesAdapter = ExercisesAdapter(bodyPartExercises)

        arguments?.let {
            val muscleName = it.getSerializable("muscle") as MuscleGroupModel
            binding.muscleNameText.text =
                TranslationConstants.mapEnglishToRussianMuscleGroups[muscleName.muscleName]
                    ?: muscleName.muscleName
            muscleExercisesViewModel.getExercises(muscleName.muscleName.lowercase(), LIMIT)
        }

        binding.goBackMuscleDetails.setOnClickListener {
            val action =
                MusclesDetailFragmentDirections.actionMusclesDetailFragmentToWorkoutFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        if (Locale.getDefault().language == "ru") {
            dropDownTargetSettingsRussian()
            dropDownEquipmentSettingsRussian()
        } else {
            dropDownTargetSettingsEnglish()
            dropDownEquipmentSettingsEnglish()
        }

        observeBodyPartExercises()

        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun observeBodyPartExercises() = with(binding) {
        muscleExercisesViewModel.bodyPartExercises.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                bodyPartExercises.clear()
                bodyPartExercises.addAll(result)
                exerciseRecycler.adapter = exercisesAdapter
                exerciseRecycler.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun dropDownEquipmentSettingsEnglish() {
        val goalItems = listOf(
            "band",
            "body weight",
            "assisted",
            "cable",
            "hammer",
            "olympic barbell",
            "ez barbell",
            "dumbbell",
            "barbell",
            "roller",
            "kettlebell",
            "smith machine"
        )
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goalItems)
        binding.goalDropDowntargetEquipment.setAdapter(adapter)
        binding.goalDropDowntargetEquipment.setOnItemClickListener { parent, _, position, _ ->
            val selectedEnglishEquipment = adapter.getItem(position)
            selectedEnglishEquipment?.let {
                muscleExercisesViewModel.updateRecyclerByEquipment(it)
            }
        }
    }

    private fun dropDownTargetSettingsEnglish() {
        val goalItems = listOf(
            "abductors",
            "abs",
            "adductors",
            "biceps",
            "calves",
            "delts",
            "forearms",
            "glutes",
            "hamstrings",
            "lats",
            "kettlebell",
            "pectorals",
            "quads",
            "traps",
            "triceps",
            "upper back"
        )
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goalItems)
        binding.goalDropDownTargetMuscleGroup.setAdapter(adapter)
        binding.goalDropDownTargetMuscleGroup.setOnItemClickListener { _, _, position, _ ->
            val selectedEnglishTarget = adapter.getItem(position)
            selectedEnglishTarget?.let {
                muscleExercisesViewModel.updateRecyclerByTarget(it)
            }
        }
    }

    private fun dropDownEquipmentSettingsRussian() {
        val goalItems = listOf(
            "петля",
            "собственный вес",
            "помощь",
            "трос",
            "молот",
            "олимпийский гантельный штанга",
            "простая гантельная штанга",
            "гантель",
            "штанга",
            "роллер",
            "гиря",
            "стойка"
        )
        val englishTextMap = mapOf(
            "петля" to "band",
            "собственный вес" to "body weight",
            "асистент" to "assisted",
            "трос" to "cable",
            "молот" to "hammer",
            "олимпийская штанга" to "olympic barbell",
            "кривая штанга" to "ez barbell",
            "гантель" to "dumbbell",
            "штанга" to "barbell",
            "роллер" to "roller",
            "гиря" to "kettlebell",
            "стойка" to "smith machine"
        )
        val adapter = RussianToEnglishAdapter(requireContext(), goalItems, englishTextMap)
        binding.goalDropDowntargetEquipment.setAdapter(adapter)
        binding.goalDropDowntargetEquipment.setOnItemClickListener { _, _, position, _ ->
            val selectedEnglishEquipment = adapter.getEnglishValue(position)
            selectedEnglishEquipment?.let {
                muscleExercisesViewModel.updateRecyclerByEquipment(it)
            }
        }
    }

    private fun dropDownTargetSettingsRussian() {
        val goalItems = listOf(
            "отводящие",
            "пресс",
            "приводящие",
            "бицепс",
            "икры",
            "дельты",
            "предплечья",
            "ягодицы",
            "бедра",
            "широчайшие мышцы спины",
            "гири",
            "грудные",
            "квадрицепсы",
            "трапеции",
            "трицепсы",
            "верхняя спина"
        )
        val englishTextMap = mapOf(
            "отводящие" to "abductors",
            "пресс" to "abs",
            "приводящие" to "adductors",
            "бицепс" to "biceps",
            "икры" to "calves",
            "дельты" to "delts",
            "предплечья" to "forearms",
            "ягодицы" to "glutes",
            "бедра" to "hamstrings",
            "широчайшие мышцы спины" to "lats",
            "гири" to "kettlebell",
            "грудные" to "pectorals",
            "квадрицепсы" to "quads",
            "трапеции" to "traps",
            "трицепсы" to "triceps",
            "верхняя спина" to "upper back"
        )
        val adapter = RussianToEnglishAdapter(requireContext(), goalItems, englishTextMap)
        binding.goalDropDownTargetMuscleGroup.setAdapter(adapter)
        binding.goalDropDownTargetMuscleGroup.setOnItemClickListener { _, _, position, _ ->
            val selectedEnglishTarget = adapter.getEnglishValue(position)
            selectedEnglishTarget?.let {
                muscleExercisesViewModel.updateRecyclerByTarget(it)
            }
        }
    }

    companion object {
        private const val LIMIT = "100"
    }
}