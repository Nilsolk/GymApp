package ru.nilsolk.gymapp.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.model.ToDoModel
import ru.nilsolk.gymapp.service.FirebaseAuthService
import ru.nilsolk.gymapp.service.FirebaseFirestoreService

class StatisticViewModel(private val application: Application) : AndroidViewModel(application) {
    private val authService = FirebaseAuthService(application.applicationContext)
    private val firestoreService = FirebaseFirestoreService(application.applicationContext)
    val statisticLiveData = MutableLiveData<ArrayList<ToDoModel>?>()
    private var toDoList = ArrayList<ToDoModel>()
    fun getAllExercises() {
        val currentEmail = authService.getCurrentUserEmail()
        toDoList = ArrayList()
        firestoreService.firestore.collection("toDoRecyclerViewItems").document(currentEmail)
            .collection("toDoRecyclerViewItems").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val selectedDay = document.getString("selectedDay") ?: ""
                        val selectedDate = document.getString("selectedDate") ?: ""
                        val todoText = document.getString("todoText") ?: ""
                        val todoId = document.getString("todoId") ?: ""
                        val createdAt = document.getLong("createdAt")
                        val muscleGroup = document.getString("muscleGroup") ?: "Активность"
                        val toDoItem =
                            ToDoModel(
                                selectedDay,
                                selectedDate,
                                todoText,
                                todoId,
                                createdAt,
                                muscleGroup
                            )
                        toDoList.add(toDoItem)
                    }

                    statisticLiveData.value = toDoList
                } else {
                    statisticLiveData.value = null
                }
            }.addOnFailureListener {
                statisticLiveData.value = null
                showErrorToastMessage(it.message.toString())
            }
    }

    fun updateRecyclerViewByGoal(choiceNumber: String) {
        Log.d("StatisticViewModel", "Selected goal: $choiceNumber")
        val currentTimeMillis = System.currentTimeMillis()
        val millisecondsInDay: Long = 24 * 60 * 60 * 1000L
        val filteredToDoList = when (choiceNumber) {
            "1" -> toDoList.filter {
                (currentTimeMillis - (it.createdAt ?: 0)) <= 3 * millisecondsInDay
            }

            "2" -> toDoList.filter {
                (currentTimeMillis - (it.createdAt ?: 0)) <= 7 * millisecondsInDay
            }

            "3" -> toDoList.filter {
                (currentTimeMillis - (it.createdAt ?: 0)) <= 30 * millisecondsInDay
            }

            "4" -> toDoList.filter {
                (currentTimeMillis - (it.createdAt ?: 0)) <= 6 * 30 * millisecondsInDay
            }

            "5" -> toDoList.filter {
                (currentTimeMillis - (it.createdAt ?: 0)) <= 365 * millisecondsInDay
            }

            else -> toDoList
        }
        statisticLiveData.value = ArrayList(filteredToDoList)
    }


    private fun showErrorToastMessage(error: String) {
        Toast.makeText(application.applicationContext, error, Toast.LENGTH_LONG).show()
    }
}
