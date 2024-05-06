package ru.nilsolk.gymapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.adapter.ChosenProgramAdapter
import ru.nilsolk.gymapp.databinding.FragmentChosenProgramBinding
import ru.nilsolk.gymapp.utils.AppPreferences
import ru.nilsolk.gymapp.viewmodel.ChosenProgramViewModel

class ChosenProgramFragment : Fragment() {

    private lateinit var binding: FragmentChosenProgramBinding
    private val chosenProgramViewModel: ChosenProgramViewModel by viewModels()
    private lateinit var programExercisesAdapter: ChosenProgramAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChosenProgramBinding.inflate(layoutInflater)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility =
            View.VISIBLE

        programExercisesAdapter = ChosenProgramAdapter(emptyList(), chosenProgramViewModel)
        binding.chosenProgramRecycler.adapter = programExercisesAdapter

        // Проверяем, есть ли уже загруженные данные в списке упражнений
        if (chosenProgramViewModel.exercisesResult.value.isNullOrEmpty()) {
            // Если список пустой, загружаем данные из сети и базы данных
            val programName = AppPreferences(requireContext()).getString("programName", "")
            if (programName.isNotEmpty()) {
                val programDay = AppPreferences(requireContext()).getInt("programDay", 1)
                if (!chosenProgramViewModel.isDataLoaded) {
                    Log.d("Load", "LoadData")
                    chosenProgramViewModel.getProgramExercises(programName, programDay)
                }
                observeBodyPartExercises()
                binding.progressViewStats.progress = programDay.toFloat()
                binding.trainingProgramName.text = programName
            }
        } else {
            // Если список не пустой, обновляем адаптер с данными из viewModel
            chosenProgramViewModel.exercisesResult.value?.let { programExercises ->
                programExercisesAdapter.updateExercises(programExercises)
            }
        }

        return binding.root
    }

    private fun observeBodyPartExercises() {
        chosenProgramViewModel.exercisesResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                Log.d("Load Data", it.toString())
                programExercisesAdapter.updateExercises(it)
            }
        }
    }

    private fun updateProgress(day: Int, totalDaysInProgram: Int) {
        val progress = (day.toFloat() / totalDaysInProgram.toFloat()) * 100
        binding.progressViewStats.progress = progress
    }
}