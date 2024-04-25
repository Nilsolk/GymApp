package ru.nilsolk.gymapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.nilsolk.gymapp.R
import ru.nilsolk.gymapp.databinding.ItemBestTrainersBinding
import ru.nilsolk.gymapp.model.BestTrainersModel

class BestTrainersAdapter(private val trainersList: ArrayList<BestTrainersModel>) : RecyclerView.Adapter<BestTrainersAdapter.ItemHolder>() {

    inner class ItemHolder(val itemBestTrainersBinding: ItemBestTrainersBinding) : RecyclerView.ViewHolder(itemBestTrainersBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemBestTrainersBinding>(inflater, R.layout.item_best_trainers, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return trainersList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) = with(holder.itemBestTrainersBinding) {
        trainers = trainersList[position]
    }


}