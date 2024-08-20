package com.elifnuroksuz.quiz

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlin.random.Random

class WheelFragment : Fragment(R.layout.fragment_wheel) {
    private var currentRotation = 0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wheel = view.findViewById<ImageView>(R.id.wheel)
        val button = view.findViewById<Button>(R.id.button)
        val arrow = view.findViewById<ImageView>(R.id.arrow)
        val sectionTextView = view.findViewById<TextView>(R.id.sectionTextView)

        // Ensure the arrow is positioned correctly and doesn't rotate
        arrow.rotation = 0f

        button.setOnClickListener {
            val randomAngle = Random.nextFloat() * 360f
            val endRotation = currentRotation + 360f + randomAngle

            val animator = ObjectAnimator.ofFloat(wheel, "rotation", currentRotation, endRotation)
            animator.duration = 1000
            animator.interpolator = LinearInterpolator()
            animator.start()

            animator.addUpdateListener {
                currentRotation = wheel.rotation
                updateArrowPosition(sectionTextView, currentRotation)
            }

            animator.addListener(onEnd = {
                Handler(Looper.getMainLooper()).postDelayed({
                    val section = getSectionFromRotation(currentRotation)
                    val bundle = Bundle().apply {
                        putString("selected_section", section)
                    }
                    findNavController().navigate(R.id.action_wheelFragment_to_questionFragment, bundle)
                }, 3000)
            })
        }
    }

    private fun updateArrowPosition(sectionTextView: TextView, rotation: Float) {
        val normalizedRotation = (rotation % 360 + 360) % 360

        val section = when {
            normalizedRotation in 0.0..119.99 -> "Kitap"
            normalizedRotation in 120.0..239.99 -> "Ülke"
            else -> "Başkent"
        }

        sectionTextView.text = "Current Section: $section"
    }

    private fun getSectionFromRotation(rotation: Float): String {
        val normalizedRotation = (rotation % 360 + 360) % 360

        return when {
            normalizedRotation in 0.0..89.99 -> "Kitap"
            normalizedRotation in 90.0..179.99 -> "Ülke"
            else -> "Başkent"
        }
    }
}
