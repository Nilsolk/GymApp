package ru.nilsolk.gymapp.ui.workout

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.nilsolk.gymapp.App
import ru.nilsolk.gymapp.repository.db.ProgramStatistic
import ru.nilsolk.gymapp.repository.model.ProfileDetailModel
import ru.nilsolk.gymapp.repository.network.FirebaseAuthService
import ru.nilsolk.gymapp.repository.network.FirebaseFirestoreService

class WorkoutsDetailViewModel(private val application: Application) :
    AndroidViewModel(application) {
    private val exerciseDao = App.database.exerciseDao()
    private val authService = FirebaseAuthService(application.applicationContext)
    private val firestoreService = FirebaseFirestoreService(application.applicationContext)
    val profileDetails = MutableLiveData<ProfileDetailModel?>()
    fun insertProgram(programStatistic: ProgramStatistic) {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseDao.deleteProgramStatisticsTable()
            exerciseDao.deleteDailyProgramStatisticsTable()
            exerciseDao.deleteExercisesProgramStatisticsTable()
            exerciseDao.insertProgramStatistic(programStatistic)
        }
    }

    fun getProfileDetail() {
        val currentEmail = authService.getCurrentUserEmail()
        firestoreService.firestore.collection("profileDetail").document(currentEmail).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val data = task.result?.data
                    val profileDetail = if (data != null) {
                        ProfileDetailModel(
                            username = data["username"].toString(),
                            email = data["email"].toString(),
                            gender = data["gender"].toString(),
                            profileImageURL = data["profileImageURL"].toString(),
                            age = data["age"].toString(),
                            height = data["height"].toString().toIntOrNull() ?: 0,
                            weight = data["weight"].toString().toDoubleOrNull() ?: 0.0,
                            targetWeight = data["targetWeight"].toString().toDoubleOrNull() ?: 0.0,
                            goal = data["goal"].toString(),
                            activityLevel = data["levelActivity"].toString()
                        )
                    } else null
                    profileDetails.value = profileDetail
                } else {
                    profileDetails.value = null
                }
            }.addOnFailureListener {
                profileDetails.value = null
                showErrorToastMessage(it.message.toString())
            }
    }

    private fun showErrorToastMessage(error: String) {
        Toast.makeText(application.applicationContext, error, Toast.LENGTH_LONG).show()
    }

}