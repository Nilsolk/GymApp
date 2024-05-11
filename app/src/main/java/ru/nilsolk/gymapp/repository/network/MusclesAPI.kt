package ru.nilsolk.gymapp.repository.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.nilsolk.gymapp.repository.model.BodyPartExercises
import ru.nilsolk.gymapp.repository.model.BodyPartExercisesItem

interface MusclesAPI {

    @Headers(
        "X-RapidAPI-Key: 7fec0813f2msh8f893d8b05d8a34p1c4932jsna0768d26404a",
        "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    )
    @GET("exercises/bodyPart/{bodyPart}")
    fun getBodyPartExercises(
        @Path("bodyPart") bodyPart: String,
        @Query("limit") limit: String
    ): Single<BodyPartExercises>

    @Headers(
        "X-RapidAPI-Key: 7fec0813f2msh8f893d8b05d8a34p1c4932jsna0768d26404a",
        "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    )
    @GET("/exercises/exercise/{id}")
    fun getExerciseById(@Path("id") bodyPart: String): Single<BodyPartExercisesItem>

}