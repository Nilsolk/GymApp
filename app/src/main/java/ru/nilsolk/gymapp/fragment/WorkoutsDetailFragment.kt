package ru.nilsolk.gymapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ru.nilsolk.gymapp.databinding.FragmentWorkoutsDetailBinding
import ru.nilsolk.gymapp.model.PopularWorkoutsModel
import ru.nilsolk.gymapp.utils.AppPreferences
import ru.nilsolk.gymapp.utils.downloadImageFromURL


class WorkoutsDetailFragment : Fragment() {

    private lateinit var binding: FragmentWorkoutsDetailBinding

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
                appPreferences.saveString("programName", model.workoutName)
                appPreferences.saveInt("programDay", 1)
                appPreferences.saveBoolean("isDataLoaded", false)
                val action =
                    WorkoutsDetailFragmentDirections.actionWorkoutsDetailFragmentToChosenProgramFragment()
                requireView().findNavController().navigate(action)
            }
        }
        return binding.root
    }


}