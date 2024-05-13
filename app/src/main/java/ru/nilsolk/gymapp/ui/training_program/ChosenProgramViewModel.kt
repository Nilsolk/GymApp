package ru.nilsolk.gymapp.ui.training_program

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nilsolk.gymapp.App
import ru.nilsolk.gymapp.repository.db.Exercise
import ru.nilsolk.gymapp.repository.model.ExerciseProgramModel
import ru.nilsolk.gymapp.repository.network.FirebaseFirestoreService
import ru.nilsolk.gymapp.repository.network.MusclesAPIService
import ru.nilsolk.gymapp.utils.AppPreferences
import java.time.LocalDate

class ChosenProgramViewModel(application: Application) :
    AndroidViewModel(application) {

    private val firestoreService = FirebaseFirestoreService(application.applicationContext)
    private val musclesAPIService = MusclesAPIService()
    private val exerciseDao = App.database.exerciseDao()
    val exercisesResult = MutableLiveData<List<Exercise>?>()
    private var appPreferences: AppPreferences? = null
    private var resultProgramModelList = mutableListOf<ExerciseProgramModel>()
    private val disposables = CompositeDisposable()


    fun setAppPref(appPref: AppPreferences) {
        appPreferences = appPref
    }

    fun getProgramExercises(programName: String, programDay: Int) {
        Log.d("LoadDataFromNetwork", "")
        getProgramExercisesId(programName) { programList ->
            resultProgramModelList = programList.toMutableList()
            val program = resultProgramModelList.firstOrNull { it.programName == programName }
            if (program != null) {
                val idsList = when (programDay) {
                    1 -> program.firstDayIds.split(" ").associate {
                        val (key, value) = it.split(":")
                        key to value.split("/")
                    }

                    2 -> program.secondDayIds.split(" ").associate {
                        val (key, value) = it.split(":")
                        key to value.split("/")
                    }

                    3 -> program.thirdDayIds.split(" ").associate {
                        val (key, value) = it.split(":")
                        key to value.split("/")
                    }

                    else -> mutableMapOf()
                }

                val tempList = mutableListOf<Exercise>()

                disposables.addAll(
                    *idsList.map { map ->
                        Log.d("ID", map.key)
                        musclesAPIService.getExerciseById(map.key)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ newExercise ->
                                val exercise = Exercise(
                                    newExercise.id,
                                    newExercise.name,
                                    newExercise.gifUrl,
                                    newExercise.bodyPart,
                                    newExercise.equipment,
                                    newExercise.instructions,
                                    newExercise.secondaryMuscles,
                                    newExercise.target,
                                    programName,
                                    map.value[0],
                                    map.value[1]
                                )
                                tempList.add(exercise)
                                exercisesResult.value = tempList.toList()
                                viewModelScope.launch {
                                    exerciseDao.insert(exercise)
                                }
                            }, { error ->
                                Log.e(
                                    "ChosenProgramViewModel",
                                    "Error loading exercise: ${error.message}"
                                )
                                exercisesResult.value = null
                            })
                    }.toTypedArray()
                )
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun skipDay(programName: String) {
        viewModelScope.launch {
            val exerciseDay = LocalDate.parse(exerciseDao.getExerciseDate(programName))
            exerciseDao.updateExerciseDate(programName, exerciseDay.plusDays(1L).toString())
            exercisesResult.value = emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNextTrainingDay(programName: String) {
        viewModelScope.launch {
            val exerciseDay = LocalDate.parse(exerciseDao.getExerciseDate(programName))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeExercise(exercise: Exercise, programName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseDao.delete(exercise)
            updateProgressBar(programName)
            Log.d("Data in room", exerciseDao.getAllExercises(programName).toString())
            if (exerciseDao.getAllExercises(programName).isEmpty()) {
                updateProgramDay(programName)
                updateNextTrainingDay(2, programName)
            }
            exercisesResult.postValue(exerciseDao.getAllExercises(programName))
        }
    }

    suspend fun updateProgramDay(programName: String) {
        val day = exerciseDao.getCurrentDay(programName)
        if (day == 3) {
            exerciseDao.updateCurrentDay(programName, 1)
        } else {
            day?.let { exerciseDao.updateCurrentDay(programName, it.plus(1)) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateNextTrainingDay(restDays: Int, programName: String) {
        val date = exerciseDao.getExerciseDate(programName)
        date.let {
            exerciseDao.updateExerciseDate(
                programName,
                LocalDate.parse(it).plusDays(restDays.toLong()).toString()
            )
        }
    }

    suspend fun updateProgressBar(programName: String) {
        val progress = exerciseDao.getProgress(programName)
        exerciseDao.updateProgress(programName, progress = progress?.plus(0.1F) ?: 0f)
    }

    private fun getProgramExercisesId(
        programName: String,
        callback: (List<ExerciseProgramModel>) -> Unit,
    ) {
        viewModelScope.launch {
            val tempList = arrayListOf<ExerciseProgramModel>()
            firestoreService.firestore.collection("popularPrograms").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (snapshot in task.result.documents) {
                            val program = ExerciseProgramModel(
                                snapshot.data!!["programName"].toString(),
                                snapshot.data!!["aboutProgram"].toString(),
                                snapshot.data!!["firstDayIds"].toString(),
                                snapshot.data!!["secondDayIds"].toString(),
                                snapshot.data!!["thirdDayIds"].toString(),
                                snapshot.data!!["instructionsToDo"].toString(),
                                snapshot.data!!["numDays"].toString(),
                                snapshot.data!!["numWeeks"].toString(),
                            )
                            tempList.add(program)
                            Log.d("Load data", tempList.toString())
                        }
                        callback(tempList)
                    } else {
                        Log.e(
                            "ChosenProgramViewModel",
                            "Error getting program exercises: ${task.exception?.message}"
                        )
                    }
                }
            val exercises = exerciseDao.getAllExercises(programName)
            Log.d("Load data from getProgramExercisesId", exercises.toString())
            exercisesResult.value = exercises
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }


}
