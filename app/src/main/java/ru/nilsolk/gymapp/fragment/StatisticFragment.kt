package ru.nilsolk.gymapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.chip.Chip
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.adapter.StatisticAdapter
import ru.nilsolk.gymapp.databinding.FragmentStatisticBinding
import ru.nilsolk.gymapp.model.ToDoModel
import ru.nilsolk.gymapp.utils.CustomProgress
import ru.nilsolk.gymapp.viewmodel.StatisticViewModel

class StatisticFragment : Fragment() {

    private lateinit var statisticViewModel: StatisticViewModel
    private lateinit var statistic: ArrayList<ToDoModel>
    private lateinit var statisticAdapter: StatisticAdapter
    private lateinit var fragmentStatisticBinding: FragmentStatisticBinding
    private lateinit var customProgress: CustomProgress
    private var chipsAdded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentStatisticBinding = FragmentStatisticBinding.inflate(inflater, container, false)

        customProgress = CustomProgress(requireActivity())
        customProgress.show()

        statistic = ArrayList()
        statisticAdapter = StatisticAdapter(statistic)
        statisticViewModel = ViewModelProvider(this)[StatisticViewModel::class.java]
        statisticViewModel.getAllExercises()

        observeExercise()
        goalsDropDownSettings()

        return fragmentStatisticBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentStatisticBinding.goBackStatistic.setOnClickListener {
            val action = StatisticFragmentDirections.actionStatisticFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        fragmentStatisticBinding.statisticRecycler.adapter = statisticAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeExercise() {
        statisticViewModel.statisticLiveData.observe(viewLifecycleOwner) { resultStatistic ->
            customProgress.dismiss()
            if (resultStatistic != null) {
                statistic.clear()
                statistic.addAll(resultStatistic)
                statisticAdapter.notifyDataSetChanged()
                val muscleGroups = resultStatistic.map { it.muscleGroup }.distinct()
                if (!chipsAdded) {
                    chips(ArrayList(muscleGroups))
                    chipsAdded = true
                }
            }
        }
    }

    private fun chips(categories: ArrayList<String>) = with(fragmentStatisticBinding) {
        for (chipText in categories) {
            val chip = Chip(requireContext())
            chip.text = chipText
            chip.isCheckable = true
            chip.chipBackgroundColor =
                ContextCompat.getColorStateList(requireContext(), R.color.chip_background)
            chip.setChipStrokeColorResource(R.color.light_gray)
            chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            chipGroup.addView(chip)
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedChip = group.findViewById<Chip>(group.checkedChipId)
            if (selectedChip != null) {
                if (selectedChip.id == R.id.chipAll) {
                    statisticAdapter.setData(statistic)
                    fragmentStatisticBinding.exerciseCount.text = statistic.size.toString()
                } else {
                    val muscleGroup = selectedChip.text.toString()
                    val filteredStatistic = statistic.filter { it.muscleGroup == muscleGroup }
                    statisticAdapter.setData(filteredStatistic)
                    fragmentStatisticBinding.exerciseCount.text = filteredStatistic.size.toString()
                }
            } else {
                chipAll.isChecked = true
            }

        }
    }

    private fun goalsDropDownSettings() {
        val goalItems = listOf(
            getString(R.string.week),
            getString(R.string.three_day),
            getString(R.string.month),
            getString(R.string.six_month),
            getString(R.string.year)
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_items, goalItems)
        fragmentStatisticBinding.goalDropDownStatistic.setAdapter(adapter)

        fragmentStatisticBinding.goalDropDownStatistic.setOnItemClickListener { parent, _, position, _ ->
            val selectedGoal = parent.getItemAtPosition(position).toString()
            statisticViewModel.updateRecyclerViewByGoal(selectedGoal)
        }
    }

}
