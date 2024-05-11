package ru.nilsolk.gymapp.ui.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import ru.nilsolk.gymapp.repository.model.BodyPartExercises
import ru.nilsolk.gymapp.repository.model.BodyPartExercisesItem
import ru.nilsolk.gymapp.repository.network.MusclesAPIService

class MuscleExercisesViewModel : ViewModel() {

    private val musclesAPIService = MusclesAPIService()
    private val disposable = CompositeDisposable()
    private val _filteredBodyPartExercises = MutableLiveData<List<BodyPartExercisesItem>?>()
    val bodyPartExercises: LiveData<List<BodyPartExercisesItem>?> = _filteredBodyPartExercises

    private var originalBodyPartExercises: List<BodyPartExercisesItem>? = null

    fun getExercises(bodyPart: String, limit: String) {
        disposable.add(
            musclesAPIService.getExercises(bodyPart, limit)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<BodyPartExercises>() {
                    override fun onSuccess(t: BodyPartExercises) {
                        originalBodyPartExercises = t
                        _filteredBodyPartExercises.value = t
                    }

                    override fun onError(e: Throwable) {
                        _filteredBodyPartExercises.value = null
                    }
                })
        )
    }

    fun updateRecyclerByEquipment(equipment: String?) {
        val updatedList = if (equipment.isNullOrEmpty()) {
            originalBodyPartExercises
        } else {
            originalBodyPartExercises?.filter { it.equipment == equipment }
        }
        _filteredBodyPartExercises.value = updatedList
    }

    fun updateRecyclerByTarget(target: String) {
        val updatedList = if (target.isEmpty()) {
            originalBodyPartExercises
        } else {
            originalBodyPartExercises?.filter { it.target == target }
        }
        _filteredBodyPartExercises.value = updatedList
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
