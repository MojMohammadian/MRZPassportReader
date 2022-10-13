package com.mohammadian.mrzpassportreader.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.innovatrics.mrz.MrzParseException
import com.innovatrics.mrz.MrzParser
import com.innovatrics.mrz.MrzRecord
import java.io.IOException


class TextReaderAnalyzer(
    private val context: Context,
    private val passFoundListener: (MrzRecord) -> Unit
) : ImageAnalysis.Analyzer {

    private var converter: YuvToRgbConverter? = null

    init {
        converter = YuvToRgbConverter(context)

    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        imageProxy.image?.let { process(imageProxy) }
        imageProxy.close()

    }

    private fun process(imageProxy: ImageProxy) {
        try {
            readTextFromImage(imageProxy)
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun readTextFromImage(imageProxy: ImageProxy) {

        val bitmap = allocateBitmapIfNecessary(imageProxy.width, imageProxy.height)
        converter!!.yuvToRgb(imageProxy.image!!, bitmap)


        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS).process(bitmap, 0)
            .addOnSuccessListener { visionText ->
                processTextFromImage(visionText, imageProxy)
                imageProxy.close()
            }
            .addOnFailureListener {

            }
            .addOnCompleteListener {
                imageProxy.close()
            }


    }

    private fun processTextFromImage(visionText: Text, imageProxy: ImageProxy) {
        Log.d("masss", visionText.textBlocks.size.toString())

        if (visionText.textBlocks.size >= 1) {

            for (block in visionText.textBlocks) {
                if (block.lines.size == 2) {
                    Log.d("masss", block.text)
                    checkCorrectMrzScanned(block)
                }
            }
        }
    }

    private fun checkCorrectMrzScanned(textBlock: Text.TextBlock) {
        if (!isPaused) {

            val firstLine = correctWrongCharacters(textBlock.lines[0].text)
            val secondLine = correctWrongCharacters(textBlock.lines[1].text)


            if (firstLine.length - secondLine.length == 0 && firstLine.length == 44) {
                if (secondLine[20] == 'F' || secondLine[20] == 'M' || secondLine[20] == '<') {
                    try {
                        val record = MrzParser.parse(firstLine + "\n" + secondLine)
                        record.surname = record.surname.replace("9", " ")
                        record.givenNames = record.givenNames.replace("9", " ")
                        if (!(record.surname.contains(',') || record.givenNames.contains(','))) {

                            isPaused = true

                            passFoundListener(record)
                        }


                    } catch (ex: MrzParseException) {
                        Log.d("mlkit_error_mrz: ", ex.message.toString())
                    }
                }

            }


        }


    }

    private fun correctWrongCharacters(mrzLine: String): String {
        var line = mrzLine
            .replace("«", "<")
            .replace(" ", "")
            .replace(",", "<")
            .replace("<K<", "<<<")
            .replace("<KK<", "<<<<")
            .replace("<KKK<", "<<<<<")
            .replace("<KKKK<", "<<<<<<")
            .replace("<KKKKK<", "<<<<<<<")
            .replace("<KKKKKK<", "<<<<<<<<")
            .replace("&", "8")
            .uppercase()
        val split = line.split("<<")[0].split("<")

        if (split.size > 2) {
            val index = split[0].length + split[1].length + 1
            line = line.substring(0, index) + "9" + line.substring(index + 1)
        }
        return line
    }


    var isPaused = false
    private var bitmap: Bitmap? = null

    private fun allocateBitmapIfNecessary(width: Int, height: Int): Bitmap {
        if (bitmap == null || bitmap!!.width != width || bitmap!!.height != height) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        return bitmap!!
    }

}