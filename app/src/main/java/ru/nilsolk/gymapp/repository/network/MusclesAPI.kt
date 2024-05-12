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
        "X-RapidAPI-Key: adf412e2e9msh71f83cf292ccb3ap195af4jsn9a870e288d5d",
        "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    )
    @GET("exercises/bodyPart/{bodyPart}")
    fun getBodyPartExercises(
        @Path("bodyPart") bodyPart: String,
        @Query("limit") limit: String
    ): Single<BodyPartExercises>

    @Headers(
        "X-RapidAPI-Key: adf412e2e9msh71f83cf292ccb3ap195af4jsn9a870e288d5d",
        "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    )
    @GET("/exercises/exercise/{id}")
    fun getExerciseById(@Path("id") bodyPart: String): Single<BodyPartExercisesItem>

}