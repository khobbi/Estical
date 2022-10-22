package com.edon.estical.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.edon.estical.models.Item
import com.edon.estical.models.Project

class MainDatabase private constructor(context: Context):
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private var instance: MainDatabase? = null
        fun getInstance(context: Context): MainDatabase{
            if (instance == null){
                instance = MainDatabase(context)
            }
            return instance!!
        }

        private val DB_NAME = "projects"
        private val DB_VERSION = 4

        private val TB_PROJECTS = "project"
        private val COL_ID_PROJ = "_id"
        private val COL_IMAGE_PROJ = "image"
        private val COL_TITLE_PROJ = "title"
        private val COL_ESTIMATION_PROJ = "estimation"
        private val COL_DESCRIPTION_PROJ = "description"

        private val TB_ITEMS = "item"
        private val COL_ID_ITEM = "_id"
        private val COL_TITLE_ITEM = "title"
        private val COL_UNIT_PRICE_ITEM = "unit_price"
        private val COL_DESC_ITEM = "description"
        private val COL_IMAGE_ITEM = "image"
        private val COL_PROJ_ID_ITEM = "proj_id"
        private val COL_QUANTITY_ITEM = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_PROJECTS = "CREATE TABLE " + TB_PROJECTS + "(" +
                COL_ID_PROJ + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_IMAGE_PROJ + " INTEGER," +
                COL_TITLE_PROJ + " TEXT," +
                COL_ESTIMATION_PROJ + " FLOAT," +
                COL_DESCRIPTION_PROJ + " TEXT" + " );"
        db?.execSQL(CREATE_TABLE_PROJECTS)

        val CREATE_TABLE_ITEMS = "CREATE TABLE " + TB_ITEMS + "(" +
                COL_ID_ITEM + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TITLE_ITEM + " TEXT," +
                COL_UNIT_PRICE_ITEM + " FLOAT," +
                COL_DESC_ITEM + " TEXT," +
                COL_IMAGE_ITEM + " INTEGER," +
                COL_PROJ_ID_ITEM + " INTEGER NOT NULL, " +
                COL_QUANTITY_ITEM + " INTEGER, " +
                "FOREIGN KEY (" + COL_PROJ_ID_ITEM + ") " +
                "REFERENCES " + TB_PROJECTS + " (" + COL_ID_PROJ + ") " +
                "ON UPDATE CASCADE ON DELETE CASCADE" + ");"
        db?.execSQL(CREATE_TABLE_ITEMS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TB_PROJECTS;")
        db?.execSQL("DROP TABLE IF EXISTS $TB_ITEMS;")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        if(!db!!.isReadOnly){
            //enable foreign key constraint
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
    }

    //insert project
    fun insertProject(project: Project): Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_IMAGE_PROJ, project.image)
        values.put(COL_TITLE_PROJ, project.title)
        values.put(COL_ESTIMATION_PROJ, project.estimation)
        values.put(COL_DESCRIPTION_PROJ, project.description)

        val success = db.insert(TB_PROJECTS, null, values)
        db.close()
        return success.toInt()
    }

    //update project
    fun updateProject(project: Project): Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_IMAGE_PROJ, project.image)
        values.put(COL_TITLE_PROJ, project.title)
        values.put(COL_ESTIMATION_PROJ, project.estimation)
        values.put(COL_DESCRIPTION_PROJ, project.description)

        val success = db.update(TB_PROJECTS, values, COL_ID_PROJ + "=" + project.id, null)
        db.close()
        return success
    }

    //delete project
    fun deleteProject(project: Project): Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID_PROJ, project.id)

        val success = db.delete(TB_PROJECTS, COL_ID_PROJ + " = ?", arrayOf(project.id.toString()))
        db.close()
        return success
    }

    //get all the projects in table
    fun getAllProjects(): ArrayList<Project>{
        val db = this. readableDatabase
        val projects = ArrayList<Project>()
        val SELECT_QUERY = "SELECT * FROM $TB_PROJECTS"
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(SELECT_QUERY, null)
        } catch (ex: SQLiteException){
            db.execSQL(SELECT_QUERY)
            return ArrayList()
        }

        if(cursor.moveToFirst()){
            do{
                val id = cursor.getInt(cursor.getColumnIndex(COL_ID_PROJ))
                val image = cursor.getInt(cursor.getColumnIndex(COL_IMAGE_PROJ))
                val title = cursor.getString(cursor.getColumnIndex(COL_TITLE_PROJ))
                val estimation = cursor.getFloat(cursor.getColumnIndex(COL_ESTIMATION_PROJ))
                val description = cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION_PROJ))

                projects.add(Project(id, image, title, estimation, description))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return projects
    }

    //insert item
    fun insertItem(item: Item): Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TITLE_ITEM, item.title)
        values.put(COL_UNIT_PRICE_ITEM, item.unitPrice)
        values.put(COL_DESC_ITEM, item.description)
        values.put(COL_IMAGE_ITEM, item.image)
        values.put(COL_PROJ_ID_ITEM, item.projectId)
        values.put(COL_QUANTITY_ITEM, item.quantity)

        val success = db.insert(TB_ITEMS, null, values)
        db.close()
        return success.toInt()
    }

    //update item
    fun updateItem(item: Item): Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TITLE_ITEM, item.title)
        values.put(COL_UNIT_PRICE_ITEM, item.unitPrice)
        values.put(COL_DESC_ITEM, item.description)
        values.put(COL_IMAGE_ITEM, item.image)
        values.put(COL_PROJ_ID_ITEM, item.projectId)
        values.put(COL_QUANTITY_ITEM, item.quantity)

        val success = db.update(TB_ITEMS, values, COL_ID_ITEM + "=" + item.id, null)
        db.close()
        return success
    }

    //delete item
    fun deleteItem(item: Item): Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID_PROJ, item.id)

        val success = db.delete(TB_ITEMS, COL_ID_ITEM + " = ?", arrayOf(item.id.toString()))
        db.close()
        return success
    }

    //get all items in a Project
    fun getItemsInProject(projectId: Int): ArrayList<Item>{
        val db = this.readableDatabase
        val items = ArrayList<Item>()
        val selectQuery = "SELECT * FROM " + TB_ITEMS + " WHERE " + COL_PROJ_ID_ITEM + "=?"
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, arrayOf(projectId.toString()))
        } catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if(cursor.moveToFirst()){
            do{
                val id = cursor.getInt(cursor.getColumnIndex(COL_ID_ITEM))
                val title = cursor.getString(cursor.getColumnIndex(COL_TITLE_ITEM))
                val unitPrice = cursor.getFloat(cursor.getColumnIndex(COL_UNIT_PRICE_ITEM))
                val description = cursor.getString(cursor.getColumnIndex(COL_DESC_ITEM))
                val image = cursor.getInt(cursor.getColumnIndex(COL_IMAGE_ITEM))
                val projectId = cursor.getInt(cursor.getColumnIndex(COL_PROJ_ID_ITEM))
                val quantity = cursor.getInt(cursor.getColumnIndex(COL_QUANTITY_ITEM))

                items.add(Item(id, title, unitPrice, description, image, projectId, quantity))
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return items
    }

    fun updateProjectEstimate(projectId: Int, itemUnitPrice: Float, itemQuantity: Int){
        //add the new price to the total estimate of the project as new project estimate
        val projectList = this.getAllProjects()
        var totalEstimate = 0f
        //find the project with the same id and calculate new cost
        for(project in projectList){
            if(project.id == projectId){
                totalEstimate += project.estimation
            }
        }
        val newEstimate = totalEstimate + (itemUnitPrice * itemQuantity)

        //update it
        val db = this.writableDatabase
        val values: ContentValues = ContentValues()
        values.put(COL_ESTIMATION_PROJ, newEstimate)
        db.update(TB_PROJECTS, values, COL_ID_PROJ + "=?", arrayOf(projectId.toString()))
        db.close()
    }
}