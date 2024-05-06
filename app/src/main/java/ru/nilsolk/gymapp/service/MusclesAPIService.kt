package ru.nilsolk.gymapp.service

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.nilsolk.gymapp.db.Exercise
import ru.nilsolk.gymapp.model.BodyPartExercises
import ru.nilsolk.gymapp.model.BodyPartExercisesItem

class MusclesAPIService {

    private val BASE_URL = "https://exercisedb.p.rapidapi.com/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(MusclesAPI::class.java)

    fun getExercises(bodyPart: String, limit: String): Single<BodyPartExercises> {
        return api.getBodyPartExercises(bodyPart, limit)
    }

    fun getExerciseById(id: String): Single<BodyPartExercisesItem> {
        return api.getExerciseById(id)
    }

}