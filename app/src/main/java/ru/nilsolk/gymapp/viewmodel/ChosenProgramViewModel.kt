package ru.nilsolk.gymapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nilsolk.gymapp.db.App
import ru.nilsolk.gymapp.db.Exercise
import ru.nilsolk.gymapp.model.ExerciseProgramModel
import ru.nilsolk.gymapp.service.FirebaseFirestoreService
import ru.nilsolk.gymapp.service.MusclesAPIService

class ChosenProgramViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreService = FirebaseFirestoreService(application.applicationContext)
    private val musclesAPIService = MusclesAPIService()
    private val exerciseDao = App.database.exerciseDao()
    val exercisesResult = MutableLiveData<List<Exercise>?>()
    private var resultProgramModelList = mutableListOf<ExerciseProgramModel>()
    private val disposables = CompositeDisposable()
    var isDataLoaded = false

    fun getProgramExercises(programName: String, programDay: Int) {
        if (!isDataLoaded) {
            Log.d("LoadDataFromNetwork", "")
            getProgramExercisesId { programList ->
                resultProgramModelList = programList.toMutableList()
                val program = resultProgramModelList.firstOrNull { it.programName == programName }
                if (program != null) {
                    val idsList = when (programDay) {
                        1 -> program.firstDayIds.split(" ")
                        2 -> program.secondDayIds.split(" ")
                        3 -> program.thirdDayIds.split(" ")
                        4 -> program.fourthDayIds.split(" ")
                        5 -> program.fifthDayIds.split(" ")
                        6 -> program.sixthDayIds.split(" ")
                        else -> emptyList()
                    }

                    val tempList = mutableListOf<Exercise>()

                    disposables.addAll(
                        *idsList.map { id ->
                            musclesAPIService.getExerciseById(id)
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
                                        newExercise.secondaryMuscles, newExercise.target
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
            isDataLoaded = true
        }
    }

    fun removeExercise(exercise: Exercise) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("BeforeRemoveExercise", exerciseDao.getAllExercises().toString())
            exerciseDao.delete(exercise)
            exercisesResult.postValue(exerciseDao.getAllExercises())
            Log.d("AfterRemoveExercise", exerciseDao.getAllExercises().toString())
        }
    }

    private fun getProgramExercisesId(callback: (List<ExerciseProgramModel>) -> Unit) {
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
                                snapshot.data!!["fourthDayIds"].toString(),
                                snapshot.data!!["fifthDayIds"].toString(),
                                snapshot.data!!["sixthDayIds"].toString()
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
            val exercises = exerciseDao.getAllExercises()
            Log.d("Load data", exercises.toString())
            exercisesResult.value = exercises
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}