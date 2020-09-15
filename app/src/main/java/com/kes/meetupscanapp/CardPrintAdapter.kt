package com.kes.meetupscanapp

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import java.io.FileOutputStream
import java.io.IOException


class CardPrintAdapter(private val context: Context, private val user: UserModel?) : PrintDocumentAdapter() {

    private var pdfDocument: PrintedPdfDocument? = null
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        pdfDocument = PrintedPdfDocument(context, newAttributes)
        // Respond to cancellation request
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }

        // Compute the expected number of printed pages
        val pages = 1

        if (pages > 0) {
            // Return print information to print framework
            PrintDocumentInfo.Builder("print_output.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pages)
                .build()
                .also { info ->
                    // Content layout reflow is complete
                    callback?.onLayoutFinished(info, true)
                }
        } else {
            // Otherwise report an error to the print framework
            callback?.onLayoutFailed("Page count calculation failed.")
        }
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        // Iterate over each page of the document,
        // check if it's in the output range.

        pdfDocument?.startPage(1)?.also { page ->

            // check for cancellation
            if (cancellationSignal?.isCanceled == true) {
                callback?.onWriteCancelled()
                pdfDocument?.close()
                pdfDocument = null
                return
            }

            // Draw page content for printing
            drawPage(page)

            // Rendering is complete, so page can be finalized.
            pdfDocument?.finishPage(page)

        }

        // Write PDF document to file
        try {
            pdfDocument?.writeTo(FileOutputStream(destination!!.fileDescriptor))
        } catch (e: IOException) {
            callback?.onWriteFailed(e.toString())
            return
        } finally {
            pdfDocument?.close()
            pdfDocument = null
        }

        // Signal the print framework the document is complete
        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }


    private fun drawPage(page: PdfDocument.Page) {
        page.canvas.apply {

            // units are in points (1/72 of an inch)
            val titleBaseLine = 72f
            val leftMargin = 54f

            val paint = Paint()
            paint.color = Color.BLACK
            paint.textSize = 36f
            drawText("${user?.user?.surname +  user?.user?.name}", leftMargin, titleBaseLine, paint)

            paint.textSize = 11f
            drawText("${user?.user?.company}", leftMargin, titleBaseLine + 25, paint)

        }
    }

}