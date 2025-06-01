package com.paintology.lite.photorealistic.drawing.findAbility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.FragmentLevelBinding

class LevelFragment : Fragment() {

    private lateinit var binding: FragmentLevelBinding

    private var progressDensity = 60
    private var progressHardness = 30

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = resources.getStringArray(R.array.arr_art_ability)
        val abilityAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(), R.layout.spinner_style, list
        )

        binding.spArtAbility.adapter = abilityAdapter

        binding.tvProgressDensity.text = getString(R.string.percent_value, progressDensity)
        binding.tvProgressHardness.text = getString(R.string.percent_value, progressHardness)

        binding.progressBarDensity.progress = progressDensity.toFloat()
        binding.progressBarHardness.progress = progressHardness.toFloat()

        binding.tvPoints.text = getString(R.string.points, 300)

    }


}