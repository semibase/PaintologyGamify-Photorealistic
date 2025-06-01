package com.paintology.lite.photorealistic.drawing.Activity.country.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.paintology.lite.photorealistic.drawing.Activity.Lists
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.databinding.ItemViewCountryBinding
import com.squareup.picasso.Picasso
import java.util.Locale


class CountryAdapter(
    val context: Context,
    private var mainCountries: List<CountryModel>,
    var listener: CountryClickListener
) : RecyclerView.Adapter<CountryAdapter.CountryHolder>() {

    private var countries: MutableList<CountryModel> = mutableListOf()
    var selected: String = ""

    interface CountryClickListener {
        fun onClick(country: CountryModel, position: Int)
    }


    fun refresh(mainCountries: List<CountryModel>) {
        this.mainCountries = mainCountries
        this.countries.addAll(mainCountries)
        notifyDataSetChanged()
    }

    fun filterList(filter: String?) {
        if (filter == null || filter.trim { it <= ' ' } == "") {
            countries = ArrayList()
            countries.addAll(mainCountries)
        } else {
            countries = ArrayList()
            for (item in mainCountries) {
                if (item.name.lowercase(Locale.getDefault())
                        .contains(filter.lowercase(Locale.getDefault())) || item.code.lowercase(
                        Locale.getDefault()
                    ).contains(filter.lowercase(Locale.getDefault()))
                ) {
                    countries.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryHolder {

        return CountryHolder(
            ItemViewCountryBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CountryHolder, position: Int) {
        val item = countries[position]

        if (item.code == selected) {
            holder.countryBinding.ivCheck.visibility = View.VISIBLE
            holder.countryBinding.cvMain.strokeColor =
                ContextCompat.getColor(context, R.color.colorPrimary)
        } else {
            holder.countryBinding.ivCheck.visibility = View.GONE
            holder.countryBinding.cvMain.strokeColor =
                ContextCompat.getColor(context, android.R.color.transparent)
        }
        Picasso.get().load(Lists.getCountryFlagResource(item.code))
            .placeholder(R.drawable.paintology_logo).into(holder.countryBinding.ivFlag)

        holder.countryBinding.txtTitle.setText(item.name)
        holder.itemView.setOnClickListener { view: View? ->
            val lastPos = selected
            selected = countries[holder.layoutPosition].code
            val pos = countries.indexOfFirst { lastPos == it.code }
            if (pos != -1) {
                notifyItemChanged(pos)
            }
            holder.countryBinding.ivCheck.visibility = View.VISIBLE
            holder.countryBinding.cvMain.strokeColor =
                ContextCompat.getColor(context, R.color.colorPrimary)
            listener.onClick(countries[holder.layoutPosition], holder.layoutPosition)
        }
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    class CountryHolder(val countryBinding: ItemViewCountryBinding) :
        RecyclerView.ViewHolder(countryBinding.getRoot()) {
    }
}
