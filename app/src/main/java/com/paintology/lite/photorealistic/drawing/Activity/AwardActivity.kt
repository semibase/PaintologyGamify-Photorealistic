package com.paintology.lite.photorealistic.drawing.Activity

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.paintology.lite.photorealistic.drawing.Adapter.AwardAdapter
import com.paintology.lite.photorealistic.drawing.Model.AwardDataModel
import com.paintology.lite.photorealistic.drawing.databinding.ActivityAwardBinding
import com.paintology.lite.photorealistic.drawing.util.FirebaseUtils.context
import com.paintology.lite.photorealistic.drawing.util.SpecingDecoration


class AwardActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAwardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAwardBinding.inflate(layoutInflater)
        val view: ConstraintLayout = binding.root
        setContentView(view)


      binding.backAward.setOnClickListener{
         finish()
        }





        val list=ArrayList<AwardDataModel>()
        list.add(AwardDataModel("Gallery","Do 10 drawings posted to ‘gallery’"))
        list.add(AwardDataModel("Pencil Drawing","Do 5 Tutorials to achive"))
        list.add(AwardDataModel("Gallery","Do 10 drawings posted to ‘gallery’"))
        list.add(AwardDataModel("Pencil Drawing","Do 5 Tutorials to achive"))
        list.add(AwardDataModel("Gallery","Do 10 drawings posted to ‘gallery’"))
        list.add(AwardDataModel("Pencil Drawing","Do 5 Tutorials to achive"))


        val adaptor = AwardAdapter(context, list)
        binding.recyclerViewAward.setAdapter(adaptor)
        binding.recyclerViewAward.setLayoutManager(LinearLayoutManager(context))

        val space = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 150f,
            resources.displayMetrics
        ).toInt()
        val spacingDecoration = SpecingDecoration(0,space)
        binding.recyclerViewAward.addItemDecoration(spacingDecoration)

    }
}