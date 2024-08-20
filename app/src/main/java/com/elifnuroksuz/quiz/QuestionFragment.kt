package com.elifnuroksuz.quiz

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

class QuestionFragment : Fragment(R.layout.fragment_question) {

    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get references to the views
        val textViewQuestion = view.findViewById<TextView>(R.id.textview_question)
        val answerButton = view.findViewById<Button>(R.id.answer)
        val trueAnswerTextView = view.findViewById<TextView>(R.id.trueanswer)

        // Get section from arguments
        val selectedSection = arguments?.getString("selected_section") ?: ""

        // Log to verify the value of selectedSection
        Log.d("Selected section: $selectedSection", "QuestionFragment")

        // Define question template based on section
        val questionTemplate = when (selectedSection) {
            "Kitap" -> "sadece bir tane kitap ismi söyle.Dünya edebiyatında herkesin bildiği bir kitap olsun."
            "Ülke" -> "sadece bir tane ülke ismi söyle.Bu ülke isimleri hep farklı olsun."
            "Başkent" -> "sadece bir başkent ismi söyle.Bu başkent isimleri hep farklı olsun."
            else -> "Bu bölüm hakkında bir örnek ver."
        }

        // Fetch the response from the generative model
        viewModel.getGenerativeModelResponse(questionTemplate)

        // LiveData observer for response (question)
        viewModel.responseLiveData.observe(viewLifecycleOwner, Observer { response ->
            val question = when (selectedSection) {
                "Kitap" -> "hangi yazar tarafından yazılmıştır?"
                "Ülke" -> "hangi kıtadadır?"
                "Başkent" -> "hangi ülkenin başkentidir?"
                else -> "Bilinmeyen bölüm"
            }
            // Combine response and question
            val fullQuestion = "$response $question"
            textViewQuestion.text = fullQuestion

            // Request the answer based on the full question
            viewModel.getAnswer(fullQuestion)
        })

        // Observe the answer LiveData
        viewModel.answerLiveData.observe(viewLifecycleOwner, Observer { answer ->
            // Set the answer to the TextView
            trueAnswerTextView.text = answer
            trueAnswerTextView.visibility = View.GONE // Initially hidden
        })

        // Set the OnClickListener for the answer button
        answerButton.setOnClickListener {
            // Show the answer if it has been fetched
            if (trueAnswerTextView.text.isNotEmpty()) {
                trueAnswerTextView.visibility = View.VISIBLE
            } else {
                // Optionally show a message or handle the case where the answer isn't ready
                trueAnswerTextView.text = "Yanıt hazırlanıyor..."
                trueAnswerTextView.visibility = View.VISIBLE
            }
        }
    }
}
