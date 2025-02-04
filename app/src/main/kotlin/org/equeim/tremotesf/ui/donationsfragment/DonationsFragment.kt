/*
 * Copyright (C) 2017-2020 Alexey Rochev <equeim@gmail.com>
 *
 * This file is part of Tremotesf.
 *
 * Tremotesf is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Tremotesf is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.equeim.tremotesf.ui.donationsfragment

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.google.android.material.snackbar.Snackbar

import org.equeim.tremotesf.BuildConfig
import org.equeim.tremotesf.R
import org.equeim.tremotesf.billing.GoogleBillingHelper
import org.equeim.tremotesf.databinding.DonationsFragmentFdroidBinding
import org.equeim.tremotesf.databinding.DonationsFragmentGoogleBinding
import org.equeim.tremotesf.ui.addNavigationBarBottomPadding
import org.equeim.tremotesf.ui.utils.ArrayDropdownAdapter
import org.equeim.tremotesf.ui.utils.Utils
import org.equeim.tremotesf.ui.utils.showSnackbar
import org.equeim.tremotesf.ui.utils.viewBinding
import org.equeim.tremotesf.ui.utils.collectWhenStarted

class DonationsFragment :
    Fragment(if (BuildConfig.GOOGLE) R.layout.donations_fragment_google else R.layout.donations_fragment_fdroid) {
    companion object {
        private const val PAYPAL_USER = "DDQTRHTY5YV2G"
        private const val PAYPAL_CURRENCY_CODE = "USD"
        private const val PAYPAL_ITEM_NAME = "Support Tremotesf (Android) development"

        private const val YANDEX_URL = "https://yasobe.ru/na/equeim_tremotesf_android"
    }

    private val model: Model by viewModels()

    private val bindingFdroid by viewBinding(DonationsFragmentFdroidBinding::bind)
    private val bindingGoogle by viewBinding(DonationsFragmentGoogleBinding::bind)

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (BuildConfig.GOOGLE) {
            model.billing?.isSetUp?.collectWhenStarted(viewLifecycleOwner, ::onBillingSetup)
        } else {
            with(bindingFdroid) {
                paypalDonateButton.setOnClickListener { donatePayPal() }
                yandexDonateButton.setOnClickListener { donateYandex() }
            }
        }

        addNavigationBarBottomPadding()
    }

    private fun onBillingSetup(isSetUp: Boolean) {
        val billing = model.billing ?: return
        with(bindingGoogle) {
            if (isSetUp) {
                val items = if (BuildConfig.DEBUG) {
                    billing.skus.map { "${it.sku}: ${it.price}" }
                } else {
                    billing.skus.map(GoogleBillingHelper.SkuData::price)
                }
                skusView.setAdapter(ArrayDropdownAdapter(items))
                if (items.isNotEmpty()) {
                    skusView.setText(items.first())
                }
                donateButton.setOnClickListener {
                    donateGoogle(items.indexOf(skusView.text.toString()))
                }
                billing.purchasesUpdatedEvent.collectWhenStarted(
                    viewLifecycleOwner,
                    ::onBillingPurchasesUpdated
                )
            }
            skusViewLayout.isEnabled = isSetUp
            donateButton.isEnabled = isSetUp
        }
    }

    private fun donateGoogle(skuIndex: Int) {
        model.billing?.let { billing ->
            val error = billing.launchBillingFlow(skuIndex, requireActivity())
            if (error != GoogleBillingHelper.PurchaseError.None) {
                onBillingPurchasesUpdated(error)
            }
        }
    }

    private fun onBillingPurchasesUpdated(error: GoogleBillingHelper.PurchaseError) {
        when (error) {
            GoogleBillingHelper.PurchaseError.None -> {
                requireView().showSnackbar(R.string.donations_snackbar_ok, Snackbar.LENGTH_SHORT)
            }
            GoogleBillingHelper.PurchaseError.Error -> {
                requireView().showSnackbar(R.string.donations_snackbar_error, Snackbar.LENGTH_LONG)
            }
            GoogleBillingHelper.PurchaseError.Cancelled -> {
            }
        }
    }

    private fun donatePayPal() {
        val builder = Uri.Builder()
            .scheme("https")
            .authority("www.paypal.com")
            .path("cgi-bin/webscr")
            .appendQueryParameter("cmd", "_donations")
            .appendQueryParameter("business", PAYPAL_USER)
            .appendQueryParameter("lc", "US")
            .appendQueryParameter("item_name", PAYPAL_ITEM_NAME)
            .appendQueryParameter("no_note", "1")
            .appendQueryParameter("no_shipping", "1")
            .appendQueryParameter("currency_code", PAYPAL_CURRENCY_CODE)
        Utils.startActivityChooser(
            Intent(Intent.ACTION_VIEW, builder.build()),
            getText(R.string.donations_paypal_title),
            requireContext()
        )
    }

    private fun donateYandex() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, YANDEX_URL.toUri()))
        } catch (ignore: ActivityNotFoundException) {
        }
    }

    class Model(application: Application) : AndroidViewModel(application) {
        @Suppress("RedundantNullableReturnType")
        val billing: GoogleBillingHelper? = GoogleBillingHelper(application, viewModelScope)

        override fun onCleared() {
            billing?.endConnection()
        }
    }
}