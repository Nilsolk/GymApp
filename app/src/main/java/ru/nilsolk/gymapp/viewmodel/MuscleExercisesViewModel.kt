package ru.nilsolk.gymapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.nilsolk.gymapp.model.BodyPartExercises
import ru.nilsolk.gymapp.service.MusclesAPIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MuscleExercisesViewModel : ViewModel() {

    private val musclesAPIService = MusclesAPIService()
    private val disposable = CompositeDisposable()
    val bodyPartExercises = MutableLiveData<BodyPartExercises?>()

    fun getExercises(bodyPart: String) {

        disposable.add(
            musclesAPIService.getExercises(bodyPart)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<BodyPartExercises>() {
                    override fun onSuccess(t: BodyPartExercises) {
                        bodyPartExercises.value = t
                    }

                    override fun onError(e: Throwable) {
                        bodyPartExercises.value = null
                    }
                })
        )

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}