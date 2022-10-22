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
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.edon.estical.MainActivity
import com.edon.estical.R
import com.edon.estical.databases.MainDatabase
import com.edon.estical.models.Project
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator

class ProjectAdapter(private val dataSet: ArrayList<Project>, private val context: Context, private val totalEstimate: Float):
    RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    var isExpanded = false

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val projectImage: ImageView = itemView.findViewById(R.id.imgProjectImage)
        val options: ImageView = itemView.findViewById(R.id.imgOptions)
        val expandOrCollapse: ImageView = itemView.findViewById(R.id.imgExpandOrCollapse)
        val title: TextView = itemView.findViewById(R.id.tvProjectTitle)
        val estimate: TextView = itemView.findViewById(R.id.tvProjectEstimate)
        val description: TextView = itemView.findViewById(R.id.tvProjectDescription)
        val sep: View = itemView.findViewById(R.id.vSeparator)
        val progress: CircularProgressIndicator = itemView.findViewById(R.id.progProjectPercentage)

        val cardProject: MaterialCardView = itemView.findViewById(R.id.cardProjectContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.custom_layout_for_project,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            projectImage.setImageResource(dataSet[position].image)
            //projectImage.setImageResource(R.drawable.fl_studio_logo)
            title.text = dataSet[position].title
            estimate.text = "\$" + String.format("%,.2f", dataSet[position].estimation)
            description.text = dataSet[position].description
            progress.max = 100
            progress.progress = ((dataSet[position].estimation / totalEstimate) * 100).toInt()
            //collapse card
            collapse(holder)
            //onclicks
            options.setOnClickListener { view ->
                editAndDeletePopupMenu(dataSet[position], holder.adapterPosition, view)
            }

            expandOrCollapse.setOnClickListener {
                if(isExpanded){
                    collapse(holder)
                } else {
                    expand(holder)
                }
            }
            //open items that belong to this project
            cardProject.setOnClickListener{
                if(context is MainActivity){
                    context.showProjectItems(dataSet[position])
                }
                //Toast.makeText(context, "Db:" + dataSet[position].image.toString() + " App:" + R.drawable.fl_studio_logo, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun expand(holder: ViewHolder){
        holder.apply {
            sep.visibility = View.VISIBLE
            description.visibility = View.VISIBLE
            progress.visibility = View.VISIBLE

            //change icon with transition
            TransitionManager.beginDelayedTransition(holder.cardProject, AutoTransition())
            expandOrCollapse.setImageResource(R.drawable.ic_expand_less_24)
            isExpanded = true //change state
        }
    }

    fun collapse(holder: ViewHolder){
        holder.apply {
            sep.visibility = View.GONE
            description.visibility = View.GONE
            progress.visibility = View.GONE

            //change icon with transition
            TransitionManager.beginDelayedTransition(holder.cardProject, AutoTransition())
            expandOrCollapse.setImageResource(R.drawable.ic_expand_more_24)
            isExpanded = false //change state
        }
    }

    //for displaying menu
    private fun editAndDeletePopupMenu(project: Project, position: Int, view: View){
        val popupMenu = PopupMenu(context, view)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.menuEdit -> {
                    //editTodo(todo)
                    Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menuDelete -> {
                    //deleteTodo(todo, position)
                    deleteProject(project, position)
                    true
                }
                else -> false
            }
        }
        popupMenu.inflate(R.menu.menu_edit_delete)
        popupMenu.show()
    }

    //delete project
    private fun deleteProject(project: Project, position: Int){
        val db = MainDatabase.getInstance(context)

        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Delete Project")
        alertDialog.setMessage("Are you sure you want to delete ${project.title}?")

        alertDialog.setPositiveButton("Delete"){ _, _ ->
            val success = db.deleteProject(project)
            if(success < 0){
                Toast.makeText(context, "Error deleting...", Toast.LENGTH_SHORT).show()
            } else {
                dataSet.removeAt(position)
                notifyItemRemoved(position)
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