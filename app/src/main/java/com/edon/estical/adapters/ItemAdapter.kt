package com.edon.estical.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.edon.estical.MainActivity
import com.edon.estical.R
import com.edon.estical.databases.MainDatabase


import com.edon.estical.models.Item
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator

class ItemAdapter(private val dataSet: ArrayList<Item>, private val context: Context, private var totalEstimate: Float):
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val itemTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
        val itemEstimate: TextView = itemView.findViewById(R.id.tvItemEstimate)
        val itemDescription: TextView = itemView.findViewById(R.id.tvItemDescription)
        val itemImage: ImageView = itemView.findViewById(R.id.imgItemImage)
        val itemOptions: ImageView = itemView.findViewById(R.id.imgOptionsItem)
        val itemPercentage: CircularProgressIndicator = itemView.findViewById(R.id.progItemPercentage)
        val itemQuantity: TextView = itemView.findViewById(R.id.tvItemQuantity)
        val itemUnitPrice: TextView = itemView.findViewById(R.id.tvUnitPrice)

        val itemContainer: MaterialCardView = itemView.findViewById(R.id.cardItemContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(
            R.layout.custom_item_layout,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            itemTitle.text = dataSet[position].title
            itemEstimate.text = "\$" + String.format("%,.2f", (dataSet[position].unitPrice * dataSet[position].quantity))
            itemDescription.text = dataSet[position].description
            itemImage.setImageResource(dataSet[position].image)
            //itemImage.setImageResource(R.drawable.fl_studio_logo)
            itemPercentage.max = 100
            itemPercentage.progress = (((dataSet[position].unitPrice * dataSet[position].quantity) / totalEstimate) * 100).toInt()
            itemQuantity.text = dataSet[position].quantity.toString()
            itemUnitPrice.text = "\$" + String.format("%,.2f", dataSet[position].unitPrice)
            //on click for the options
            itemOptions.setOnClickListener { view ->
                editAndDeletePopupMenu(dataSet[position], holder.adapterPosition, view)
            }

            itemContainer.setOnClickListener{
                Toast.makeText(context, dataSet[position].title, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    //for displaying menu
    private fun editAndDeletePopupMenu(item: Item, position: Int, view: View){
        val popUpMenu = PopupMenu(context, view)
        popUpMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.menuEdit -> {
                    Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menuDelete-> {
                    deleteItem(item, position)
                    true
                }
                else -> false
            }
        }
        popUpMenu.inflate(R.menu.menu_edit_delete)
        popUpMenu.show()
    }

    //delete item
    private fun deleteItem(item: Item, position: Int){
        val db = MainDatabase.getInstance(context)
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Delete Item")
        alertDialog.setMessage("Are you sure you want to delete ${item.title}?")

        alertDialog.setPositiveButton("Delete"){_, _ ->
            val success = db.deleteItem(item)
            if(success > 0){
                dataSet.removeAt(position)
                notifyItemRemoved(position)
            } else {
                Toast.makeText(context, "Error deleting...", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.setNegativeButton("Cancel"){dialogInterface, _ ->
            dialogInterface.cancel()
        }

        alertDialog.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
}