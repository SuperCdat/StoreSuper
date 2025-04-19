package com.supercdat.storesuper.ui.options

import android.Manifest
import android.app.usage.ExternalStorageStats
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.audiofx.Equalizer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import android.util.ArraySet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.ArrayMap
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat.Action
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.supercdat.storesuper.BuildConfig
import com.supercdat.storesuper.R
import com.supercdat.storesuper.databinding.FragmentOptionsBinding
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.transition.Visibility
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView
import java.security.Permission

class OptionsFragment : Fragment() {

    private var _binding: FragmentOptionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val optionsViewModel =
            ViewModelProvider(this).get(OptionsViewModel::class.java)

        _binding = FragmentOptionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var opened: Boolean = false
        val managePermission: MaterialCardView = root.findViewById(R.id.ManagePermission)
        managePermission.setOnClickListener {
            if (!opened) {
                opened=true

                val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireActivity())
                val view1: View = LayoutInflater.from(requireActivity()).inflate(R.layout.bottom_sheet, null)
                bottomSheetDialog.setContentView(view1)
                bottomSheetDialog.show()

                val cancel: MaterialButton = view1.findViewById(R.id.cancel)
                cancel.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }

                bottomSheetDialog.setOnDismissListener {
                    opened=false
                }

                val changeReadAndWrite: MaterialButton = view1.findViewById(R.id.ChangeReadAndWrite)
                changeReadAndWrite.setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            ("package:" + BuildConfig.APPLICATION_ID).toUri()))
                    } else {
                        val result: Int = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                2
                            );
                        }
                    }
                }

                val installPackage: MaterialButton = view1.findViewById(R.id.InstallPackage)
                installPackage.setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            ("package:" + BuildConfig.APPLICATION_ID).toUri()))
                    }

                }


            }

        }


        val cardcustomdarkmode: MaterialCardView = root.findViewById(R.id.cardcustomdarkmode)
        val customdarkmode: MaterialSwitch = root.findViewById(R.id.customdarkmode)
        val customdarkmodedescription: MaterialTextView =
            root.findViewById(R.id.customdarkmodedescription)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            customdarkmode.isEnabled = true
            customdarkmodedescription.visibility = View.GONE

            if (requireActivity().getSharedPreferences("Mode", Context.MODE_PRIVATE).getBoolean("nightMode", false)) {
                cardcustomdarkmode.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.A))
                cardcustomdarkmode.strokeColor = ContextCompat.getColor(requireActivity(), R.color.A)
            } else {
                cardcustomdarkmode.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.mainBackgroundLightSecondary))
                cardcustomdarkmode.strokeColor = ContextCompat.getColor(requireActivity(), R.color.mainBackgroundLightSecondary)
            }

        }
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("Mode", Context.MODE_PRIVATE)
        val nightMode: Boolean = sharedPreferences.getBoolean("nightMode", false)
        var editor: SharedPreferences.Editor
        if (nightMode) {
            customdarkmode.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        customdarkmode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor = sharedPreferences.edit()
                editor.putBoolean("nightMode", true)
                cardcustomdarkmode.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.mainBackgroundLightSecondary))
                cardcustomdarkmode.strokeColor = ContextCompat.getColor(requireActivity(), R.color.mainBackgroundLightSecondary)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor = sharedPreferences.edit()
                editor.putBoolean("nightMode", false)
                cardcustomdarkmode.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.A))
                cardcustomdarkmode.strokeColor = ContextCompat.getColor(requireActivity(), R.color.A)
            }
            editor.apply()
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}