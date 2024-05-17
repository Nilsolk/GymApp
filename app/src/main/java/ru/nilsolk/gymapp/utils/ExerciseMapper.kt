package ru.nilsolk.gymapp.utils

import ru.nilsolk.gymapp.repository.db.Exercise
import ru.nilsolk.gymapp.repository.db.ExerciseDone
import ru.nilsolk.gymapp.repository.model.BodyPartExercisesItem

object ExerciseMapper {
    fun mapToExercise(
        bodyPartItem: BodyPartExercisesItem,
        programName: String,
        sets: String,
        reps: String
    ): Exercise {
        return Exercise(
            id = bodyPartItem.id,
            name = bodyPartItem.name,
            gifUrl = bodyPartItem.gifUrl,
            bodyPart = bodyPartItem.bodyPart,
            equipment = bodyPartItem.equipment,
            instructions = bodyPartItem.instructions,
            secondaryMuscles = bodyPartItem.secondaryMuscles,
            target = bodyPartItem.target,
            programName = programName,
            sets = sets,
            reps = reps
        )
    }

    fun mapToBodyPartExercisesItem(exercise: Exercise): BodyPartExercisesItem {
        return BodyPartExercisesItem(
            id = exercise.id,
            name = exercise.name,
            gifUrl = exercise.gifUrl,
            bodyPart = exercise.bodyPart,
            equipment = exercise.equipment,
            instructions = exercise.instructions,
            secondaryMuscles = exercise.secondaryMuscles,
            target = exercise.target
        )
    }

    fun mapToBodyPartExercisesItem(exercise: ExerciseDone): BodyPartExercisesItem {
        return BodyPartExercisesItem(
            id = exercise.id,
            name = exercise.name,
            gifUrl = exercise.gifUrl,
            bodyPart = exercise.bodyPart,
            equipment = exercise.equipment,
            instructions = exercise.instructions,
            secondaryMuscles = exercise.secondaryMuscles,
            target = exercise.target
        )
    }

    fun mapToExerciseDone(exercise: Exercise): ExerciseDone {
        return ExerciseDone(
            id = exercise.id,
            name = exercise.name,
            gifUrl = exercise.gifUrl,
            bodyPart = exercise.bodyPart,
            equipment = exercise.equipment,
            instructions = exercise.instructions,
            secondaryMuscles = exercise.secondaryMuscles,
            target = exercise.target,
            programName = exercise.programName,
            sets = exercise.sets,
            reps = exercise.reps,
            exerciseId = exercise.id
        )
    }
}