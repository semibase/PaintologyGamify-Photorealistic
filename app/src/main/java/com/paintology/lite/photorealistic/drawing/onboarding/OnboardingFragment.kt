package com.paintology.lite.photorealistic.drawing.onboarding

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.paintology.lite.photorealistic.drawing.databinding.FragmentOnboarding1Binding
import com.paintology.lite.photorealistic.drawing.policy.PrivacyPolicyActivity

class OnboardingFragment : Fragment() {

    private var binding: FragmentOnboarding1Binding? = null
    private var title: String? = null
    private var description: String? = null
    private var fromMain: Boolean = false

    //    private var imageResource = 0
    private var imageUrl: String? = null
    private var slidePosition: Int? = 0
    private var lastSlide = false
    private lateinit var tvTitle: AppCompatTextView
    private lateinit var tvDescription: AppCompatTextView

    //    private lateinit var image: LottieAnimationView
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(ARG_PARAM1)
            description = requireArguments().getString(ARG_PARAM2)
            imageUrl = requireArguments().getString(ARG_PARAM3)
            slidePosition = requireArguments().getInt(ARG_PARAM4)
            fromMain = requireArguments().getBoolean(ARG_PARAM5)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnboarding1Binding.inflate(inflater, container, false)

//        val rootLayout: View =
//            inflater.inflate(R.layout.fragment_onboarding1, container, false)
        tvTitle = binding!!.textOnboardingTitle //rootLayout.text_onboarding_title
        tvDescription = binding!!.textOnboardingDescription //rootLayout.text_onboarding_description
        image = binding!!.imageOnboarding //rootLayout.image_onboarding
        tvTitle.text = title

//        if (BuildConfig.DEBUG) {
//            Toast.makeText(
//                requireContext(),
//                "help_slide" + (slidePosition?.plus(1)),
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//        FirebaseUtils.logEvents(requireContext(), "help_slide" + (slidePosition?.plus(1)))

        if (!fromMain && slidePosition == 0) {

            // spanable string begin
            val SpanString = SpannableString(description)

            val teremsAndCondition: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    val intent = Intent(
                        activity,
                        PrivacyPolicyActivity::class.java
                    )
                    intent.putExtra("value", "terms")
                    startActivity(intent)
                }
            }

            // Character starting from 32 - 45 is Terms and condition.
            // Character starting from 49 - 63 is privacy policy.


            // Character starting from 32 - 45 is Terms and condition.
            // Character starting from 49 - 63 is privacy policy.
            val privacy: ClickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    val intent = Intent(
                        activity,
                        PrivacyPolicyActivity::class.java
                    )
                    intent.putExtra("value", "privacy")
                    startActivity(intent)
                }
            }

            SpanString.setSpan(teremsAndCondition, 130, 146, 0)
            SpanString.setSpan(privacy, 111, 125, 0)
            SpanString.setSpan(ForegroundColorSpan(Color.BLUE), 130, 146, 0)
            SpanString.setSpan(ForegroundColorSpan(Color.BLUE), 111, 125, 0)
            SpanString.setSpan(UnderlineSpan(), 130, 146, 0)
            SpanString.setSpan(UnderlineSpan(), 111, 125, 0)

            tvDescription.highlightColor = resources.getColor(R.color.transparent)
            tvDescription.movementMethod = LinkMovementMethod.getInstance()
            tvDescription.setText(SpanString, TextView.BufferType.SPANNABLE)
            tvDescription.isSelected = false
        } else {
            tvDescription.text = description
        }
//        image.setAnimation(imageResource)
//                Picasso.get().load(R.drawable.thumbnaildefault).fit().centerCrop().into(myViewHolder.imageView);

        if (!TextUtils.isEmpty(imageUrl)) {
            val extension: String? =
                imageUrl?.lastIndexOf(".")?.let { imageUrl?.substring(it.plus(1)) }

            if (extension.contentEquals("png") ||
                extension.contentEquals("jpg") ||
                extension.contentEquals("jpeg")
            ) {
                activity?.let { Glide.with(it).load(imageUrl).into(image) }
            } else if (extension.contentEquals("gif")) {
                activity?.let { Glide.with(it).asGif().load(imageUrl).into(image) }
            }
        }

//        activity?.let { Glide.with(it).load(imageUrl).into(image) }
        return binding!!.root
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"
        private const val ARG_PARAM4 = "param4"
        private const val ARG_PARAM5 = "param5"
        fun newInstance(
            title: String?,
            description: String?,
//            imageResource: Int
            imageUrl: String?,
            position: Int,
            fromMain: Boolean
        ): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, title)
            args.putString(ARG_PARAM2, description)
            args.putString(ARG_PARAM3, imageUrl)
            args.putInt(ARG_PARAM4, position)
            args.putBoolean(ARG_PARAM5, fromMain)
            fragment.arguments = args
            return fragment
        }
    }
}
