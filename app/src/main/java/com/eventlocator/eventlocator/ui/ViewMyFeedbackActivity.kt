package com.eventlocator.eventlocator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eventlocator.eventlocator.data.Feedback
import com.eventlocator.eventlocator.databinding.ActivityViewMyFeedbackBinding
import java.math.BigDecimal

class ViewMyFeedbackActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewMyFeedbackBinding
    lateinit var feedback: Feedback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMyFeedbackBinding.inflate(layoutInflater)
        feedback = intent.getSerializableExtra("feedback") as Feedback

        binding.tvRating.text = BigDecimal(feedback.rating).setScale(2).toString() + "/5"

        binding.tvFeedback.text = feedback.feedback
    }
}