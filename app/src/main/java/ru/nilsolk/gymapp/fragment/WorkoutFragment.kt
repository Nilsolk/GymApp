package ru.nilsolk.gymapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import ru.nilsolk.gymapp.adapter.BestTrainersAdapter
import ru.nilsolk.gymapp.adapter.MuscleGroupsAdapter
import ru.nilsolk.gymapp.adapter.PopularWorkoutsAdapter
import ru.nilsolk.gymapp.model.BestTrainersModel
import ru.nilsolk.gymapp.model.MuscleGroupModel
import ru.nilsolk.gymapp.model.PopularWorkoutsModel
import ru.nilsolk.gymapp.viewmodel.WorkoutViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.FragmentWorkoutBinding

class WorkoutFragment : Fragment() {

    private lateinit var muscleGroups: ArrayList<MuscleGroupModel>
    private lateinit var bestTrainers: ArrayList<BestTrainersModel>
    private lateinit var popularWorkouts: ArrayList<PopularWorkoutsModel>

    private lateinit var bestTrainersAdapter: BestTrainersAdapter
    private lateinit var muscleGroupsAdapter: MuscleGroupsAdapter
    private lateinit var popularWorkoutsAdapter: PopularWorkoutsAdapter

    private val workoutViewModel: WorkoutViewModel by viewModels()
    private lateinit var fragmentWorkoutBinding: FragmentWorkoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentWorkoutBinding = FragmentWorkoutBinding.inflate(layoutInflater)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.visibility = View.VISIBLE

        muscleGroups = ArrayList()
        bestTrainers = ArrayList()
        popularWorkouts = ArrayList()

        bestTrainersAdapter = BestTrainersAdapter(bestTrainers)
        muscleGroupsAdapter = MuscleGroupsAdapter(muscleGroups)
        popularWorkoutsAdapter = PopularWorkoutsAdapter(popularWorkouts)

        fragmentWorkoutBinding.bestTrainersRecycler.adapter = bestTrainersAdapter
        fragmentWorkoutBinding.muscleGroupsRecycler.adapter = muscleGroupsAdapter
        fragmentWorkoutBinding.popularWorkoutsRecycler.adapter = popularWorkoutsAdapter

        getWorkoutAllData()

        return fragmentWorkoutBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeMuscleGroups() = with(fragmentWorkoutBinding) {
        workoutViewModel.muscleGroupLiveData.observe(viewLifecycleOwner, Observer { liveMuscleGroups ->
            if (liveMuscleGroups != null) {
                muscleGroups.clear()
                muscleGroups.addAll(liveMuscleGroups)
                muscleGroupsRecycler.adapter?.notifyDataSetChanged()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeBestTrainers() = with(fragmentWorkoutBinding) {
        workoutViewModel.bestTrainersLiveData.observe(viewLifecycleOwner, Observer { liveBestTrainers ->
            if (liveBestTrainers != null) {
                bestTrainers.clear()
                bestTrainers.addAll(liveBestTrainers)
                bestTrainersRecycler.adapter?.notifyDataSetChanged()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observePopularWorkouts() = with(fragmentWorkoutBinding) {
        workoutViewModel.popularWorkoutsLiveData.observe(viewLifecycleOwner, Observer { livePopularWorkouts ->
            if (livePopularWorkouts != null) {
                popularWorkouts.clear()
                popularWorkouts.addAll(livePopularWorkouts)
                popularWorkoutsRecycler.adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun getWorkoutAllData() {
        workoutViewModel.getPopularWorkouts()
        observePopularWorkouts()
        workoutViewModel.getBestTrainers()
        observeBestTrainers()
        workoutViewModel.getMuscleGroups()
        observeMuscleGroups()
    }


}