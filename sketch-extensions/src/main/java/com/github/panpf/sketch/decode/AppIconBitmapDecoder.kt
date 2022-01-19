package com.github.panpf.sketch.decode

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.AppIconUriFetcher.AppIconDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DataFrom.LOCAL
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.drawableToBitmap

class AppIconBitmapDecoder(
    val sketch: Sketch,
    val request: LoadRequest,
    val packageName: String,
    val versionCode: Int,
) : BitmapDecoder {

    override suspend fun decodeBitmap(): BitmapDecodeResult {
        val packageManager = sketch.appContext.packageManager
        val packageInfo: PackageInfo = try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            throw Exception("Not found PackageInfo by '$packageName'", e)
        }
        if (packageInfo.versionCode != versionCode) {
            throw Exception("App versionCode mismatch, ${packageInfo.versionCode} != $versionCode")
        }
        val iconDrawable = packageInfo.applicationInfo.loadIcon(packageManager)
            ?: throw Exception("loadIcon return null '$packageName'")
        val bitmap = drawableToBitmap(iconDrawable, false, sketch.bitmapPoolHelper)
        val imageInfo = ImageInfo(
            AppIconUriFetcher.MIME_TYPE,
            bitmap.width,
            bitmap.height,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        return BitmapDecodeResult(bitmap, imageInfo, LOCAL, true)
    }

    override fun close() {

    }

    class Factory : BitmapDecoder.Factory {

        override fun create(
            sketch: Sketch, request: LoadRequest, fetchResult: FetchResult
        ): BitmapDecoder? {
            val dataSource = fetchResult.dataSource
            return if (
                AppIconUriFetcher.MIME_TYPE.equals(fetchResult.mimeType, ignoreCase = true)
                && dataSource is AppIconDataSource
            ) {
                AppIconBitmapDecoder(
                    sketch, request, dataSource.packageName, dataSource.versionCode
                )
            } else {
                null
            }
        }
    }
}