package com.edon.estical

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.edon.estical.databases.MainDatabase
import com.edon.estical.databinding.ActivityNewItemBinding
import com.edon.estical.models.Item

class NewItemActivity : AppCompatActivity() {
    lateinit var bnd: ActivityNewItemBinding
    companion object{
        private const val GALLERY_REQUEST_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bnd = ActivityNewItemBinding.inflate(layoutInflater)
        setContentView(bnd.root)

        val intentIn = intent
        val projectId = intentIn.getIntExtra("projectId", 0)
        val projectTitle = intentIn.getStringExtra("projectTitle").toString()

        bnd.tvProjectTitle.text = projectTitle

        //pick photo
        bnd.imgItemImg.setOnClickListener { view ->
            photoPicker(view)
        }

        //for saving
        bnd.btnSave.setOnClickListener {
            saveAndUpdate(projectId, projectTitle)
        }
    }

    private fun saveAndUpdate(projectId: Int, projectTitle: String){
        val db = MainDatabase.getInstance(this)

        if(bnd.tieItemName.text.toString().isNotEmpty() &&
            bnd.tieItemEstimate.text.toString().isNotEmpty() &&
            bnd.tieItemQuantity.text.toString().isNotEmpty()){
            val success = db.insertItem(
                Item(0,
                    bnd.tieItemName.text.toString(),
                    bnd.tieItemEstimate.text.toString().toFloat(),
                    bnd.tieItemDescription.text.toString(),
                    R.drawable.fl_studio_logo,
                    projectId,
                    bnd.tieItemQuantity.text.toString().toInt()
                    )
            )
            //error in saving
            if (success < 0){
                Toast.makeText(this, "Error saving item...", Toast.LENGTH_SHORT).show()
            } else {
                //add this price to the project estimate
                db.updateProjectEstimate(
                    projectId,
                    bnd.tieItemEstimate.text.toString().toFloat(),
                    bnd.tieItemQuantity.text.toString().toInt())

                val intent = Intent(this, ItemsActivity::class.java)
                intent.putExtra("projectId", projectId)
                intent.putExtra("projectTitle", projectTitle)
                startActivity(intent)
                finish()
            }
        } else {
            bnd.tilItemName.error = getString(R.string.required)
            bnd.tilItemEstimate.error = getString(R.string.required)
            bnd.tilItemQuantity.error = getString(R.string.required)
        }
    }

    fun photoPicker(view: View){
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
            bnd.imgItemImg.setImageBitmap(bmp)
        } else if(requestCode == GALLERY_REQUEST_CODE){
            bnd.imgItemImg.setImageURI(data?.data)
        }
    }
}