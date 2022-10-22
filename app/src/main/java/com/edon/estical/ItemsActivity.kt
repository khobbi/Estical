package com.edon.estical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.edon.estical.adapters.ItemAdapter
import com.edon.estical.databases.MainDatabase
import com.edon.estical.databinding.ActivityItemsBinding
import com.edon.estical.models.Item

class ItemsActivity : AppCompatActivity() {
    lateinit var bnd: ActivityItemsBinding
    private var dataSet: ArrayList<Item>? = null
    private var adapter: ItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bnd = ActivityItemsBinding.inflate(layoutInflater)
        setContentView(bnd.root)

        val intentIn = intent //get intent

        val projectId = intentIn.getIntExtra("projectId", 0) //get data from intent
        val projectTitle = intentIn.getStringExtra("projectTitle")

        dataSet = getData(projectId)

        if(dataSet!!.size == 0){
            bnd.imgCreateNewItem.visibility = View.VISIBLE
            bnd.recProjectItems.visibility = View.GONE
            bnd.llBottomSheetItem.visibility = View.GONE
        } else {
            bnd.imgCreateNewItem.visibility = View.GONE
            bnd.recProjectItems.visibility = View.VISIBLE
            bnd.llBottomSheetItem.visibility = View.VISIBLE

            adapter = ItemAdapter(dataSet!!, this, getTotalEstimate(dataSet!!))
            bnd.recProjectItems.layoutManager = LinearLayoutManager(this)
            bnd.recProjectItems.adapter = adapter
        }
        //bottom sheet data
        bnd.tvTotalProjects.text = dataSet!!.size.toString()
        bnd.tvTotalEstimationItems.text = "\$" + String.format("%,.2f", getTotalEstimate(dataSet!!))
        bnd.tvProjectName.text = projectTitle

        //fab
        bnd.fabNewItem.setOnClickListener {
            val intent =  Intent(this, NewItemActivity::class.java)
            intent.putExtra("projectId", projectId)
            intent.putExtra("projectTitle", projectTitle)
            startActivity(intent)

            finish()
        }
    }

    //get all items that belongs to a particular project
    private fun getData(projectId: Int): ArrayList<Item>{
        val db = MainDatabase.getInstance(this)
        return db.getItemsInProject(projectId)
    }

    private fun getTotalEstimate(dataSet: ArrayList<Item>): Float{
        var estimate = 0.0f
        for(item in dataSet){
            estimate += item.unitPrice * item.quantity
        }
        return estimate
    }

    override fun onResume() {
        super.onResume()
        dataSet = getData(intent.getIntExtra("projectId", 0))
        adapter = ItemAdapter(dataSet!!, this, getTotalEstimate(dataSet!!))
    }
}