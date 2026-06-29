package com.example.myapplication.utils

import android.view.View

object AnimationUtils {

    fun pressAnimation(view: View, action: () -> Unit) {

        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .alpha(0.8f)
            .setDuration(80)
            .withEndAction {

                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(80)
                    .withEndAction {
                        action()
                    }
            }
    }

    fun victoryAnimation(view: View) {

        view.scaleX = 0f
        view.scaleY = 0f
        view.rotation = 0f
        view.alpha = 0f

        view.animate()
            .alpha(1f)
            .scaleX(1.2f)
            .scaleY(1.2f)
            .rotationBy(360f)
            .setDuration(1500)
            .withEndAction {

                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
            }
    }
}