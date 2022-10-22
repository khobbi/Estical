package com.edon.estical

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.edon.estical.adapters.ProjectAdapter
import com.edon.estical.databases.MainDatabase
import com.edon.estical.databinding.ActivityMainBinding
import com.edon.estical.models.Project

class MainActivity : AppCompatActivity() {
    lateinit var bnd: ActivityMainBinding
    private var dataSet: ArrayList<Project>? = null
    private var adapter: ProjectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bnd = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bnd.root)

        dataSet = getData()

        if (dataSet!!.size == 0){
            bnd.recMainProjects.visibility = View.GONE
            bnd.llBottomSheet.visibility = View.GONE
            bnd.imgCreateNewProject.visibility = View.VISIBLE
        } else {
            bnd.recMainProjects.visibility = View.VISIBLE
            bnd.llBottomSheet.visibility = View.VISIBLE
            bnd.imgCreateNewProject.visibility = View.GONE

            //prepare recyclerview with data and adapter
            adapter = ProjectAdapter(dataSet!!, this, getTotalEstimate(dataSet!!))
            bnd.recMainProjects.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            bnd.recMainProjects.adapter = adapter

            //fake bottom sheet values
            bnd.tvTotalProjects.text = dataSet!!.size.toString()
            //calculate estimation of all the projects
            bnd.tvTotalEstimationProjects.text = "\$" + String.format("%,.2f", getTotalEstimate(dataSet!!))
        }

        //onclick for fab
        bnd.fabNewProject.setOnClickListener {
            startActivity(Intent(this, NewProjectActivity::class.java))
            //finish()
        }
    }

    override fun onResume() {
        super.onResume()
        dataSet = getData()
        adapter = ProjectAdapter(dataSet!!, this, getTotalEstimate(dataSet!!))
    }
    //get all projects
    private fun getData(): ArrayList<Project>{
        val db = MainDatabase.getInstance(this)
        return db.getAllProjects()
    }

    private fun getTotalEstimate(dataSet: ArrayList<Project>): Float{
        var estimate = 0.0f
        for(project in dataSet){
            estimate += project.estimation
        }
        return estimate
    }

    //show all items that belong to a project clicked from the adapter
    fun showProjectItems(project: Project) {
        val intent = Intent(this, ItemsActivity::class.java)
        intent.putExtra("projectId", project.id)
        intent.putExtra("projectTitle", project.title)
        startActivity(intent)
    }
}