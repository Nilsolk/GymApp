package ru.nilsolk.gymapp.utils

import com.google.mlkit.nl.translate.TranslateLanguage
import java.util.Locale

object TranslationConstants {
    val mapEnglishToRussianMuscleGroups = mapOf(
        "back" to "спина",
        "chest" to "грудь",
        "cardio" to "кардио",
        "lower arms" to "предпелечья",
        "lower legs" to "икры",
        "shoulders" to "плечи",
        "upper arms" to "руки",
        "upper legs" to "ноги",
        "waist" to "талия"
    )

    val englishEquipmentToRussianMap = mapOf(
        "band" to "петля",
        "body weight" to "собственный вес",
        "assisted" to "асистент",
        "cable" to "трос",
        "hammer" to "молот",
        "olympic barbell" to "олимпийская штанга",
        "ez barbell" to "кривая штанга",
        "dumbbell" to "гантель",
        "barbell" to "штанга",
        "roller" to "роллер",
        "kettlebell" to "гиря",
        "smith machine" to "стойка",
        "bosu ball" to "мяч Босу",
        "elliptical machine" to "эллиптический тренажер",
        "leverage machine" to "рычажный тренажер",
        "medicine ball" to "медицинский мяч",
        "resistance band" to "резиновая лента",
        "rope" to "веревка",
        "skierg machine" to "тренажер для лыжников",
        "sled machine" to "тренажер для тяги",
        "stability ball" to "мяч для стабилизации",
        "stationary bike" to "стационарный велосипед",
        "stepmill machine" to "эспкалатор",
        "tire" to "шина",
        "trap bar" to "ловушечная штанга",
        "weighted" to "весовой",
        "wheel roller" to "валик для пресса"
    )
    val englishMusclesTextMap = mapOf(
        "abductors" to "отводящие мышцы",
        "abs" to "пресс",
        "adductors" to "приводящие мышцы",
        "biceps" to "бицепс",
        "calves" to "икры",
        "delts" to "дельты",
        "forearms" to "предплечья",
        "glutes" to "ягодицы",
        "hamstrings" to "бедра",
        "lats" to "широчайшие мышцы спины",
        "kettlebell" to "гири",
        "pectorals" to "грудные мышцы",
        "quads" to "квадрицепсы",
        "traps" to "трапеции",
        "triceps" to "трицепсы",
        "upper back" to "верхняя часть спины",
        "cardiovascular system" to "кардиоваскулярная система",
        "levator scapulae" to "поднимающие лопатку",
        "serratus anterior" to "передние зубчатые",
        "spine" to "позвоночник"
    )
    const val SOURCE = TranslateLanguage.ENGLISH
    var TARGET: String = Locale.getDefault().language
}