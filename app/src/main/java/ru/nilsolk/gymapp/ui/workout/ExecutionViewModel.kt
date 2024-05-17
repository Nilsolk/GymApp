package ru.nilsolk.gymapp.ui.workout

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nilsolk.gymapp.App
import ru.nilsolk.gymapp.repository.db.DailyProgramStatistic
import ru.nilsolk.gymapp.repository.model.BodyPartExercisesItem
import ru.nilsolk.gymapp.repository.model.ExecutionModel
import ru.nilsolk.gymapp.repository.network.FirebaseAuthService
import ru.nilsolk.gymapp.repository.network.FirebaseFirestoreService
import ru.nilsolk.gymapp.ui.training_program.ChosenProgramFragment
import ru.nilsolk.gymapp.utils.ExerciseMapper
import ru.nilsolk.gymapp.utils.StreakManager
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

class ExecutionViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreService = FirebaseFirestoreService(application.applicationContext)
    private val firebaseAuthService = FirebaseAuthService(application.applicationContext)
    private val currentEmail = firebaseAuthService.getCurrentUserEmail()
    private val exerciseDao = App.database.exerciseDao()
    private val reference =
        firestoreService.firestore.collection("toDoRecyclerViewItems").document(currentEmail)

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveWorkout(execution: ExecutionModel, result: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            reference.collection("toDoRecyclerViewItems").add(workoutHash(execution))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkStreak()
                        result(true, "Exercise Saved Successfully!")
                    } else result(false, "An error occurred while recording the exercise!")
                }.addOnFailureListener {
                    result(false, "An error occurred while recording the exercise!")
                }
        }
    }


    fun saveDailyStatistic(
        item: BodyPartExercisesItem,
        fragmentType: String,
        reps: Int,
        sets: Int,
        listener: ExerciseRemovedListener?,
        weight: Int
    ) {
        if (fragmentType == ChosenProgramFragment::class.java.name) {

            viewModelScope.launch {
                val name = exerciseDao.getProgramName()
                val date = exerciseDao.getExerciseDate(name)
                val programId = exerciseDao.getProgramIdByName(name)
                Log.d("Program id", programId.toString())
                val exercise =
                    ExerciseMapper.mapToExercise(item, name, sets.toString(), reps.toString())
                Log.d("Program id", exercise.id)
                val exerciseDone = ExerciseMapper.mapToExerciseDone(exercise)
                exerciseDao.insertIntoDone(exerciseDone)

                exerciseDao.insertDailyStatistic(
                    DailyProgramStatistic(
                        exerciseDoneId = exerciseDone.id,
                        programStatisticId = programId!!,
                        date = date,
                        completedReps = reps,
                        completedSets = sets,
                        weight = weight
                    )
                )
                listener?.onExerciseRemoved()
            }
        }
    }

    private fun checkStreak() {
        viewModelScope.launch {
            StreakManager.updateStreak()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun workoutHash(execution: ExecutionModel): HashMap<String, Any> {
        val uuid = UUID.randomUUID().toString().substring(0, 12)
        val currentTimeStamp = System.currentTimeMillis()
        val selectedDate = execution.date
        val selectedDay =
            LocalDate.now().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val todoText =
            "${execution.exerciseName}, ${execution.weight} ${execution.type}\n${execution.set}Сетов x ${execution.rep}Повторов"
        val muscleGroup = execution.muscleGroup
        val programName = execution.programName

        return hashMapOf(
            "selectedDay" to selectedDay,
            "selectedDate" to selectedDate,
            "muscleGroup" to muscleGroup,
            "todoText" to todoText,
            "todoId" to uuid,
            "createdAt" to currentTimeStamp,
            "programName" to programName
        )
    }

}