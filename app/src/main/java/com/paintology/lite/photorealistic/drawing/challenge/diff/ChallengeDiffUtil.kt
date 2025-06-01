package com.paintology.lite.photorealistic.drawing.challenge.diff

import androidx.recyclerview.widget.DiffUtil
import com.paintology.lite.photorealistic.drawing.challenge.TutorialChallengeMode

class ChallengeDiffUtil constructor(
    private val oldList: List<TutorialChallengeMode>,
    private val newList: List<TutorialChallengeMode>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].toString() == newList[newItemPosition].toString()
    }
}