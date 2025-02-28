package com.hasan.eventapp.utils

import android.view.View
import com.hasan.eventapp.databinding.FragmentLoginBinding
import com.hasan.eventapp.databinding.FragmentRegisterBinding

object UiAnimationHelper {

    fun animateLoginScreen(binding: FragmentLoginBinding) {
        val views = listOf(
            AnimationConfig(binding.appLogo, 0f, 1f, 0, 500),
            AnimationConfig(binding.titleText, 0f, 1f, 300, 500),
            AnimationConfig(binding.subtitleText, 0f, 1f, 500, 500),
            AnimationConfig(binding.loginCard, 0f, 1f, 700, 500),
            AnimationConfig(binding.registerContainer, 0f, 1f, 1100, 500)
        )

        views.forEach { config ->
            with(config) {
                view.alpha = startAlpha
                view.animate()
                    .alpha(endAlpha)
                    .setStartDelay(startDelay)
                    .setDuration(duration)
                    .start()
            }
        }
    }


    fun animateRegisterScreen(binding: FragmentRegisterBinding) {
        val views = listOf(
            AnimationConfig(binding.appLogo, 0f, 1f, 0, 500),
            AnimationConfig(binding.titleText, 0f, 1f, 300, 500),
            AnimationConfig(binding.subtitleText, 0f, 1f, 500, 500),
            AnimationConfig(binding.registerCard, 0f, 1f, 700, 500),
            AnimationConfig(binding.loginContainer, 0f, 1f, 1100, 500)
        )

        views.forEach { config ->
            with(config) {
                view.alpha = startAlpha
                view.animate()
                    .alpha(endAlpha)
                    .setStartDelay(startDelay)
                    .setDuration(duration)
                    .start()
            }
        }
    }

    data class AnimationConfig(
        val view: View,
        val startAlpha: Float,
        val endAlpha: Float,
        val startDelay: Long,
        val duration: Long
    )
}