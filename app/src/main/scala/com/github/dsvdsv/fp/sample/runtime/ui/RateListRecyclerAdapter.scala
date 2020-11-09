package com.github.dsvdsv.fp.sample
package runtime
package ui

import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.github.dsvdsv.fp.sample.domain.net.RateList

class RateListRecyclerAdapter(var rateList: RateList) extends RecyclerView.Adapter[RateListRecyclerAdapter.ViewHolder] {
  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): RateListRecyclerAdapter.ViewHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(R.layout.rate_item, parent, false)

    new RateListRecyclerAdapter.ViewHolder(view)
  }

  override def onBindViewHolder(holder: RateListRecyclerAdapter.ViewHolder, position: Int): Unit = {
    val item = rateList.rates(position)
    holder.currency.setText("EUR/" + item.currency)
    holder.rate.setText(item.rate.toString())
  }

  override def getItemCount: Int = rateList.rates.length
}

object RateListRecyclerAdapter {
  class ViewHolder(view: View) extends RecyclerView.ViewHolder(view) {
    val cardView = view.findViewById[CardView](R.id.rateView)
    val currency = view.findViewById[TextView](R.id.currency)
    val rate     = view.findViewById[TextView](R.id.value)
  }
}
