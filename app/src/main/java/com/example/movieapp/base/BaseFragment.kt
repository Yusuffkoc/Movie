package com.example.movieapp.base

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.ContactsContract
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.movieapp.Application
import com.example.movieapp.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.Contexts.getApplication

@AndroidEntryPoint
open class BaseFragment : Fragment() {

    fun showToast(resId: Int) {
        showToast(getString(resId))
    }

    fun showError(message: String) {
        showToast(message)
    }

    @SuppressLint("ShowToast")
    fun showToast(message: String) {
        val toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

    private var mProgressDialog: ProgressDialog? = null

    open fun showLoading() {
        mProgressDialog?.setMessage(getString(R.string.loading))
        mProgressDialog?.setCancelable(false)
        mProgressDialog?.show()
    }

    open fun hideLoading() {
        var handler: android.os.Handler? = android.os.Handler()
        handler?.postDelayed({
            mProgressDialog.let {
                mProgressDialog?.hide()
                mProgressDialog?.dismiss()
                mProgressDialog = null
            }

        }, 500)
    }

    fun backPressed() {
        activity?.onBackPressed()
    }
}