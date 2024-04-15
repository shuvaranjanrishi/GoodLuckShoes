package com.example.goodluckshoes

import android.os.Environment
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.File

class LocalBackup(private val activity: MainActivity) {
    //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
    fun performBackup(db: DbHelper, outFileName: String) {
        Permissions.verifyStoragePermissions(activity)
        val folder = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + activity.resources.getString(R.string.app_name)
        )
        var success = true
        if (!folder.exists()) success = folder.mkdirs()
        if (success) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("Backup Name")
            val input = EditText(activity)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton("Save") { dialog, which ->
                val m_Text = input.text.toString()
                val out = "$outFileName$m_Text.db"
                db.backup(out)
            }
            builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
            builder.show()
        } else Toast.makeText(activity, "Unable to create directory. Retry", Toast.LENGTH_SHORT)
            .show()
    }

    //ask to the user what backup to restore
    fun performRestore(db: DbHelper) {
        Permissions.verifyStoragePermissions(activity)
        val folder = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + activity.resources.getString(R.string.app_name)
        )
        if (folder.exists()) {
            val files = folder.listFiles()
            val arrayAdapter = ArrayAdapter<String>(activity, R.layout.activity_view_all)
            for (file in files!!) arrayAdapter.add(file.name)
            val builderSingle: AlertDialog.Builder = AlertDialog.Builder(activity)
            builderSingle.setTitle("Restore:")
            builderSingle.setNegativeButton(
                "cancel"
            ) { dialog, which -> dialog.dismiss() }
            builderSingle.setAdapter(
                arrayAdapter
            ) { dialog, which ->
                try {
                    db.importDB(files[which].path)
                } catch (e: Exception) {
                    Toast.makeText(activity, "Unable to restore. Retry", Toast.LENGTH_SHORT).show()
                }
            }
            builderSingle.show()
        } else Toast.makeText(
            activity,
            "Backup folder not present.\nDo a backup before a restore!",
            Toast.LENGTH_SHORT
        ).show()
    }
}