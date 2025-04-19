package com.supercdat.storesuper.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.supercdat.storesuper.R
import androidx.core.net.toUri
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class AdapterClass (private val dataList: ArrayList<DataClass>): RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolderClass(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        Glide.with(holder.appIcon.context).load(currentItem.dataAppicon.toUri()).into(holder.appIcon)
        holder.applicationName.text = currentItem.dataApplicationname
        holder.publisherName.text = "bởi " + currentItem.dataPublishername
        holder.timesDownloaded.text = currentItem.dataTimesdownloaded

        var opened: Boolean = false
        holder.itemView.setOnClickListener {
            if (!opened) {
                opened=true

                val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(holder.appIcon.context)
                val view1: View = LayoutInflater.from(holder.appIcon.context).inflate(R.layout.app_detail_sheet, null)
                bottomSheetDialog.setContentView(view1)
                bottomSheetDialog.show()

                bottomSheetDialog.setOnDismissListener {
                    opened=false
                }

                bottomSheetDialog.findViewById<ImageView>(R.id.appicon)?.let { it1 ->
                    Glide.with(holder.appIcon.context).load(currentItem.dataAppicon.toUri()).into(
                        it1
                    )

                }

                bottomSheetDialog.findViewById<MaterialTextView>(R.id.applicationname)?.text = currentItem.dataApplicationname
                bottomSheetDialog.findViewById<MaterialTextView>(R.id.publishername)?.text = currentItem.dataPublishername

                val install: MaterialButton? = bottomSheetDialog.findViewById(R.id.install)
                val packageName: String = currentItem.dataPackagename
                val installedVersion = getInstalledAppVersion(holder.itemView.context, packageName)
                if (installedVersion != null && installedVersion != currentItem.dataApplicationversion) {
                    install?.text = "Cập nhật"
                    install?.setOnClickListener {
                        downloadAndInstallAPK(
                            holder.itemView.context,
                            currentItem.dataDownloadlink,
                            currentItem.dataApplicationname + ".apk",
                            install,
                            currentItem.dataPackagename
                        )
                    }
                } else if (isAppInstalled(holder.itemView.context, packageName)) {
                    install?.text = "Mở"
                    install?.setOnClickListener {
                        val launchIntent: Intent? = holder.itemView.context.packageManager.getLaunchIntentForPackage(packageName)
                        if (launchIntent != null) {
                            holder.itemView.context.startActivity(launchIntent)
                        }
                    }
                } else {
                    install?.setOnClickListener {
                        downloadAndInstallAPK(holder.itemView.context,
                            currentItem.dataDownloadlink,
                            currentItem.dataApplicationname+".apk", install,
                            currentItem.dataPackagename)
                    }
                }

                val detailcontainer: LinearLayout? = bottomSheetDialog.findViewById(R.id.detailcontainer)
                val detail: MaterialButton? = bottomSheetDialog.findViewById(R.id.detail)
                detail?.setOnClickListener {

                    bottomSheetDialog.findViewById<MaterialTextView>(R.id.packagesize)?.text = currentItem.dataPackagesize + " MB"
                    bottomSheetDialog.findViewById<MaterialTextView>(R.id.applicationdescription)?.text = currentItem.dataAppplicationdescription
                    bottomSheetDialog.findViewById<MaterialTextView>(R.id.category)?.text = currentItem.dataAppicationcategory
                    bottomSheetDialog.findViewById<MaterialTextView>(R.id.version)?.text = currentItem.dataApplicationversion

                    if (detailcontainer?.visibility == View.GONE) {
                        detailcontainer.visibility = View.VISIBLE
                    } else {
                        detailcontainer?.visibility = View.GONE

                    }

                }




            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolderClass (itemView: View): RecyclerView.ViewHolder(itemView) {
        val appIcon: ImageView = itemView.findViewById(R.id.appicon)
        val applicationName: MaterialTextView = itemView.findViewById(R.id.applicationname)
        val publisherName: MaterialTextView = itemView.findViewById(R.id.publishername)
        val timesDownloaded: MaterialTextView = itemView.findViewById(R.id.timesdownloaded)
    }

    fun downloadAndInstallAPK(context: Context, fileUrl: String, fileName: String, install: MaterialButton, packageName: String) {
        val destination = File(context.getExternalFilesDir(null), fileName)

        Thread {
            try {
                // Disable the button to prevent multiple clicks
                (context as? android.app.Activity)?.runOnUiThread {
                    install.isEnabled = false
                }

                // Download the file
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Server returned HTTP ${connection.responseCode}")
                }

                val fileLength = connection.contentLength
                val inputStream: InputStream = connection.inputStream
                val outputStream = FileOutputStream(destination)

                val buffer = ByteArray(4096)
                var bytesRead: Int
                var totalBytesRead = 0

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    // Calculate and update progress
                    val progress = (totalBytesRead * 100 / fileLength)
                    (context as? android.app.Activity)?.runOnUiThread {
                        install.text = "$progress%"
                    }
                }

                outputStream.close()
                inputStream.close()

                // Reset button text to "Install" after download
                (context as? android.app.Activity)?.runOnUiThread {
                    install.text = "Cài đặt"
                    install.isEnabled = true // Re-enable the button
                }

                // Install the APK
                val fileUri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        destination
                    )
                } else {
                    Uri.fromFile(destination)
                }

                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(fileUri, "application/vnd.android.package-archive")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                context.startActivity(installIntent)

                // Register a BroadcastReceiver to delete the file after installation
                val packageReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        val installedPackage = intent.data?.schemeSpecificPart
                        if (installedPackage != null) {
                            // Delete the file after installation
                            destination.delete()
                            context.unregisterReceiver(this)
                            if (isAppInstalled(context, packageName)) {
                                (context as? android.app.Activity)?.runOnUiThread {
                                    install.text = "Mở"
                                    install.setOnClickListener {
                                        val launchIntent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
                                        if (launchIntent != null) {
                                            context.startActivity(launchIntent)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                val intentFilter = IntentFilter(Intent.ACTION_PACKAGE_ADDED).apply {
                    addDataScheme("package")
                }
                context.registerReceiver(packageReceiver, intentFilter)

            } catch (e: Exception) {
                e.printStackTrace()
                (context as? android.app.Activity)?.runOnUiThread {
                    install.text = "Cài đặt"
                    install.isEnabled = true // Re-enable the button in case of error
//                    Toast.makeText(context, "Error: ${e.message ?: "Unknown error occurred"}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true // Package exists
        } catch (e: PackageManager.NameNotFoundException) {
            false // Package does not exist
        }
    }

    fun getInstalledAppVersion(context: Context, packageName: String): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }



}