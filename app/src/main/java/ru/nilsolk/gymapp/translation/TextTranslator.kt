package ru.nilsolk.gymapp.translation

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import ru.nilsolk.gymapp.repository.model.BodyPartExercisesItem
import ru.nilsolk.gymapp.repository.model.ToDoModel
import java.util.Locale

class TextTranslator {
    fun translate(
        source: String,
        target: String,
        textToTranslation: String,
        callback: TranslationCallback
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(source)
            .setTargetLanguage(target)
            .build()
        val translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                translator.translate(textToTranslation)
                    .addOnSuccessListener { translatedText ->
                        callback.onTranslationComplete(translatedText)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("TranslationError", "Translation failed: $exception")
                        callback.onTranslationComplete(textToTranslation)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("DownloadModelError", "Failed to download language model: $exception")
                callback.onTranslationComplete(textToTranslation)
            }
    }


    fun translateExercise(
        exercise: BodyPartExercisesItem,
    ): String {
        return if (Locale.getDefault().language == "ru") {
            val name = exercise.name
            val map = getMap(exercise.bodyPart)
            map.getOrDefault(name, name)

        } else {
            exercise.name
        }
    }

    fun translateToDo(toDoModel: ToDoModel): String? {
        return if (Locale.getDefault().language == "ru") {
            val list = toDoModel.todoText?.split(",")
            if (list != null) {
                val map = getMap(toDoModel.muscleGroup)
                val value = map.getOrDefault(list[0], list[0])

                return "$value ${list[1]}"
            } else {
                return "Error"
            }
        } else {
            toDoModel.todoText
        }
    }

    private fun getMap(bodyPart: String): Map<String, String> {
        return when (bodyPart) {
            "waist" -> TranslationConstants.englishWaistBodyExercisesTextMap
            "back" -> TranslationConstants.englishBackExercisesTextMap
            "cardio" -> TranslationConstants.englishCardioExercisesTextMap
            "chest" -> TranslationConstants.englishChestExercisesTextMap
            "lower arms" -> TranslationConstants.englishLowerArmsExercisesTextMap
            "lower legs" -> TranslationConstants.englishLowerLegsBodyExercisesTextMap
            "upper arms" -> TranslationConstants.englishUpperArmsBodyExercisesTextMap
            "upper legs" -> TranslationConstants.englishUpperLegsBodyExercisesTextMap
            "shoulders" -> TranslationConstants.englishShouldersBodyExercisesTextMap
            else -> mapOf()
        }

    }
}

interface TranslationCallback {
    fun onTranslationComplete(translatedText: String)
}