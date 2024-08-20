package com.elifnuroksuz.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val _responseLiveData = MutableLiveData<String?>()
    val responseLiveData: LiveData<String?> get() = _responseLiveData

    private val _answerLiveData = MutableLiveData<String?>()
    val answerLiveData: LiveData<String?> get() = _answerLiveData

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.API_KEY // API key from BuildConfig
    )

    fun getGenerativeModelResponse(prompt: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    val result = generativeModel.generateContent(prompt)
                    result.text
                } catch (e: Exception) {
                    "Error: ${e.message}"
                }
            }
            _responseLiveData.value = response
        }
    }

    fun getAnswer(question: String) {
        viewModelScope.launch {
            val answer = withContext(Dispatchers.IO) {
                try {
                    val result = generativeModel.generateContent(question)
                    result.text
                } catch (e: Exception) {
                    "Error: ${e.message}"
                }
            }
            _answerLiveData.value = answer
        }
    }
}
