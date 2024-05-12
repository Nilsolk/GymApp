package ru.nilsolk.gymapp.ui.training_program

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import ru.nilsolk.gymapp.App
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentChosenProgramBinding
import ru.nilsolk.gymapp.utils.AppPreferences
import java.time.LocalDate

class ChosenProgramFragment : Fragment() {

    private lateinit var binding: FragmentChosenProgramBinding
    private val chosenProgramViewModel: ChosenProgramViewModel by viewModels()
    private lateinit var programExercisesAdapter: ChosenProgramAdapter
    private lateinit var appPreferences: AppPreferences
    private val exerciseDao = App.database.exerciseDao()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChosenProgramBinding.inflate(layoutInflater)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility =
            View.VISIBLE

        appPreferences = AppPreferences(requireContext())
        chosenProgramViewModel.setAppPref(appPreferences)

        programExercisesAdapter = ChosenProgramAdapter(emptyList(), chosenProgramViewModel)
        binding.chosenProgramRecycler.adapter = programExercisesAdapter


        val isDataLoaded = appPreferences.getBoolean(IS_DATA_LOADED_KEY)
        Log.d("isDataLoaded", isDataLoaded.toString())

        var programName: String? = ""
        var programDay: Int? = 0
        chosenProgramViewModel.viewModelScope.launch {
            programName = exerciseDao.getProgramName()
            programDay = programName?.let {
                exerciseDao.getCurrentDay(it)
            }
            binding.trainingProgramName.text = programName
            binding.progressViewStats.progress = exerciseDao.getProgress(programName!!)!!

        }
        chosenProgramViewModel.viewModelScope.launch {
            Log.d("Log", (programName?.let { exerciseDao.getExerciseDate(it) } == LocalDate.now()
                .toString()).toString())
            Log.d("Log now", LocalDate.now().toString())
            Log.d("Log from db", (programName?.let { exerciseDao.getExerciseDate(it) }.toString()))

            if (exerciseDao.getExerciseDate(programName!!).toString() == LocalDate.now().toString()) {

//        val programName = appPreferences.getString("programName", "")
//        val programDay = appPreferences.getInt("programDay", 1)

                if (!isDataLoaded) {
                    if (programName != null) {
                        Log.d("Load from network or cache", "LoadData")
                        programDay?.let {
                            chosenProgramViewModel.getProgramExercises(
                                programName!!,
                                it
                            )
                        }
                        observeBodyPartExercises()

                    }
                } else {
                    chosenProgramViewModel.viewModelScope.launch {
                        val list = programName?.let { exerciseDao.getAllExercises(it) }
                        if (list != null) {
                            if (list.isEmpty()) {
                                binding.chosenProgramRecycler.visibility = View.GONE
                                binding.endItems.visibility = View.VISIBLE
                            } else {
                                programExercisesAdapter.updateExercises(list)
                            }
                        }
                    }
                }
            } else {
                binding.chosenProgramRecycler.visibility = View.GONE
                binding.endItems.visibility = View.VISIBLE
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
