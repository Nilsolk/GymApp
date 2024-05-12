package ru.nilsolk.gymapp.ui.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nilsolk.gymapp.App
import ru.nilsolk.gymapp.repository.db.ProgramStatistic

class WorkoutsDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseDao = App.database.exerciseDao()
    fun insertProgram(programStatistic: ProgramStatistic) {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseDao.deleteProgramStatisticsTable()
            exerciseDao.insertProgramStatistic(programStatistic)
        }
    }

}