package ru.nilsolk.gymapp.ui.training_program

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.nilsolk.gymapp.App
import ru.nilsolk.gymapp.databinding.DialogDailyStatisticBinding
import ru.nilsolk.gymapp.repository.db.DailyProgramStatistic
import ru.nilsolk.gymapp.utils.AppPreferences

class DailyStatisticDialog : DialogFragment() {

    private lateinit var binding: DialogDailyStatisticBinding
    private lateinit var statisticAdapter: DailyStatisticAdapter
    private lateinit var statistics: List<DailyProgramStatistic>
    private val exerciseDAO = App.database.exerciseDao()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogDailyStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setTitle("Daily Statistics")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val statsJson = arguments?.getString(ARG_STATISTICS)
        val listType = object : TypeToken<List<DailyProgramStatistic>>() {}.type
        statistics = Gson().fromJson(statsJson, listType)
        statisticAdapter = DailyStatisticAdapter(statistics, exerciseDAO, viewLifecycleOwner)
        binding.progressStats.text =
            AppPreferences(requireContext()).getString("progress", "0") + "%"
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = statisticAdapter
        }
    }

    companion object {
        private const val ARG_STATISTICS = "statistics"

        fun newInstance(statistics: List<DailyProgramStatistic>): DailyStatisticDialog {
            val args = Bundle()
            val statsJson = Gson().toJson(statistics)
            args.putString(ARG_STATISTICS, statsJson)
            val fragment = DailyStatisticDialog()
            fragment.arguments = args
            return fragment
        }
    }
}