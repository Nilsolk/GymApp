package ru.nilsolk.gymapp.translation

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

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
}

interface TranslationCallback {
    fun onTranslationComplete(translatedText: String)
}