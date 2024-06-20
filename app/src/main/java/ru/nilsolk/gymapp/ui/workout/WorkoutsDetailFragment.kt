package ru.nilsolk.gymapp.ui.workout

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import ru.nilsolk.gymapp.translation.TranslationConstants
import ru.nilsolk.gymapp.utils.AppPreferences
import ru.nilsolk.gymapp.utils.downloadImageFromURL
import java.time.LocalDate
import java.util.Locale


class WorkoutsDetailFragment : Fragment() {

    private lateinit var binding: FragmentWorkoutsDetailBinding
    private val workoutsDetailViewModel: WorkoutsDetailViewModel by viewModels()
    private lateinit var activityLevel: String
    private var alertDialog: AlertDialog? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkoutsDetailBinding.inflate(layoutInflater)
        workoutsDetailViewModel.getProfileDetail()
        observeProfileDetail()

        arguments?.let {
            val model: PopularWorkoutsModel =
                it.getSerializable("workout") as PopularWorkoutsModel
            binding.programImage.downloadImageFromURL(model.imageURL)

            if (Locale.getDefault().language == "ru") {
                binding.programText.text =
                    TranslationConstants.mapEnglishToRussianPrograms[model.workoutName]
                        ?: model.workoutName
                binding.goal.text =
                    TranslationConstants.mapEnglishToRussianGoal[model.goal] ?: model.goal
                binding.activityLevel.text =
                    TranslationConstants.mapEnglishToRussianLevel[model.activityLevel]
                        ?: model.activityLevel
            } else {
                binding.programText.text = model.workoutName
                binding.goal.text = model.goal
                binding.activityLevel.text = model.activityLevel
            }
            binding.descriptionText.text = model.description
            binding.startProgramButton.setOnClickListener {
                Log.d("levelActivity", activityLevel)
                Log.d("levelActivity", model.activityLevel)
                if (Locale.getDefault().language != "ru") {
                    if (activityLevel != model.activityLevel) {
                        showActivityLevelMismatchDialog(model)
                    } else {
                        navigateToChosenProgramFragment(model)
                    }
                } else {
                    if (activityLevel != (TranslationConstants.mapEnglishToRussianLevel[model.activityLevel]
                            ?: model.activityLevel)
                    ) {
                        showActivityLevelMismatchDialog(model)
                    } else {
                        navigateToChosenProgramFragment(model)
                    }
                }
            }
        }

        return binding.root
    }

    private fun observeProfileDetail() {
        workoutsDetailViewModel.profileDetails.observe(requireActivity()) { userProfileDetails ->
            if (userProfileDetails != null) {
                activityLevel = userProfileDetails.activityLevel.toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showActivityLevelMismatchDialog(model: PopularWorkoutsModel) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Уведомление")
        builder.setMessage("Уровень активности программы не совпадает с вашим текущим уровнем активности.")
        builder.setPositiveButton("Продолжить") { dialog, _ ->
            dialog.dismiss()
            navigateToChosenProgramFragment(model)
        }
        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog = builder.create()
        alertDialog?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alertDialog?.dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun navigateToChosenProgramFragment(model: PopularWorkoutsModel) {
        val appPreferences = AppPreferences(requireContext())
        appPreferences.saveBoolean("isDataLoaded", false)

        workoutsDetailViewModel.insertProgram(
            ProgramStatistic(
                programName = model.workoutName,
                currentDay = 1,
                daysLeft = 60,
                exerciseDate = LocalDate.now().toString(),
                progress = 0F
            )
        )
        val action =
            WorkoutsDetailFragmentDirections.actionWorkoutsDetailFragmentToChosenProgramFragment()

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.workoutFragment, false)
            .build()

        requireView().findNavController().navigate(action, navOptions)
    }
}