package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.github.panpf.sketch.sample.BuildConfig
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentAboutBinding

class AboutFragment : ToolbarBindingFragment<FragmentAboutBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentAboutBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentAboutBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "About"

        binding.textAboutVersions.text = getString(R.string.text_version, BuildConfig.VERSION_NAME)
        binding.textAboutTypes.text =
            getString(R.string.text_types, BuildConfig.BUILD_TYPE, BuildConfig.FLAVOR)
    }
}