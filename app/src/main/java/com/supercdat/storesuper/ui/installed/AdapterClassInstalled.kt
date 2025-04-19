package com.supercdat.storesuper.ui.installed

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.supercdat.storesuper.R


class AdapterClassInstalled(private val context: Context, dataList: ArrayList<DataClassInstalled>) : RecyclerView.Adapter<AdapterClassInstalled.ViewHolderClass>() {

    private val filteredDataList: List<DataClassInstalled> = dataList.filter { isAppInstalled(context, it.dataPackagename) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_installed, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = filteredDataList[position]
        Glide.with(holder.appIcon.context).load(currentItem.dataAppicon.toUri()).into(holder.appIcon)
        holder.applicationName.text = currentItem.dataApplicationname
        holder.version.text = currentItem.dataApplicationversion
        holder.category.text = currentItem.dataAppicationcategory

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0) {
            layoutParams.topMargin = 300
        }
        holder.itemView.layoutParams = layoutParams

        val uninstall: MaterialButton = holder.itemView.findViewById(R.id.uninstall)
        uninstall.setOnClickListener {

            holder.itemView.context
                .startActivity(
                    Intent(Intent.ACTION_DELETE)
                        .setData(("package:" + currentItem.dataPackagename)
                            .toUri()
                        )
                )

        }



    }

    override fun getItemCount(): Int {
        return filteredDataList.size
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon: ImageView = itemView.findViewById(R.id.appicon)
        val applicationName: MaterialTextView = itemView.findViewById(R.id.applicationname)
        val version: MaterialTextView = itemView.findViewById(R.id.version)
        val category: MaterialTextView = itemView.findViewById(R.id.category)
    }

    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}