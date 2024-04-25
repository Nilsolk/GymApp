package ru.nilsolk.gymapp.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.nilsolk.gymapp.model.ExecutionModel
import ru.nilsolk.gymapp.service.FirebaseAuthService
import ru.nilsolk.gymapp.service.FirebaseFirestoreService
import ru.nilsolk.gymapp.utils.StreakManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

class ExecutionViewModel(private val application: Application) : AndroidViewModel(application) {

    private val firestoreService = FirebaseFirestoreService(application.applicationContext)
    private val firebaseAuthService = FirebaseAuthService(application.applicationContext)
    private val currentEmail = firebaseAuthService.getCurrentUserEmail()
    private val reference = firestoreService.firestore.collection("toDoRecyclerViewItems").document(currentEmail)

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveWorkout(execution: ExecutionModel, result: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            reference.collection("toDoRecyclerViewItems").add(workoutHash(execution)).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkStreak()
                    result(true, "Exercise Saved Successfully!")
                } else result(false, "An error occurred while recording the exercise!")
            }.addOnFailureListener {
                result(false, "An error occurred while recording the exercise!")
            }
        }
    }

    private fun checkStreak() {
        viewModelScope.launch {
            StreakManager.updateStreak()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun workoutHash(execution: ExecutionModel) : HashMap<String, Any> {
        val uuid = UUID.randomUUID().toString().substring(0, 12)
        val currentTimeStamp = System.currentTimeMillis()
        val selectedDate = execution.date
        val selectedDay = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val todoText = "${execution.exerciseName}, ${execution.weight} ${execution.type}\n${execution.set}SET x ${execution.rep}REP"

        return hashMapOf(
            "selectedDay" to selectedDay,
            "selectedDate" to selectedDate,
            "todoText" to todoText,
            "todoId" to uuid,
            "createdAt" to currentTimeStamp
        )
    }

}