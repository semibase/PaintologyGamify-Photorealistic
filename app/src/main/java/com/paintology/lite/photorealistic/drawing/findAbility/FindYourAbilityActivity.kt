package com.paintology.lite.photorealistic.drawing.findAbility

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.challenge.view.ChallengeActivity
import com.paintology.lite.photorealistic.drawing.databinding.ActivityFindYourAbilityBinding
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.videoguide.VideoGuideActivity

class FindYourAbilityActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFindYourAbilityBinding.inflate(layoutInflater)
    }

    private var progressDensity = 60
    private var progressHardness = 30
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
        initSpinner()
        initSeekbars()
        initListeners()

    }

    private fun initListeners() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioVideoGuide -> {
                    openActivity(VideoGuideActivity::class.java)
                }
                R.id.radioChallenges -> {
                    openActivity(ChallengeActivity::class.java)
                }
            }
        }
    }

    private fun initSeekbars() {
        binding.apply {
            tvProgressDensity.text = getString(R.string.percent_value, progressDensity)
            progressBarDensity.progress = progressDensity.toFloat()
            progressBarHardness.progress = progressHardness.toFloat()
        }
    }

    private fun initSpinner() {
        val list = resources.getStringArray(R.array.arr_art_ability1)
        val abilityAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, R.layout.spinner_style, list
        )

        binding.spArtAbility.adapter = abilityAdapter
    }

    private fun initToolbar() {
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = getString(R.string.find_your_ability)
            imgFav.hide()
        }
    }
}