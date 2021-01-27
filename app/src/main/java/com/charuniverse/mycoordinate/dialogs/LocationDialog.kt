package com.charuniverse.mycoordinate.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.charuniverse.mycoordinate.MainViewModel
import com.charuniverse.mycoordinate.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.item_custom_dialog.view.*

class LocationDialog : BottomSheetDialogFragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_custom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())
            .get(MainViewModel::class.java)

        setDialogUI(view)
        buttonClickListener(view)
    }

    private fun setDialogUI(view: View) {
        view.let {
            it.tvDialogPermission.text =
                getText(R.string.dialog_no_location)
            it.btnDialogPermission.text =
                getText(R.string.btn_no_location)
        }
    }

    private fun buttonClickListener(view: View) {
        view.let {
            it.ivCloseDialogPermission.setOnClickListener {
                dismiss()
            }
            it.btnDialogPermission.setOnClickListener {
                viewModel.requestEnableLocation()
                dismiss()
            }
        }
    }
}