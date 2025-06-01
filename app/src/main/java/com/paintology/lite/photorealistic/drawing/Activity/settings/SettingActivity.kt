package com.paintology.lite.photorealistic.drawing.Activity.settings

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paintology.lite.photorealistic.drawing.Activity.notifications.ui.activities.NotificationActivity
import com.paintology.lite.photorealistic.drawing.Activity.support.SupportActivity
import com.paintology.lite.photorealistic.drawing.Activity.utils.hide
import com.paintology.lite.photorealistic.drawing.Activity.utils.onSingleClick
import com.paintology.lite.photorealistic.drawing.Activity.utils.openActivity
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ActivitySettingBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBottomSheetAboutBinding
import com.paintology.lite.photorealistic.drawing.databinding.LayoutBottomSheetGeneralBinding
import com.paintology.lite.photorealistic.drawing.policy.PrivacyPolicyActivity
import com.paintology.lite.photorealistic.drawing.util.showStoreDialog

class SettingActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
        initListeners()
    }

    private fun initListeners() {
        binding.apply {
            cardNotifications.onSingleClick {
                openBottomSheetPP("cardNotifications")
            }
            cardGeneral.onSingleClick {
                openBottomSheetPP("cardGeneral")
            }
            cardAccessibility.onSingleClick {
                openBottomSheetPP("cardAccessibility")
            }
            cardSecurity.onSingleClick {
                openBottomSheetPP("cardSecurity")
            }
            cardHelpCenter.onSingleClick {
                openBottomSheetPP("cardHelpCenter")
            }
            cardAboutPaintology.onSingleClick {
                openBottomSheetAbout()
            }
            cardStore.onSingleClick {
                openBottomSheetPP("cardStore")
            }
            cardPP.onSingleClick {
                openBottomSheetPP("cardPP")
            }
        }
    }

    private fun openBottomSheetPP(name: String) {
        val bottomSheetLevels = BottomSheetDialog(this)
        val bottomSheetBehavior: BottomSheetBehavior<View>?

        val dialogBinding: LayoutBottomSheetGeneralBinding = LayoutBottomSheetGeneralBinding.inflate(layoutInflater)
        bottomSheetLevels.setContentView(dialogBinding.root)
        bottomSheetBehavior = BottomSheetBehavior.from(dialogBinding.bottomSheet.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false

        val layout: CoordinatorLayout? = bottomSheetLevels.findViewById(R.id.bottomSheet)
        val layoutParams = layout?.layoutParams
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val desiredHeight = (screenHeight * 0.6).toInt()
        // Set the height of the bottom sheet
        layoutParams?.height = desiredHeight// Apply the new layout parameters
        layout?.layoutParams = layoutParams

        dialogBinding.apply {
            when (name) {
                "cardPP" -> {
                    cancel.text = getString(R.string.close)
                    cancel.onSingleClick { bottomSheetLevels.dismiss() }
                    text4.onSingleClick { openUrl("https://paintology.com") }
                    text6.onSingleClick { openUrl("https://play.google.com/store/apps/developer?id=Paintology") }
                    text14.onSingleClick { openUrl("https://www.facebook.com/msqrd/privacy") }
                    text16.onSingleClick { openUrl("https://policies.google.com/privacy") }
                    text18.onSingleClick { openUrl("https://policies.google.com/technologies/ads") }
                    text20.onSingleClick { openUrl("https://firebase.google.com/support/privacy") }
                    text22.onSingleClick { openUrl("https://www.youtube.com/intl/us/about/policies/#community-guidelines") }
                    text25.onSingleClick { openUrl("https://policies.google.com/privacy") }
                    text35.onSingleClick { openGmail("support@paintology.com") }
                }

                "cardSecurity" -> {
                    cancel.text = getString(R.string.close)
                    cancel.onSingleClick { bottomSheetLevels.dismiss() }
                    hideViews(dialogBinding)
                    text1.setTextColor(resources.getColor(R.color.gray_color))
                    text1.text = getString(R.string.security_data)
                    nameTV.text = getString(R.string.security)
                    shapeableImageView.setImageResource(R.drawable.img_security)
                }

                "cardHelpCenter" -> {
                    cancel.onSingleClick {
                        bottomSheetLevels.dismiss()
                        openActivity(SupportActivity::class.java)
                    }
                    hideViews(dialogBinding)
                    text1.setTextColor(resources.getColor(R.color.gray_color))
                    text1.text = getString(R.string.help_center_data)
                    nameTV.text = getString(R.string.help_center)
                    shapeableImageView.setImageResource(R.drawable.img_help_center)
                }

                "cardAboutPaintology" -> {
                    hideViews(dialogBinding)
                    text1.setTextColor(resources.getColor(R.color.gray_color))
                    text1.text = getString(R.string.about_paintology_data)
                    nameTV.text = getString(R.string.about_paintology)
                    shapeableImageView.setImageResource(R.drawable.img_about_paintology)
                    cancel.onSingleClick {
                        bottomSheetLevels.dismiss()
                        openActivity(SupportActivity::class.java)
                    }
                }

                "cardStore" -> {
                    cancel.onSingleClick {
                        bottomSheetLevels.dismiss()
                        showStoreDialog(this@SettingActivity)
                    }
                    hideViews(dialogBinding)
                    text1.setTextColor(resources.getColor(R.color.gray_color))
                    text1.text = getString(R.string.store_data)
                    nameTV.text = getString(R.string.store)
                    shapeableImageView.setImageResource(R.drawable.ic_store)
                }

                "cardGeneral" -> {
                    cancel.text = getString(R.string.close)
                    cancel.onSingleClick { bottomSheetLevels.dismiss() }
                    hideViews(dialogBinding)
                    text1.setTextColor(resources.getColor(R.color.gray_color))
                    text1.text = getString(R.string.general_data)
                    nameTV.text = getString(R.string.general)
                    shapeableImageView.setImageResource(R.drawable.img_generals)
                }

                "cardAccessibility" -> {
                    cancel.text = getString(R.string.close)
                    cancel.onSingleClick { bottomSheetLevels.dismiss() }
                    hideViews(dialogBinding)
                    text1.setTextColor(resources.getColor(R.color.gray_color))
                    text1.text = getString(R.string.accessibility_data)
                    nameTV.text = getString(R.string.accessibility)
                    shapeableImageView.setImageResource(R.drawable.img_accesiblity)
                }

                "cardNotifications" -> {
                    text1.setTextColor(resources.getColor(R.color.gray_color))
                    text1.text = getString(R.string.noti_data)
                    nameTV.text = getString(R.string.notifications)
                    shapeableImageView.setImageResource(R.drawable.img_notifications)
                    hideViews(dialogBinding)
                    cancel.onSingleClick {
                        bottomSheetLevels.dismiss()
                        openActivity(NotificationActivity::class.java)
                    }
                }
            }
        }

        bottomSheetLevels.show()
    }

    private fun openBottomSheetAbout() {
        val bottomSheetLevels = BottomSheetDialog(this)
        val bottomSheetBehavior: BottomSheetBehavior<View>?

        val dialogBinding: LayoutBottomSheetAboutBinding = LayoutBottomSheetAboutBinding.inflate(layoutInflater)
        bottomSheetLevels.setContentView(dialogBinding.root)
        bottomSheetBehavior = BottomSheetBehavior.from(dialogBinding.bottomSheet.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false

        val layout: CoordinatorLayout? = bottomSheetLevels.findViewById(R.id.bottomSheet)
        val layoutParams = layout?.layoutParams
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val desiredHeight = (screenHeight * 0.6).toInt()
        // Set the height of the bottom sheet
        layoutParams?.height = desiredHeight// Apply the new layout parameters
        layout?.layoutParams = layoutParams

        dialogBinding.apply {
            text25.onSingleClick { openUrl("https://www.facebook.com/Paintology.apps") }
            text27.onSingleClick { openUrl("https://www.tiktok.com/@paintology3") }
            text29.onSingleClick { openUrl("https://www.instagram.com/paintology.app/") }
            text30.onSingleClick { openUrl("https://www.instagram.com/ferdousekhal/") }
            text32.onSingleClick { openUrl("https://www.ferdouse.com/") }
            text33.onSingleClick { openUrl("https://medium.com/@ferdousekhaleque") }
            text34.onSingleClick { openUrl("https://www.patreon.com/paintology") }
            text35.onSingleClick { openUrl("https://twitter.com/Semibase") }
            text36.onSingleClick { openUrl("https://www.reddit.com/user/FerdouseK") }
            text41.onSingleClick { openUrl("https://www.pinterest.com/FerdouseKhaleque/") }
            text42.onSingleClick { openUrl("https://www.pinterest.com/Paintology") }
            text29.onSingleClick { openUrl("https://www.instagram.com/paintology.app/") }
            text30.onSingleClick { openUrl("https://www.instagram.com/ferdousekhal/") }
            text4.onSingleClick { openUrl("https://play.google.com/store/apps/details?id=com.paintology.lite") }
            text5.onSingleClick { openUrl("https://play.google.com/store/apps/details?id=com.paintology.recorder") }
            text7.onSingleClick { openUrl("https://www.paintology.com") }
            text9.onSingleClick { openUrl("https://www.youtube.com/@Paintology") }
            text10.onSingleClick { openUrl("https://www.youtube.com/@Ferdouse") }
            text11.onSingleClick { openUrl("https://www.youtube.com/@Paintology/streams") }
            text12.onSingleClick { openUrl("https://www.youtube.com/@Paintology/shorts") }
            text14.onSingleClick { openUrl("https://paintology.quora.com") }
            text15.onSingleClick { openUrl("https://www.quora.com/profile/Ferdouse-Khaleque") }
            text17.onSingleClick { openUrl("https://allpoetry.com/Ferdouse") }
            text19.onSingleClick { openUrl("https://ferdouse.com") }
            text21.onSingleClick { openUrl("https://forms.gle/ozsKJGYPZ9X8F5YX8") }
            text23.onSingleClick { openUrl("https://forms.gle/ozsKJGYPZ9X8F5YX8") }
            cancel.onSingleClick {
                bottomSheetLevels.dismiss()
                val intent = Intent(this@SettingActivity, PrivacyPolicyActivity::class.java)
                intent.putExtra("value", "Paintology")
                startActivity(intent)
            }
        }

        bottomSheetLevels.show()
    }

    private fun hideViews(dialogBinding: LayoutBottomSheetGeneralBinding) {
        dialogBinding.apply {
            text2.hide()
            text2.hide()
            text3.hide()
            text4.hide()
            text5.hide()
            text6.hide()
            text7.hide()
            text8.hide()
            text9.hide()
            text10.hide()
            text11.hide()
            text12.hide()
            text13.hide()
            text14.hide()
            text15.hide()
            text16.hide()
            text17.hide()
            text18.hide()
            text19.hide()
            text20.hide()
            text21.hide()
            text22.hide()
            text23.hide()
            text24.hide()
            text25.hide()
            text26.hide()
            text27.hide()
            text28.hide()
            text29.hide()
            text30.hide()
            text31.hide()
            text32.hide()
            text33.hide()
            text34.hide()
            text35.hide()
            text36.hide()
            text37.hide()
            text38.hide()
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun openGmail(to: String) {
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:")

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here...")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email Body...")
        emailIntent.selector = selectorIntent

        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun initToolbar() {
        binding.customToolbar.apply {
            imgMenu.onSingleClick {
                onBackPressedDispatcher.onBackPressed()
            }
            tvTitle.text = getString(R.string.settings)
            imgFav.hide()
        }
    }
}