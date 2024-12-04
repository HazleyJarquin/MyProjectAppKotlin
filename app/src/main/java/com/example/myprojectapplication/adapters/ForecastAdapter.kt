    package com.example.myprojectapplication.adapters

    import android.view.LayoutInflater
    import android.view.ViewGroup
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.myprojectapplication.databinding.ForecastCardBinding

    class ForecastAdapter(private val items: List<ForecastItem>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

        inner class ForecastViewHolder(private val binding: ForecastCardBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(item: ForecastItem) {
                binding.apply {
                    dateTextView.text = item.date
                    tempTextView.text = item.temp
                    descriptionTextView.text = item.description
                    Glide.with(itemView.context)
                        .load(item.iconUrl)
                        .placeholder(com.example.myprojectapplication.R.drawable.ic_launcher_foreground)
                        .error(com.example.myprojectapplication.R.drawable.ic_launcher_foreground)
                        .into(weatherIcon)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ForecastCardBinding.inflate(inflater, parent, false)
            return ForecastViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size
    }
