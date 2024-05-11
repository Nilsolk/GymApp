package ru.nilsolk.gymapp.ui.training_program

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentChosenProgramBinding
import ru.nilsolk.gymapp.App
import ru.nilsolk.gymapp.utils.AppPreferences

class ChosenProgramFragment : Fragment() {

    private lateinit var binding: FragmentChosenProgramBinding
    private val chosenProgramViewModel: ChosenProgramViewModel by viewModels()
    private lateinit var programExercisesAdapter: ChosenProgramAdapter
    private lateinit var appPreferences: AppPreferences
    private val exerciseDao = App.database.exerciseDao()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChosenProgramBinding.inflate(layoutInflater)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility =
            View.VISIBLE

        appPreferences = AppPreferences(requireContext())
        programExercisesAdapter = ChosenProgramAdapter(emptyList(), chosenProgramViewModel)
        binding.chosenProgramRecycler.adapter = programExercisesAdapter


        val isDataLoaded = appPreferences.getBoolean(IS_DATA_LOADED_KEY)
        Log.d("isDataLoaded", isDataLoaded.toString())
        val programName = appPreferences.getString("programName", "")
        val programDay = appPreferences.getInt("programDay", 1)

        binding.trainingProgramName.text = programName
        binding.progressViewStats.progress = programDay * 10F
        if (!isDataLoaded) {
            if (programName.isNotEmpty()) {
                Log.d("Load from network or cache", "LoadData")
                chosenProgramViewModel.getProgramExercises(programName, programDay)
                observeBodyPartExercises()

            }
        } else {
            chosenProgramViewModel.viewModelScope.launch {
                val list = exerciseDao.getAllExercises(programName)
                if (list.isEmpty()) {
                    binding.chosenProgramRecycler.visibility = View.GONE
                    binding.endItems.visibility = View.VISIBLE
                } else {
                    programExercisesAdapter.updateExercises(list)
                }
            }
        }

        return binding.root
    }

    private fun observeBodyPartExercises() {
        chosenProgramViewModel.exercisesResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                activity?.runOnUiThread {
                    programExercisesAdapter.updateExercises(it)
                    appPreferences.saveBoolean(
                        IS_DATA_LOADED_KEY,
                        true
                    )
                }
            }
        }
    }


    companion object {
        private const val IS_DATA_LOADED_KEY = "isDataLoaded"
    }
}
