package com.github.panpf.sketch.sample.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.databinding.FragmentImageBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.viewability.showArcProgressIndicator
import com.github.panpf.sketch.viewability.showDataFromLogo

class ImageFragment : BindingFragment<FragmentImageBinding>() {

    private val args by navArgs<ImageFragmentArgs>()

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentImageBinding.inflate(inflater, parent, false)

    override fun onInitViews(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        super.onInitViews(binding, savedInstanceState)
        binding.imageFragmentImageView.showArcProgressIndicator()
        binding.imageFragmentImageView.showDataFromLogo()
    }

    override fun onInitData(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        binding.imageFragmentImageView.displayImage(args.url) {
            lifecycle(viewLifecycleOwner.lifecycle)
        }
    }
}