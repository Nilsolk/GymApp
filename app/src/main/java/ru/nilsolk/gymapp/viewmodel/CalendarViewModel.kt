package ru.nilsolk.gymapp.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.nilsolk.gymapp.model.ToDoModel
import ru.nilsolk.gymapp.service.FirebaseAuthService
import ru.nilsolk.gymapp.service.FirebaseFirestoreService

class CalendarViewModel(private val application: Application) : AndroidViewModel(application) {

    val indexExists = MutableLiveData<Boolean>()

    private val firestoreService = FirebaseFirestoreService(application.applicationContext)
    private val firebaseAuthService = FirebaseAuthService(application.applicationContext)
    private val currentEmail = firebaseAuthService.getCurrentUserEmail()
    private val userDocumentReference =
        firestoreService.firestore.collection("toDoRecyclerViewItems").document(currentEmail)

    fun addToDoItem(todoArray: ToDoModel, callback: (Boolean) -> Unit) {
        val currentTimeStamp = System.currentTimeMillis()
        val dataMap = hashMapOf(
            "selectedDay" to todoArray.selectedDay,
            "selectedDate" to todoArray.selectedDate,
            "todoText" to todoArray.todoText,
            "todoId" to todoArray.todoId,
            "createdAt" to currentTimeStamp,
            "programName" to todoArray.programName,
            "muscleGroup" to todoArray.muscleGroup
        )

        userDocumentReference.collection("toDoRecyclerViewItems")
            .add(dataMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }


    fun getToDoItems(callback: (List<ToDoModel>) -> Unit) {
        userDocumentReference.collection("toDoRecyclerViewItems")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val toDoList = mutableListOf<ToDoModel>()

                    for (document in task.result!!) {
                        val selectedDay = document.getString("selectedDay") ?: ""
                        val selectedDate = document.getString("selectedDate") ?: ""
                        val todoText = document.getString("todoText") ?: ""
                        val todoId = document.getString("todoId") ?: ""
                        val createdAt = document.getLong("createdAt")
                        val muscleGroup = document.getString("muscleGroup") ?: ""
                        val programName = document.getString("programName") ?: ""
                        val toDoItem =
                            ToDoModel(
                                selectedDay,
                                selectedDate,
                                todoText,
                                todoId,
                                createdAt,
                                muscleGroup,
                                programName
                            )
                        toDoList.add(toDoItem)
                    }

                    callback(toDoList)
                } else {
                    showErrorToastMessage(task.toString())
                    callback(emptyList())
                }
            }
    }

    fun deleteToDoItem(todoId: String?) {
        if (todoId != null) {
            userDocumentReference.collection("toDoRecyclerViewItems")
                .whereEqualTo("todoId", todoId)
                .get()
                .addOnCompleteListener { querySnapshot ->
                    if (querySnapshot.isSuccessful) {
                        for (document in querySnapshot.result!!) {
                            document.reference.delete()
                        }

                    }
                }
        }
    }

    private fun showErrorToastMessage(error: String) {
        Toast.makeText(application.applicationContext, error, Toast.LENGTH_LONG).show()
    }


}