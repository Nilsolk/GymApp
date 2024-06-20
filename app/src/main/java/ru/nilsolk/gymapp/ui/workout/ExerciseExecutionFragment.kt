package ru.nilsolk.gymapp.ui.workout

import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentExerciseExecutionBinding
import ru.nilsolk.gymapp.repository.model.BodyPartExercisesItem
import ru.nilsolk.gymapp.repository.model.ExecutionModel
import ru.nilsolk.gymapp.translation.TextTranslator
import ru.nilsolk.gymapp.ui.training_program.ChosenProgramFragment
import ru.nilsolk.gymapp.utils.AppPreferences
import ru.nilsolk.gymapp.utils.CountDownDialog
import ru.nilsolk.gymapp.utils.CustomProgress
import ru.nilsolk.gymapp.utils.downloadGifFromURL
import ru.nilsolk.gymapp.utils.downloadImageFromURL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExerciseExecutionFragment : Fragment() {

    private var isRunning = false
    private var elapsedSet = 0
    private var elapsedTime: Long = 0

    private var enteredSets = "0"
    private var enteredReps = "0"
    private var enteredWeight = "0"
    private var enteredType = ""

    private lateinit var fragmentType: String
    private lateinit var binding: FragmentExerciseExecutionBinding
    private lateinit var customProgress: CustomProgress
    private lateinit var countDownDialog: CountDownDialog
    private lateinit var exerciseItem: BodyPartExercisesItem
    private var exerciseRemovedCallback: ExerciseRemovedListener? = null
    private val executionViewModel: ExecutionViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExerciseExecutionBinding.inflate(inflater)
        customProgress = CustomProgress(requireContext())
        countDownDialog = CountDownDialog(requireContext())

        arguments?.let {
            exerciseRemovedCallback = it.getSerializable("listener") as? ExerciseRemovedListener
            exerciseItem = it.getSerializable("exercise") as BodyPartExercisesItem
            fragmentType = it.getString("fragmentType")!!

            with(binding) {
                exerciseGifView.downloadImageFromURL(exerciseItem.gifUrl)
                exerciseName.text = TextTranslator().translateExercise(exerciseItem)
            }
        }

        setClickListeners()
        showBottomSheet()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClickListeners() {
        with(binding) {
            startButton.setOnClickListener {
                handleStartButtonClick()
            }

            nextSetButton.setOnClickListener {
                handleNextSetButtonClick()
            }

            pauseAndPlay.setOnClickListener {
                handlePausePlayClick()
            }

            settings.setOnClickListener {
                showBottomSheet()
            }
        }
    }

    private fun handleStartButtonClick() {
        if (enteredSets.toInt() != 0 && enteredReps.toInt() != 0) {
            countDownDialog.showCountDownDialog(5000) { started ->
                if (started) {
                    handleExerciseStart()
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.num_of_sets_and_reps),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleExerciseStart() {
        with(binding) {
            exerciseGifView.downloadGifFromURL(exerciseItem.gifUrl)
            startButton.visibility = View.GONE
            startedLayout.visibility = View.VISIBLE
            startChronometer()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleNextSetButtonClick() {
        if (elapsedSet != 1) {
            handleNextSet()
        } else {
            saveExerciseData()
        }
    }

    private fun handleNextSet() {
        with(binding) {
            exerciseGifView.downloadImageFromURL(exerciseItem.gifUrl)
            chronometer.stop()
            countDownDialog.showCountDownDialog(1000) {
                resetChronometer()
                updateNextSetUI()
            }
        }
    }

    private fun resetChronometer() {
        with(binding) {
            pauseAndPlay.setBackgroundResource(R.drawable.round_6dp_background)
            pauseAndPlay.setImageResource(R.drawable.baseline_play_arrow_24)
            chronometer.base = SystemClock.elapsedRealtime()
            elapsedTime = 0
            isRunning = false
            if (elapsedSet > 1) {
                elapsedSet--
                elapsedSetText.text = "$elapsedSet x Осталось сетов"
            }
        }
    }

    private fun updateNextSetUI() {
        with(binding) {
            if (elapsedSet == 1) nextSetButton.text = getString(R.string.finish_workout)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveExerciseData() {
        val currentYear: String = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)

        val exerciseProgram =
            if (fragmentType == ChosenProgramFragment::class.java.name) {
                AppPreferences(
                    requireActivity()
                ).getString("programName", "None")
            } else ""


        val exerciseData = ExecutionModel(
            exerciseItem.name,
            enteredWeight.toDouble(),
            enteredType,
            enteredSets.toInt(),
            enteredReps.toInt(),
            currentYear,
            exerciseItem.bodyPart,
            exerciseProgram
        )
        executionViewModel.saveDailyStatistic(
            exerciseItem,
            fragmentType,
            enteredReps.toInt(),
            enteredSets.toInt(),
            exerciseRemovedCallback,
            enteredWeight.toInt()
        )
        customProgress.show()
        executionViewModel.saveWorkout(exerciseData) { result, message ->
            customProgress.dismiss()
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            if (result) {
                val action = when (fragmentType) {
                    ChosenProgramFragment::class.java.name -> ExerciseExecutionFragmentDirections.actionExerciseExecutionFragmentToChosenProgramFragment()
                    MusclesDetailFragment::class.java.name -> ExerciseExecutionFragmentDirections.actionExerciseExecutionFragmentToWorkoutFragment()
                    else -> {
                        ExerciseExecutionFragmentDirections.actionExerciseExecutionFragmentToHomeFragment()
                    }
                }
                findNavController().navigate(action)
            }
        }
    }

    private fun handlePausePlayClick() {
        if (isRunning) {
            pauseChronometer()
        } else {
            playChronometer()
        }
    }

    private fun pauseChronometer() {
        with(binding) {
            val chronometer = this.chronometer
            elapsedTime = SystemClock.elapsedRealtime() - chronometer.base
            chronometer.stop()
            isRunning = false
            exerciseGifView.downloadImageFromURL(exerciseItem.gifUrl)
            pauseAndPlay.setBackgroundResource(R.drawable.round_6dp_background)
            pauseAndPlay.setImageResource(R.drawable.baseline_play_arrow_24)
        }
    }

    private fun playChronometer() {
        with(binding) {
            val chronometer = this.chronometer
            chronometer.base = SystemClock.elapsedRealtime() - elapsedTime
            chronometer.start()
            isRunning = true
            exerciseGifView.downloadGifFromURL(exerciseItem.gifUrl)
            pauseAndPlay.setBackgroundResource(R.drawable.round_6dp_background)
            pauseAndPlay.setImageResource(R.drawable.baseline_pause_24)
            if (elapsedSet == 1) nextSetButton.text = getString(R.string.finish_workout)
        }
    }

    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_execution, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.setCanceledOnTouchOutside(false)

        var selectedType: String? = null

        val weightTypes = listOf("кг", "фунты")
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_items, weightTypes)
        val typeDropDown = view.findViewById<AutoCompleteTextView>(R.id.typeDropDown)
        typeDropDown.setAdapter(typeAdapter)

        val weightEditText = view.findViewById<TextInputEditText>(R.id.weightText)
        val setsEditText = view.findViewById<TextInputEditText>(R.id.setsText)
        val repsEditText = view.findViewById<TextInputEditText>(R.id.repsText)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            enteredWeight = weightEditText.text.toString()
            enteredSets = setsEditText.text.toString()
            enteredReps = repsEditText.text.toString()

            if (listOf(
                    enteredWeight,
                    enteredSets,
                    enteredReps,
                    selectedType
                ).any { it.isNullOrEmpty() }
            ) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_fill_in_the_empty_fields),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.selectedWeight.text = "$enteredWeight $selectedType"
                binding.selectedSets.text = "$enteredSets Сетов"
                binding.selectedReps.text = "$enteredReps Повторов"
                elapsedSet = enteredSets.toInt()
                binding.elapsedSetText.text = "$elapsedSet x Осталось сетов"
                bottomSheetDialog.dismiss()
            }
        }

        typeDropDown.setOnItemClickListener { adapterView, view, i, l ->
            selectedType = adapterView.getItemAtPosition(i).toString()
            enteredType = selectedType.toString()
        }

        bottomSheetDialog.show()
    }

    private fun startChronometer() {
        val chronometer = binding.chronometer
        if (!isRunning) {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
            isRunning = true
        } else {
            chronometer.stop()
            isRunning = false
        }
    }

}



