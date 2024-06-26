package ru.nilsolk.gymapp.service

import ru.nilsolk.gymapp.model.BodyPartExercises
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MusclesAPIService {

    private val BASE_URL = "https://exercisedb.p.rapidapi.com/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(MusclesAPI::class.java)

    fun getExercises(bodyPart: String) : Single<BodyPartExercises> {
        return api.getBodyPartExercises(bodyPart)
    }

}