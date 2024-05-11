package ru.nilsolk.gymapp.translation

import android.content.Context
import android.widget.ArrayAdapter

class RussianToEnglishAdapter(
    context: Context,
    private val russianTextList: List<String>,
    private val englishTextMap: Map<String, String>
) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, russianTextList) {

    override fun getItem(position: Int): String {
        return russianTextList[position]
    }

    override fun getCount(): Int {
        return russianTextList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getEnglishValue(position: Int): String? {
        val russianText = getItem(position)
        return russianText.let {
            englishTextMap[it]
        }
    }
}