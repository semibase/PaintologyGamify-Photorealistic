package com.paintology.lite.photorealistic.drawing.findAbility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paintology.lite.photorealistic.drawing.R

class ChallengesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.activity_challenge, container, false
        )
    }

}