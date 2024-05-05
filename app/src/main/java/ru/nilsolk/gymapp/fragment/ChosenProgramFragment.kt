package ru.nilsolk.gymapp.fragment

import android.annotation.SuppressLint
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
import ru.nilsolk.gymapp.model.BodyPartExercisesItem
import ru.nilsolk.gymapp.utils.AppPreferences
import ru.nilsolk.gymapp.viewmodel.ChosenProgramViewModel

class ChosenProgramFragment : Fragment() {

    private lateinit var exercises: ArrayList<BodyPartExercisesItem>
    private val chosenProgramViewModel: ChosenProgramViewModel by viewModels()
    private lateinit var binding: FragmentChosenProgramBinding
    private lateinit var programExercisesAdapter: ChosenProgramAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChosenProgramBinding.inflate(layoutInflater)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility =
            View.VISIBLE

        exercises = arrayListOf()
        programExercisesAdapter = ChosenProgramAdapter(exercises, chosenProgramViewModel)

        binding.chosenProgramRecycler.adapter = programExercisesAdapter

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
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeBodyPartExercises() = with(binding) {
        chosenProgramViewModel.exercisesResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                exercises.clear()
                exercises.addAll(result)
                chosenProgramRecycler.adapter = programExercisesAdapter
                chosenProgramRecycler.adapter?.notifyDataSetChanged()
            }
        }
    }


}