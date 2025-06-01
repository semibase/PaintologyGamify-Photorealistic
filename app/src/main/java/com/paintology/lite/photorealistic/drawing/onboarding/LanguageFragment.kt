package com.paintology.lite.photorealistic.drawing.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.paintology.lite.photorealistic.drawing.BuildConfig
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.FragmentLanguageBinding
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils
import com.paintology.lite.photorealistic.drawing.util.StringConstants


class LanguageFragment : Fragment(), SingleRecyclerViewAdapter.SingleClickListener {

    private var binding: FragmentLanguageBinding? = null
    private var mAdapter: SingleRecyclerViewAdapter? = null
    private var constants: StringConstants? = StringConstants()
    private var isLangSelected: Boolean = false
    private var selectedLanguage: Int = 0
    var languageContainer: CardView? = null

    companion object {
        fun newInstance(bundle: Bundle): LanguageFragment {
            val fragment = LanguageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLanguageBinding.inflate(inflater, container, false)

        languageContainer = binding!!.languageContainer

        if (constants!!.getString(constants!!.isLanguageSelected, requireContext())
                .equals("true", ignoreCase = true)
        ) {
            isLangSelected = true
        }

        if (isLangSelected) {
            getSelectedLanguage()
        }

        val list = resources.getStringArray(R.array.languages)
        val listFlags = arrayOf(
            R.drawable.flag_uk,
            R.drawable.flag_indian,
            R.drawable.flag_bangladesh,
            R.drawable.flag_pakistan,
            R.drawable.flag_saudi_arabia,
            R.drawable.flag_phillipines,
            R.drawable.flag_china,
            R.drawable.flag_spanish,
            R.drawable.flag_france,
            R.drawable.flag_portugese

        )

        mAdapter = SingleRecyclerViewAdapter(list, listFlags, selectedLanguage)

        binding?.rvLanguage?.adapter = mAdapter
        binding?.rvLanguage?.setHasFixedSize(true)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        val itemDecoration: ItemDecoration = DividerItemDecoration(activity, 1)
        binding?.rvLanguage?.addItemDecoration(itemDecoration)
        binding?.rvLanguage?.layoutManager = layoutManager
        mAdapter!!.setOnItemClickListener(this)


        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val elevation = requireArguments().getFloat("elevation")
        val hideHeading = requireArguments().getBoolean("hide_heading")

        binding?.tvTitle?.visibility = if (hideHeading) View.GONE else View.VISIBLE

        updateCardElevation(elevation)
    }

    private fun getSelectedLanguage() {

        when (constants!!.getString(constants!!.selected_language, requireContext())) {
            "en" -> selectedLanguage = 0
            "hi" -> selectedLanguage = 1
            "bn" -> selectedLanguage = 2
            "ur" -> selectedLanguage = 3
            "ar" -> selectedLanguage = 4
            "tl" -> selectedLanguage = 5
            "zh" -> selectedLanguage = 6
            "es" -> selectedLanguage = 7
            "fr" -> selectedLanguage = 8
            "pt" -> selectedLanguage = 9
        }
    }

    override fun onItemClickListener(position: Int, view: View?) {
        if (context == null) {
            return
        }
        mAdapter?.selectedItem(position)

        var msg = ""

        when (position) {
            0 -> {
                constants!!.putString(constants!!.selected_language, "en", requireContext())
                msg = constants!!.lang_select_start_ + "english"
            }

            1 -> {
                constants!!.putString(constants!!.selected_language, "hi", requireContext())
                msg = constants!!.lang_select_start_ + "hindi"
            }

            2 -> {
                constants!!.putString(constants!!.selected_language, "bn", requireContext())
                msg = constants!!.lang_select_start_ + "bangla"
            }

            3 -> {
                constants!!.putString(constants!!.selected_language, "ur", requireContext())
                msg = constants!!.lang_select_start_ + "urdu"
            }

            5 -> {
                constants!!.putString(constants!!.selected_language, "tl", requireContext())
                msg = constants!!.lang_select_start_ + "filipino"
            }

            4 -> {
                constants!!.putString(constants!!.selected_language, "ar", requireContext())
                msg = constants!!.lang_select_start_ + "egypt"
            }

            6 -> {
                constants!!.putString(constants!!.selected_language, "zh", requireContext())
                msg = constants!!.lang_select_start_ + "chinese"
            }

            7 -> {
                constants!!.putString(constants!!.selected_language, "es", requireContext())
                msg = constants!!.lang_select_start_ + "spanish"
            }

            8 -> {
                constants!!.putString(constants!!.selected_language, "fr", requireContext())
                msg = constants!!.lang_select_start_ + "french"
            }

            9 -> {
                constants!!.putString(constants!!.selected_language, "pt", requireContext())
                msg = constants!!.lang_select_start_ + "portuguese"
            }
        }

        if (BuildConfig.DEBUG) {
            Toast.makeText(
                requireContext(),
                msg,
                Toast.LENGTH_SHORT
            ).show()
        }
        FirebaseUtils.logEvents(requireContext(), msg)

        constants?.putString(constants!!.isLanguageSelected, "true", requireContext())

    }

    private fun updateCardElevation(elevation: Float) {
        if (languageContainer != null) {
            languageContainer?.elevation = elevation
        }
    }

}