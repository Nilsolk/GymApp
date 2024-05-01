package ru.nilsolk.gymapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import ru.nilsolk.gymapp.model.BodyPartExercisesItem
import ru.nilsolk.gymapp.model.ExerciseProgramModel
import ru.nilsolk.gymapp.service.FirebaseFirestoreService
import ru.nilsolk.gymapp.service.MusclesAPIService

class ChosenProgramViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreService = FirebaseFirestoreService(application.applicationContext)
    private val musclesAPIService = MusclesAPIService()
    private val disposable = CompositeDisposable()
    val exercisesResult = MutableLiveData<List<BodyPartExercisesItem>?>()
    private var resultProgramModelList = mutableListOf<ExerciseProgramModel>()

    fun getProgramExercises(programName: String, programDay: Int) {
        getProgramExercisesId { programList ->
            resultProgramModelList = programList.toMutableList()
            Log.d("count", resultProgramModelList.size.toString())
            val program = resultProgramModelList.firstOrNull { it.programName == programName }
            Log.d("count", program?.programName ?: "empty")
            Log.d("count", "$programName inner")
            if (program != null) {
                val idsList = when (programDay) {
                    1 -> program.firstDayIds.split(" ")
                    2 -> program.secondDayIds.split(" ")
                    3 -> program.thirdDayIds.split(" ")
                    4 -> program.fourthDayIds.split(" ")
                    5 -> program.fifthDayIds.split(" ")
                    6 -> program.sixthDayIds.split(" ")
                    else -> {
                        emptyList()
                    }
                }
                Log.d("count", idsList.joinToString { it })
                val tempList = mutableListOf<BodyPartExercisesItem>()
                val observables = idsList.map { id ->
                    musclesAPIService.getExerciseById(id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                }

                disposable.addAll(
                    *observables.map { observable ->
                        observable.subscribeWith(object :
                            DisposableSingleObserver<BodyPartExercisesItem>() {
                            override fun onSuccess(t: BodyPartExercisesItem) {
                                tempList.add(t)
                                exercisesResult.value = tempList.toList()
                                Log.d(
                                    "count",
                                    tempList.joinToString { it.id + it.gifUrl + it.name })
                            }

                            override fun onError(e: Throwable) {
                                Log.d(
                                    "count",
                                    e.message.toString()
                                )
                                exercisesResult.value = null
                            }
                        })
                    }.toTypedArray()
                )
            } else {
            }
        }
    }

    private fun getProgramExercisesId(callback: (List<ExerciseProgramModel>) -> Unit) {
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
                    }
                    callback(tempList)
                } else {
                    Log.d("Error", task.exception?.message.toString())
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}