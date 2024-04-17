package com.example.goodluckshoes

import android.annotation.SuppressLint
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.goodluckshoes.databinding.ActivityViewAllBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.RuntimeException
import java.util.Calendar

class ViewAllActivity : AppCompatActivity() {

    private val TAG = "ViewAllActivity"

    var binding: ActivityViewAllBinding? = null

    var date: String = ""

    private lateinit var adapter: ListViewAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var db: DbHelper

    private var localBackup: LocalBackup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)

        if (intent != null) {
            if (!intent.extras!!.isEmpty) {
                date = intent.getStringExtra("DATE").toString()
                binding!!.createDateTv.text = date
            }
        }

        binding!!.pdfBtn.setOnClickListener {
            convertXmlToPdf()
        }

//        localBackup = LocalBackup(this)
        db = DbHelper(this, null)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.data.observe(this) {

            var total1 = "0"
            var total2 = "0"
            var total3 = "0"
            var total4 = "0"

            Log.d(TAG, "Data Size: ${it.size}")
            adapter = ListViewAdapter(it)
            binding!!.recyclerView.adapter = adapter

            for (item in it) {
                total1 = (total1.toDouble() + item.text1.toDouble()).toString()
                total2 = (total2.toDouble() + item.text2.toDouble()).toString()
                total3 = (total3.toDouble() + item.text3.toDouble()).toString()
                total4 = (total4.toDouble() + item.text4.toDouble()).toString()
            }

            binding!!.totalTv.text = "মোট\n(" + it.size.toString() + ")"
            binding!!.total1.text = total1
            binding!!.total2.text = total2
            binding!!.total3.text = total3
            binding!!.total4.text = total4
        }

        viewModel.getDataByDate(db, date)
    }

    @SuppressLint("ResourceType")
    fun convertXmlToPdf() {
        val view: View = findViewById(R.id.mainHSV)
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display!!.getRealMetrics(displayMetrics)
        } else this.windowManager.defaultDisplay.getMetrics(displayMetrics)

        view.measure(
            View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val pdfDocument = PdfDocument()
        val width: Int = view.measuredWidth
        val height: Int = view.measuredHeight

        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(
            binding!!.recyclerView.width,
            binding!!.recyclerView.height,
            1
        ).create()
        val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)

        //canvas
        val canvas = page.canvas
        view.draw(canvas)

        pdfDocument.finishPage(page)

        val downloadDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = "$date.pdf"
        val folder = File(downloadDir, "Good Luck Shoes")
        if (!folder.exists()) {
            folder.mkdir()
        }
        val file = File(folder, fileName)
        if (file.exists()) {
            file.delete()
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()
            Toast.makeText(this, "ফাইলটি পিডিএফ হিসেবে ডাউনলোড হয়েছে", Toast.LENGTH_LONG).show()

        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.message.toString())
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}