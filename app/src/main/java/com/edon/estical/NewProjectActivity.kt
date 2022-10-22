package com.edon.estical

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.edon.estical.databases.MainDatabase
import com.edon.estical.databinding.ActivityNewProjectBinding
import com.edon.estical.models.Project

class NewProjectActivity : AppCompatActivity() {
    lateinit var bnd: ActivityNewProjectBinding
    companion object{
        private const val GALLERY_REQUEST_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bnd = ActivityNewProjectBinding.inflate(layoutInflater)
        setContentView(bnd.root)

        //pick image
        bnd.imgProjectImg.setOnClickListener { view ->
            photoPicker(view)
        }

        //save new or update existing
        bnd.btnSave.setOnClickListener {
            updateAndSaveNew()
        }
    }

    private fun updateAndSaveNew(){
        val db = MainDatabase.getInstance(this)

        if(bnd.tieProjectName.text.toString().isNotEmpty()){
            val success = db.insertProject(Project(
                0,
                R.drawable.fl_studio_logo,
                bnd.tieProjectName.text.toString(),
                0f,
                bnd.tieProjectDescription.text.toString())
            )
            //error in saving
            if (success < 0){
                Toast.makeText(this, "Error saving project...", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        } else {
            bnd.tilProjectName.error = getString(R.string.required)
        }
    }

    private fun photoPicker(view: View){
        val popupMenu = PopupMenu(this, view)
        popupMenu.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.menuTakePhoto -> {
                    val intent  = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMERA_REQUEST_CODE)
                    true
                }
                R.id.menuChoosePhoto -> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, GALLERY_REQUEST_CODE)
                    true
                }
                else -> false
            }
        }
        popupMenu.inflate(R.menu.menu_photo_picker)
        popupMenu.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CAMERA_REQUEST_CODE){
            val bmp: Bitmap = data?.extras?.get("data") as Bitmap
            bnd.imgProjectImg.setImageBitmap(bmp)
        } else if(requestCode == GALLERY_REQUEST_CODE){
            bnd.imgProjectImg.setImageURI(data?.data)
        }
    }
}