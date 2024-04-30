package ru.nilsolk.gymapp.viewmodel

import android.app.Application
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
        val targetDate = calculateTargetDate(choiceNumber)
        val filteredToDoList = toDoList.filter { (it.createdAt ?: 0) < targetDate }
        statisticLiveData.value = ArrayList(filteredToDoList)
    }


    private fun calculateTargetDate(choiceNumber: String): Long {
        var targetDate = System.currentTimeMillis()
        when (choiceNumber) {
            "1" -> {
                targetDate -= (7L * 24 * 60 * 60 * 1000)
            }

            "2" -> {
                targetDate -= (3L * 24 * 60 * 60 * 1000)
            }

            "3" -> {
                targetDate -= (3L * 30.44 * 24 * 60 * 60 * 1000).toLong()
            }

            "4" -> {
                targetDate -= (6L * 30.44 * 24 * 60 * 60 * 1000).toLong()
            }

            "5" -> {
                targetDate -= (365.24 * 24 * 60 * 60 * 1000).toLong()
            }
        }
        return targetDate
    }

    private fun showErrorToastMessage(error: String) {
        Toast.makeText(application.applicationContext, error, Toast.LENGTH_LONG).show()
    }
}
