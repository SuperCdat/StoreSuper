package com.supercdat.storesuper.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.supercdat.storesuper.R
import com.supercdat.storesuper.databinding.FragmentOptionsBinding

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

        try {

            val managePermission: MaterialCardView = root.findViewById(R.id.ManagePermission)
            managePermission.setOnClickListener {

                val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireActivity())
                val view1: View = LayoutInflater.from(requireActivity()).inflate(R.layout.bottom_sheet, null)
                bottomSheetDialog.setContentView(view1)
                bottomSheetDialog.show()

                val cancel: MaterialButton = view1.findViewById(R.id.cancel)
                cancel.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }


            }

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}