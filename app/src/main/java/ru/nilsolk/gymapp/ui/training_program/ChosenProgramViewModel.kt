package ru.nilsolk.gymapp.ui.training_program

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
import ru.nilsolk.gymapp.App
import ru.nilsolk.gymapp.repository.db.Exercise
import ru.nilsolk.gymapp.repository.model.ExerciseProgramModel
import ru.nilsolk.gymapp.repository.network.FirebaseFirestoreService
import ru.nilsolk.gymapp.repository.network.MusclesAPIService

class ChosenProgramViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreService = FirebaseFirestoreService(application.applicationContext)
    private val musclesAPIService = MusclesAPIService()
    private val exerciseDao = App.database.exerciseDao()
    val exercisesResult = MutableLiveData<List<Exercise>?>()
    private var resultProgramModelList = mutableListOf<ExerciseProgramModel>()
    private val disposables = CompositeDisposable()



    fun getProgramExercises(programName: String, programDay: Int) {
        Log.d("LoadDataFromNetwork", "")
        getProgramExercisesId(programName) { programList ->
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
                                    newExercise.secondaryMuscles,
                                    newExercise.target,
                                    programName
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

    fun removeExercise(exercise: Exercise, programName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseDao.delete(exercise)
            Log.d("Data in room", exerciseDao.getAllExercises(programName).toString())
            exercisesResult.postValue(exerciseDao.getAllExercises(programName))
        }
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
