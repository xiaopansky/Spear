package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.sketch.sample.databinding.FragmentTestBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment

class TestFragment : BindingFragment<FragmentTestBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentTestBinding.inflate(inflater, parent, false)

    override fun onInitData(binding: FragmentTestBinding, savedInstanceState: Bundle?) {
        binding.testFragmentImageView.apply {
//            scaleType = ScaleType.CENTER
//            setImageDrawable(RingProgressDrawable(160).apply {
////                    progress = 1.4f
//                binding.testFragmentImageView.postDelayed({
//                    viewLifecycleOwner.lifecycleScope.launch {
//                        progress = 0.2f
//                        delay(1000)
//                        progress = 0.4f
//                        delay(1000)
//                        progress = 0.6f
//                        delay(1000)
//                        progress = 0.8f
//                        delay(1000)
//                        progress = 1f
//                    }
//                }, 1000)
//            })
//            displayImage(AssetImages.ANIMS[0]) {
//                placeholder(
//                    IconStateImage(R.drawable.ic_image_outline, ResColor(R.color.placeholder_bg))
//                )
//                crossfade()
//                resizeApplyToDrawable()
//                lifecycle(viewLifecycleOwner.lifecycle)
//                bitmapMemoryCachePolicy(DISABLED)
//            }
//            displayImage(AssetImages.FORMATS[0]) {
////                placeholder(
////                    IconStateImage(R.drawable.ic_image_outline, ResColor(R.color.placeholder_bg))
////                )
////                crossfade()
////                resizeApplyToDrawable()
////                lifecycle(viewLifecycleOwner.lifecycle)
////                bitmapMemoryCachePolicy(DISABLED)
//            }
        }
    }
}