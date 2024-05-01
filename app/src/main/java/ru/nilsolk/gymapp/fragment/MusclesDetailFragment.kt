package ru.nilsolk.gymapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import ru.nilsolk.gymapp.adapter.ExercisesAdapter
import ru.nilsolk.gymapp.model.BodyPartExercises
import ru.nilsolk.gymapp.model.MuscleGroupModel
import ru.nilsolk.gymapp.utils.downloadImageFromURL
import ru.nilsolk.gymapp.viewmodel.MuscleExercisesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentMusclesDetailBinding

class MusclesDetailFragment : Fragment() {

    private lateinit var exercisesAdapter: ExercisesAdapter
    private lateinit var bodyPartExercises: BodyPartExercises
    private lateinit var binding: FragmentMusclesDetailBinding
    private val muscleExercisesViewModel: MuscleExercisesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMusclesDetailBinding.inflate(layoutInflater)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.VISIBLE

        bodyPartExercises = BodyPartExercises()
        exercisesAdapter = ExercisesAdapter(bodyPartExercises)

        arguments.let {

            val muscleName = it!!.getSerializable("muscle") as MuscleGroupModel
            binding.muscleImage.downloadImageFromURL(muscleName.muscleImageURL)
            binding.muscleNameText.text = muscleName.muscleName

            muscleExercisesViewModel.getExercises(muscleName.muscleName.lowercase())
            observeBodyPartExercises()

        }


        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun observeBodyPartExercises() = with(binding) {
        muscleExercisesViewModel.bodyPartExercises.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                bodyPartExercises.clear()
                bodyPartExercises.addAll(result)
                exerciseRecycler.adapter = exercisesAdapter
                exerciseRecycler.adapter?.notifyDataSetChanged()
            }
        }
    }



}