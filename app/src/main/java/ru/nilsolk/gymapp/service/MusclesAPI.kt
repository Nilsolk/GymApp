package ru.nilsolk.gymapp.service

import ru.nilsolk.gymapp.model.BodyPartExercises
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface MusclesAPI {

    @Headers(
        "X-RapidAPI-Key: 7fec0813f2msh8f893d8b05d8a34p1c4932jsna0768d26404a",
        "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    )
    @GET("exercises/bodyPart/{bodyPart}")
    fun getBodyPartExercises(@Path("bodyPart") bodyPart: String): Single<BodyPartExercises>

}