package ru.nilsolk.gymapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.nilsolk.gymapp.databinding.ItemsArticleBinding
import ru.nilsolk.gymapp.fragment.HomeFragmentDirections
import ru.nilsolk.gymapp.model.ArticleModel

class ArticleAdapter(private var articles: ArrayList<ArticleModel>) : RecyclerView.Adapter<ArticleAdapter.ItemHolder>() {

    class ItemHolder(val binding: ItemsArticleBinding) : ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemsArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }


    override fun getItemCount() = articles.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) = with(holder.binding) {
        article = articles[position]
        articleLayout.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToArticleDetailFragment(articles[position])
            it.findNavController().navigate(action)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newArticles: List<ArticleModel>) {
        articles = newArticles as ArrayList<ArticleModel>
        notifyDataSetChanged()
    }

}