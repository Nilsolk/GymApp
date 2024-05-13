package ru.nilsolk.gymapp.ui.workout

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentWorkoutsDetailBinding
import ru.nilsolk.gymapp.repository.db.ProgramStatistic
import ru.nilsolk.gymapp.repository.model.PopularWorkoutsModel
import ru.nilsolk.gymapp.utils.AppPreferences
import ru.nilsolk.gymapp.utils.downloadImageFromURL
import java.time.LocalDate


class WorkoutsDetailFragment : Fragment() {

    private lateinit var binding: FragmentWorkoutsDetailBinding
    private val workoutsDetailViewModel: WorkoutsDetailViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkoutsDetailBinding.inflate(layoutInflater)

        arguments.let {
            @Suppress("DEPRECATION") val model: PopularWorkoutsModel =
                it!!.getSerializable("workout") as PopularWorkoutsModel
            binding.programImage.downloadImageFromURL(model.imageURL)
            binding.programText.text = model.workoutName
            binding.descriptionText.text = model.description
            binding.startProgramButton.setOnClickListener {

                val appPreferences = AppPreferences(requireContext())
                workoutsDetailViewModel.insertProgram(
                    ProgramStatistic(
                        programName = model.workoutName,
                        currentDay = 1,
                        daysLeft = 60,
                        exerciseDate = LocalDate.now().toString(),
                        progress = 0F

                    )
                )
                appPreferences.saveBoolean("isDataLoaded", false)
                val action =
                    WorkoutsDetailFragmentDirections.actionWorkoutsDetailFragmentToChosenProgramFragment()

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.workoutFragment, false)
                    .build()

                requireView().findNavController().navigate(action, navOptions)
            }
        }
        return binding.root
    }


}