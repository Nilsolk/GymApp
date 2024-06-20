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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.nilsolk.gymapp.App
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentChosenProgramBinding
import ru.nilsolk.gymapp.translation.TranslationConstants
import ru.nilsolk.gymapp.utils.AppPreferences
import java.time.LocalDate
import java.util.Locale

class ChosenProgramFragment : Fragment() {

    private lateinit var binding: FragmentChosenProgramBinding
    private val chosenProgramViewModel: ChosenProgramViewModel by viewModels()
    private lateinit var programExercisesAdapter: ChosenProgramAdapter
    private lateinit var appPreferences: AppPreferences
    private val exerciseDao = App.database.exerciseDao()
    private lateinit var programName: String
    private var programDay: Int? = 0
    private lateinit var nextDayProgram: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChosenProgramBinding.inflate(layoutInflater)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility =
            View.VISIBLE

        appPreferences = AppPreferences(requireContext())
        chosenProgramViewModel.setAppPref(appPreferences)

        programExercisesAdapter =
            ChosenProgramAdapter(emptyList(), chosenProgramViewModel, requireContext())
        binding.chosenProgramRecycler.adapter = programExercisesAdapter

        val isDataLoaded = appPreferences.getBoolean(IS_DATA_LOADED_KEY)
        Log.d("isDataLoaded", isDataLoaded.toString())

        var programId = 0
        chosenProgramViewModel.viewModelScope.launch {
            async(Dispatchers.IO) {
                programName = exerciseDao.getProgramName()
                programId = exerciseDao.getProgramIdByName(programName)!!
                programDay = programName.let {
                    exerciseDao.getCurrentDay(it)
                }
                nextDayProgram = exerciseDao.getExerciseDate(programName)
            }.await()

            if (Locale.getDefault().language == "ru") {
                binding.trainingProgramName.text =
                    TranslationConstants.mapEnglishToRussianPrograms[programName] ?: programName
            } else binding.trainingProgramName.text = programName
//          binding.progressViewStats.progress = exerciseDao.getProgress(programName)!!
            binding.progressViewStats.progress =
                (AppPreferences(requireContext()).getString("progress", "0")).toFloat()
            binding.nextDayProgram.text = nextDayProgram

            binding.skipDayProgramButton.setOnClickListener {
                chosenProgramViewModel.skipDay(programName)
                observeBodyPartExercises()
                binding.chosenProgramRecycler.visibility = View.GONE
                binding.endItems.visibility = View.VISIBLE
                binding.skipDayProgramButton.visibility = View.GONE
            }

            binding.todayStatistic.setOnClickListener {
                observeBodyPartExercises()
                chosenProgramViewModel.getDailyStatistics(
                    LocalDate.now().toString(),
                    programId
                ) { statistics ->
                    Log.d("Show statistic", statistics.toString())
                    val dialogFragment = DailyStatisticDialog.newInstance(statistics)
                    dialogFragment.show(childFragmentManager, "DailyStatisticDialog")
                }
            }

            if (LocalDate.parse(exerciseDao.getExerciseDate(programName))
                    .isEqual(LocalDate.now())
            ) {
                Log.d("Data loaded", isDataLoaded.toString())
                if (!isDataLoaded) {
                    Log.d("Load from network or cache", "LoadData")
                    programDay?.let {
                        chosenProgramViewModel.getProgramExercises(
                            programName,
                            it
                        )
                    }
                    observeBodyPartExercises()

                } else {
                    val list = programName.let { exerciseDao.getAllExercises(it) }
                    if (list.isEmpty()) {
                        binding.chosenProgramRecycler.visibility = View.GONE
                        binding.endItems.visibility = View.VISIBLE
                    } else {
                        programExercisesAdapter.updateExercises(list)
                    }
                }

            } else {
                binding.skipDayProgramButton.visibility = View.GONE
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
                }
            }
        }
    }

    companion object {
        private const val IS_DATA_LOADED_KEY = "isDataLoaded"
    }
}
