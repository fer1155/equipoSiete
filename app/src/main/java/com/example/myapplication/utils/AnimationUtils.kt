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
}