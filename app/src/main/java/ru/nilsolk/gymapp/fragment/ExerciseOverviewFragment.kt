package ru.nilsolk.gymapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.mlkit.nl.translate.TranslateLanguage
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentExerciseOverviewBinding
import ru.nilsolk.gymapp.model.BodyPartExercisesItem
import ru.nilsolk.gymapp.utils.TextTranslator
import ru.nilsolk.gymapp.utils.TranslationCallback
import java.util.Locale

class ExerciseOverviewFragment : Fragment() {

    private lateinit var exerciseItem: BodyPartExercisesItem
    private lateinit var exerciseOverviewBinding: FragmentExerciseOverviewBinding
    private lateinit var fragmentType: String

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        exerciseOverviewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.fragment_exercise_overview,
            container,
            false
        )
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.GONE

        arguments?.let {
            fragmentType = it.getString("fragmentType")!!
            exerciseItem = it.getSerializable("exercise") as BodyPartExercisesItem
            exerciseOverviewBinding.exercise = exerciseItem
            setInstructionsToTextView(exerciseItem.instructions)
        }

        return exerciseOverviewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exerciseOverviewBinding.letsDoItButton.setOnClickListener {
            val action =
                ExerciseOverviewFragmentDirections.actionExerciseOverviewFragmentToExerciseExecutionFragment(
                    exerciseItem,
                    fragmentType
                )
            findNavController().navigate(action)
        }

    }

    private fun setInstructionsToTextView(instructions: List<String>) {
        val stringBuilder = StringBuilder()
        var instructionNumber = 1

        for (instruction in instructions) {
            stringBuilder.append("$instructionNumber. $instruction\n")
            instructionNumber++
        }

        val translator = TextTranslator()
        translator.translate(
            SOURCE,
            TARGET,
            stringBuilder.toString(),
            object : TranslationCallback {
                override fun onTranslationComplete(translatedText: String) {
                    exerciseOverviewBinding.instructionsText.text = translatedText
                }

            })
    }


    companion object {
        private const val SOURCE = TranslateLanguage.ENGLISH
        private var TARGET = Locale.getDefault().language
    }
}
